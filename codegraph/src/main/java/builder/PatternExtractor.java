package builder;

import com.github.gumtreediff.actions.model.Action;
import model.CodeGraph;
import model.Pattern;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.actions.ActionNode;
import model.graph.node.actions.Insert;
import model.graph.node.actions.Move;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PatternExtractor {
    int MAX_NODE_SIZE;
    int MAX_EXTEND_LEVEL = 3;  // default is 3

    public Set<Pattern> extractPattern(List<CodeGraph> instances) {
        Set<Pattern> patterns = new LinkedHashSet<>();
/*
        // try each action-attached not-newly-created node as a start node
        for (CodeGraph instance : instances) {
            for (Node action : instance.getNodes().stream().filter(p -> p instanceof ActionNode).collect(Collectors.toList())) {
                Node start = action.getParent();
                // init
                Pattern aPattern = new Pattern(start);
                aPattern.addPatternNode(action);
                if (action instanceof Insert) {
                    aPattern.addPatternNode(((Insert) action).getInsert());
                } else if (action instanceof Move) {
                    aPattern.addPatternNode(((Move) action).getMove());
                }
                int support = Matcher.findInstancesIn(instances.stream().filter(p -> p != instance).collect(Collectors.toSet()));
                aPattern.setSupport(support);
                patterns.add(aPattern);
                // try with each edge
                for (Edge outEdge : start.outEdges) {
                    if (!aPattern.getPatternNodes().contains(outEdge.getTarget())) {
                        Pattern bPattern = new Pattern(aPattern);
                        bPattern.addPatternNode(outEdge.getTarget());
                        // match in other graphs
                        support = Matcher.findInstancesIn(instances.stream().filter(p -> p != instance).collect(Collectors.toSet()));
                        bPattern.setSupport(support);
                        patterns.add(bPattern);
                    }
                }
            }
        }
*/
        return patterns;
    }
}
