import builder.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.Options;
import config.pojo.Option;
import model.CodeGraph;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DotGraph;
import utils.ObjectUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class RunOnDataset {
    private final static Logger logger = LoggerFactory.getLogger(RunOnDataset.class);

    public static void main(String[] args) {
        Option option = processArgs(args);
        logger.info("option: {}", option);
        run(option);
    }

    private static void run(Option option) {
        long startTime;
        long currentTime;
        if(option.testOnDataset.datasetName.equals("c3")) {
            List<String> projects = List.of("ant", "checkstyle", "cobertura", "drjava", "junit", "swt");
            for (String project : projects) {
                System.out.println("project: " + project);
                Path projectRootPath = Paths.get(option.testOnDataset.datasetPath).resolve(project);
                Path patchRootPath = Paths.get(option.testOnDataset.patchPath);
                File[] groups = Arrays.stream(Objects.requireNonNull(projectRootPath.toFile().listFiles())).filter(File::isDirectory).toArray(File[]::new);
                for (File group : groups) {
                    String groupID = group.getName();
                    Path projectCodeGroupRoot = projectRootPath.resolve(groupID);
                    List<String> cases = Arrays.stream(Objects.requireNonNull(projectCodeGroupRoot.toFile().listFiles()))
                            .filter(File::isDirectory)
                            .map(File::getName)
                            .collect(Collectors.toList());

                    for (String patternCaseNum : cases) {
                        try{
                            startTime = System.currentTimeMillis();
                            Path patternBeforePath = projectCodeGroupRoot.resolve(patternCaseNum).resolve("before.java");
                            Path patternAfterPath = projectCodeGroupRoot.resolve(patternCaseNum).resolve("after.java");
                            CodeGraph ag = GraphBuilder.buildActionGraph(patternBeforePath.toString(), patternAfterPath.toString(), new int[]{});
                            List<Pattern> patterns = PatternExtractor.combineGraphs(List.of(ag), "new");
                            currentTime = System.currentTimeMillis();
                            System.out.println("extract pattern time: " + (currentTime - startTime));

                            if(patterns.size() > 1) {
                                logger.info("more than one pattern: {}", patternBeforePath);
                                continue;
                            }

                            Map<String, JSONArray> patternsByID = new LinkedHashMap<>();
                            for (Pattern pat : patterns) {
                                // collect attributes
                                PatternAbstractor abs = new PatternAbstractor(1);
                                pat = abs.abstractPattern(pat);
                                // get feature json object
                                List<Pair<String, JSONObject>> patternByID = ObjectUtil.getFeatureJsonObj(pat, pat.getIdPattern());
                                for (Pair<String, JSONObject> pair : patternByID) {
                                    if (!patternsByID.containsKey(pair.getValue0())) {
                                        patternsByID.put(pair.getValue0(), new JSONArray());
                                    }
                                    patternsByID.get(pair.getValue0()).add(pair.getValue1());
                                }
                            }

                            startTime = System.currentTimeMillis();

                            // write json object to file
                            String jsonFileName = String.format("%s_%s_%s.json", option.testOnDataset.datasetName, groupID, patternCaseNum);
                            String jsonPath = option.jsonBefore + "/" + jsonFileName;
                            ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);

                            String[] cmds = {option.pythonCmd, option.modelPath, "--path", jsonFileName};
                            logger.info("cmd: {}", Arrays.toString(cmds));

                            Process process = (new ProcessBuilder(cmds).directory(new File(option.modelWorkPath)))
                                    .redirectErrorStream(true).start();
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                            process.getInputStream(),
                                            StandardCharsets.UTF_8
                                    )
                            );

                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                System.out.println(line);
                            }
                            System.out.println(process.waitFor());

                            String modelJsonPath = option.jsonAfter + "/" +  jsonFileName;
                            Map<String, JSONObject> modelPrediction = (Map<String, JSONObject>) ObjectUtil.readJsonFromFile(modelJsonPath.toString());

                            for (int i = 0; i < patterns.size(); i++) {
                                Pattern pattern = patterns.get(i);

                                String key = projectCodeGroupRoot.resolve(patternCaseNum).resolve("before.java").toString() + "$$" + i;
                                JSONObject labelJson = modelPrediction.get(key);
                                JSONObject oriJson = ((JSONObject) ObjectUtil.readJsonFromFile(jsonPath)).getJSONArray(ag.getFileName()).getJSONObject(i);
                                InteractPattern.abstractByJSONObject(pattern, oriJson, labelJson, ag.getFileName());
                            }

                            currentTime = System.currentTimeMillis();
                            System.out.println("abstract pattern time: " + (currentTime - startTime));

                            List<String> subjectCaseCandidates = cases.stream()
                                    .filter(name -> !name.equals(patternCaseNum))
                                    .collect(Collectors.toList());

                            for (String subjectCaseNum : subjectCaseCandidates) {
                                startTime = System.currentTimeMillis();
                                Path subjectBeforePath = projectCodeGroupRoot.resolve(subjectCaseNum).resolve("before.java");
                                CodeGraph SubjectActionGraph = GraphBuilder.buildGraph(subjectBeforePath.toString(), new String[]{}, 8, new int[]{});

                                for (int i = 0; i < patterns.size(); i++) {
                                    Pattern pattern = patterns.get(i);

                                    // check whether all actions are abstracted
                                    if (pattern.getActionSet().stream().allMatch(n -> !n.isActionRelated() && n.isAbstract())) {
                                        System.out.printf("[error]all abstracted actions: %s %s %s\n", option.testOnDataset.datasetName, groupID, patternCaseNum);
                                        continue;
                                    }

                                    // check action source whether valid
                                    boolean abstractValid = true;
                                    for (PatternNode action : pattern.getActionSet().stream().filter(n -> !n.isAbstract()).collect(Collectors.toSet())) {
                                        for (PatternEdge ie : action.inEdges()) {
                                            if (ie.isAbstract() || ie.getSource().isAbstract())
                                                abstractValid = false;
                                        }
                                    }
                                    if (!abstractValid) {
                                        System.out.printf("[error]action node has invalid-abstracted source: %s %s %s\n", option.testOnDataset.datasetName, groupID, patternCaseNum);
                                        continue;
                                    }
                                    // locate the buggy line
                                    BugLocator detector = new BugLocator(0.6);

                                    DotGraph dot = new DotGraph(pattern, 0, true, false);
                                    File dotFile = new File(String.format("%s/%s/%s_group_%s_subjectCaseNum_%s_patternCaseNum_%s_patch_predict.dot",
                                            option.patternGraphPath, option.testOnDataset.datasetName, groupID, subjectCaseNum, patternCaseNum, i));
                                    dot.toDotFile(dotFile);

                                    String patchPath = String.format("%s/%s/%s/%s/%s_patch_%d.java", patchRootPath, project, groupID, subjectCaseNum, patternCaseNum, i);
                                    detector.applyPattern(pattern, SubjectActionGraph, patchPath, "new");
                                    currentTime = System.currentTimeMillis();
                                    System.out.println("apply pattern time: " + (currentTime - startTime));
                                }
                            }

                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

        } else {
            Path projectRootPath = Paths.get(option.testOnDataset.datasetPath);
            Path patchRootPath = Paths.get(option.testOnDataset.patchPath);

            File[] groups = Arrays.stream(Objects.requireNonNull(projectRootPath.toFile().listFiles())).filter(File::isDirectory).toArray(File[]::new);
            for (File group : groups) {
                String groupID = group.getName();
                Path projectCodeGroupRoot = projectRootPath.resolve(groupID);
                List<String> cases = Arrays.stream(Objects.requireNonNull(projectCodeGroupRoot.toFile().listFiles()))
                        .filter(File::isDirectory)
                        .map(File::getName)
                        .collect(Collectors.toList());

                for (String patternCaseNum : cases) {
                    try{
                        startTime = System.currentTimeMillis();
                        Path patternBeforePath = projectCodeGroupRoot.resolve(patternCaseNum).resolve("before.java");
                        Path patternAfterPath = projectCodeGroupRoot.resolve(patternCaseNum).resolve("after.java");
                        CodeGraph ag = GraphBuilder.buildActionGraph(patternBeforePath.toString(), patternAfterPath.toString(), new int[]{});
                        List<Pattern> patterns = PatternExtractor.combineGraphs(List.of(ag), "new");
                        currentTime = System.currentTimeMillis();
                        System.out.println("extract pattern time: " + (currentTime - startTime));

                        if(patterns.size() > 1) {
                            logger.info("more than one pattern: {}", patternBeforePath);
                            continue;
                        }

                        Map<String, JSONArray> patternsByID = new LinkedHashMap<>();
                        for (Pattern pat : patterns) {
                            // collect attributes
                            PatternAbstractor abs = new PatternAbstractor(1);
                            pat = abs.abstractPattern(pat);
                            // get feature json object
                            List<Pair<String, JSONObject>> patternByID = ObjectUtil.getFeatureJsonObj(pat, pat.getIdPattern());
                            for (Pair<String, JSONObject> pair : patternByID) {
                                if (!patternsByID.containsKey(pair.getValue0())) {
                                    patternsByID.put(pair.getValue0(), new JSONArray());
                                }
                                patternsByID.get(pair.getValue0()).add(pair.getValue1());
                            }
                        }

                        startTime = System.currentTimeMillis();

                        // write json object to file
                        String jsonFileName = String.format("%s_%s_%s.json", option.testOnDataset.datasetName, groupID, patternCaseNum);
                        String jsonPath = option.jsonBefore + "/" + jsonFileName;
                        ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);

                        String[] cmds = {option.pythonCmd, option.modelPath, "--path", jsonFileName};
                        logger.info("cmd: {}", Arrays.toString(cmds));

                        Process process = (new ProcessBuilder(cmds).directory(new File(option.modelWorkPath)))
                                .redirectErrorStream(true).start();
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(
                                        process.getInputStream(),
                                        StandardCharsets.UTF_8
                                )
                        );

                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                        System.out.println(process.waitFor());

                        String modelJsonPath = option.jsonAfter + "/" +  jsonFileName;
                        Map<String, JSONObject> modelPrediction = (Map<String, JSONObject>) ObjectUtil.readJsonFromFile(modelJsonPath.toString());

                        for (int i = 0; i < patterns.size(); i++) {
                            Pattern pattern = patterns.get(i);

                            String key = projectCodeGroupRoot.resolve(patternCaseNum).resolve("before.java").toString() + "$$" + i;
                            JSONObject labelJson = modelPrediction.get(key);
                            JSONObject oriJson = ((JSONObject) ObjectUtil.readJsonFromFile(jsonPath)).getJSONArray(ag.getFileName()).getJSONObject(i);
                            InteractPattern.abstractByJSONObject(pattern, oriJson, labelJson, ag.getFileName());
                        }

                        currentTime = System.currentTimeMillis();
                        System.out.println("abstract pattern time: " + (currentTime - startTime));

                        List<String> subjectCaseCandidates = cases.stream()
                                .filter(name -> !name.equals(patternCaseNum))
                                .collect(Collectors.toList());

                        for (String subjectCaseNum : subjectCaseCandidates) {
                            startTime = System.currentTimeMillis();
                            Path subjectBeforePath = projectCodeGroupRoot.resolve(subjectCaseNum).resolve("before.java");
                            CodeGraph SubjectActionGraph = GraphBuilder.buildGraph(subjectBeforePath.toString(), new String[]{}, 8, new int[]{});

                            for (int i = 0; i < patterns.size(); i++) {
                                Pattern pattern = patterns.get(i);

                                // check whether all actions are abstracted
                                if (pattern.getActionSet().stream().allMatch(n -> !n.isActionRelated() && n.isAbstract())) {
                                    System.out.printf("[error]all abstracted actions: %s %s %s\n", option.testOnDataset.datasetName, groupID, patternCaseNum);
                                    continue;
                                }

                                // check action source whether valid
                                boolean abstractValid = true;
                                for (PatternNode action : pattern.getActionSet().stream().filter(n -> !n.isAbstract()).collect(Collectors.toSet())) {
                                    for (PatternEdge ie : action.inEdges()) {
                                        if (ie.isAbstract() || ie.getSource().isAbstract())
                                            abstractValid = false;
                                    }
                                }
                                if (!abstractValid) {
                                    System.out.printf("[error]action node has invalid-abstracted source: %s %s %s\n", option.testOnDataset.datasetName, groupID, patternCaseNum);
                                    continue;
                                }
                                // locate the buggy line
                                BugLocator detector = new BugLocator(0.6);

                                DotGraph dot = new DotGraph(pattern, 0, true, false);
                                File dotFile = new File(String.format("%s/%s/%s_group_%s_subjectCaseNum_%s_patternCaseNum_%s_patch_predict.dot",
                                        option.patternGraphPath, option.testOnDataset.datasetName, groupID, subjectCaseNum, patternCaseNum, i));
                                dot.toDotFile(dotFile);

                                String patchPath = String.format("%s/%s/%s/%s/%s_patch_%d.java", patchRootPath, option.testOnDataset.datasetName, groupID, subjectCaseNum, patternCaseNum, i);
                                detector.applyPattern(pattern, SubjectActionGraph, patchPath, "new");
                                currentTime = System.currentTimeMillis();
                                System.out.println("apply pattern time: " + (currentTime - startTime));
                            }
                        }

                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }


    private static Option processArgs(String[] args) {
        return Options.parse();
    }

}
