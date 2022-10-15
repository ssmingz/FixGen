package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class IfStmt extends StmtNode{
    private ExprNode _expression;
    private StmtNode _then;
    private StmtNode _else;

    public IfStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        Edge.createEdge(this, expr, new ASTEdge(this, expr));
    }

    public void setThen(StmtNode then) {
        _then = then;
        Edge.createEdge(this, then, new ASTEdge(this, then));
    }

    public void setElse(StmtNode els) {
        _else = els;
        Edge.createEdge(this, els, new ASTEdge(this, els));
    }
}
