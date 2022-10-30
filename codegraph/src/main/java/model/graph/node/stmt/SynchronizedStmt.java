package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class SynchronizedStmt extends StmtNode {
    private ExprNode _expression;
    private BlockStmt _body;

    public SynchronizedStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setBody(BlockStmt body) {
        _body = body;
        new ASTEdge(this, body);
    }

    public ExprNode getExpression() {
        return _expression;
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof SynchronizedStmt) {
            SynchronizedStmt synchronizedStmt = (SynchronizedStmt) other;
            match = _expression.compare(synchronizedStmt._expression) && _body.compare(synchronizedStmt._body);
        }
        return match;
    }
}
