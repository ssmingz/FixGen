import builder.BugLocator;
import builder.GraphBuilder;
import builder.PatternAbstractor;
import builder.PatternExtractor;
import model.CodeGraph;
import model.pattern.Pattern;
import utils.DiffUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class Main {
    //    zip -d /Users/yangchen/Desktop/FixGen.jar  'META-INF/.SF' 'META-INF/.RSA' 'META-INF/*SF'
    public static boolean isPatchCorrect(List<String> groundtruth, List<String> patch) {
        if (groundtruth.size() != patch.size())
            return false;
        for (int i = 0; i < groundtruth.size(); i++) {
            if (groundtruth.get(i).startsWith("--- ") != patch.get(i).startsWith("--- "))
                return false;
            else if (groundtruth.get(i).startsWith("+++ ") != patch.get(i).startsWith("+++ "))
                return false;
            if (!groundtruth.get(i).startsWith("--- ") && !groundtruth.get(i).startsWith("+++ ")) {
                if (!groundtruth.get(i).equals(patch.get(i)))
                    return false;
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
                        if (!patchDir.exists())
                            continue;
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
                                if (correctness)
                                    break;
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

    public static void main(String[] args) {
        String project = "";
        String base = "";
        String type = "";
        String id = "";
        String patchBase = String.format("%s/out/patch/", System.getProperty("user.dir"));

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
            }
        }

        String runLogPath = String.format("%s/log/%s/%s.log", System.getProperty("user.dir"), project, id);
        String checkLogPath = String.format("%s/log/%s/check.log", System.getProperty("user.dir"), project);

        // create if not exist
        File tmpLog = new File(runLogPath);
        if (!tmpLog.exists()) {
            try {
                tmpLog.getParentFile().mkdirs();
                tmpLog.createNewFile();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (type.equals("check")) {
            tmpLog = new File(checkLogPath);
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
        } else {
            System.out.println("Wrong type");
            return;
        }

        System.getProperty("user.dir");
        System.out.println(project);
        System.out.println(base);
        System.out.println(logFile);
        System.out.println(type);


        boolean INCLUE_INSTANCE_ITSELF = true;
        boolean SKIP_IF_EXIST = true;
        boolean OUTPUT_TO_FILE = true;


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
            AtomicInteger targetCounter = new AtomicInteger();
            long start = System.currentTimeMillis();

            for (int i = 0; i < projects.length; i++) {
                System.out.println(project);

                AtomicInteger total = new AtomicInteger();
                File dir = new File(base + "dataset/" + projects[i]);


                int testId = Integer.parseInt(id);
                String baseDir = String.format("%s/%d", dir, testId);
                int size = (int) Arrays.stream(new File(baseDir).listFiles()).filter(File::isDirectory).count();

                String patchDir = String.format("%s/out/patch/%s/%s", System.getProperty("user.dir"), projects[i], testId);
                System.out.println(patchDir);
                if (SKIP_IF_EXIST && new File(patchDir).exists())
                    continue;

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

        } else {
            testPatchCorrectness2(project, base, patchBase);
        }

//        // run a python script here
//        try {
//            String[] cmd = new String[3];
//            cmd[0] = "python";
//            cmd[1] = String.format("%s/src/main/python/patch_correctness.py", System.getProperty("user.dir"));
//            cmd[2] = patchBase;
//            Process proc = Runtime.getRuntime().exec(cmd);
//            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);
//            }
//            in.close();
//            proc.waitFor();
//        } catch (Exception e) {
//            System.out.println("[error]python script error");
//        }

    }
}

