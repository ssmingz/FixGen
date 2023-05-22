package codegraph;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtVirtualElement extends CtElementImpl {
    private static final long serialVersionUID = 1L;
    private CtElementImpl parent;
    private String label;
    private String roleInParent;

    public CtVirtualElement(CtElementImpl p, String l, String role) {
        parent = p;
        label = l;
        roleInParent = role;
        setFactory(p.getFactory());
        // add ASTEdge
        new ASTEdge(p, this);
    }

    public String getLabel() {
        return label;
    }

    public CtElementImpl getCtParent() {
        return parent;
    }

    public String getLocationInParent() {
        return roleInParent;
    }

    @Override
    public void accept(CtVisitor visitor) {
        // TODO: how to handle
        parent.accept(visitor);
    }

    @Override
    public CtElementImpl getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public String prettyprint() {
        return label;
    }

    @Override
    public SourcePosition getPosition() {
        return parent.getPosition();
    }
}
