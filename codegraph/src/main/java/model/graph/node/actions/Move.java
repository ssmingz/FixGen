package model.graph.node.actions;

import com.github.gumtreediff.actions.model.Action;
import model.graph.edge.ActionEdge;
import model.graph.node.Node;

public class Move extends ActionNode {
    protected Node _move;

    public Move(Node parent, Action action) {
        super(parent);
        _action = action;
        new ActionEdge(parent, this);
    }

    public void setNode(Node move) {
        _move = move;
        new ActionEdge(this, move);
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

    @Override
    public ActionType getType() {
        return ActionType.MOVE;
    }
}
