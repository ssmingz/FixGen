import builder.GraphBuilder;
import builder.GraphConfiguration;
import builder.PatternAbstractor;
import builder.PatternExtractor;
import model.CodeGraph;
import model.pattern.Pattern;
import org.junit.Test;
import utils.DotGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class TestPatternAbstractor {
    @Test
    public void testPatternAbstractFromMultiplePairs() {
        String testPro = "ant";
        int testId = 13;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("src/test/resources/c3/%s/%d", testPro, testId);
        int size = new File(base).listFiles().length;
        for (int i=0; i<size; i++) {
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
            pat = abs.abstractPattern(pat);
            DotGraph dot = new DotGraph(pat, 0, true);
            File dir = new File(System.getProperty("user.dir") + String.format("/out/c3_%s_%d_pattern_abstract_%d.dot", testPro, testId, combinedGraphs.indexOf(pat)));
            dot.toDotFile(dir);
        }
    }

    @Test
    public void testPatternAbstractorOnC3() {
        String[] projects = {"junit", "cobertura", "checkstyle", "ant"};
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
                    if (basedir.listFiles().length>0)
                        continue;
                    System.out.println("start " + group.getAbsolutePath());
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
                            int size = group.listFiles(p -> p.isDirectory()).length;
                            PatternAbstractor abs = new PatternAbstractor(size*0.8);
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
    public void testPatternAbstractorOnC3_fordebug() {
        String pro = "junit";
        int group = 19;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("%s/dataset/%s/%d", TestConfig.WIN_BASE, pro, group);
        int size = new File(base).listFiles(p -> p.isDirectory()).length;
        for (int i=0; i<size; i++) {
            String srcPath = String.format("%s/%d/before.java", base, i);
            String tarPath = String.format("%s/%d/after.java", base, i);
            // build action graph
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath);
            ags.add(ag);
            // draw dot graph
            GraphConfiguration config = new GraphConfiguration();
            int nodeIndexCounter = 0;
            DotGraph dg = new DotGraph(ag, config, nodeIndexCounter);
            File dir = new File(System.getProperty("user.dir") + "/out/" + String.format("c3_%s_%d_%d_action.dot", pro, group, i));
            dg.toDotFile(dir);
        }
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot = new DotGraph(pat, 0, false);
            File dir = new File(System.getProperty("user.dir") + String.format("/out/c3_%s_%d_pattern_%d.dot", pro, group, combinedGraphs.indexOf(pat)));
            dot.toDotFile(dir);
            PatternAbstractor abs = new PatternAbstractor(size);
            pat = abs.abstractPattern(pat);
            DotGraph dot2 = new DotGraph(pat, 0, true);
            File dir2 = new File(System.getProperty("user.dir") + String.format("/out/c3_%s_%d_pattern_abstract_%d.dot", pro, group, combinedGraphs.indexOf(pat)));
            dot2.toDotFile(dir2);
        }
    }
}
