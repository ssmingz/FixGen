package model.graph.node.actions;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public abstract class ActionNode extends Node {
    private Node _parent;

    protected ActionNode(Node parent) {
        super(null, null, -1, -1);
        _parent = parent;
    }

    public Node getParent() {
        return _parent;
    }
}
