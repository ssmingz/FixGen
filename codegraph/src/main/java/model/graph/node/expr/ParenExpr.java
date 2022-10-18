package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class ParenExpr extends ExprNode {
    private ExprNode _expression;

    public ParenExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expression) {
        _expression = expression;
        new ASTEdge(this, expression);
    }
}
