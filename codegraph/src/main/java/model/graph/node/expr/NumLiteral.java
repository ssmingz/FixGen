package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class NumLiteral extends ExprNode {
    private String _value;
    private String _type;

    public NumLiteral(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setValue(String value) {
        _value = value;
    }

    public void setType(String typeStr) { _type = typeStr; }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof NumLiteral) {
            NumLiteral numLiteral = (NumLiteral) other;
            match = _value.equals(numLiteral._value)
                    && _type.equals(numLiteral._type);
        }
        return match;
    }
}
