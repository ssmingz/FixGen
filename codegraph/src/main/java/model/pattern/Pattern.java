package model.pattern;

import model.graph.node.Node;

import java.util.*;

public class Pattern {
    private Set<PatternNode> _patternNodes = new LinkedHashSet<>();
    private Set<PatternEdge> _patternEdges = new LinkedHashSet<>();
    private PatternNode _start;
    private Map<Node, PatternNode> _patternNodeByNode = new LinkedHashMap<>();

    public Pattern(PatternNode aNode) {
        _start = aNode;
        _patternNodes.add(aNode);
    }

    public void addNode(PatternNode aNode) {
        _patternNodes.add(aNode);
    }

    public void addNode(PatternNode pNode, Node node) {
        _patternNodes.add(pNode);
        _patternNodeByNode.put(node, pNode);
    }

    public Map<Node, PatternNode> getNodeMapping() {
        return _patternNodeByNode;
    }

    public void addEdge(PatternNode src, PatternNode target, PatternEdge.EdgeType type) {
        PatternEdge anEdge = new PatternEdge(src, target, type);
        _patternEdges.add(anEdge);
    }

    public PatternNode getStart() {
        return _start;
    }

    // TODO: set name for a pattern
    public String getPatternName() {
        return "Pattern";
    }

    public List<PatternNode> getNodes() {
        return new ArrayList<>(_patternNodes);
    }

    public boolean hasEdge(PatternNode src, PatternNode tar, PatternEdge.EdgeType type) {
        for (PatternEdge e : _patternEdges) {
            if (e.getSource().equals(src) && e.getTarget().equals(tar) && e.type == type)
                return true;
        }
        return false;
    }
}
