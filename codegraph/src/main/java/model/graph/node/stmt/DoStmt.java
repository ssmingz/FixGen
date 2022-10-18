package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class DoStmt extends StmtNode{
    private StmtNode _stmt;
    private ExprNode _expression;

    public DoStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setBody(StmtNode body) {
        _stmt = body;
        new ASTEdge(this, body);
    }
}
