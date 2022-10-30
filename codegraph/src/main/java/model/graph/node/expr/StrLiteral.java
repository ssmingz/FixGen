package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public class StrLiteral extends ExprNode {
    private String _literalValue;
    private String _escapedValue;

    public StrLiteral(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setLiteralValue(String literalValue) {
        _literalValue = literalValue;
    }

    public void setEscapedValue(String escapedValue) {
        _escapedValue = escapedValue;
    }
}
