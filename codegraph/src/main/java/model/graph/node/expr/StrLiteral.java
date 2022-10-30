package model.graph.node.expr;

import model.graph.node.Node;
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof StrLiteral) {
            StrLiteral strLiteral = (StrLiteral) other;
            match = _literalValue.equals(strLiteral._literalValue)
                    && _escapedValue.equals(strLiteral._escapedValue);
        }
        return match;
    }
}
