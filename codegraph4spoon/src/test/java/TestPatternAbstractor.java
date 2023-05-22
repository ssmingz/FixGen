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
            PatternAbstractor abs = new PatternAbstractor((int) Math.floor(size*0.7));
            pat = abs.abstractPattern(pat);
            DotGraph dot = new DotGraph(pat, 0);
            File dir = new File(System.getProperty("user.dir") + String.format("/out/c3_%s_%d_pattern_abstract.dot", testPro, testId));
            dot.toDotFile(dir);
        }
    }

    @Test
    public void testPatternAbstractorOnC3() {
        String base = "/Users/yumeng/PycharmProjects/c3/dataset/";
        String[] projects = {"ant", "junit", "checkstyle", "cobertura"};
        for (int i=0; i<projects.length; i++) {
            File dir = new File(String.format(base + projects[i]));
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    List<CodeGraph> ags = new ArrayList<>();
                    for (File pair : group.listFiles()) {
                        if (pair.isDirectory()) {
                            String srcPath = pair.getAbsolutePath()+"/before.java";
                            String tarPath = pair.getAbsolutePath()+"/after.java";
                            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath);
                            ags.add(ag);
                        }
                    }
                    // extract pattern from more-than-one graphs
                    try {
                        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
                        for (Pattern pat : combinedGraphs) {
                            // abstract pattern
                            PatternAbstractor abs = new PatternAbstractor(group.listFiles().length);
                            pat = abs.abstractPattern(pat);
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
        String pro = "ant";
        int group = 135;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("/Users/yumeng/PycharmProjects/c3/dataset/%s/%d", pro, group);
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
            DotGraph dot = new DotGraph(pat, 0);
            File dir = new File(System.getProperty("user.dir") + String.format("/out/c3_%s_%d_pattern.dot", pro, group));
            dot.toDotFile(dir);
            PatternAbstractor abs = new PatternAbstractor(size);
            pat = abs.abstractPattern(pat);
            DotGraph dot2 = new DotGraph(pat, 0);
            File dir2 = new File(System.getProperty("user.dir") + String.format("/out/c3_%s_%d_pattern_abstract.dot", pro, group));
            dot2.toDotFile(dir2);
        }
    }
}
