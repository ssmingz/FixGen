package model.graph.node.expr;

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
}
