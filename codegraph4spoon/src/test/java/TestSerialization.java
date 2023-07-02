import builder.GraphBuilder;
import builder.PatternAbstractor;
import builder.PatternExtractor;
import model.CodeGraph;
import model.pattern.Pattern;
import org.junit.Assert;
import org.junit.Test;
import utils.ObjectUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestSerialization {
    @Test
    public void testSaveAndLoadCodeGraph() {
        String srcPath = String.format("src/test/resources/c3/ant/13/%d/before.java", 0);
        String tarPath = String.format("src/test/resources/c3/ant/13/%d/after.java", 0);
        // build action graph
        CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});

        String filePath = String.format("%s/out/c3_ant_13_%d_ActionGraph.dat", System.getProperty("user.dir"), 0);
        ObjectUtil.writeObjectToFile(ag, filePath);  // save
        CodeGraph ag_reload = (CodeGraph) ObjectUtil.readObjectFromFile(filePath);  // load

        Assert.assertEquals(ag_reload._allNodes.size(), ag._allNodes.size());
    }

    @Test
    public void testSaveAndLoadPattern() {
        String testPro = "ant";
        int testId = 13;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("src/test/resources/c3/%s/%d", testPro, testId);
        int size = new File(base).listFiles().length;
        for (int i=0; i<size; i++) {
            String srcPath = String.format("%s/%d/before.java", base, i);
            String tarPath = String.format("%s/%d/after.java", base, i);
            // build action graph
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
            ags.add(ag);
        }
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        for (Pattern pat : combinedGraphs) {
            // abstract pattern
            PatternAbstractor abs = new PatternAbstractor((int) Math.floor(size*0.8));
            pat = abs.abstractPattern(pat);

            String filePath = String.format("%s/out/c3_%s_%d_Pattern_%d.dat", System.getProperty("user.dir"), testPro, testId, combinedGraphs.indexOf(pat));
            ObjectUtil.writeObjectToFile(pat, filePath);  // save
            Pattern pat_reload = (Pattern) ObjectUtil.readObjectFromFile(filePath);  // load

            Assert.assertEquals(pat_reload.getNodes().size(), pat.getNodes().size());
        }
    }
}
