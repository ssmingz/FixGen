package model.graph.node.actions;

import model.graph.edge.ActionEdge;
import model.graph.node.Node;

public class Move extends ActionNode {
    protected Node _move;

    protected Move(Node parent) {
        super(parent);
    }

    public Move(Node parent, Node move) {
        super(parent);
        _move = move;
        new ActionEdge(move, this);
    }

    public Node getMove() {
        return _move;
    }

    @Override
    public boolean compare(Node other) {
        if(other != null && other instanceof Move) {
            Move mov = (Move) other;
            return _move.compare(mov._move);
        }
        return false;
    }

    @Override
    public String toLabelString() {
        return "MOVE";
    }
}
