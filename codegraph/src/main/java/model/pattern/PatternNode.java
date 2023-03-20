package model.pattern;

import model.CodeGraph;
import model.graph.node.Node;

import java.util.*;

public class PatternNode {
    Set<Attribute> _comparedAttrs = new LinkedHashSet<>();

    int _freq = 0;
    private Map<Node, CodeGraph> _nodeGraphInstances = new LinkedHashMap<>();

    private Set<PatternEdge> _inEdges = new LinkedHashSet<>();
    private Set<PatternEdge> _outEdges = new LinkedHashSet<>();
    private Pattern _pattern = null;
    private String _nodeType = "";
    private String _locationInParent = "";

    public PatternNode(Node aNode, CodeGraph aGraph, String loc, String astType) {
        _nodeGraphInstances.put(aNode, aGraph);
        _nodeType = astType;
        _locationInParent = loc;
    }

    public Set<Attribute> getComparedAttributes() {
        return _comparedAttrs;
    }

    public String getType() {
        return _nodeType;
    }

    public String getLocationInParent() {
        return _locationInParent;
    }

    public void setComparedAttribute(Attribute attr) {
        if (attr.getValueSet().size() > 0)
            _comparedAttrs.add(attr);
    }

    public Attribute getAttribute(String name) {
        for (Attribute a : _comparedAttrs) {
            if (a.getName().equals(name))
                return a;
        }
        return null;
    }

    public void addOutEdge(PatternEdge anEdge) {
        _outEdges.add(anEdge);
    }

    public void addInEdge(PatternEdge anEdge) {
        _inEdges.add(anEdge);
    }

    public void addInstance(Node iNode, CodeGraph iGraph) {
        _nodeGraphInstances.put(iNode, iGraph);
        if (_pattern != null) {
            _pattern.getNodeMapping().put(iNode, this);
        }
    }

    public Set<PatternEdge> inEdges() {
        return _inEdges;
    }

    public Set<PatternEdge> outEdges() {
        return _outEdges;
    }

    public Map<Node, CodeGraph> getInstance() {
        return _nodeGraphInstances;
    }

    public String toLabel() {
        StringBuilder label = new StringBuilder();
        if (!_nodeType.equals("")) {
            label.append(_nodeType).append("##");
        } else {
            label.append("NOT_PARSED_AST_TYPE##");
        }
        if (!_locationInParent.equals("")) {
            label.append(_locationInParent).append("##");
        } else {
            label.append("NOT_PARSED_LOCATION_IN_PARENT##");
        }
        if (isPatternStart()) {
            label.append("ActionPoint##");
        }
        for (Map.Entry<Node, CodeGraph> entry : _nodeGraphInstances.entrySet()) {
            if (label.length() != 0)
                label.append("\n");
            String ins = entry.getValue().getGraphName() + "#" + entry.getKey().getStartSourceLine() + ":" + entry.getKey().toLabelString();
            label.append(ins);
        }
        return label.toString();
    }

    public void setPattern(Pattern pattern) {
        _pattern = pattern;
    }

    public boolean isPatternStart() {
        if (_pattern != null) {
            return _pattern.getStart() == this;
        }
        return false;
    }

    public String toLabelAfterAbstract() {
        StringBuilder label = new StringBuilder();
        if (isPatternStart()) {
            label.append("##ActionPoint##");
        }
        for (Attribute a : _comparedAttrs) {
            if (label.length() != 0)
                label.append("\n");
            label.append(a.getName()).append(":").append(a.getTag());
        }
        return label.toString();
    }
}
