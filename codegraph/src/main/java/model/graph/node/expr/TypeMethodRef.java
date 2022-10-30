package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class TypeMethodRef extends ExprNode {
    public TypeMethodRef(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof TypeMethodRef) {
            match = toLabelString().equals(((TypeMethodRef) other).toLabelString());
        }
        return match;
    }
}
