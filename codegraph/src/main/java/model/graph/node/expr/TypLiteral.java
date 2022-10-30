package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class TypLiteral extends ExprNode {
    private String _value;

    public TypLiteral(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setValue(String type) {
        _value = type;
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof TypLiteral) {
            TypLiteral literal = (TypLiteral) other;
            match = _value.equals(literal._value);
        }
        return match;
    }
}
