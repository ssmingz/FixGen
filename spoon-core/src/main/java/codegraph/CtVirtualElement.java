package codegraph;

import spoon.reflect.cu.SourcePosition;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtVirtualElement extends CtElementImpl {
    private static final long serialVersionUID = 1L;
    private CtElementImpl parent;
    private String label;

    public CtVirtualElement(CtElementImpl p, String l) {
        parent = p;
        label = l;
        // add ASTEdge
        new ASTEdge(p, this);
    }

    public String getLabel() {
        return label;
    }

    public CtElementImpl getCtParent() {
        return parent;
    }

    @Override
    public void accept(CtVisitor visitor) {
        // do not handle
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
