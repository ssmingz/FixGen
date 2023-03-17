import builder.PatternAbstracter;
import builder.PatternExtractor;
import model.CodeGraph;
import model.graph.node.actions.ActionNode;
import model.pattern.Pattern;
import org.junit.Test;
import utils.DotGraph;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestPatternAbstract {
    @Test
    public void testPatternAbstractFromSinglePair() {
        CodeGraph change1 = TestPatternExtractor.constructActionGraph("73");
        CodeGraph change2 = TestPatternExtractor.constructActionGraph("74");

        List<Pattern> combinedGraphs = PatternExtractor.extractPattern(change1, change2);
        for (Pattern pat : combinedGraphs) {
            PatternAbstracter abstracter = new PatternAbstracter(2);
            Pattern patAbs = abstracter.abstractPattern(pat);
            DotGraph dot = new DotGraph(patAbs, 0);
            File dir1 = new File(System.getProperty("user.dir") + "/out/pattern73-74_abstract.dot");
            dot.toDotFile(dir1);
        }

        assertEquals(change1.getNodes().stream().filter(s -> s instanceof ActionNode).collect(Collectors.toList()).size(), 1);
        assertEquals(change2.getNodes().stream().filter(s -> s instanceof ActionNode).collect(Collectors.toList()).size(), 1);

    }
}
