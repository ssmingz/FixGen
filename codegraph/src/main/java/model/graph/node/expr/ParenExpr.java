package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof ParenExpr) {
            ParenExpr parenthesiszedExpr = (ParenExpr) other;
            match = _expression.compare(parenthesiszedExpr._expression);
        }
        return match;
    }
}
