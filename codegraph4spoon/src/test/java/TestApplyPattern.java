import builder.*;
import model.CodeGraph;
import model.pattern.Pattern;
import org.junit.Test;
import utils.DotGraph;

import java.io.File;
import java.util.*;

public class TestApplyPattern {
    @Test
    public void testApplyPattern(){
        String testPro = "ant";
        int testId = 13;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("src/test/resources/c3/%s/%d", testPro, testId);
        int size = (int) Arrays.stream(new File(base).listFiles()).filter(File::isDirectory).count();

        // build for the target
        int targetNo = 0;
        CodeGraph target_ag = GraphBuilder.buildGraph(
                String.format("%s/%d/before.java", base, targetNo), new String[] {}, 8, new int[] {});
        GraphConfiguration config = new GraphConfiguration();
        DotGraph dot3 = new DotGraph(target_ag, config, 0);
        File dir3 = new File(System.getProperty("user.dir") + "/out/cg_temp.dot");
        dot3.toDotFile(dir3);

        // build for the pattern
        for (int i=0; i<size; i++) {
            if (i==targetNo) continue;
            String srcPath = String.format("%s/%d/before.java", base, i);
            String tarPath = String.format("%s/%d/after.java", base, i);
            // build action graph
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
            ags.add(ag);
        }
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        for (Pattern pat : combinedGraphs) {
            // abstract pattern
            PatternAbstractor abs = new PatternAbstractor((int) Math.floor(size*0.8));
            abs.abstractPattern(pat);
            DotGraph dot = new DotGraph(pat, 0, true);
            File dir = new File(String.format("%s/out/pattern_temp_%d.dot", System.getProperty("user.dir"), combinedGraphs.indexOf(pat)));
            dot.toDotFile(dir);
            // locate the buggy line
            BugLocator detector = new BugLocator(0.6);
            String patchPath = String.format("%s/out/patch_temp_%d.java", System.getProperty("user.dir"), combinedGraphs.indexOf(pat));
            detector.applyPattern(pat, target_ag, patchPath);
        }
    }

    @Test
    public void testApplyPatternOnC3(){
        String[] projects = {"ant", "junit", "checkstyle", "cobertura"};
        String base = TestConfig.MAC_BASE;
        for (int i=0; i<projects.length; i++) {
            File dir = new File(base + "dataset/" + projects[i]);
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    int testId = Integer.parseInt(group.getName());
                    List<CodeGraph> ags = new ArrayList<>();
                    String baseDir = String.format("%s/%d", dir, testId);
                    int size = (int) Arrays.stream(new File(baseDir).listFiles()).filter(File::isDirectory).count();
                    // each as target
                    for (int targetNo=0; targetNo<size; targetNo++) {
                        // build for the target
                        CodeGraph target_ag = GraphBuilder.buildGraph(
                                String.format("%s/%d/before.java", baseDir, targetNo), new String[] {}, 8, new int[] {});
                        System.out.println("[start]"+target_ag.getFileName());

                        // build for the pattern
                        for (int k=0; k<size; k++) {
                            if (k==targetNo) continue;
                            String srcPath = String.format("%s/%d/before.java", baseDir, k);
                            String tarPath = String.format("%s/%d/after.java", baseDir, k);
                            // build action graph
                            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
                            ags.add(ag);
                        }
                        // extract pattern from more-than-one graphs
                        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
                        for (Pattern pat : combinedGraphs) {
                            // abstract pattern
                            PatternAbstractor abs = new PatternAbstractor((int) Math.floor(size*0.8));
                            abs.abstractPattern(pat);
                            // locate the buggy line
                            BugLocator detector = new BugLocator(0.6);
                            String patchPath = String.format("%s/out/patch_%s_%d_%d_%d.java", System.getProperty("user.dir"), projects[i], testId, targetNo, combinedGraphs.indexOf(pat));
                            detector.applyPattern(pat, target_ag, patchPath);
                        }
                        System.out.println("[finished]"+target_ag.getFileName());
                    }
                }
            }
        }
    }

    @Test
    public void testApplyPatternOnC3_debug(){
        String pro = "ant";
        int testId = 1069;
        int targetNo = 0;
        String base = TestConfig.MAC_BASE;
        String baseDir = String.format("%s/dataset/%s/%d", base, pro, testId);
        int size = (int) Arrays.stream(new File(baseDir).listFiles()).filter(File::isDirectory).count();

        // build for the target
        CodeGraph target_ag = GraphBuilder.buildGraph(
                String.format("%s/%d/before.java", baseDir, targetNo), new String[] {}, 8, new int[] {});

        // build for the pattern
        List<CodeGraph> ags = new ArrayList<>();
        for (int k=0; k<size; k++) {
            if (k==targetNo) continue;
            String srcPath = String.format("%s/%d/before.java", baseDir, k);
            String tarPath = String.format("%s/%d/after.java", baseDir, k);
            // build action graph
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
            ags.add(ag);
        }
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        for (Pattern pat : combinedGraphs) {
            // abstract pattern
            PatternAbstractor abs = new PatternAbstractor((int) Math.floor(size*0.8));
            abs.abstractPattern(pat);
            // locate the buggy line
            BugLocator detector = new BugLocator(0.6);
            String patchPath = String.format("%s/out/patch_%s_%d_%d_%d.java", System.getProperty("user.dir"), pro, testId, targetNo, combinedGraphs.indexOf(pat));
            detector.applyPattern(pat, target_ag, patchPath);
        }
        System.out.println("[finished]"+target_ag.getFileName());
    }
}
