import builder.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.CodeGraph;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.javatuples.Pair;
import org.junit.Test;
import utils.DiffUtil;
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

public class SysEditTest {
    private boolean isPatchCorrect(List<String> groundtruth, List<String> patch) {
        if (groundtruth.size() != patch.size()) return false;
        for (int i = 0; i < groundtruth.size(); i++) {
            if (groundtruth.get(i).startsWith("--- ") != patch.get(i).startsWith("--- ")) return false;
            else if (groundtruth.get(i).startsWith("+++ ") != patch.get(i).startsWith("+++ ")) return false;
            if (!groundtruth.get(i).startsWith("--- ") && !groundtruth.get(i).startsWith("+++ ")) {
                if (!groundtruth.get(i).equals(patch.get(i))) return false;
            }
        }
        return true;
    }
    @Test
    public void checkPatchCorrectness() {
        Path sysEditCodeRoot = Paths.get("E:\\dataset\\FixBench\\SysEdit-c3");
        Path patchRoot = Paths.get(System.getProperty("user.dir")).resolve("out").resolve("model_patch").resolve("sysEdit");
        File[] patchGroups = patchRoot.toFile().listFiles();
        int totalCases = 0;
        int correctCases = 0;
        for (File group : patchGroups) {
            String groupID = group.getName();
            Path patchGroupRoot = patchRoot.resolve(groupID);
            File[] patchCases = Arrays.stream(Objects.requireNonNull(patchGroupRoot.toFile().listFiles())).filter(File::isDirectory).toArray(File[]::new);
            for (File patchCase : patchCases) {
                String patchCaseID = patchCase.getName();
                totalCases ++;
                Path beforePath = sysEditCodeRoot.resolve(groupID).resolve(patchCaseID).resolve("before.java");
                Path afterPath = sysEditCodeRoot.resolve(groupID).resolve(patchCaseID).resolve("after.java");

                if(groupID.equals("36") && patchCaseID.equals("0")) {
                    System.out.println("here");
                }

                List<String> beforeAfter = DiffUtil.getDiff(beforePath.toString(), afterPath.toString());
                Path patchCaseRoot = patchGroupRoot.resolve(patchCaseID);
                File[] patches = patchCaseRoot.toFile().listFiles();
                for (File patch : patches) {
                    String patchID = patch.getName();
                    Path patchPath = patchCaseRoot.resolve(patchID);
                    List<String> beforePatch = DiffUtil.getDiff(beforePath.toString(), patchPath.toString());

                    if(isPatchCorrect(beforeAfter, beforePatch)) {
                        System.out.println("[true]correct patch: " + patchPath);
                        correctCases++;
                        break;
                    } else {
                        System.out.println("[false]incorrect patch: " + patchPath);
                    }
                }
            }
        }
        System.out.println("total cases: " + totalCases);
        System.out.println("correct cases: " + correctCases);
    }

    /*
     * 1. generate pattern
     * 2. run python command
     * 3. abstract pattern
     * 2. generate patch
     */
    @Test
    public void testPatternAbstractGeneratePatch() {
        Random random = new Random(42);
        Path sysEditCodeRoot = Paths.get("E:\\dataset\\FixBench\\SysEdit-c3");
        Path patchRoot = Paths.get(System.getProperty("user.dir")).resolve("out").resolve("model_patch");
        File[] groups = Arrays.stream(Objects.requireNonNull(sysEditCodeRoot.toFile().listFiles())).filter(File::isDirectory).toArray(File[]::new);
        int moreThanOnePattern = 0;
        for (File group : groups) {
            String groupID = group.getName();
            Path sysEditCodeGroupRoot = sysEditCodeRoot.resolve(group.getName());

            List<String> cases = Arrays.stream(Objects.requireNonNull(sysEditCodeGroupRoot.toFile().listFiles()))
                    .map(File::getName)
                    .collect(Collectors.toList());

            for (String patternCaseNum : cases) {
                try{
                    Path patternBeforePath = sysEditCodeGroupRoot.resolve(patternCaseNum).resolve("before.java");
                    Path patternAfterPath = sysEditCodeGroupRoot.resolve(patternCaseNum).resolve("after.java");
                    CodeGraph ag = GraphBuilder.buildActionGraph(patternBeforePath.toString(), patternAfterPath.toString(), new int[]{});
                    List<Pattern> patterns = PatternExtractor.combineGraphs(List.of(ag), "new");

                    if(patterns.size() > 1) {
                        moreThanOnePattern += 1;
                        System.out.println("more than one pattern: " + patternBeforePath);
                        continue;
                    }

                    // 存储json
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

                    // write json object to file
                    String jsonFileName = String.format("%s_%s_%s.json", "sysEdit", groupID, patternCaseNum);
                    String jsonPath = System.getProperty("user.dir") + ("/test_json_out/" + jsonFileName);
                    ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);

                    String[] cmds = {"E:\\Anaconda3\\envs\\fix_graph_2\\python.exe", "D:\\workspace\\fix_graph_1\\run.py", "--path", jsonFileName};
                    System.out.println("cmd: " + Arrays.toString(cmds));

                    Process process = (new ProcessBuilder(cmds).directory(new File("D:\\workspace\\fix_graph_1")))
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

                    String modelJsonPath = System.getProperty("user.dir") + ("/model_json_out/" + jsonFileName);

                    Map<String, JSONObject> modelPrediction = (Map<String, JSONObject>) ObjectUtil.readJsonFromFile(modelJsonPath.toString());

                    for (int i = 0; i < patterns.size(); i++) {
                        Pattern pattern = patterns.get(i);

                        String key = sysEditCodeGroupRoot.resolve(patternCaseNum).resolve("before.java").toString() + "$$" + i;
                        JSONObject labelJson = modelPrediction.get(key);
                        JSONObject oriJson = ((JSONObject) ObjectUtil.readJsonFromFile(jsonPath)).getJSONArray(ag.getFileName()).getJSONObject(i);
                        InteractPattern.abstractByJSONObject(pattern, oriJson, labelJson, ag.getFileName());
                    }

                    List<String> subjectCaseCandidates = cases.stream()
                            .filter(name -> !name.equals(patternCaseNum))
                            .collect(Collectors.toList());

                    for (String subjectCaseNum : subjectCaseCandidates) {
                        Path subjectBeforePath = sysEditCodeGroupRoot.resolve(subjectCaseNum).resolve("before.java");
                        CodeGraph SubjectActionGraph = GraphBuilder.buildGraph(subjectBeforePath.toString(), new String[]{}, 8, new int[]{});

                        for (int i = 0; i < patterns.size(); i++) {
                            Pattern pattern = patterns.get(i);

                            // check whether all actions are abstracted
                            if (pattern.getActionSet().stream().allMatch(n -> !n.isActionRelated() && n.isAbstract())) {
                                System.out.printf("[error]all abstracted actions: %s %s %s\n", "sysEdit", groupID, patternCaseNum);
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
                                System.out.printf("[error]action node has invalid-abstracted source: %s %s %s\n", "sysEdit", groupID, patternCaseNum);
                                continue;
                            }
                            // locate the buggy line
                            BugLocator detector = new BugLocator(0.6);

                            DotGraph dot = new DotGraph(pattern, 0, true, false);
                            File dotFile = new File(String.format("%s/out/pattern_out/%s_group_%s_subjectCaseNum_%s_patternCaseNum_%s_patch_predict_%s.dot",
                                    System.getProperty("user.dir"), "sysEdit", groupID, subjectCaseNum, patternCaseNum, i));
                            dot.toDotFile(dotFile);

                            String patchPath = String.format("%s/%s/%s/%s/%s_patch_%d.java", patchRoot, "sysEdit", groupID, subjectCaseNum, patternCaseNum, i);
                            detector.applyPattern(pattern, SubjectActionGraph, patchPath, "new");
                        }
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        }

        System.out.println("more than one pattern: " + moreThanOnePattern);



    }


}
