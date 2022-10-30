package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;

public class PostfixOpr extends ExprNode {
    private PostfixExpression.Operator _operator;

    public PostfixOpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setOperator(PostfixExpression.Operator operator) {
        _operator = operator;
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof PostfixOpr) {
            return _operator.equals(((PostfixOpr) other)._operator);
        }
        return false;
    }
}
