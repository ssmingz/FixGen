package model.graph.node.actions;

import model.graph.edge.ASTEdge;
import model.graph.edge.ActionEdge;
import model.graph.node.Node;

public class Update extends ActionNode {
    protected Node _before;
    protected Node _after;

    protected Update(Node parent) {
        super(parent);
    }

    public Update(Node before, Node after) {
        super(before);
        _before = before;
        _after = after;
        new ActionEdge(before, this);
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
