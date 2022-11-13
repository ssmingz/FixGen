package model.graph.node.actions;

import model.graph.edge.ActionEdge;
import model.graph.node.Node;

public class Insert extends ActionNode {
    protected Node _insert;

    protected Insert(Node parent) {
        super(parent);
    }

    public Insert(Node parent, Node insert) {
        super(parent);
        _insert = insert;
        new ActionEdge(parent, this);
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
