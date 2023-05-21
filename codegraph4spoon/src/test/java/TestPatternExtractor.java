import builder.GraphBuilder;
import builder.GraphConfiguration;
import builder.PatternExtractor;
import model.CodeGraph;
import model.pattern.Pattern;
import org.junit.Test;
import utils.DotGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class TestPatternExtractor {
    @Test
    public void testPatternExtractFromMultiplePairs() {
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
            // draw dot graph
            GraphConfiguration config = new GraphConfiguration();
            int nodeIndexCounter = 0;
            DotGraph dg = new DotGraph(ag, config, nodeIndexCounter);
            File dir = new File(System.getProperty("user.dir") + "/out/" + String.format("c3_%s_%d_%d_action.dot", testPro, testId, i));
            dg.toDotFile(dir);
        }
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot = new DotGraph(pat, 0);
            File dir = new File(System.getProperty("user.dir") + String.format("/out/c3_%s_%d_pattern.dot", testPro, testId));
            dot.toDotFile(dir);
        }
    }

    @Test
    public void testPatternExtractorOnC3() {
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
                        assertTrue(combinedGraphs.stream().anyMatch(Objects::nonNull));
                        System.out.println(group.getAbsolutePath() + ": generate code graph ok");
                    } catch (Exception e) {
                        System.out.println(group.getAbsolutePath() + " " + e.getMessage());
                    }
                }
            }
        }
    }

    @Test
    public void testPatternExtractorOnC3_fordebug() {
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
        }
    }
}
