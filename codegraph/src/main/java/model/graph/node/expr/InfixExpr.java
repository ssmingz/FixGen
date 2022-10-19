package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class InfixExpr extends ExprNode {
    private ExprNode _lhs;
    private ExprNode _rhs;
    private InfixOpr _operator;

    public InfixExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return null;
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
}
