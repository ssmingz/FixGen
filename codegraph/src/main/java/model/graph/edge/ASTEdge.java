package model.graph.edge;

import model.graph.node.Node;

public class ASTEdge extends Edge{

    public ASTEdge(Node source, Node target) {
        super(source, target);
        this.type = EdgeType.AST;
        this.target.setParent(this.source);
    }

    @Override
    public String getLabel() {
        return "AST";
    }

}
