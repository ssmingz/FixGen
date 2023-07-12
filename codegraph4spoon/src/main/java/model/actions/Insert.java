package model.actions;

import gumtree.spoon.diff.operations.Operation;
import org.javatuples.Pair;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

public class Insert extends ActionNode {
    protected CtElementImpl _parent;
    protected CtRole _position;

    public Insert(CtElementImpl insert, CtElementImpl parent, CtRole role, Operation op) {
        super(insert, op);
        _parent = parent;
        _position = role;
        new ActionEdge(parent, this);
        new ActionEdge(this, insert);
        // role list from end to root
        // notice it is in statements list if role is statement
        CtElementImpl ptr = insert;
        while (ptr != null) {
            _roleList.add(new Pair<>(ptr.getRoleInParent(), ptr.getClass()));
            ptr = (CtElementImpl) ptr.getParent();
        }
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

    public CtRole insertTo() { return _position; }
}
