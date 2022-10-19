package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public class NumLiteral extends ExprNode {
    private String _value;

    public NumLiteral(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return null;
    }

    public void setValue(String value) {
        _value = value;
    }
}
