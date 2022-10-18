package model.graph.edge;

import model.graph.node.Node;

public abstract class Edge {
    public enum EdgeType {AST, CONTROL_DEP};
    public EdgeType type;
    protected Node source;
    protected Node target;
    protected String label;

    public Edge(Node source, Node target) {
        this.source = source;
        this.target = target;
    }

    public abstract String getLabel();

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
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
