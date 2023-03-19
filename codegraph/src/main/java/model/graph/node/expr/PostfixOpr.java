package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.PostfixExpression;

public class PostfixOpr extends ExprNode {
    private PostfixExpression.Operator _postfixOperator;

    public PostfixOpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setOperator(PostfixExpression.Operator operator) {
        _postfixOperator = operator;
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof PostfixOpr) {
            return _postfixOperator.equals(((PostfixOpr) other)._postfixOperator);
        }
        return false;
    }

    @Override
    public String toLabelString() {
        return _postfixOperator.toString();
    }
}
