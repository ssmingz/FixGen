package model.graph.edge;

import model.graph.node.Node;

public class DefUseEdge extends DataEdge{
    public DefUseEdge(Node source, Node target) {
        super(source, target);
        this.type = EdgeType.DEF_USE;
    }

    @Override
    public String getLabel() {
        return "Define-Use";
    }
}
