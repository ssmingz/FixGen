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

    @Override
    public String toLabelString() {
        return _astNode.toString();
    }

    public void setCondition(ExprNode condition) {
        _expression = condition;
        new ASTEdge(this, condition);
    }

    public void setThenExpr(ExprNode thenExpr) {
        _thenExpression = thenExpr;
        new ASTEdge(this, thenExpr);
    }

    public void setElseExpr(ExprNode elseExpr) {
        _elseExpression = elseExpr;
        new ASTEdge(this, elseExpr);
    }
}
