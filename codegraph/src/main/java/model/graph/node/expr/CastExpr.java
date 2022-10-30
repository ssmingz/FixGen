package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class CastExpr extends ExprNode {
    private String _castType;
    private ExprNode _expression;

    public CastExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return _astNode.toString();
    }

    public void setCastType(String typeStr) {
        _castType = typeStr;
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof CastExpr) {
            CastExpr castExpr = (CastExpr) other;
            match = _castType.equals(castExpr._castType);
            match = match && _expression.compare(castExpr._expression);
        }
        return match;
    }
}
