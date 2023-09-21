import builder.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.CodeGraph;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.javatuples.Pair;
import org.junit.Test;
import utils.ASTUtil;
import utils.DotGraph;
import utils.FileFilterImpl;
import utils.ObjectUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Defects4JTest {
    @Test
    public void testAll_ExtractJson() {
        boolean OUTPUT_TO_FILE = true;

        if (OUTPUT_TO_FILE) {
            FileOutputStream puts = null;
            try {
                puts = new FileOutputStream(TestConfig.LOG_PATH,true);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            PrintStream out = new PrintStream(puts);
            System.setOut(out);
        }

        String base = "/Users/yumeng/JavaProjects/FixGen/data/d4j-info-2.0";
        for (Map.Entry<String, Integer> entry : TestConfig.D4J_PROJECTS.entrySet()) {
            for (int id=1; id<=entry.getValue(); id++) {
                String proj = entry.getKey();
                int finalId = id;
                if (TestConfig.D4J_DEPRECATED.containsKey(proj) && Arrays.stream(TestConfig.D4J_DEPRECATED.get(proj)).anyMatch(i -> i == finalId))
                    continue;
                System.out.printf("[start]%s %d\n", proj, id);
                testExtractJsonFromSingleChange(base, proj, id);
                System.out.printf("[finish]%s %d\n", proj, id);
            }
        }
    }

    public void testExtractJsonFromSingleChange(String base, String proj, int id) {
        FileFilterImpl fileFilter1 = new FileFilterImpl();
        fileFilter1.accept(new File(String.format("%s/buggy_fix/buggy/%s/%s_%d_buggy", base, proj, proj, id)));
        List<String> srcList = fileFilter1.target;
        FileFilterImpl fileFilter2 = new FileFilterImpl();
        fileFilter2.accept(new File(String.format("%s/buggy_fix/fixed/%s/%s_%d_fixed", base, proj, proj, id)));
        List<String> tarList = fileFilter2.target;
        String diffFile = String.format("%s/patches/%s/%d.src.patch", base, proj, id);
        Map<String, int[]> map = ASTUtil.getDiffLinesInBuggyFile(diffFile);
        int diffIndex = 0;
        for (Map.Entry<String, int[]> entry : map.entrySet()) {
            diffIndex++;
            String[] srcPath = srcList.stream().filter(f -> f.endsWith(entry.getKey())).toArray(String[]::new);
            String[] tarPath = tarList.stream().filter(f -> f.endsWith(entry.getKey())).toArray(String[]::new);
            if (srcPath.length == 1 && tarPath.length == 1) {
                try {
                    // build action graph
                    CodeGraph ag = GraphBuilder.buildActionGraph(srcPath[0], tarPath[0], entry.getValue());
                    // init the pattern
                    List<Pattern> patterns = PatternExtractor.combineGraphs(new ArrayList<>(){
                        {
                            add(ag);
                        }
                    });
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
                    // write json object to file
                    String jsonPath = System.getProperty("user.dir") + String.format("/out/defects4j/%s/%d_%d.json", proj, id, diffIndex);
                    ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);
                } catch (NullPointerException npe) {
                    System.out.println("[error]Fix changes in more than one method in a file");
                }
            } else {
                System.out.println("[error]Do not have the only target file");
            }
        }
    }

    @Test
    public void testAll_LoadModelResult() {
        boolean OUTPUT_TO_FILE = false;

        if (OUTPUT_TO_FILE) {
            FileOutputStream puts = null;
            try {
                puts = new FileOutputStream(TestConfig.LOG_PATH,true);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            PrintStream out = new PrintStream(puts);
            System.setOut(out);
        }

        String base = "/Users/yumeng/JavaProjects/FixGen/data/d4j-info-2.0";
        for (Map.Entry<String, Integer> entry : TestConfig.D4J_PROJECTS.entrySet()) {
            for (int id=1; id<=entry.getValue(); id++) {
                String proj = entry.getKey();
                int finalId = id;
                if (TestConfig.D4J_DEPRECATED.containsKey(proj) && Arrays.stream(TestConfig.D4J_DEPRECATED.get(proj)).anyMatch(i -> i == finalId))
                    continue;
                String outDir = String.format("/Users/yumeng/JavaProjects/FixGen/out/defects4j/%s/%d", proj, id);
                String modelResDir = String.format("/Users/yumeng/JavaProjects/FixGen/data/model_result/defects4j/%s", proj);
                String oriJsonDir = String.format("/Users/yumeng/JavaProjects/FixGen/codegraph4spoon/out/defects4j/%s", proj);
                System.out.printf("[start]%s %d\n", proj, id);
                testModelResult(base, modelResDir, oriJsonDir, proj, id, outDir);
                System.out.printf("[finish]%s %d\n", proj, id);
            }
        }
    }

    public void testModelResult(String base, String modelResBase, String jsonBase, String proj, int id, String outDir) {
        if (!new File(outDir).isDirectory())
            new File(outDir).mkdirs();

        FileFilterImpl fileFilter1 = new FileFilterImpl();
        fileFilter1.accept(new File(String.format("%s/buggy_fix/buggy/%s/%s_%d_buggy", base, proj, proj, id)));
        List<String> srcList = fileFilter1.target;
        FileFilterImpl fileFilter2 = new FileFilterImpl();
        fileFilter2.accept(new File(String.format("%s/buggy_fix/fixed/%s/%s_%d_fixed", base, proj, proj, id)));
        List<String> tarList = fileFilter2.target;
        String diffFile = String.format("%s/patches/%s/%d.src.patch", base, proj, id);
        Map<String, int[]> map = ASTUtil.getDiffLinesInBuggyFile(diffFile);
        int diffIndex = 0;
        for (Map.Entry<String, int[]> entry : map.entrySet()) {
            diffIndex++;
            String[] srcPath = srcList.stream().filter(f -> f.endsWith(entry.getKey())).toArray(String[]::new);
            String[] tarPath = tarList.stream().filter(f -> f.endsWith(entry.getKey())).toArray(String[]::new);
            if (srcPath.length == 1 && tarPath.length == 1) {
                try {
                    // build action graph
                    CodeGraph ag = GraphBuilder.buildActionGraph(srcPath[0], tarPath[0], entry.getValue());
                    // init the pattern
                    List<Pattern> patterns = PatternExtractor.combineGraphs(new ArrayList<>(){
                        {
                            add(ag);
                        }
                    });

                    // load element label
                    String oriJsonPath = String.format("%s/%d_%d.json", jsonBase, id, diffIndex);
                    String modelResPath = String.format("%s/%d_%d.json", modelResBase, id, diffIndex);
                    JSONObject ids = (JSONObject) ObjectUtil.readJsonFromFile(oriJsonPath);
                    JSONObject labels = (JSONObject) ObjectUtil.readJsonFromFile(modelResPath);
                    // modify the pattern according to the label
                    for (int i=0; i<patterns.size(); i++) {
                        System.out.printf("[start]Abstract pattern: %s %d %d\n", proj, id, i);
                        Pattern pattern = patterns.get(i);
                        try {
                            PatternAbstractor.buildWithoutAbstract(pattern);

                            for (Map.Entry<String, Object> entry2 : labels.entrySet()) {
                                String key = entry2.getKey().substring(0, entry2.getKey().indexOf("$$"));
                                JSONObject label = (JSONObject) entry2.getValue();
                                if (entry2.getKey().endsWith(String.format("$$%d", i))) {
                                    InteractPattern.abstractByJSONObject(pattern, ids.getJSONArray(key).getJSONObject(i), label, key);
                                    // save the pattern
                                    String patternPath = String.format("%s/pattern_abstract_%d.dat", outDir, i);
                                    ObjectUtil.writeObjectToFile(pattern, patternPath);
                                    DotGraph.drawPattern(pattern, String.format("%s/pattern_abstract_%d.dot", outDir, i), true);
                                    System.out.printf("[finish]Abstract pattern: %s %d %d\n", proj, id, i);
                                }
                            }
                        } catch (Exception e) {
                            System.out.printf("[error]Unknown exception during pattern abstraction: %s %d %d\n", proj, id, i);
                        } catch (Error e) {
                            System.out.printf("[error]Unknown error during pattern abstraction: %s %d %d\n", proj, id, i);
                        }
                    }
                } catch (NullPointerException npe) {
                    System.out.println("[error]Fix changes in more than one method in a file");
                }
            } else {
                System.out.printf("[error]Do not have the only target file: %s %d\n", proj, id);
            }
        }
    }


    @Test
    public void testOneModelPatternAndGeneratePatch() {
        String current_project = "math";
        String bugId = "79";
        Path d4jCodeGroupRoot = Paths.get("E:/dataset/d4j").resolve(current_project).resolve(bugId);
        Path patchRoot = Paths.get(System.getProperty("user.dir")).resolve("out").resolve("d4j_model_patch");
        System.out.println("code root: " + d4jCodeGroupRoot);

        try{
            Path patternBeforePath = d4jCodeGroupRoot.resolve("pattern").resolve("before.java");
            Path patternAfterPath = d4jCodeGroupRoot.resolve("pattern").resolve("after.java");
            CodeGraph ag = GraphBuilder.buildActionGraph(patternBeforePath.toString(), patternAfterPath.toString(), new int[]{});
            List<Pattern> patterns = PatternExtractor.combineGraphs(List.of(ag), "new");

            for (Pattern pattern : patterns) {
                DotGraph dot = new DotGraph(pattern, 0, false, false);
                File dotFile = new File(String.format("%s/out/d4j_pattern_out/currentProject_%s_bugId_%s_.dot",
                        System.getProperty("user.dir"), current_project, bugId));
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
            String jsonFileName = String.format("%s_%s.json", current_project, bugId);
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

                String key = d4jCodeGroupRoot.resolve("pattern").resolve("before.java").toString() + "$$" + i;
                JSONObject labelJson = modelPrediction.get(key);
                JSONObject oriJson = ((JSONObject) ObjectUtil.readJsonFromFile(jsonPath)).getJSONArray(ag.getFileName()).getJSONObject(i);
                InteractPattern.abstractByJSONObject(pattern, oriJson, labelJson, ag.getFileName());
            }

            Path subjectBeforePath = d4jCodeGroupRoot.resolve("apply").resolve("before.java");
            List<CodeGraph> subjectActionGraphs = GraphBuilder.buildMethodGraphs(subjectBeforePath.toString(), new String[]{}, 8, new int[]{});

            for (int i = 0; i < patterns.size(); i++) {
                Pattern pattern = patterns.get(i);

                // check whether all actions are abstracted
                if (pattern.getActionSet().stream().allMatch(n -> !n.isActionRelated() && n.isAbstract())) {
                    System.out.printf("[error]all abstracted actions: %s %s\n", current_project, bugId);
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
                    System.out.printf("[error]action node has invalid-abstracted source: %s %s\n", current_project, bugId);
                    continue;
                }
                // locate the buggy line
                BugLocator detector = new BugLocator(0.6);

                DotGraph dot = new DotGraph(pattern, 0, true, true);
                File dotFile = new File(String.format("%s/out/d4j_pattern_out/currentProject_%s_bugId_%s_patch_predict_%s.dot",
                        System.getProperty("user.dir"), current_project, bugId, i));
                dot.toDotFile(dotFile);

                for (CodeGraph subjectActionGraph : subjectActionGraphs) {
                    DotGraph dot_cg = new DotGraph(subjectActionGraph, new GraphConfiguration(), 0);
                    File dotcgFile = new File(String.format("%s/out/d4j_graph_out/currentProject_%s_bugId_%s_%d.dot",
                            System.getProperty("user.dir"), current_project, bugId, subjectActionGraphs.indexOf(subjectActionGraph)));
                    dot_cg.toDotFile(dotcgFile);

                    String patchPath = String.format("%s/%s/%s/pattern%d_patch_%d.java", patchRoot, current_project, bugId, i, subjectActionGraphs.indexOf(subjectActionGraph));
                    detector.applyPattern(pattern, subjectActionGraph, patchPath, "new");
                }

            }

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

}
