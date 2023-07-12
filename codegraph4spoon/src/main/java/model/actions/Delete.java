package model.actions;

import gumtree.spoon.diff.operations.Operation;
import org.javatuples.Pair;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class Delete extends ActionNode {

    public Delete(CtElementImpl del, Operation op) {
        super(del, op);
        new ActionEdge(del, this);
        // role list from end to root
        // notice it is in statements list if role is statement
        CtElementImpl ptr = del;
        while (ptr != null) {
            _roleList.add(new Pair<>(ptr.getRoleInParent(), ptr.getClass()));
            ptr = (CtElementImpl) ptr.getParent();
        }
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
