package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class ExprStmt extends StmtNode {
    private ExprNode _expression;

    public ExprStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof ExprStmt) {
            ExprStmt expressionStmt = (ExprStmt) other;
            return _expression.compare(expressionStmt._expression);
        }
        return false;
    }
}
