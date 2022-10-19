package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.PrefixExpression;

public class PrefixOpr extends ExprNode{
    private PrefixExpression.Operator _operator;

    public PrefixOpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return _operator.toString();
    }

    public void setOperator(PrefixExpression.Operator operator) {
        _operator = operator;
    }

}
