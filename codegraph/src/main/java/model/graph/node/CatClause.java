package model.graph.node;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.stmt.BlockStmt;
import model.graph.node.varDecl.SingleVarDecl;
import org.eclipse.jdt.core.dom.ASTNode;

public class CatClause extends Node{
    private SingleVarDecl _exception;
    private BlockStmt _body;

    public CatClause(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setException(SingleVarDecl svd) {
        _exception = svd;
        Edge.createEdge(this, svd, new ASTEdge(this, svd));
    }

    public void setBody(BlockStmt body) {
        _body = body;
        Edge.createEdge(this, body, new ASTEdge(this, body));
    }
}
