package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class PrefixExpr extends ExprNode {
    private ExprNode _expression;
    private PrefixOpr _operator;

    public PrefixExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public Node getExpression() {
        return _expression;
    }

    public void setOpr(PrefixOpr postfixOpr) {
        _operator = postfixOpr;
        new ASTEdge(this, postfixOpr);
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof PrefixExpr) {
            PrefixExpr prefixExpr = (PrefixExpr) other;
            return _operator.compare(prefixExpr._operator)
                    && _expression.compare(prefixExpr._expression);
        }
        return false;
    }
}
