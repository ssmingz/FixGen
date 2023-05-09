package codegraph;

import spoon.support.reflect.declaration.CtElementImpl;

public class ControlEdge extends Edge {
    public ControlEdge(CtElementImpl source, CtElementImpl target) {
        super(source, target);
        this.type = Edge.EdgeType.CONTROL_DEP;
    }

    @Override
    public String getLabel() {
        return "Control Dep";
    }
}
