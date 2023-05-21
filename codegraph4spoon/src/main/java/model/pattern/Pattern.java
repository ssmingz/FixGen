package model.pattern;

import codegraph.Edge;
import model.CodeGraph;
import model.CtWrapper;

import java.util.*;

public class Pattern {
    private Set<PatternNode> _patternNodes = new LinkedHashSet<>();
    private Set<PatternEdge> _patternEdges = new LinkedHashSet<>();
    private PatternNode _start;
    private Map<CtWrapper, PatternNode> _patternNodeByNode = new LinkedHashMap<>();
    private Set<String> _attributes = new LinkedHashSet<>();

    public Pattern(PatternNode aNode) {
        _start = aNode;
        _patternNodes.add(aNode);
    }

    public Set<PatternNode> getNodeSet() { return _patternNodes; }

    public void addNode(PatternNode pNode, CtWrapper node) {
        _patternNodes.add(pNode);
        _patternNodeByNode.put(node, pNode);
    }

    public void addEdge(PatternNode src, PatternNode target, PatternEdge.EdgeType type, Edge e, CodeGraph cg) {
        PatternEdge edge = findEdge(src, target, type);
        if (edge == null) {
            PatternEdge anEdge = new PatternEdge(src, target, type);
            anEdge.addInstance(e, cg);
            _patternEdges.add(anEdge);
        } else {
            edge.addInstance(e, cg);
        }
    }

    public boolean hasEdge(PatternNode src, PatternNode tar, PatternEdge.EdgeType type) {
        for (PatternEdge e : _patternEdges) {
            if (e.getSource().equals(src) && e.getTarget().equals(tar) && e.type == type)
                return true;
        }
        return false;
    }

    public PatternEdge findEdge(PatternNode src, PatternNode tar, PatternEdge.EdgeType type) {
        for (PatternEdge e : _patternEdges) {
            if (e.getSource().equals(src) && e.getTarget().equals(tar) && e.type == type)
                return e;
        }
        return null;
    }

    public Map<CtWrapper, PatternNode> getNodeMapping() {
        return _patternNodeByNode;
    }

    // TODO: set name for a pattern
    public String getPatternName() {
        return "Pattern";
    }

    public List<PatternNode> getNodes() {
        return new ArrayList<>(_patternNodes);
    }

}