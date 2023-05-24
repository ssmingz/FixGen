package codegraph;

import spoon.support.reflect.declaration.CtElementImpl;

public class ASTEdge extends Edge{
    public ASTEdge(CtElementImpl source, CtElementImpl target) {
        super(source, target);
        this.type = EdgeType.AST;
        this.target.setParent(this.source);
    }

    @Override
    public String getLabel() {
        String role = this.target instanceof CtVirtualElement? ((CtVirtualElement) this.target).getLocationInParent()
                : this.target.getRoleInParent().name();
        return "AST";
//        return "AST:"+role;
    }

}
