package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class InstanceofExpr extends ExprNode {
    private ExprNode _expression;
    private TypeNode _instanceType;

    public InstanceofExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setInstanceType(TypeNode instType) {
        _instanceType = instType;
        new ASTEdge(this, instType);
    }
}
