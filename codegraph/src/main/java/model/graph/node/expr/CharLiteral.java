package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public class CharLiteral extends ExprNode {
    private char _value;

    public CharLiteral(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setValue(char charValue) {
        _value = charValue;
    }
}
