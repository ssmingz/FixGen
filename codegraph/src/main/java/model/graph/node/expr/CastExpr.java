package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class CastExpr extends ExprNode {
    private TypeNode _castType;
    private ExprNode _expression;

    public CastExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setCastType(TypeNode typeNode) {
        _castType = typeNode;
        new ASTEdge(this, typeNode);
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }
}
