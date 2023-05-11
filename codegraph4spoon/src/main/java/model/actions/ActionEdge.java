package model.actions;

import codegraph.Edge;
import spoon.support.reflect.declaration.CtElementImpl;

public class ActionEdge extends Edge {
    public ActionEdge(CtElementImpl source, CtElementImpl target) {
        super(source, target);
        this.type = EdgeType.ACTION;
    }

    @Override
    public String getLabel() {
        return "Action";
    }
}

