package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class PostfixExpr extends ExprNode {
    private ExprNode _operand;
    private PostfixOpr _operator;

    public PostfixExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _operand = expr;
        new ASTEdge(this, expr);
    }

    public Node getExpression() {
        return _operand;
    }

    public void setOpr(PostfixOpr postfixOpr) {
        _operator = postfixOpr;
        new ASTEdge(this, postfixOpr);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof PostfixExpr) {
            PostfixExpr postfixExpr = (PostfixExpr) other;
            match = _operator.compare(postfixExpr._operator)
                    && _operand.compare(postfixExpr._operand);
        }
        return match;
    }
}
