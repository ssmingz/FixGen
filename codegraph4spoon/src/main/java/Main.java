import builder.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.CodeGraph;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.javatuples.Pair;
import utils.DiffUtil;
import utils.DotGraph;
import utils.ObjectUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class Main {
    //    zip -d /Users/yangchen/Desktop/FixGen.jar  'META-INF/.SF' 'META-INF/.RSA' 'META-INF/*SF'
    public static boolean isPatchCorrect(List<String> groundtruth, List<String> patch) {
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

    public static void testPatchCorrectness2(String project, String base_gt, String base_patch) {
        String[] projects = {project};
        int targetCounter = 0, correctCounter = 0;
        int targetCounter_single = 0, correctCounter_single = 0;
        for (int i = 0; i < projects.length; i++) {
            File dir = new File(base_gt + "dataset/" + projects[i]);
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    int testId = Integer.parseInt(group.getName());
                    String baseDir = String.format("%s/%d", dir, testId);
                    int size = (int) Arrays.stream(new File(baseDir).listFiles()).filter(File::isDirectory).count();
                    // each as target
                    for (int targetNo = 0; targetNo < size; targetNo++) {
                        File patchDir = new File(String.format("%s/%s/%d/%d", base_patch, projects[i], testId, targetNo));
                        if (!patchDir.exists()) continue;
                        if (Arrays.stream(patchDir.listFiles()).filter(f -> f.getName().endsWith(".java")).count() == 1)
                            targetCounter_single++;
                        targetCounter++;
                        for (File patch : patchDir.listFiles()) {
                            if (patch.getName().endsWith(".java")) {
                                String patchPath = patch.getAbsolutePath();
                                String beforePath = String.format("%s/dataset/%s/%d/%d/before.java", base_gt, projects[i], testId, targetNo);
                                String afterPath = String.format("%s/dataset/%s/%d/%d/after.java", base_gt, projects[i], testId, targetNo);
                                List<String> beforePatch = DiffUtil.getDiff(beforePath, patchPath);
                                List<String> beforeAfter = DiffUtil.getDiff(beforePath, afterPath);
                                boolean correctness = isPatchCorrect(beforeAfter, beforePatch);
                                correctCounter += correctness ? 1 : 0;
                                if (Arrays.stream(patchDir.listFiles()).filter(f -> f.getName().endsWith(".java")).count() == 1)
                                    correctCounter_single += correctness ? 1 : 0;
//                                if (!correctness) {
                                System.out.printf("[%b]%s%n", correctness, patchPath);
//                                }
                                if (correctness) break;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("======== All ========");
        System.out.println("[stat]target bug instance number: " + targetCounter);
        System.out.println("[stat]patch correct number: " + correctCounter);
        System.out.println("[stat]patch correct %: " + (correctCounter * 1.0 / targetCounter));
        System.out.println("======== Single Pattern ========");
        System.out.println("[stat]target bug instance number: " + targetCounter_single);
        System.out.println("[stat]patch correct number: " + correctCounter_single);
        System.out.println("[stat]patch correct %: " + (correctCounter_single * 1.0 / targetCounter_single));
    }

    public static void runAllCases(String[] projects, String runType, String base, String id, boolean SKIP_IF_EXIST) {
        AtomicInteger targetCounter = new AtomicInteger();
        long start = System.currentTimeMillis();

        for (int i = 0; i < projects.length; i++) {
            System.out.println(projects[i]);

            AtomicInteger total = new AtomicInteger();
            File dir = new File(base + "dataset/" + projects[i]);


            int testId = Integer.parseInt(id);
            String baseDir = String.format("%s/%d", dir, testId);
            int size = (int) Arrays.stream(new File(baseDir).listFiles()).filter(File::isDirectory).count();

            String patchDir = String.format("%s/out/patch/%s/%s", System.getProperty("user.dir"), projects[i], testId);
            System.out.println(patchDir);
            if (SKIP_IF_EXIST && new File(patchDir).exists()) continue;

            ExecutorService executor = Executors.newCachedThreadPool();

            // 使用Callable接口作为构造参数
            int finalI = i;
            FutureTask<String> future = new FutureTask<>(() -> {
                // 真正的任务代码在这里执行，返回值为你需要的类型
                try {
                    // all action graph
                    long step_start = System.currentTimeMillis();
                    List<CodeGraph> ags = new ArrayList<>();
                    for (int k = 0; k < size; k++) {
                        String srcPath = String.format("%s/%d/before.java", baseDir, k);
                        String tarPath = String.format("%s/%d/after.java", baseDir, k);
                        // build action graph
                        System.out.printf("[prepare]build action graph: %s %d %d\n", projects[finalI], testId, k);
                        CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[]{});
                        ags.add(ag);
                    }
                    List<CodeGraph> ags_temp = new ArrayList<>();
                    for (int k = 0; k < size; k++) {
                        ags_temp.add(ags.get(k));
                    }
                    List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags_temp, runType);
                    long step_end = System.currentTimeMillis();
                    System.out.printf("[time]build all instance action graphs: %f s\n", (step_end - step_start) / 1000.0);

                    // each as target
                    for (int targetNo = 0; targetNo < size; targetNo++) {
                        // exclude the cases where more than one pattern are generated
                        if (combinedGraphs.size() > 1) {
                            System.out.printf("[warn]extracted pattern not single, size:%d\n", combinedGraphs.size());
                            continue;
                        }

                        targetCounter.getAndIncrement();
                        // build for the target
                        String path = String.format("%s/%d/before.java", baseDir, targetNo);
                        System.out.println("[start]" + path);
                        step_start = System.currentTimeMillis();
                        CodeGraph target_ag = GraphBuilder.buildGraph(path, new String[]{}, 8, new int[]{});
                        step_end = System.currentTimeMillis();
                        System.out.printf("[time]build target codegraph: %f s\n", (step_end - step_start) / 1000.0);

                        for (Pattern pat : combinedGraphs) {
                            total.getAndIncrement();
                            step_start = System.currentTimeMillis();
                            PatternAbstractor abs = new PatternAbstractor((int) Math.ceil(size * 1.0));
                            abs.abstractPattern(pat);
                            step_end = System.currentTimeMillis();
                            System.out.printf("[time]abstract pattern: %f s\n", (step_end - step_start) / 1000.0);

                            BugLocator detector = new BugLocator(0.6);
                            String patchPath = String.format("%s/%d/patch_%d.java", patchDir, targetNo, combinedGraphs.indexOf(pat));

                            step_start = System.currentTimeMillis();
                            detector.applyPattern(pat, target_ag, patchPath, runType);
                            step_end = System.currentTimeMillis();
                            System.out.printf("[time]apply pattern: %f s\n", (step_end - step_start) / 1000.0);
                            System.out.println("[patch]" + patchPath);
                        }
                        System.out.println("[finished]" + path);
                    }
                } catch (Exception e) {
                    System.out.println("[error]Unknown exception");
                } catch (Error e) {
                    System.out.println("[error]Unknown error");
                }
                return "";
            });

            executor.execute(future);
            try {
                // 取得结果，同时设置超时执行时间默认为10秒。同样可以用future.get()，不设置执行超时时间取得结果
                future.get(1, TimeUnit.MINUTES);
            } catch (Exception e) {
                System.out.println("[error]Timeout");
                future.cancel(true);
            } finally {
                executor.shutdown();
            }

            System.out.println(projects[i] + total.get());
        }
        long end = System.currentTimeMillis();
        double time = (end - start) / 1000.0;
        System.out.printf("[stat]total time: %f  (in second)\n", time);
        System.out.println("[stat]target bug instance number: " + targetCounter);

    }


    public static void generatePatches(String[] projects, String runType, String base, boolean SKIP_EXIST_OUTPUT, int MAX_GROUP) {
        for (int i = 0; i < projects.length; i++) {
            Map<String, JSONArray> patternsByID = new LinkedHashMap<>();
            int graphCounter = 0, groupCounter = 0, groupBuffer = 0;
            File dir;
            if ("SysEdit".equals(projects[i]))
                dir = new File(String.format(base + projects[i]));
            else
                dir = new File(String.format(base + "dataset/" + projects[i]));
            File[] dirs = dir.listFiles();
            if ("SysEdit".equals(projects[i])) {
                List<File> temp = new ArrayList<>();
                Arrays.stream(dirs).forEach(f -> {
                    if (f.isDirectory()) {
                        temp.add(new File(f.getAbsolutePath() + "/l/"));
                        temp.add(new File(f.getAbsolutePath() + "/r/"));
                    }
                });
                dirs = temp.toArray(new File[temp.size()]);
            }
            for (File group : dirs) {
                if (SKIP_EXIST_OUTPUT) {
                    if ("SysEdit".equals(projects[i])) {
                        String id = group.getAbsolutePath().split("/")[group.getAbsolutePath().split("/").length - 2];
                        String prefix = group.getAbsolutePath().split("/")[group.getAbsolutePath().split("/").length - 1];
                        if (new File(String.format("%s/out/json/%s/%s_%s.json", System.getProperty("user.dir"), projects[i], id, prefix)).exists())
                            continue;
                    } else {
                        if (new File(String.format("%s/out/json/%s/%s.json", System.getProperty("user.dir"), projects[i], group.getName())).exists())
                           continue;
                    }
                }
                if (group.isDirectory()) {
                    try {
                        List<CodeGraph> ags = new ArrayList<>();
                        if ("SysEdit".equals(projects[i])) {
                            String srcPath = group.getAbsolutePath() + "/before.java";
                            String tarPath = group.getAbsolutePath() + "/after.java";
                            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[]{});
                            ags.add(ag);
                            graphCounter++;
                        } else {
                            for (File pair : group.listFiles()) {
                                if (pair.isDirectory()) {
                                    String srcPath = pair.getAbsolutePath() + "/before.java";
                                    String tarPath = pair.getAbsolutePath() + "/after.java";
                                    CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[]{});
                                    ags.add(ag);
                                    graphCounter++;
                                }
                            }
                        }
                        // extract pattern from more-than-one graphs
                        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags, runType);
                        for (Pattern pat : combinedGraphs) {
                            // abstract pattern
                            PatternAbstractor abs = new PatternAbstractor(group.listFiles(File::isDirectory).length);
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
                        System.out.println(group.getAbsolutePath() + ": " + combinedGraphs.size() + " patterns");
                        groupCounter++;
                    } catch (Exception e) {
                        System.out.println(group.getAbsolutePath() + ": " + " 0 patterns");
                    }
                    if (groupCounter == MAX_GROUP) {
                        groupBuffer++;
                        // write json object to file
                        String jsonPath;

                        jsonPath = System.getProperty("user.dir") + String.format("/out/json/%s/%s.json", projects[i], group.getName());
                        if ("SysEdit".equals(projects[i])) {
                            String id = group.getAbsolutePath().split("/")[group.getAbsolutePath().split("/").length - 2];
                            String prefix = group.getAbsolutePath().split("/")[group.getAbsolutePath().split("/").length - 1];
                            jsonPath = String.format("%s/out/json/%s/%s_%s.json", System.getProperty("user.dir"), projects[i], id, prefix);
                        }

                        File file = new File(jsonPath);
                        if (!file.getParentFile().exists()) {
                            file.getParentFile().mkdirs();
                        }

                        ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);
                        groupCounter = 0;
                        patternsByID.clear();
                    }
                }
            }
            System.out.printf("Total codegraph instances: %d\n", graphCounter);
            System.out.printf("Total group instances: %d\n", (groupBuffer - 1) * MAX_GROUP + groupCounter);
        }
    }


    public static void testModelPatches(String modelResult, String runType, String dataBase) {
        // modelResult : xxx.json
        // load the json file and iterate all keys and values in it
        JSONObject jsonObject = (JSONObject) ObjectUtil.readJsonFromFile(modelResult);
        for (String key : jsonObject.keySet()) {
            try {
                // "/data02/hanjiachen/yc_fixgen/dataset/ant/403/1/before.java$$0" parse the string to get 'ant', '403', '1'
                String[] keySplit = key.split("/");
                String project = keySplit[keySplit.length - 4];
                String groupID = keySplit[keySplit.length - 3];
                String targetID = keySplit[keySplit.length - 2];

                //            String project = "ant";
                //            String groupID = "488";
                //            String targetID = "1";
                //            String key = String.format("/data02/hanjiachen/yc_fixgen/dataset/%s/%s/%s/before.java$$0", project, groupID, targetID);

                String patchDir = String.format("%s/out/model_patch/%s/%s", System.getProperty("user.dir"), project, groupID);

                // use l and r in SysEdit in a cross way
                if (project.equals("SysEdit"))
                    targetID = targetID.equals("l") ? "r" : "l";

                String srcPath = dataBase + "dataset/" + project + "/" + groupID + "/" + targetID + "/before.java";
                String tarPath = dataBase + "dataset/" + project + "/" + groupID + "/" + targetID + "/after.java";


                CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[]{});

                // 3. json file as model input
                // init the pattern
                List<Pattern> patterns = PatternExtractor.combineGraphs(new ArrayList<>() {
                    {
                        add(ag);
                    }
                }, runType);
                if (patterns.size() > 1) {
                    continue;
                }
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
                String jsonPath = System.getProperty("user.dir") + String.format("/test_json_out/c3_%s_%s_%s.json", project, groupID, targetID);
                ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);

                String cg_name = ag.getFileName();
                for (int i = 0; i < patterns.size(); i++) {
                    Pattern pattern = patterns.get(i);
                    PatternAbstractor.buildWithoutAbstract(pattern);

                    JSONObject label = ((JSONObject) ObjectUtil.readJsonFromFile(modelResult)).getJSONObject(key);
                    JSONObject ori = ((JSONObject) ObjectUtil.readJsonFromFile(jsonPath)).getJSONArray(cg_name).getJSONObject(i);
                    InteractPattern.abstractByJSONObject(pattern, ori, label, cg_name);
                    // save the pattern
                    String patternPath = String.format("%s/pattern_out/pattern_c3_%s_%s_%s_%s_predict.dat", System.getProperty("user.dir"), project, groupID, targetID, i);
                    ObjectUtil.writeObjectToFile(pattern, patternPath);

                    DotGraph dot2 = new DotGraph(pattern, 0, true, false);
                    File dir2 = new File(String.format("%s/graph/pattern_c3_%s_%s_%s_%s.dot", System.getProperty("user.dir"), project, groupID, targetID, i));
                    dot2.toDotFile(dir2);
                }
                // 5. apply pattern to source file
                // build for the target
                CodeGraph target_ag = GraphBuilder.buildGraph(srcPath, new String[]{}, 8, new int[]{});

                for (int i = 0; i < patterns.size(); i++) {
                    Pattern pattern = patterns.get(i);

                    // check whether all actions are abstracted
                    if (pattern.getActionSet().stream().allMatch(n -> n.isAbstract())) {
                        System.out.println(String.format("[error]all abstracted actions: %s %s %s", project, groupID, targetID));
                        continue;
                    }

                    // check action source and target whether valid
                    boolean abstractValid = true;
                    for (PatternNode action : pattern.getActionSet().stream().filter(n -> !n.isAbstract()).collect(Collectors.toSet())) {
                        for (PatternEdge ie : action.inEdges()) {
                            if (ie.isAbstract() || ie.getSource().isAbstract())
                                abstractValid = false;
                        }
                        for (PatternEdge oe : action.outEdges()) {
                            if (oe.isAbstract() || oe.getTarget().isAbstract())
                                abstractValid = false;
                        }
                    }
                    if (!abstractValid) {
                        System.out.println(String.format("[error]action node has invalid-abstracted source or target: %s %s %s", project, groupID, targetID));
                        continue;
                    }

                    // locate the buggy line
                    BugLocator detector = new BugLocator(0.6);

                    String patchPath = String.format("%s/%s/patch_%d.java", patchDir, targetID, i);
                    detector.applyPattern(pattern, target_ag, patchPath, runType);
                }
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    public static void main(String[] args) {
        String project = "";
        String base = "";
        String type = "";
        String id = "";
        String patchBase = String.format("%s/out/patch/", System.getProperty("user.dir"));
//        String.format("%s/out/model_patch/%s/%s", System.getProperty("user.dir"))
        String modelResult = "";

//        String pythonScript = String.format("%s/reformat_patch.py", System.getProperty("user.dir"));

        if (args.length == 0) {
            System.out.println("No command-line arguments provided.");
            return;
        }

        for (String arg : args) {
            if (arg.startsWith("--proj=")) {
                project = arg.substring("--proj=".length());
            } else if (arg.startsWith("--database=")) {
                base = arg.substring("--database=".length());
            } else if (arg.startsWith("--type=")) {
                type = arg.substring("--type=".length());
            } else if (arg.startsWith("--id=")) {
                id = arg.substring("--id=".length());
            } else if (arg.startsWith("--model_res=")) {
                modelResult = arg.substring("--model_res=".length());
            } else if (arg.startsWith("--patch_base=")) {
                patchBase = arg.substring("--patch_base=".length());
            } else {
                System.out.println("Unknown command-line argument: " + arg);
                return;
            }
        }

        String runLogPath = String.format("%s/log/%s/%s.log", System.getProperty("user.dir"), project, id);
        String checkLogPath = String.format("%s/log/%s/check.log", System.getProperty("user.dir"), project);
        String generateTrainingDataPath = String.format("%s/model_log/%s.log", System.getProperty("user.dir"), project);
        String testModelPath = String.format("%s/model_log/progress.log", System.getProperty("user.dir"));

        List<String> allLogFiles = new ArrayList<>();
        allLogFiles.add(runLogPath);
        allLogFiles.add(checkLogPath);
        allLogFiles.add(generateTrainingDataPath);
        allLogFiles.add(testModelPath);

        for (String singleFile : allLogFiles) {
            File tmpLog = new File(singleFile);
            if (!tmpLog.exists()) {
                try {
                    tmpLog.getParentFile().mkdirs();
                    tmpLog.createNewFile();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        String logFile = "";
        if (type.equals("run")) {
            logFile = runLogPath;
        } else if (type.equals("check")) {
            logFile = checkLogPath;
        } else if (type.equals("generateTraining")) {
            logFile = generateTrainingDataPath;
        } else if (type.equals("testModel")) {
            logFile = testModelPath;
        }

        System.getProperty("user.dir");
        System.out.println(project);
        System.out.println(base);
        System.out.println(logFile);
        System.out.println(type);


        boolean INCLUE_INSTANCE_ITSELF = true;
        boolean SKIP_IF_EXIST = true;
        boolean OUTPUT_TO_FILE = false;


        if (OUTPUT_TO_FILE) {
            FileOutputStream puts = null;
            try {
                puts = new FileOutputStream(logFile, true);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            PrintStream out = new PrintStream(puts);
            System.setOut(out);
        }


        String[] projects = {project};
        String runType = "new";


        if (type.equals("run")) {
            runAllCases(projects, runType, base, id, SKIP_IF_EXIST);
        } else if (type.equals("check")) {
            testPatchCorrectness2(project, base, patchBase);
        } else if (type.equals("generateTraining")) {
            generatePatches(projects, runType, base, true, 1);
        } else if (type.equals("testModel")) {
            testModelPatches(modelResult, runType, base);
        }
    }
}