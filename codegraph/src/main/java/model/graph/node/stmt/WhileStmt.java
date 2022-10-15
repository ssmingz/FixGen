package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class WhileStmt extends StmtNode {
    private ExprNode _expression;
    private StmtNode _body;

    public WhileStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        Edge.createEdge(this, expr, new ASTEdge(this, expr));
    }

    public void setBody(StmtNode body) {
        _body = body;
        Edge.createEdge(this, body, new ASTEdge(this, body));
    }
}
