package model.graph.edge;

import model.graph.node.Node;

public class ASTEdge extends Edge{
    public ASTEdge(Node source, Node target) {
        super(source, target);
        this.type = Type.AST;
        this.source.addOutEdge(this);
        this.target.addInEdge(this);
    }

    @Override
    public String getLabel() {
        return "ASTEdge";
    }
}
