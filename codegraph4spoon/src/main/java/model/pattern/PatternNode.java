package model.pattern;

import model.CodeGraph;
import model.CtWrapper;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class PatternNode {
    Set<Attribute> _comparedAttrs = new LinkedHashSet<>();

    private Map<CtWrapper, CodeGraph> _nodeGraphInstances = new LinkedHashMap<>();

    private Set<PatternEdge> _inEdges = new LinkedHashSet<>();
    private Set<PatternEdge> _outEdges = new LinkedHashSet<>();
    private Pattern _pattern = null;

    public PatternNode(CtWrapper aNode, CodeGraph aGraph) {
        _nodeGraphInstances.put(aNode, aGraph);
    }

    public void setComparedAttribute(Attribute attr) {
        if (attr.getValueSet().size() > 0)
            _comparedAttrs.add(attr);
    }

    public Set<Attribute> getComparedAttributes() {
        return _comparedAttrs;
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

    public void addInstance(CtWrapper iNode, CodeGraph iGraph) {
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

    public Map<CtWrapper, CodeGraph> getInstance() {
        return _nodeGraphInstances;
    }

    public String toLabel() {
        StringBuilder label = new StringBuilder();
        for (Map.Entry<CtWrapper, CodeGraph> entry : _nodeGraphInstances.entrySet()) {
            if (label.length() != 0)
                label.append("\n");
            int srcLine = -1;
            CtElementImpl ctElement = entry.getKey().getCtElementImpl();
            if (ctElement.getPosition().isValidPosition()) {
                srcLine = ctElement.getPosition().getLine();
            }
            String ins = entry.getValue().getGraphName() + "#" + srcLine + ":" + ctElement.prettyprint();
            label.append(ins);
        }
        return label.toString();
    }

    public void setPattern(Pattern pattern) {
        _pattern = pattern;
    }
}
