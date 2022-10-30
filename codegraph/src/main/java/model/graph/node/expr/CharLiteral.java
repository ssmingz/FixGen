package model.graph.node.expr;

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
}
