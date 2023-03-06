package model;

import model.graph.node.Node;

import java.util.*;

public class Pattern {
    private Set<Node> _patternNodes;
    private int _freq = 0;
    private int _size = 0;

    public Pattern(Node aNode) {
        _patternNodes = new LinkedHashSet<>();
        _patternNodes.add(aNode);
    }

    public Pattern(Pattern aPattern) {
        _patternNodes = new LinkedHashSet<>(aPattern.getPatternNodes());
    }

    public void addPatternNode(Node node) {
        _patternNodes.add(node);
    }

    public Set<Node> getPatternNodes() {
        return _patternNodes;
    }

    public int getFrequency() { return _freq; }

    public void setFrequency(int num) { _freq = num; }

    public int getSize() { return _size; }

    public void setSize(int size) { _size = size; }
}
