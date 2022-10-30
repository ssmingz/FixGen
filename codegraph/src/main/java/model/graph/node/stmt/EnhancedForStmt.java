package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import model.graph.node.varDecl.SingleVarDecl;
import org.eclipse.jdt.core.dom.ASTNode;

public class EnhancedForStmt extends StmtNode {
    private SingleVarDecl _singleVariableDeclaration;
    private ExprNode _expression;
    private StmtNode _statement;

    public EnhancedForStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setSVD(SingleVarDecl svd) {
        _singleVariableDeclaration = svd;
        new ASTEdge(this, svd);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setBody(StmtNode stmt) {
        _statement = stmt;
        new ASTEdge(this, stmt);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof EnhancedForStmt) {
            EnhancedForStmt enhancedForStmt = (EnhancedForStmt) other;
            match = _singleVariableDeclaration.compare(enhancedForStmt._singleVariableDeclaration)
                            && _expression.compare(enhancedForStmt._expression)
                            && _statement.compare(enhancedForStmt._statement);
        }
        return match;
    }
}
