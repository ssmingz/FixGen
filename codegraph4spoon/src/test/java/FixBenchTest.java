import builder.*;
import codegraph.ASTEdge;
import codegraph.CtVirtualElement;
import codegraph.Edge;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.gumtreediff.tree.Tree;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.*;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.Delete;
import model.actions.Insert;
import model.actions.Move;
import model.actions.Update;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.*;
import utils.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FixBenchTest {
    Map<String, List<String>> groups = new HashMap<>();
    @Test
    public void testOnFixBench() {
        String[] projects = {"Genesis-NP"};
        String base = TestConfig.FIXBENCH_MAC_BASE;
        int patSizeCounter = 0, patCounter = 0;
        for (int i=0; i<projects.length; i++) {
            File dir = new File(base + projects[i]);
            for (File group : dir.listFiles(File::isDirectory)) {
                List<CodeGraph> ags = new ArrayList<>();
                File basedir = new File(String.format("%s/codegraphs/%s/%s", base, projects[i], group.getName()));
                if (!basedir.exists() || !basedir.isDirectory()) {
                    basedir.mkdirs();
                }
                System.out.println("start " + group.getAbsolutePath());
                String diffFile = String.format("%s/diff.diff", group.getAbsolutePath());
                Map<String, int[]> map = ASTUtil.getDiffLinesInBuggyFile(diffFile);
                for (Map.Entry<String, int[]> entry : map.entrySet()) {
                    String srcPath = String.format("%s/old/%s", group.getAbsolutePath(), entry.getKey());
                    String tarPath = String.format("%s/new/%s", group.getAbsolutePath(), entry.getKey());

                    // code graph
                    CtElementImpl cg1 = (CtElementImpl) findEntry(srcPath, new String[] {}, 8, entry.getValue());
                    CtElementImpl cg2 = (CtElementImpl) findEntry(tarPath, new String[] {}, 8, entry.getValue());
                    // gumtree diff
                    AstComparator diff = new AstComparator();
                    if (cg1==null || cg2==null) {
                        System.out.println("[error parsing]" + group.getAbsolutePath());
                        continue;
                    }
                    Diff editScript = diff.compare(cg1, cg2);
                    // add actions to src graph
                    StringBuilder builder = new StringBuilder();
                    for (Operation op : editScript.getRootOperations()) {
                        if (builder.length()>0)
                            builder.append("-->");
                        builder.append(op.getAction().getName());
                        builder.append(" ");
                        if (op instanceof DeleteOperation) {
                            CtElementImpl src = (CtElementImpl) op.getSrcNode();
                            builder.append(src.getClass().getSimpleName());
                        } else if (op instanceof UpdateOperation) {
                            CtElementImpl src = (CtElementImpl) op.getSrcNode();
                            CtElementImpl dst = (CtElementImpl) op.getDstNode();
                            builder.append(src.getClass().getSimpleName());
                            builder.append(" to ");
                            builder.append(dst.getClass().getSimpleName());
                        } else if (op instanceof InsertOperation) {
                            CtElementImpl insTar = (CtElementImpl) op.getSrcNode();
                            // step1. use the mapping if finds: parent-in-dstgraph <--> parent-in-srcgraph
                            CtElementImpl insSrc = (CtElementImpl) ((InsertOperation) op).getParent();
                            builder.append(insTar.getClass().getSimpleName());
                            builder.append(" at ");
                            builder.append(insSrc.getClass().getSimpleName());
                        } else if (op instanceof MoveOperation) {
                            CtElementImpl movedInSrc = (CtElementImpl) op.getSrcNode();
                            CtElementImpl movedInDst = (CtElementImpl) editScript.getMappingsComp().getDstForSrc((Tree) movedInSrc.getMetadata("gtnode")).getMetadata("spoon_object");
                            CtElementImpl parent = (CtElementImpl) ((MoveOperation) op).getParent();
                            builder.append(movedInDst.getClass().getSimpleName());
                            builder.append(" to ");
                            builder.append(parent.getClass().getSimpleName());
                        }
                    }
                    String actionStr = builder.toString();
                    if (groups.containsKey(actionStr)) {
                        groups.get(actionStr).add(group.getAbsolutePath());
                    } else {
                        List<String> paths = new ArrayList<>();
                        paths.add(group.getAbsolutePath());
                        groups.put(actionStr, paths);
                    }
                }
            }
        }

        groups = sortByValueSize(groups);
        for (Map.Entry<String, List<String>> entry : groups.entrySet()) {
            StringBuilder builder = new StringBuilder();
            builder.append(entry.getValue().size());
            builder.append("##");
            builder.append(entry.getKey());
            builder.append(":");
            for (String path : entry.getValue()) {
                builder.append("\n");
                builder.append(path);
            }
            System.out.println(builder);
        }
        System.out.printf("extracted pattern not single: %d/%d%n", patSizeCounter, patCounter);
    }

    public static CtElement findEntry(String inputPath, String[] classPaths, int compileLevel, int[] includeLines) {
        Launcher launcher = new Launcher();

        launcher.addInputResource(inputPath);
        if (classPaths.length > 0)
            launcher.getEnvironment().setSourceClasspath(classPaths);
        launcher.getEnvironment().setComplianceLevel(compileLevel);

        launcher.buildModel();
        CtModel model = launcher.getModel();

        for (CtElement method : model.getElements(s -> s instanceof CtMethodImpl
                || s instanceof CtConstructorImpl || s instanceof CtAnonymousExecutableImpl)) {
            if (checkByLine((CtElementImpl) method, includeLines)) {
                return method;
            }
        }
        return null;
    }

    private static boolean checkByLine(CtElementImpl method, int[] includeLines) {
        if (!method.getPosition().isValidPosition())
            return false;
        int start = method.getPosition().getLine();
        int end = method.getPosition().getEndLine();
        for (int line : includeLines) {
            // the method include these lines ?
            // any line in lines is included in the method return true ?
            if (line < start || line > end) {
                return false;
            }
        }
        return true;
    }

    /**
     * sort by value
     */
    public static Map<String, List<String>> sortByValueSize(Map<String, List<String>> aMap) {
        HashMap<String, List<String>> sorted = new LinkedHashMap<>();
        aMap.entrySet()
                .stream()
                .sorted((p1, p2) -> (Integer.compare(p2.getValue().size(), p1.getValue().size())))
                .collect(Collectors.toList()).forEach(ele -> sorted.put(ele.getKey(), ele.getValue()));
        return sorted;
    }




    @Test
    public void generateFixBenchFeatures() {
        String runType = "new";
        String base = TestConfig.FIXBENCH_WIN_BASE;
//        List<String> projects = List.of("FindBugs-DM_CONVERT_CASE", "FindBugs-DM_DEFAULT_ENCODING", "Genesis-NP", "Genesis-OOB");
        List<String> projects = List.of("FindBugs-DM_CONVERT_CASE");
        Map<String, JSONArray> patternsByID = new LinkedHashMap<>();
        int groupCounter = 0;

        for (String project : projects) {
            Path path = Paths.get(base + project);
            File[] groups = Objects.requireNonNull(path.toFile().listFiles(File::isDirectory));

            for (File group : groups) {
                groupCounter++;
                List<File> cases = List.of(Objects.requireNonNull(group.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        return file.isDirectory() && file.getName().matches("\\d+");
                    }
                })));

                List<CodeGraph> ags = new ArrayList<>();
                for (File c : cases) {
                    Path srcPath = c.toPath().resolve("before.java");
                    Path tarPath = c.toPath().resolve("after.java");
                    long start = System.currentTimeMillis();
                    CodeGraph ag = GraphBuilder.buildActionGraph(srcPath.toString(), tarPath.toString(), new int[]{});
                    long end = System.currentTimeMillis();
                    DotGraph dot = new DotGraph(ag, new GraphConfiguration(), 0);
                    File dir = new File(String.format("%s/out/codegraph_temp_%d.dot", System.getProperty("user.dir"), cases.indexOf(c)));
                    dot.toDotFile(dir);
//                    System.out.println("build graph time: " + (end - start) + "ms");
                    ags.add(ag);
                }
                long start = System.currentTimeMillis();
                List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags, runType);
                long end = System.currentTimeMillis();
                System.out.println("extractor pattern time: " + (end - start) + "ms");
                for (Pattern pat : combinedGraphs) {
                    DotGraph dot = new DotGraph(pat, 0, false, false);
                    File dir = new File(String.format("%s/out/pattern_temp_%d_%d.dot", System.getProperty("user.dir"), Arrays.asList(groups).indexOf(group), combinedGraphs.indexOf(pat)));
                    dot.toDotFile(dir);

                    // abstract pattern
                    PatternAbstractor abs = new PatternAbstractor(Objects.requireNonNull(group.listFiles(File::isDirectory)).length);
                    pat = abs.abstractPattern(pat);

                    DotGraph dot1 = new DotGraph(pat, 0, false, false);
                    File dir1 = new File(String.format("%s/out/pattern_ab_%d_%d.dot", System.getProperty("user.dir"), Arrays.asList(groups).indexOf(group), combinedGraphs.indexOf(pat)));
                    dot1.toDotFile(dir1);

                    // get feature json object
                    List<Pair<String, JSONObject>> patternByID = ObjectUtil.getFeatureJsonObj(pat, pat.getIdPattern());
                    for (Pair<String, JSONObject> pair : patternByID) {
                        if (!patternsByID.containsKey(pair.getValue0())) {
                            patternsByID.put(pair.getValue0(), new JSONArray());
                        }
                        patternsByID.get(pair.getValue0()).add(pair.getValue1());
                    }
                }

                String jsonPath = System.getProperty("user.dir") + String.format("/out/json/fixBench/%s/%s.json", project, group.getName());

                File file = new File(jsonPath);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);
                patternsByID.clear();

            }

            System.out.printf("Total group instances: %d\n", groupCounter);

        }
    }

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
        Path fixBenchCodeRoot = Paths.get("E:\\dataset\\FixBench\\WithinSingleMethod");
        Path patchRoot = Paths.get(System.getProperty("user.dir")).resolve("out").resolve("model_patch");
        List<String> projects = List.of("FindBugs-DM_CONVERT_CASE", "FindBugs-DM_DEFAULT_ENCODING", "Genesis-NP", "Genesis-OOB");
        for (String project : projects) {
            Path patchProjectRoot = patchRoot.resolve(project);
            File[] patchGroups = patchProjectRoot.toFile().listFiles();
            int totalCases = 0;
            int correctCases = 0;
            for (File group : patchGroups) {
                String groupID = group.getName();
                Path patchGroupRoot = patchProjectRoot.resolve(groupID);
                File[] patchCases = patchGroupRoot.toFile().listFiles();
                for (File patchCase : patchCases) {
                    String patchCaseID = patchCase.getName();
                    if(project.equals("Genesis-OOB") && groupID.equals("1") && patchCaseID.equals("0")) {
                        System.out.println("here");
                    }
                    totalCases ++;
                    Path beforePath = fixBenchCodeRoot.resolve(project).resolve(groupID).resolve(patchCaseID).resolve("before.java");
                    Path afterPath = fixBenchCodeRoot.resolve(project).resolve(groupID).resolve(patchCaseID).resolve("after.java");

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
            System.out.println("project: " + project);
            System.out.println("total cases: " + totalCases);
            System.out.println("correct cases: " + correctCases);

        }
    }

    @Test
    @Deprecated
    public void testAllModelPatternAndGeneratePatch() {
        Random random = new Random(42);
        List<String> projects = List.of("FindBugs-DM_CONVERT_CASE", "FindBugs-DM_DEFAULT_ENCODING", "Genesis-NP", "Genesis-OOB");
        Path fixBenchCodeRoot = Paths.get("E:\\dataset\\FixBench\\WithinSingleMethod");
        Path modelPredictionJson = Paths.get("E:\\dataset\\FixBench\\output_fixbench");
        Path patchRoot = Paths.get(System.getProperty("user.dir")).resolve("out").resolve("model_patch");

        for (String project : projects) {
            Path fixBenchCodeProjectRoot = fixBenchCodeRoot.resolve(project);
            File[] groups = fixBenchCodeProjectRoot.toFile().listFiles();
            for (File group : groups) {
                String groupID = group.getName();
                Path fixBenchCodeGroupRoot = fixBenchCodeProjectRoot.resolve(group.getName());
                Path modelPredictionGroupJson = modelPredictionJson.resolve(project).resolve(groupID + ".json");
                try{
                    Map<String, JSONObject> modelPrediction = (Map<String, JSONObject>) ObjectUtil.readJsonFromFile(modelPredictionGroupJson.toString());

                    // 遍历每一个case
                    for (String key : modelPrediction.keySet()) {
                        String[] keySplit = key.split("/");
                        assertEquals(project, keySplit[keySplit.length - 4]);
                        assertEquals(groupID, keySplit[keySplit.length - 3]);
                        String patternCaseNum = keySplit[keySplit.length - 2];

                        Path patternBeforePath = fixBenchCodeGroupRoot.resolve(patternCaseNum).resolve("before.java");
                        Path patternAfterPath = fixBenchCodeGroupRoot.resolve(patternCaseNum).resolve("after.java");

                        List<String> subjectCaseCandidates = Arrays.stream(Objects.requireNonNull(fixBenchCodeGroupRoot.toFile().listFiles()))
                                .map(File::getName)
                                .filter(name -> !name.equals(patternCaseNum))
                                .collect(Collectors.toList());

//                        int index = random.nextInt(subjectCaseCandidates.size());
//                        String subjectCaseNum = subjectCaseCandidates.get(index);

                        for (String subjectCaseNum : subjectCaseCandidates) {
                            Path subjectBeforePath = fixBenchCodeGroupRoot.resolve(subjectCaseNum).resolve("before.java");
                            CodeGraph ag = GraphBuilder.buildActionGraph(patternBeforePath.toString(), patternAfterPath.toString(), new int[]{});
                            List<Pattern> patterns = PatternExtractor.combineGraphs(List.of(ag), "new");
                            CodeGraph SubjectActionGraph = GraphBuilder.buildGraph(subjectBeforePath.toString(), new String[]{}, 8, new int[]{});

                            Map<String, JSONArray> patternsByID = new LinkedHashMap<>();
                            for (Pattern pat : patterns) {
                                // abstract pattern
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

                            String jsonFileName = String.format("%s_%s_%s.json", project, groupID, patternCaseNum);

                            // write json object to file
                            String jsonPath = System.getProperty("user.dir") + ("/test_json_out/jsonFileName");
                            ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);

                            for (int i = 0; i < patterns.size(); i++) {
                                Pattern pattern = patterns.get(i);
                                PatternAbstractor.buildWithoutAbstract(pattern);

                                JSONObject labelJson = modelPrediction.get(key);
                                JSONObject oriJson = ((JSONObject) ObjectUtil.readJsonFromFile(jsonPath)).getJSONArray(ag.getFileName()).getJSONObject(i);
                                InteractPattern.abstractByJSONObject(pattern, oriJson, labelJson, ag.getFileName());
                            }

                            // 多个pattern
                            for (int i = 0; i < patterns.size(); i++) {
                                Pattern pattern = patterns.get(i);

                                // check whether all actions are abstracted
                                if (pattern.getActionSet().stream().allMatch(n -> !n.isActionRelated() && n.isAbstract())) {
                                    System.out.printf("[error]all abstracted actions: %s %s %s\n", project, groupID, patternCaseNum);
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
                                    System.out.printf("[error]action node has invalid-abstracted source: %s %s %s\n", project, groupID, patternCaseNum);
                                    continue;
                                }
                                // locate the buggy line
                                BugLocator detector = new BugLocator(0.6);

                                DotGraph dot = new DotGraph(pattern, 0, true, false);
                                File dotFile = new File(String.format("%s/out/pattern_out/%s_group_%s_patternCaseNum_%s_subjectCaseNum_%s_patch_predict_%s.dot",
                                        System.getProperty("user.dir"), project, groupID, patternCaseNum, subjectCaseNum, i));
                                dot.toDotFile(dotFile);

                                String patchPath = String.format("%s/%s/%s/%s/%s_patch_%d.java", patchRoot, project, groupID, patternCaseNum, subjectCaseNum, i);
                                detector.applyPattern(pattern, SubjectActionGraph, patchPath, "new");
                            }
                        }




                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

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
        List<String> projects = List.of("FindBugs-DM_CONVERT_CASE", "FindBugs-DM_DEFAULT_ENCODING", "Genesis-NP", "Genesis-OOB");
        Path fixBenchCodeRoot = Paths.get("E:\\dataset\\FixBench\\WithinSingleMethod");
        Path patchRoot = Paths.get(System.getProperty("user.dir")).resolve("out").resolve("model_patch");

        for (String project : projects) {
            Path fixBenchCodeProjectRoot = fixBenchCodeRoot.resolve(project);
            File[] groups = fixBenchCodeProjectRoot.toFile().listFiles();
            for (File group : groups) {
                String groupID = group.getName();
                Path fixBenchCodeGroupRoot = fixBenchCodeProjectRoot.resolve(group.getName());

                List<String> cases = Arrays.stream(Objects.requireNonNull(fixBenchCodeGroupRoot.toFile().listFiles()))
                        .map(File::getName)
                        .collect(Collectors.toList());

                for (String patternCaseNum : cases) {
                    try{
                        Path patternBeforePath = fixBenchCodeGroupRoot.resolve(patternCaseNum).resolve("before.java");
                        Path patternAfterPath = fixBenchCodeGroupRoot.resolve(patternCaseNum).resolve("after.java");
                        CodeGraph ag = GraphBuilder.buildActionGraph(patternBeforePath.toString(), patternAfterPath.toString(), new int[]{});
                        List<Pattern> patterns = PatternExtractor.combineGraphs(List.of(ag), "new");

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
                        String jsonFileName = String.format("%s_%s_%s.json", project, groupID, patternCaseNum);
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

                            String key = fixBenchCodeGroupRoot.resolve(patternCaseNum).resolve("before.java").toString() + "$$" + i;
                            JSONObject labelJson = modelPrediction.get(key);
                            JSONObject oriJson = ((JSONObject) ObjectUtil.readJsonFromFile(jsonPath)).getJSONArray(ag.getFileName()).getJSONObject(i);
                            InteractPattern.abstractByJSONObject(pattern, oriJson, labelJson, ag.getFileName());
                        }

                        List<String> subjectCaseCandidates = cases.stream()
                                .filter(name -> !name.equals(patternCaseNum))
                                .collect(Collectors.toList());

                        for (String subjectCaseNum : subjectCaseCandidates) {
                            Path subjectBeforePath = fixBenchCodeGroupRoot.resolve(subjectCaseNum).resolve("before.java");
                            CodeGraph SubjectActionGraph = GraphBuilder.buildGraph(subjectBeforePath.toString(), new String[]{}, 8, new int[]{});

                            for (int i = 0; i < patterns.size(); i++) {
                                Pattern pattern = patterns.get(i);

                                // check whether all actions are abstracted
                                if (pattern.getActionSet().stream().allMatch(n -> !n.isActionRelated() && n.isAbstract())) {
                                    System.out.printf("[error]all abstracted actions: %s %s %s\n", project, groupID, patternCaseNum);
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
                                    System.out.printf("[error]action node has invalid-abstracted source: %s %s %s\n", project, groupID, patternCaseNum);
                                    continue;
                                }
                                // locate the buggy line
                                BugLocator detector = new BugLocator(0.6);

                                DotGraph dot = new DotGraph(pattern, 0, true, false);
                                File dotFile = new File(String.format("%s/out/pattern_out/%s_group_%s_subjectCaseNum_%s_patternCaseNum_%s_patch_predict_%s.dot",
                                        System.getProperty("user.dir"), project, groupID, subjectCaseNum, patternCaseNum, i));
                                dot.toDotFile(dotFile);

                                String patchPath = String.format("%s/%s/%s/%s/%s_patch_%d.java", patchRoot, project, groupID, subjectCaseNum, patternCaseNum, i);
                                detector.applyPattern(pattern, SubjectActionGraph, patchPath, "new");
                            }
                        }

                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }

    }


    @Test
    public void testOneModelPatternAndGeneratePatch() {
        String current_project = "Genesis-NP";
        String groupID = "12";
        String targetSubjectCaseNum = "1";
        String targetPatternCaseNum = "0";
        Path fixBenchCodeGroupRoot = Paths.get("E:\\dataset\\FixBench\\WithinSingleMethod").resolve(current_project).resolve(groupID);
        Path patchRoot = Paths.get(System.getProperty("user.dir")).resolve("out").resolve("debug_model_patch");
        System.out.println("code root: " + fixBenchCodeGroupRoot);

        try{
            Path patternBeforePath = fixBenchCodeGroupRoot.resolve(targetPatternCaseNum).resolve("before.java");
            Path patternAfterPath = fixBenchCodeGroupRoot.resolve(targetPatternCaseNum).resolve("after.java");
            CodeGraph ag = GraphBuilder.buildActionGraph(patternBeforePath.toString(), patternAfterPath.toString(), new int[]{});
            List<Pattern> patterns = PatternExtractor.combineGraphs(List.of(ag), "new");

            for (Pattern pattern : patterns) {
                DotGraph dot = new DotGraph(pattern, 0, false, false);
                File dotFile = new File(String.format("%s/out/debug_pattern_out/%s_group_%s_subjectCaseNum_%s_patternCaseNum_%s_pattern_%s.dot",
                        System.getProperty("user.dir"), current_project, groupID, targetSubjectCaseNum, targetPatternCaseNum, patterns.indexOf(pattern)));
                dot.toDotFile(dotFile);
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
            String jsonFileName = String.format("%s_%s_%s.json", current_project, groupID, targetPatternCaseNum);
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

                String key = fixBenchCodeGroupRoot.resolve(targetPatternCaseNum).resolve("before.java").toString() + "$$" + i;
                JSONObject labelJson = modelPrediction.get(key);
                JSONObject oriJson = ((JSONObject) ObjectUtil.readJsonFromFile(jsonPath)).getJSONArray(ag.getFileName()).getJSONObject(i);
                InteractPattern.abstractByJSONObject(pattern, oriJson, labelJson, ag.getFileName());
            }


            Path subjectBeforePath = fixBenchCodeGroupRoot.resolve(targetSubjectCaseNum).resolve("before.java");
            CodeGraph SubjectActionGraph = GraphBuilder.buildGraph(subjectBeforePath.toString(), new String[]{}, 8, new int[]{});

            for (int i = 0; i < patterns.size(); i++) {
                Pattern pattern = patterns.get(i);

                // check whether all actions are abstracted
                if (pattern.getActionSet().stream().allMatch(n -> !n.isActionRelated() && n.isAbstract())) {
                    System.out.printf("[error]all abstracted actions: %s %s %s\n", current_project, groupID, targetPatternCaseNum);
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
                    System.out.printf("[error]action node has invalid-abstracted source: %s %s %s\n", current_project, groupID, targetPatternCaseNum);
                    continue;
                }
                // locate the buggy line
                BugLocator detector = new BugLocator(0.6);

                DotGraph dot = new DotGraph(pattern, 0, true, false);
                File dotFile = new File(String.format("%s/out/debug_pattern_out/%s_group_%s_subjectCaseNum_%s_patternCaseNum_%s_patch_predict_%s.dot",
                        System.getProperty("user.dir"), current_project, groupID, targetSubjectCaseNum, targetPatternCaseNum, i));
                dot.toDotFile(dotFile);

                String patchPath = String.format("%s/%s/%s/%s/%s_patch_%d.java", patchRoot, current_project, groupID, targetSubjectCaseNum, targetPatternCaseNum, i);
                detector.applyPattern(pattern, SubjectActionGraph, patchPath, "new");
            }

        } catch(Exception e) {
            e.printStackTrace();
        }



    }

}
