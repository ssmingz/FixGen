package model.graph.node.actions;

import com.github.gumtreediff.actions.model.Action;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public abstract class ActionNode extends Node {
    protected Action _action;

    protected ActionNode(Node parent) {
        super(null, null, -1, -1);
        _parent = parent;
    }

    public void setAction(Action action) {
        _action = action;
    }

    public Node getParent() {
        return _parent;
    }
}
