package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class PrefixExpr extends ExprNode{
    private ExprNode _expression;
    private PrefixOpr _operator;

    public PrefixExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        Edge.createEdge(this, expr, new ASTEdge(this, expr));
    }

    public void setOpr(PrefixOpr postfixOpr) {
        _operator = postfixOpr;
        Edge.createEdge(this, postfixOpr, new ASTEdge(this, postfixOpr));
    }
}
