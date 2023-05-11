package model.actions;

import gumtree.spoon.diff.operations.Operation;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class Delete extends ActionNode {

    public Delete(CtElementImpl del, Operation op) {
        super(del, op);
        new ActionEdge(del, this);
    }

    @Override
    public ActionType getType() {
        return ActionType.DELETE;
    }

    @Override
    public void accept(CtVisitor visitor) {
        // do not handle
    }

    @Override
    public String toString() {
        return "DELETE";
    }

    @Override
    public String prettyprint() {
        return "DELETE";
    }
}
