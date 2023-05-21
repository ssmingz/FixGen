package model.actions;

import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class Insert extends ActionNode {
    protected CtElementImpl _parent;
    protected int _position;

    public Insert(CtElementImpl insert, CtElementImpl parent, int pos, Operation op) {
        super(insert, op);
        _parent = parent;
        _position = pos;
        new ActionEdge(parent, this);
        new ActionEdge(this, insert);
    }

    public CtElementImpl getParent() { return _parent; }

    @Override
    public ActionType getType() {
        return ActionType.INSERT;
    }

    @Override
    public void accept(CtVisitor visitor) {
        // do not handle
    }

    @Override
    public String toString() {
        return "INSERT";
    }

    @Override
    public String prettyprint() {
        return "INSERT";
    }
}
