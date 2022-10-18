package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class SwitchStmt extends StmtNode {
    private ExprNode _expression;
    private List<StmtNode> _statements;

    public SwitchStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setStatements(List<StmtNode> stmts) {
        _statements = stmts;
        for (StmtNode obj : stmts) {
            new ASTEdge(this, obj);
        }
    }
}
