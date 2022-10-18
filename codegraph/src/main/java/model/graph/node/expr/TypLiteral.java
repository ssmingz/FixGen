package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class TypLiteral extends ExprNode {
    private TypeNode _value;

    public TypLiteral(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setValue(TypeNode typeNode) {
        _value = typeNode;
        new ASTEdge(this, typeNode);
    }
}
