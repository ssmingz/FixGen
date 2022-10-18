package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class PostfixExpr extends ExprNode {
    private ExprNode _expression;
    private PostfixOpr _operator;

    public PostfixExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setOpr(PostfixOpr postfixOpr) {
        _operator = postfixOpr;
        new ASTEdge(this, postfixOpr);
    }
}
