package model.pattern;

import codegraph.CtVirtualElement;
import model.CodeGraph;
import model.CtWrapper;
import org.javatuples.Pair;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.declaration.CtElementImpl;
import utils.ObjectUtil;

import java.io.Serializable;
import java.util.*;

public class PatternNode implements Serializable {
    List<Attribute> _comparedAttrs = new ArrayList<>();

    private Map<CtWrapper, CodeGraph> _nodeGraphInstances = new LinkedHashMap<>();

    private Set<PatternEdge> _inEdges = new LinkedHashSet<>();
    private Set<PatternEdge> _outEdges = new LinkedHashSet<>();
    private Pattern _pattern = null;
    private boolean _actionRelated;
    private boolean isAbstract = false;
    /**
     * pattern node features, e.g. for repairing
     */
    public Attribute position = new Attribute("position");
    public Attribute listIndex =  new Attribute("listIndex");  // list index if is list type, or else -1
    public Attribute listSize = new Attribute("listSize");  // list length if is list type, or else -1
    public Attribute implicit = new Attribute("implicit");  // field of CtElement, to avoid complicated this.xxx

    public PatternNode(CtWrapper aNode, CodeGraph aGraph) {
        _nodeGraphInstances.put(aNode, aGraph);
        _actionRelated = aNode.getCtElementImpl().isActionRelated();
    }

    public void setComparedAttribute(Attribute attr) {
        if (attr.getValueSet().size() > 0)
            _comparedAttrs.add(attr);
    }

    public void replaceAttribute(Attribute old, Attribute newly) {
        int oldIndex = _comparedAttrs.indexOf(old);
        _comparedAttrs.remove(old);
        _comparedAttrs.add(oldIndex, newly);
    }

    public void removeAttribute(Attribute attr) {
        _comparedAttrs.remove(attr);
    }

    public List<Attribute> getComparedAttributes() {
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
            CodeGraph cg = entry.getValue();
            String ins = String.format("%s:%d#L%d:%s", cg.getGraphName(), cg.getElementId(entry.getKey()), srcLine, ObjectUtil.printNode(ctElement));
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
            String tag = String.valueOf(a.getTag());
            label.append(a.getName()).append(":").append(tag.length()>60?"TOO LONG TO BE PRINTED":tag);
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

    public boolean isVirtual() {
        return getAttribute("nodeType")!=null && getAttribute("nodeType").getTag().equals(CtVirtualElement.class);
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

    public boolean isAction() {
        return getAttribute("locationInParent")!=null && getAttribute("locationInParent").getTag().equals("ACTION");
    }

    public boolean isAbstract() { return isAbstract; }

    public void setAbstract(boolean abs) { isAbstract = abs; }
}
