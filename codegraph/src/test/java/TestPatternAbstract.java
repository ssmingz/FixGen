import builder.PatternAbstracter;
import builder.PatternExtractor;
import model.CodeGraph;
import model.GraphConfiguration;
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

        List<CodeGraph> cgs = new ArrayList<>();
        cgs.add(change1);
        cgs.add(change2);
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(cgs);
        for (Pattern pat : combinedGraphs) {
            PatternAbstracter abstracter = new PatternAbstracter(2);
            Pattern patAbs = abstracter.abstractPattern(pat, abstracter.getThreshold());
            DotGraph dot = new DotGraph(patAbs, 0, true);
            File dir1 = new File(System.getProperty("user.dir") + "/out/pattern74-75_abstract.dot");
            dot.toDotFile(dir1);
        }
    }

    @Test
    public void testPatternAbstractFromMultiplePairs1() {
        CodeGraph change1 = TestPatternExtractor.constructActionGraph("73");
        CodeGraph change2 = TestPatternExtractor.constructActionGraph("74");
        CodeGraph change3 = TestPatternExtractor.constructActionGraph("75");
        CodeGraph change4 = TestPatternExtractor.constructActionGraph("76");

        List<CodeGraph> cgs = new ArrayList<>();
        cgs.add(change1);
        cgs.add(change2);
        cgs.add(change3);
        cgs.add(change4);
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(cgs);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot2 = new DotGraph(pat, 0);
            dot2.toDotFile(new File(System.getProperty("user.dir") + "/out/73-76.dot"));

            long startTime = System.currentTimeMillis();
            PatternAbstracter abstracter = new PatternAbstracter(2);
            Pattern patAbs = abstracter.abstractPattern(pat, abstracter.getThreshold());
            long endTime = System.currentTimeMillis();
            DotGraph dot = new DotGraph(patAbs, 0, true);
            File dir1 = new File(System.getProperty("user.dir") + "/out/pattern73-76_abstract.dot");
            dot.toDotFile(dir1);
            System.out.println("pattern abstraction time cost : " + (endTime - startTime) + " ms");
        }
    }

    @Test
    public void testPatternAbstractFromMultiplePairs2() {
        CodeGraph change1 = TestPatternExtractor.constructActionGraph2("EmptyCheck/Genesis#69");
        //DotGraph dot2 = new DotGraph(change2, new GraphConfiguration(), 0);
        //dot2.toDotFile(new File(System.getProperty("user.dir") + "/out/69.dot"));
        CodeGraph change2 = TestPatternExtractor.constructActionGraph2("EmptyCheck/Genesis#194");

        List<CodeGraph> cgs = new ArrayList<>();
        cgs.add(change1);
        cgs.add(change2);
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(cgs);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot2 = new DotGraph(pat, 0);
            dot2.toDotFile(new File(System.getProperty("user.dir") + "/out/pattern_EmptyCheck.dot"));

            PatternAbstracter abstracter = new PatternAbstracter(2);
            Pattern patAbs = abstracter.abstractPattern(pat, abstracter.getThreshold());
            DotGraph dot = new DotGraph(patAbs, 0, true);
            int patternIndex = combinedGraphs.indexOf(pat);
            File dir1 = new File(System.getProperty("user.dir") + "/out/pattern_EmptyCheck_abstract" + patternIndex + ".dot");
            dot.toDotFile(dir1);
        }
    }

    @Test
    public void testPatternAbstractFromMultiplePairs3() {
        CodeGraph change1 = TestPatternExtractor.constructActionGraph2("NullCheck/Genesis#eval_1");
        CodeGraph change2 = TestPatternExtractor.constructActionGraph2("NullCheck/Genesis#eval_2");
        CodeGraph change3 = TestPatternExtractor.constructActionGraph2("NullCheck/Genesis#401");

        DotGraph dot3 = new DotGraph(change1, new GraphConfiguration(), 0);
        dot3.toDotFile(new File(System.getProperty("user.dir") + "/out/codegraph_eval_1.dot"));


        List<CodeGraph> cgs = new ArrayList<>();
        cgs.add(change1);
        cgs.add(change2);
        cgs.add(change3);
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(cgs);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot2 = new DotGraph(pat, 0);
            dot2.toDotFile(new File(System.getProperty("user.dir") + "/out/pattern_NullCheck.dot"));

            PatternAbstracter abstracter = new PatternAbstracter(2);
            Pattern patAbs = abstracter.abstractPattern(pat, abstracter.getThreshold());
            DotGraph dot = new DotGraph(patAbs, 0, true);
            int patternIndex = combinedGraphs.indexOf(pat);
            File dir1 = new File(System.getProperty("user.dir") + "/out/pattern_NullCheck_abstract" + patternIndex + ".dot");
            dot.toDotFile(dir1);
        }
    }
}
