package model.graph.node;

import model.graph.node.actions.Insert;
import spoon.reflect.declaration.CtElement;

public class PatchNode extends Node {
    private CtElement _spoonNode;

    public PatchNode(CtElement ctNode, Node parent) {
        super(null, null, -1, -1, parent);
        _spoonNode = ctNode;
    }

    public CtElement getSpoonNode() {
        return _spoonNode;
    }

    @Override
    public boolean compare(Node other) {
        if(other instanceof PatchNode) {
            PatchNode pn = (PatchNode) other;
            return _spoonNode.equals(pn._spoonNode);
        }
        return false;
    }

    @Override
    public String toLabelString() {
        if (_spoonNode != null) {
            return _spoonNode.toString();
        }
        return "";
    }
}
