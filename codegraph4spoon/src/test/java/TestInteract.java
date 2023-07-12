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

public class TestInteract {
    @Test
    public void testCodeGraphAbstract() {
//        String testPro = "ant";
//        int testId = 13;
//        int targetNo = 0;
//        String base = String.format("src/test/resources/c3/%s/%d", testPro, testId);
//        String srcPath = String.format("%s/%d/before.java", base, targetNo);
//        String tarPath = String.format("%s/%d/after.java", base, targetNo);
//        // build action graph
//        CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
//
//        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
//        for (Pattern pat : combinedGraphs) {
//            // abstract pattern
//            PatternAbstractor abs = new PatternAbstractor((int) Math.floor(size*0.8));
//            pat = abs.abstractPattern(pat);
//
//            String filePath = String.format("%s/out/c3_%s_%d_Pattern_%d.dat", System.getProperty("user.dir"), testPro, testId, combinedGraphs.indexOf(pat));
//            // save
//            ObjectUtil.writeObjectToFile(pat, filePath);
//            // save as json
//            ObjectUtil.writeFeatureJsonToFile(pat, pat.getIdPattern(), jsonPath);
//            // load
//            Pattern pat_reload = (Pattern) ObjectUtil.readObjectFromFile(filePath);
//
//            //
//            Assert.assertEquals(pat_reload.getNodes().size(), pat.getNodes().size());
//        }
    }
}
