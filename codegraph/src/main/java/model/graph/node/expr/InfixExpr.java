package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class InfixExpr extends ExprNode {
    private ExprNode _lhs;
    private ExprNode _rhs;
    private InfixOpr _operator;

    public InfixExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setLeftHandSide(ExprNode lhs) {
        _lhs = lhs;
        new ASTEdge(this, lhs);
    }

    public void setRightHandSide(ExprNode rhs) {
        _rhs = rhs;
        new ASTEdge(this, rhs);
    }

    public void setOperatior(InfixOpr infixOpr) {
        _operator = infixOpr;
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof InfixExpr) {
            InfixExpr infixExpr = (InfixExpr) other;
            match = _operator == null ? infixExpr._operator == null : _operator.compare(infixExpr._operator);
            match = match && _lhs.compare(infixExpr._lhs)
                    && _rhs.compare(infixExpr._rhs);
        }
        return match;
    }
}
