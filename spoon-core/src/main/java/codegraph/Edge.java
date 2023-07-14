package codegraph;

import spoon.support.reflect.declaration.CtElementImpl;

import java.io.Serializable;

public abstract class Edge implements Serializable {
    public enum EdgeType {AST, CONTROL_DEP, DATA_DEP, DEF_USE, ACTION};
    public EdgeType type;
    protected CtElementImpl source;
    protected CtElementImpl target;
    public String _graphName = "";
    public int _graphId = -1;

    public Edge(CtElementImpl source, CtElementImpl target) {
        this.source = source;
        this.target = target;
        this.source.addOutEdge(this);
        this.target.addInEdge(this);
    }

    public CtElementImpl getSource() {
        return source;
    }

    public CtElementImpl getTarget() {
        return target;
    }

    public void setTarget(CtElementImpl tar) {
        target = tar;
        target.addInEdge(this);
    }

    public void setSource(CtElementImpl src) {
        source = src;
        source.addOutEdge(this);
    }

    public void delete() {
        this.source._outEdges.remove(this);
        this.target._inEdges.remove(this);
        this.source = null;
        this.target = null;
    }

    public abstract String getLabel();

    @Override
    public String toString() {
        return source + "-" + getLabel() +"->" + target;
    }
}
