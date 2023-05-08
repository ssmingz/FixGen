package model.graph.edge;

import model.graph.node.Node;

public class ControlEdge extends Edge {
    public ControlEdge(Node source, Node target) {
        super(source, target);
        this.type = Edge.EdgeType.CONTROL_DEP;
    }

    @Override
    public String getLabel() {
        return "Control Dep";
    }
}
