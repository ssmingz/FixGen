package model.graph.node.varDecl;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class VarDeclFrag extends Node {
    private TypeNode _type;
    private String _typeStr;

    public VarDeclFrag(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setType(TypeNode type, String typeStr) {
        _type = type;
        _typeStr = typeStr;
        Edge.createEdge(this, type, new ASTEdge(this, type));

    }
}
