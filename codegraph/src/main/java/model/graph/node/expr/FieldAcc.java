package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class FieldAcc extends ExprNode {
    private ExprNode _expression;
    private SimpName _identifier;
    private String _type;

    public FieldAcc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if(other != null && other instanceof FieldAcc) {
            FieldAcc fieldAcc = (FieldAcc) other;
            match = _expression.compare(fieldAcc._expression);
            match = match && _identifier.compare(fieldAcc._identifier);
            match = match && _type.equals(fieldAcc._type);
        }
        return match;
    }
}
