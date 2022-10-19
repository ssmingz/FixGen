package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;

public class PostfixOpr extends ExprNode{
    private PostfixExpression.Operator _operator;

    public PostfixOpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return _operator.toString();
    }

    public void setOperator(PostfixExpression.Operator operator) {
        _operator = operator;
    }

}
