package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class AssertStmt extends StmtNode{

    private ExprNode _expression;
    private ExprNode _message;
    public AssertStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        Edge.createEdge(this, expr, new ASTEdge(this, expr));
    }

    public void setMessage(ExprNode message) {
        _message = message;
        Edge.createEdge(this, message, new ASTEdge(this, message));
    }
}
