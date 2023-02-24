package model.graph.node.actions;

import com.github.gumtreediff.actions.model.Action;
import model.graph.edge.ActionEdge;
import model.graph.node.Node;

public class Insert extends ActionNode {
    protected Node _insert;

    public Insert(Node parent, Action action) {
        super(parent);
        _action = action;
        new ActionEdge(parent, this);
    }

    public void setNode(Node insert) {
        _insert = insert;
        new ActionEdge(this, insert);
    }

    public Node getInsert() {
        return _insert;
    }

    @Override
    public boolean compare(Node other) {
        if(other != null && other instanceof Insert) {
            Insert ins = (Insert) other;
            return _insert.compare(ins._insert);
        }
        return false;
    }

    @Override
    public String toLabelString() {
        return "INSERT";
    }
}
