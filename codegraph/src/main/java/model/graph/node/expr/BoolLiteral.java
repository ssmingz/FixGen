package model.graph.node.expr;

import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class BoolLiteral extends ExprNode {
    private boolean _value;
    private String _type;
    public BoolLiteral(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return null;
    }

    public void setValue(boolean booleanValue) {
        _value = booleanValue;
    }

    public void setType(String typeStr) { _type = typeStr; }
}
