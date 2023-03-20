import builder.PatternAbstracter;
import builder.PatternExtractor;
import model.CodeGraph;
import model.graph.node.actions.ActionNode;
import model.pattern.Pattern;
import org.junit.Test;
import utils.DotGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestPatternAbstract {
    @Test
    public void testPatternAbstractFromSinglePair() {
        CodeGraph change1 = TestPatternExtractor.constructActionGraph("74");
        CodeGraph change2 = TestPatternExtractor.constructActionGraph("75");

        List<Pattern> combinedGraphs = PatternExtractor.extractPattern(change1, change2);
        for (Pattern pat : combinedGraphs) {
            PatternAbstracter abstracter = new PatternAbstracter(2);
            Pattern patAbs = abstracter.abstractPattern(pat, abstracter.getThreshold());
            DotGraph dot = new DotGraph(patAbs, 0, true);
            File dir1 = new File(System.getProperty("user.dir") + "/out/pattern74-75_abstract.dot");
            dot.toDotFile(dir1);
        }
    }

    @Test
    public void testPatternAbstractFromMultiplePairs2() {
        CodeGraph change1 = TestPatternExtractor.constructActionGraph2("EmptyCheck/Genesis#26");
        CodeGraph change2 = TestPatternExtractor.constructActionGraph2("EmptyCheck/Genesis#69");
        //DotGraph dot2 = new DotGraph(change2, new GraphConfiguration(), 0);
        //dot2.toDotFile(new File(System.getProperty("user.dir") + "/out/69.dot"));
        CodeGraph change3 = TestPatternExtractor.constructActionGraph2("EmptyCheck/Genesis#101");
        CodeGraph change4 = TestPatternExtractor.constructActionGraph2("EmptyCheck/Genesis#161");

        List<CodeGraph> cgs = new ArrayList<>();
        cgs.add(change1);
        cgs.add(change2);
        cgs.add(change3);
        cgs.add(change4);
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(cgs);
        for (Pattern pat : combinedGraphs) {
            PatternAbstracter abstracter = new PatternAbstracter(2);
            Pattern patAbs = abstracter.abstractPattern(pat, abstracter.getThreshold());
            DotGraph dot = new DotGraph(patAbs, 0, true);
            int patternIndex = combinedGraphs.indexOf(pat);
            File dir1 = new File(System.getProperty("user.dir") + "/out/pattern_EmptyCheck_abstract" + patternIndex + ".dot");
            dot.toDotFile(dir1);
        }
    }
}
