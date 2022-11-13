package model.graph.node.actions;

import model.graph.edge.ActionEdge;
import model.graph.node.Node;

public class Delete extends ActionNode {
    protected Node _delete;

    protected Delete(Node parent) {
        super(parent);
    }

    public Delete(Node parent, Node delete) {
        super(parent);
        _delete = delete;
        new ActionEdge(delete, this);
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
