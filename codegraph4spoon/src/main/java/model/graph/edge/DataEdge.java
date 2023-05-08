package model.graph.edge;

import model.graph.node.Node;

public class DataEdge extends Edge{
    public DataEdge(Node source, Node target) {
        super(source, target);
        this.type = Edge.EdgeType.DATA_DEP;
        this.target.addDataDepNode(this.source);
    }

    @Override
    public String getLabel() {
        return "Data Dep";
    }
}
