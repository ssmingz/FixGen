package model.graph.edge;

import model.graph.node.Node;

public class DataEdge extends Edge{
    public DataEdge(Node source, Node target) {
        super(source, target);
        this.type = EdgeType.DATA_DEP;
        this.source.addOutEdge(this);
        this.target.addInEdge(this);
        this.target.addDataDepNode(this.source);
    }

    @Override
    public String getLabel() {
        return "Data Dep";
    }
}
