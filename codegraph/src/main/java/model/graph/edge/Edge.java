package model.graph.edge;

import model.graph.node.Node;

import java.io.Serializable;

public abstract class Edge {
    public enum EdgeType {AST, CONTROL_DEP, DATA_DEP, DEF_USE, ACTION};
    public EdgeType type;
    protected Node source;
    protected Node target;
    protected String label;

    public Edge(Node source, Node target) {
        this.source = source;
        this.target = target;
        this.source.addOutEdge(this);
        this.target.addInEdge(this);
    }

    public abstract String getLabel();

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node tar) {
        target = tar;
        target.addInEdge(this);
    }

    public void setSource(Node src) {
        source = src;
        source.addOutEdge(this);
    }

    @Override
    public String toString() {
        return source + "-" + getLabel() +"->" + target;
    }

    public void delete() {
        this.source.outEdges.remove(this);
        this.target.inEdges.remove(this);
        this.source = null;
        this.target = null;
    }
}
