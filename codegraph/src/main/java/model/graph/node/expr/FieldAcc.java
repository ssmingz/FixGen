package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class FieldAcc extends ExprNode {
    private ExprNode _expression;
    private SimpName _identifier;
    private String _type;

    public FieldAcc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return _identifier.getName();
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setIdentifier(SimpName iden) {
        _identifier = iden;
        new ASTEdge(this, iden);
    }

    public void setType(String typeStr) { _type = typeStr; }
}
