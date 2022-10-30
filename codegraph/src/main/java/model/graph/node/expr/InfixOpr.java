package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.InfixExpression;

public class InfixOpr extends ExprNode {
    private InfixExpression.Operator _operator;

    public InfixOpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setOperator(InfixExpression.Operator operator) {
        _operator = operator;
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof InfixOpr) {
            InfixOpr infixOperator = (InfixOpr) other;
            return _operator.equals(infixOperator._operator);
        }
        return false;
    }
}
