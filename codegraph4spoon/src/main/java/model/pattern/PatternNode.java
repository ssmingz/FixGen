package model.pattern;

import model.CodeGraph;
import model.CtWrapper;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.reference.CtLocalVariableReferenceImpl;
import utils.ObjectUtil;

import java.util.*;

public class PatternNode {
    Set<Attribute> _comparedAttrs = new LinkedHashSet<>();

    private Map<CtWrapper, CodeGraph> _nodeGraphInstances = new LinkedHashMap<>();

    private Set<PatternEdge> _inEdges = new LinkedHashSet<>();
    private Set<PatternEdge> _outEdges = new LinkedHashSet<>();
    private Pattern _pattern = null;
    private boolean _actionRelated;

    public PatternNode(CtWrapper aNode, CodeGraph aGraph) {
        _nodeGraphInstances.put(aNode, aGraph);
        _actionRelated = aNode.getCtElementImpl().isActionRelated();
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
            String ins = entry.getValue().getGraphName() + "#" + srcLine + ":" + ObjectUtil.printNode(ctElement);
            label.append(ins);
        }
        return label.toString();
    }

    public void setPattern(Pattern pattern) {
        _pattern = pattern;
    }

    public String toLabelAfterAbstract() {
        StringBuilder label = new StringBuilder();
        if (isPatternStart()) {
            label.append("##ActionPoint##");
        }
        for (Attribute a : _comparedAttrs) {
            if (a.isAbstract())
                continue;
            if (label.length() != 0)
                label.append("\n");
            label.append(a.getName()).append(":").append(a.getTag());
        }
        return label.toString();
    }

    public boolean isPatternStart() {
        if (_pattern != null) {
            return _pattern.getStart() == this;
        }
        return false;
    }

    public boolean isActionRelated() {
        return _actionRelated;
    }

    public boolean hasInEdge(PatternNode src, PatternEdge.EdgeType edgeType) {
        for (PatternEdge ie : _inEdges) {
            if (ie.getSource().equals(src) && ie.type == edgeType)
                return true;
        }
        return false;
    }

    public boolean hasOutEdge(PatternNode tar, PatternEdge.EdgeType edgeType) {
        for (PatternEdge oe : _outEdges) {
            if (oe.getTarget().equals(tar) && oe.type == edgeType)
                return true;
        }
        return false;
    }

    public List<PatternNode> getAllASTChildren(List<PatternNode> traversed) {
        List<PatternNode> result = new ArrayList<>();
        for (PatternEdge oe : _outEdges) {
            if (oe.type == PatternEdge.EdgeType.AST && !traversed.contains(oe.getTarget())) {
                result.add(oe.getTarget());
                traversed.add(oe.getTarget());
                result.addAll(oe.getTarget().getAllASTChildren(traversed));
            }
        }
        return result;
    }
}
