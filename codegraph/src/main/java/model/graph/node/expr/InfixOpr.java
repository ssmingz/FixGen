package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.InfixExpression;

public class InfixOpr extends ExprNode{
    private InfixExpression.Operator _operator;

    public InfixOpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setOperator(InfixExpression.Operator operator) {
        _operator = operator;
    }

}
