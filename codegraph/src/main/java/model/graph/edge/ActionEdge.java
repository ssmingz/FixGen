package model.graph.edge;

import model.graph.node.Node;

public class ActionEdge extends Edge{
    public ActionEdge(Node source, Node target) {
        super(source, target);
        this.type = EdgeType.ACTION;
    }

    @Override
    public String getLabel() {
        return "Action";
    }
}
