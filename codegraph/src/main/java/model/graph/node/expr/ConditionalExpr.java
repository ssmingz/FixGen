package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class ConditionalExpr extends ExprNode {
    private ExprNode _expression;
    private ExprNode _thenExpression;
    private ExprNode _elseExpression;

    public ConditionalExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setCondition(ExprNode condition) {
        _expression = condition;
        Edge.createEdge(this, condition, new ASTEdge(this, condition));
    }

    public void setThenExpr(ExprNode thenExpr) {
        _thenExpression = thenExpr;
        Edge.createEdge(this, thenExpr, new ASTEdge(this, thenExpr));
    }

    public void setElseExpr(ExprNode elseExpr) {
        _elseExpression = elseExpr;
        Edge.createEdge(this, elseExpr, new ASTEdge(this, elseExpr));
    }
}
