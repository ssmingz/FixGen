package model.graph.node;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.stmt.BlockStmt;
import model.graph.node.varDecl.SingleVarDecl;
import org.eclipse.jdt.core.dom.ASTNode;

public class CatClause extends Node {
    private SingleVarDecl _exception;
    private BlockStmt _body;

    public CatClause(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        StringBuffer buf = new StringBuffer();
        buf.append("catch(");
        buf.append(_exception.toLabelString());
        buf.append(")");
        buf.append(_body.toLabelString());
        return buf.toString();
    }

    public void setException(SingleVarDecl svd) {
        _exception = svd;
        new ASTEdge(this, svd);
    }

    public void setBody(BlockStmt body) {
        _body = body;
        new ASTEdge(this, body);
    }

    @Override
    public boolean compare(Node other) {
        if(other != null && other instanceof CatClause) {
            CatClause catClause = (CatClause) other;
            return _exception.compare(catClause._exception) && _body.compare(catClause._body);
        }
        return false;
    }
}
