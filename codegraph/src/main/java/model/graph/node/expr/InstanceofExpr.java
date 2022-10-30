package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class InstanceofExpr extends ExprNode {
    private ExprNode _expression;
    private String _instanceType;

    public InstanceofExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setInstanceType(String instType) {
        _instanceType = instType;
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof InstanceofExpr) {
            InstanceofExpr instanceofExpr = (InstanceofExpr) other;
            match = _instanceType.equals(instanceofExpr._instanceType)
                    && _expression.compare(instanceofExpr._expression);
        }
        return match;
    }
}
