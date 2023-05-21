package model.actions;

import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class Move extends ActionNode {
    protected CtElementImpl _dstNode;
    protected int _position;

    public Move(CtElementImpl move, CtElementImpl dst, int pos, Operation op) {
        super(move, op);
        _dstNode = dst;
        _position = pos;
        new ActionEdge(move, this);
        // TODO: add edge to dst.pos
        new ActionEdge(this, dst);
    }

    public CtElementImpl getDst() {
        return _dstNode;
    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE;
    }

    @Override
    public void accept(CtVisitor visitor) {
        // do not handle
    }

    @Override
    public String toString() {
        return "MOVE";
    }

    @Override
    public String prettyprint() {
        return "MOVE";
    }
}
