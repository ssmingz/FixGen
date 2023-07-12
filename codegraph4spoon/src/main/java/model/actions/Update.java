package model.actions;

import gumtree.spoon.diff.operations.Operation;
import org.javatuples.Pair;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class Update extends ActionNode {
    protected CtElementImpl _dstNode;

    public Update(CtElementImpl srcNode, CtElementImpl dstNode, Operation op) {
        super(srcNode, op);
        _dstNode = dstNode;
        new ActionEdge(srcNode, this);  // in src graph
        new ActionEdge(this, dstNode);  // in dst graph
        // role list from end to root
        // notice it is in statements list if role is statement
        CtElementImpl ptr = dstNode;
        while (ptr != null) {
            _roleList.add(new Pair<>(ptr.getRoleInParent(), ptr.getClass()));
            ptr = (CtElementImpl) ptr.getParent();
        }
    }

    public CtElementImpl getDstNode() {
        return _dstNode;
    }

    @Override
    public ActionType getType() {
        return ActionType.UPDATE;
    }

    @Override
    public void accept(CtVisitor visitor) {
        // do not handle
    }

    @Override
    public String toString() {
        return "UPDATE";
    }

    @Override
    public String prettyprint() {
        return "UPDATE";
    }
}
