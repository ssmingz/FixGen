package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class DoStmt extends StmtNode {
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if(other != null && other instanceof DoStmt) {
            DoStmt doStmt = (DoStmt) other;
            match = _expression.compare(doStmt._expression) && _stmt.compare(doStmt._stmt);
        }
        return match;
    }
}
