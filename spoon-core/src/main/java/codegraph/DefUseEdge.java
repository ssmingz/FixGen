package codegraph;

import spoon.support.reflect.declaration.CtElementImpl;

public class DefUseEdge extends Edge {
    public DefUseEdge(CtElementImpl source, CtElementImpl target) {
        super(source, target);
        this.type = Edge.EdgeType.DEF_USE;
    }

    @Override
    public String getLabel() {
        return "Define-Use";
    }
}
