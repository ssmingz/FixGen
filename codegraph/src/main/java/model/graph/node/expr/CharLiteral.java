package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class CharLiteral extends ExprNode {
    private char _value;
    private String _type;

    public CharLiteral(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return String.valueOf(_value);
    }

    public void setValue(char charValue) {
        _value = charValue;
    }
    public void setType(String typeStr) { _type = typeStr; }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof CharLiteral) {
            CharLiteral charLiteral = (CharLiteral) other;
            match = (_value == charLiteral._value);
            match = match && (_type.equals(charLiteral._type));
        }
        return match;
    }
}
