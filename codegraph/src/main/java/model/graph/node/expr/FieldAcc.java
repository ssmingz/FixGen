package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class FieldAcc extends ExprNode {
    private ExprNode _expression;
    private SimpName _identifier;

    public FieldAcc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        Edge.createEdge(this, expr, new ASTEdge(this, expr));
    }

    public void setIdentifier(SimpName iden) {
        _identifier = iden;
        Edge.createEdge(this, iden, new ASTEdge(this, iden));
    }
}
