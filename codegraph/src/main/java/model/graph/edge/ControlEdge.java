package model.graph.edge;

import model.graph.node.Node;

public class ControlEdge extends Edge{

    public ControlEdge(Node source, Node target) {
        super(source, target);
        this.type = EdgeType.CONTROL_DEP;
        this.source.addOutEdge(this);
        this.target.addInEdge(this);
    }

    @Override
    public String getLabel() {
        return "Control Dep";
    }
}
