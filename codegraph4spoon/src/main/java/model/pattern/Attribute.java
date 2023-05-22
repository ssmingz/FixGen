package model.pattern;

import codegraph.CtVirtualElement;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.ActionNode;
import model.graph.node.Node;
import model.graph.node.PatchNode;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.SimpName;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.CtExpressionImpl;
import spoon.support.reflect.code.CtVariableAccessImpl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Attribute {
    private String _name;
    private Map<String, Integer> _numByValues = new LinkedHashMap<>();
    private Map<CodeGraph, String> _valueByCG = new LinkedHashMap<>();
    private String _tag = ""; // choose which value to use
    private boolean isAbstract = false;

    public Attribute(String name) {
        _name = name;
    }

    public void setTag(String v) {
        _tag = v;
    }

    public String getName() {
        return _name;
    }

    public Set<String> getValueSet() {
        return _numByValues.keySet();
    }

    public boolean isAbstract() { return isAbstract; }

    public void setAbstract(boolean abs) { isAbstract = abs; }

    public void addValue(String v, CodeGraph g) {
        _valueByCG.put(g, v);
        if (_numByValues.containsKey(v)) {
            int s = _numByValues.get(v) + 1;
            _numByValues.put(v, s);
        } else {
            _numByValues.put(v, 1);
        }
    }

    public String getValueByCG(CodeGraph g) {
        return _valueByCG.getOrDefault(g, "MISSING");
    }

    public String getTag() {
        return _tag;
    }

    public int getSupport(String aValue) {
        return _numByValues.getOrDefault(aValue, 0);
    }

    public Map<String, Integer> sort() {
        return  _numByValues
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
    }

    public void computeLocationInParent(CtWrapper n, CodeGraph g) {
        String role = null;
        if (n.getCtElementImpl() instanceof ActionNode) {
            role = "ACTION";
        } else if (n.isVirtual()) {
            role = ((CtVirtualElement) n.getCtElementImpl()).getLocationInParent();
        } else {
            role = n.getCtElementImpl().getRoleInParent().name();
        }
        addValue(role, g);
    }

    public void computeNodeType(CtWrapper n, CodeGraph g) {
        String type = n.getCtElementImpl().getClass().getSimpleName();
        addValue(type, g);
    }

    public void computeValue(CtWrapper n, CodeGraph g) {
        String value = n.toLabelString();
        addValue(value, g);
    }

}
