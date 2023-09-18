package codegraph;

import codegraph.visitor.ReplaceNameVisitor;
import codegraph.visitor.TokenVisitor;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtBiScannerDefault;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.code.CtCodeElementImpl;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.visitor.HashcodeVisitor;

public class CtVirtualElement extends CtCodeElementImpl {
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
        if (visitor instanceof TokenVisitor)
            ((TokenVisitor) visitor).visitCtVirtual(this);
        else if (visitor instanceof ReplaceNameVisitor)
            ((ReplaceNameVisitor) visitor).visitCtVirtual(this);
        else
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

    @Override
    public int hashCode() {
        return label.hashCode();
    }
}
