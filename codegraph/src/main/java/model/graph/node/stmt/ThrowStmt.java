package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class ThrowStmt extends StmtNode {
    private ExprNode _expression;

    public ThrowStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if(other != null && other instanceof ThrowStmt) {
            ThrowStmt throwStmt = (ThrowStmt) other;
            match = _expression.compare(throwStmt._expression);
        }
        return match;
    }
}
