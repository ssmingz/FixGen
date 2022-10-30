package model.graph.node.expr;

import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class BoolLiteral extends ExprNode {
    private boolean _value;
    private String _type;
    public BoolLiteral(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return String.valueOf(_value);
    }

    public void setValue(boolean booleanValue) {
        _value = booleanValue;
    }

    public void setType(String typeStr) { _type = typeStr; }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof BoolLiteral) {
            return (_value == ((BoolLiteral) other)._value) && (_type == ((BoolLiteral) other)._type);
        }
        return false;
    }
}
