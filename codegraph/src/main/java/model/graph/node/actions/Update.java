package model.graph.node.actions;

import com.github.gumtreediff.actions.model.Action;
import model.graph.edge.ASTEdge;
import model.graph.edge.ActionEdge;
import model.graph.node.Node;

public class Update extends ActionNode {
    protected Node _before;
    protected Node _after;

    public Update(Node parent, Action action) {
        super(parent);
        _action = action;
        _before = parent;
        new ActionEdge(parent, this);
    }

    public void setNewNode(Node after) {
        _after = after;
        new ActionEdge(this, after);
    }

    public Node getBefore() {
        return _before;
    }

    public Node getAfter() {
        return _after;
    }

    @Override
    public boolean compare(Node other) {
        if(other != null && other instanceof Update) {
            Update upt = (Update) other;
            return _before.compare(upt._before) && _after.compare(upt._after);
        }
        return false;
    }

    @Override
    public String toLabelString() {
        return "UPDATE";
    }
}
