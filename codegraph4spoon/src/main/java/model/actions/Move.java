package model.actions;

import gumtree.spoon.diff.operations.Operation;
import org.javatuples.Pair;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

public class Move extends ActionNode {
    protected CtElementImpl _dstNode;
    protected CtRole _position;
    public List<Pair<CtRole, Class>> _roleList = new ArrayList<>();

    public Move(CtElementImpl move, CtElementImpl dst, CtElementImpl movedInDst, Operation op) {
        super(move, op);
        _dstNode = dst;
        _position = movedInDst.getRoleInParent();
        new ActionEdge(move, this);
        // TODO: add edge to dst.pos
        new ActionEdge(this, dst);
        // role list from end to root
        // notice it is in statements list if role is statement
        CtElementImpl ptr = movedInDst;
        while (ptr != null) {
            Pair pair = new Pair<>(ptr.getRoleInParent(), ptr.getClass());
            _roleList.add(pair);
            ptr = (CtElementImpl) ptr.getParent();
        }
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

    public CtRole moveTo() { return _position; }
}
