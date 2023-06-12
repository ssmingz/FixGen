import builder.*;
import model.CodeGraph;
import model.pattern.Pattern;
import org.junit.Assert;
import org.junit.Test;
import utils.DotGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
}
