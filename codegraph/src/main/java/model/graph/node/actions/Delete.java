package model.graph.node.actions;

import com.github.gumtreediff.actions.model.Action;
import model.graph.edge.ActionEdge;
import model.graph.node.Node;

public class Delete extends ActionNode {
    protected Node _delete;

    public Delete(Node parent, Action action) {
        super(parent);
        _action = action;
        _delete = parent;
        new ActionEdge(parent, this);
    }

    public Node getDelete() {
        return _delete;
    }

    @Override
    public String toLabelString() {
        return "DELETE";
    }

    @Override
    public boolean compare(Node other) {
        if(other != null && other instanceof Delete) {
            Delete del = (Delete) other;
            return _delete.compare(del._delete);
        }
        return false;
    }
}
