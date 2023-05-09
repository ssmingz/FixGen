package codegraph;

import spoon.support.reflect.declaration.CtElementImpl;

public class DataEdge extends Edge{
    public DataEdge(CtElementImpl source, CtElementImpl target) {
        super(source, target);
        this.type = Edge.EdgeType.DATA_DEP;
        this.target.addDataDepNode(this.source);
    }

    @Override
    public String getLabel() {
        return "Data Dep";
    }
}
