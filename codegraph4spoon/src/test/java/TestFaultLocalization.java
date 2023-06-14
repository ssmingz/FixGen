import builder.*;
import model.CodeGraph;
import model.pattern.Pattern;
import org.junit.Assert;
import org.junit.Test;
import utils.DotGraph;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertNotNull;

public class TestFaultLocalization {
    /**
     * test fault localization by using the first instance as the target and the others as the examples
     */
    @Test
    public void testFaultLocalization(){
        String testPro = "ant";
        int testId = 13;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("src/test/resources/c3/%s/%d", testPro, testId);
        int size = new File(base).listFiles().length;

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
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath);
            ags.add(ag);
        }
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        for (Pattern pat : combinedGraphs) {
            // abstract pattern
            PatternAbstractor abs = new PatternAbstractor((int) Math.floor(size*0.8));
            abs.abstractPattern(pat);
            DotGraph dot = new DotGraph(pat, 0, true);
            File dir = new File(System.getProperty("user.dir") + String.format("/out/pattern_temp_%d.dot", combinedGraphs.indexOf(pat)));
            dot.toDotFile(dir);
            // locate the buggy line
            BugLocator detector = new BugLocator(0.6);
            String result = detector.locateFaultByPattern(pat, target_ag);
            Assert.assertEquals(String.format("%s/%s/%d/before.java#5", System.getProperty("user.dir"), base, targetNo), result);
        }
    }

    @Test
    public void testPatternAbstractorOnC3() {
        String[] projects = {"ant", "junit", "checkstyle", "cobertura"};
        String base = TestConfig.WIN_BASE;
        for (int i=0; i<projects.length; i++) {
            File dir = new File(String.format(base + "dataset/" + projects[i]));
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    List<CodeGraph> ags = new ArrayList<>();
                    File basedir = new File(String.format("%s/codegraphs/%s/%s", base, projects[i], group.getName()));
                    if (!basedir.exists() || !basedir.isDirectory()) {
                        basedir.mkdirs();
                    }
                    for (File pair : group.listFiles()) {
                        if (pair.isDirectory()) {
                            String srcPath = pair.getAbsolutePath()+"/before.java";
                            String tarPath = pair.getAbsolutePath()+"/after.java";
                            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath);
                            ags.add(ag);

                            GraphConfiguration config = new GraphConfiguration();
                            int nodeIndexCounter = 0;
                            DotGraph dg = new DotGraph(ag, config, nodeIndexCounter);
                            File dogf0 = new File(String.format("%s/codegraph_%s.dot", basedir, pair.getName()));
                            dg.toDotFile(dogf0);
                        }
                    }
                    // extract pattern from more-than-one graphs
                    try {
                        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
                        for (Pattern pat : combinedGraphs) {
                            DotGraph dot = new DotGraph(pat, 0, false);
                            File dotf = new File(String.format("%s/pattern_%d.dot", basedir, combinedGraphs.indexOf(pat)));
                            dot.toDotFile(dotf);

                            // abstract pattern
                            PatternAbstractor abs = new PatternAbstractor(group.listFiles().length);
                            pat = abs.abstractPattern(pat);

                            DotGraph dot2 = new DotGraph(pat, 0, true);
                            File dotf2 = new File(String.format("%s/pattern_abstract_%d.dot", basedir, combinedGraphs.indexOf(pat)));
                            dot2.toDotFile(dotf2);

                            assertNotNull(pat);
                            System.out.println(group.getAbsolutePath() + ": abstract pattern ok");
                        }
                    } catch (Exception e) {
                        System.out.println(group.getAbsolutePath() + " " + e.getMessage());
                    }
                }
            }
        }
    }

    @Test
    public void testFaultLocalizationOnC3(){
        String[] projects = {"ant", "junit", "checkstyle", "cobertura"};
        Integer[] finished =
                {34, 35, 51, 56, 57, 58, 61, 66, 92, 93, 94, 103, 133, 135, 150, 151, 156, 159, 160, 161, 166, 167, 168, 169, 192, 193, 210, 211, 216, 217, 218, 221, 226, 227, 228, 242, 243, 244, 245, 250, 274, 281, 287, 288, 289, 300, 301, 306, 308, 330, 331, 336, 337, 339, 352, 353, 354, 355, 364, 365, 390, 391, 396, 397, 399, 401, 406, 412, 414, 422, 423, 425, 439, 440, 441, 446, 447, 449, 470, 471, 476, 477, 478, 479, 482, 483, 485, 502, 504, 505};
        HashSet<Integer> fset = new HashSet<>();
        Collections.addAll(fset, finished);
        String base = TestConfig.MAC_BASE;
        for (int i=0; i<projects.length; i++) {
            File dir = new File(base + "dataset/" + projects[i]);
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    int testId = Integer.parseInt(group.getName());
                    if(projects[i].equals("ant")&&fset.contains(Integer.valueOf(testId)))
                        continue;
                    List<CodeGraph> ags = new ArrayList<>();
                    String baseDir = String.format("%s/%d", dir, testId);
                    int size = (int) Arrays.stream(new File(baseDir).listFiles()).filter(p -> p.isDirectory()).count();

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
                            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath);
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
                            String result = detector.locateFaultByPattern(pat, target_ag);
                        }
                        System.out.println("[finished]"+target_ag.getFileName());
                    }
                }
            }
        }
    }

    @Test
    public void testFaultLocalizationOnC3_debug(){
        String pro = "ant";
        int testId = 66;
        int targetNo = 0;
        String base = TestConfig.MAC_BASE;
        String baseDir = String.format("%s/dataset/%s/%d", base, pro, testId);
        int size = (int) Arrays.stream(new File(baseDir).listFiles()).filter(p -> p.isDirectory()).count();

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
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath);
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
            String result = detector.locateFaultByPattern(pat, target_ag);
        }
        System.out.println("[finished]"+target_ag.getFileName());
    }
}
