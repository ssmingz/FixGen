import builder.*;
import model.CodeGraph;
import model.pattern.Pattern;
import model.pattern.PatternNode;
import org.junit.Test;
import utils.DotGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TestApplyPattern {
    @Test
    public void testApplyPattern() {
        String testPro = "ant";
        int testId = 13;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("src/test/resources/c3/%s/%d", testPro, testId);
        int size = (int) Arrays.stream(new File(base).listFiles()).filter(File::isDirectory).count();

        // build for the target
        int targetNo = 0;
        CodeGraph target_ag = GraphBuilder.buildGraph(
                String.format("%s/%d/before.java", base, targetNo), new String[]{}, 8, new int[]{});
        GraphConfiguration config = new GraphConfiguration();
        DotGraph dot3 = new DotGraph(target_ag, config, 0);
        File dir3 = new File(System.getProperty("user.dir") + "/out/cg_temp.dot");
        dot3.toDotFile(dir3);

        // build for the pattern
        for (int i = 0; i < size; i++) {
            if (i == targetNo) continue;
            String srcPath = String.format("%s/%d/before.java", base, i);
            String tarPath = String.format("%s/%d/after.java", base, i);
            // build action graph
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[]{});
            ags.add(ag);
        }
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        for (Pattern pat : combinedGraphs) {
            // abstract pattern
            PatternAbstractor abs = new PatternAbstractor((int) Math.floor(size * 0.8));
            abs.abstractPattern(pat);
            DotGraph dot = new DotGraph(pat, 0, true, false);
            File dir = new File(String.format("%s/out/pattern_temp_%d.dot", System.getProperty("user.dir"), combinedGraphs.indexOf(pat)));
            dot.toDotFile(dir);
            // locate the buggy line
            BugLocator detector = new BugLocator(0.6);
            String patchPath = String.format("%s/out/patch_temp_%d.java", System.getProperty("user.dir"), combinedGraphs.indexOf(pat));
            detector.applyPattern(pat, target_ag, patchPath);
        }
    }

    @Test
    public void testApplyPatternOnC3() {
        boolean INCLUE_INSTANCE_ITSELF = true;
        boolean SKIP_IF_EXIST = true;
        boolean OUTPUT_TO_FILE = true;

        if (OUTPUT_TO_FILE) {
            FileOutputStream puts = null;
            try {
                puts = new FileOutputStream(TestConfig.LOG_PATH, true);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            PrintStream out = new PrintStream(puts);
            System.setOut(out);
        }

//        String[] projects = {"drjava", "ant", "swt"};
        String[] projects = {"junit"};
//        String[] projects = {"cobertura"};
        String runType = "new";
        String base = TestConfig.MAC_BASE;
        AtomicInteger targetCounter = new AtomicInteger();
        long start = System.currentTimeMillis();
        for (int i = 0; i < projects.length; i++) {
            AtomicInteger total = new AtomicInteger();
            File dir = new File(base + "dataset/" + projects[i]);
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    int testId = Integer.parseInt(group.getName());
                    String baseDir = String.format("%s/%d", dir, testId);
                    int size = (int) Arrays.stream(new File(baseDir).listFiles()).filter(File::isDirectory).count();
                    String patchDir = String.format("%s/out/patch/%s/%s/", System.getProperty("user.dir"), projects[i], testId);
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

//                                 build for the pattern
//                                List<CodeGraph> ags_temp = new ArrayList<>();
//                                for (int k = 0; k < size; k++) {
//                                    if (!INCLUE_INSTANCE_ITSELF && k == targetNo) continue;
//                                    ags_temp.add(ags.get(k));
//                                }
                                // extract pattern from more-than-one graphs
//                                step_start = System.currentTimeMillis();
//                                List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags_temp, runType);
//                                step_end = System.currentTimeMillis();
//                                System.out.printf("[time]cluster instance graphs: %f s\n", (step_end - step_start) / 1000.0);

                                for (Pattern pat : combinedGraphs) {
                                    Set<PatternNode> actionNodes = pat.getNodeSet().stream()
                                            .filter(PatternNode::isActionRelated)
                                            .collect(Collectors.toSet());
                                    // filter out the cases where not all modifications are the same within a group
                                    if (actionNodes.stream().allMatch(node -> node.getInstance().keySet().size() == size)) {
                                        total.getAndIncrement();
                                        step_start = System.currentTimeMillis();
                                        PatternAbstractor abs = new PatternAbstractor((int) Math.ceil(size * 1.0));
                                        abs.abstractPattern(pat);
                                        step_end = System.currentTimeMillis();
                                        System.out.printf("[time]abstract pattern: %f s\n", (step_end - step_start) / 1000.0);

                                        BugLocator detector = new BugLocator(0.4);
                                        String patchPath = String.format("%s/%d/patch_%d.java", patchDir, targetNo, combinedGraphs.indexOf(pat));

                                        step_start = System.currentTimeMillis();
                                        detector.applyPattern(pat, target_ag, patchPath, runType);
                                        step_end = System.currentTimeMillis();
                                        System.out.printf("[time]apply pattern: %f s\n", (step_end - step_start) / 1000.0);
                                    } else {
                                        System.out.println("[warn]instance size not equal to all instances");
                                        break;
                                    }
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
                }
            }
            System.out.println(projects[i] + total.get());
        }
        long end = System.currentTimeMillis();
        double time = (end - start) / 1000.0;
        System.out.printf("[stat]total time: %f  (in second)\n", time);
        System.out.println("[stat]target bug instance number: " + targetCounter);
    }

    @Test
    public void testApplyPatternOnC3_debug() {
        boolean INCLUE_INSTANCE_ITSELF = true;
        String pro = "junit";
        int testId = 79;
        int targetNo = 0;
        String runType = "new";
        String base = TestConfig.MAC_BASE;
        String baseDir = String.format("%s/dataset/%s/%d", base, pro, testId);
        int size = (int) Arrays.stream(new File(baseDir).listFiles()).filter(File::isDirectory).count();

        // build for the target
        CodeGraph target_ag = GraphBuilder.buildGraph(
                String.format("%s/%d/before.java", baseDir, targetNo), new String[]{}, 8, new int[]{});
        DotGraph tttdot = new DotGraph(target_ag, new GraphConfiguration(), 0);
        File tttdir = new File(String.format("%s/out/codegraph_base_%d.dot", System.getProperty("user.dir"), targetNo));
        tttdot.toDotFile(tttdir);
        // build for the pattern
        List<CodeGraph> ags = new ArrayList<>();
        for (int k = 0; k < size; k++) {
            if (!INCLUE_INSTANCE_ITSELF && k == targetNo) continue;
            String srcPath = String.format("%s/%d/before.java", baseDir, k);
            String tarPath = String.format("%s/%d/after.java", baseDir, k);
            // build action graph
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[]{});
            ags.add(ag);
            DotGraph dot = new DotGraph(ag, new GraphConfiguration(), 0);
            File dir = new File(String.format("%s/out/codegraph_temp_%d.dot", System.getProperty("user.dir"), k));
            dot.toDotFile(dir);
        }

        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags, runType);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot = new DotGraph(pat, 0, false, false);
            File dir = new File(String.format("%s/out/pattern_temp_%d.dot", System.getProperty("user.dir"), combinedGraphs.indexOf(pat)));
            dot.toDotFile(dir);
            // abstract pattern
            PatternAbstractor abs = new PatternAbstractor((int) Math.ceil(size * 1.0));
            abs.abstractPattern(pat);
            DotGraph dot2 = new DotGraph(pat, 0, true, false);
            File dir2 = new File(String.format("%s/out/pattern_abstract_temp_%d.dot", System.getProperty("user.dir"), combinedGraphs.indexOf(pat)));
            dot2.toDotFile(dir2);
            // locate the buggy line
            BugLocator detector = new BugLocator(0.6);
            String patchPath = String.format("%s/out/patch_%s_%d_%d_%d.java", System.getProperty("user.dir"), pro, testId, targetNo, combinedGraphs.indexOf(pat));
            detector.applyPattern(pat, target_ag, patchPath, runType);
        }
        System.out.println("[finished]" + target_ag.getFileName());
    }
}
