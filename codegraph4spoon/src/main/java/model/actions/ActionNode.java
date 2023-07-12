package model.actions;

import gumtree.spoon.diff.operations.Operation;
import org.javatuples.Pair;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.List;

public abstract class ActionNode extends CtElementImpl {
    public enum ActionType {UPDATE, INSERT, DELETE, MOVE};
    protected transient Operation _action;
    protected CtElementImpl _srcNode;
    public List<Pair<CtRole, Class>> _roleList = new ArrayList<>();

    public ActionNode(CtElementImpl src, Operation op) {
        _action = op;
        _srcNode = src;
        parent = _srcNode;
        setFactory(src.getFactory());
        _actionRelated = true;
    }

    public Operation getAction() {
        return _action;
    }

    public CtElementImpl getSrcNode() { return _srcNode; }

    public abstract ActionType getType();

    @Override
    public SourcePosition getPosition() {
        return _srcNode.getPosition();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
