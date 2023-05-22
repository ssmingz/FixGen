import builder.GraphBuilder;
import builder.PatternAbstractor;
import builder.PatternExtractor;
import model.CodeGraph;
import model.pattern.Pattern;
import org.junit.Test;
import utils.ObjectUtil;

import java.io.File;
import java.util.*;

public class TestPatternFeature2Csv {
    @Test
    public void testWriteFeatCsvFromMultiplePairs() {
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
            // elements before abstraction
            HashMap<Integer, Object> idPattern = pat.getIdPattern();
            // abstract pattern
            PatternAbstractor abs = new PatternAbstractor(size);
            pat = abs.abstractPattern(pat);
            // write feature csv
            String csvPath = System.getProperty("user.dir") + String.format("/out/c3_%s_%d.csv", testPro, testId);
            ObjectUtil.writeFeatureCsv(pat, pat.getIdPattern(), csvPath);
        }
    }
}
