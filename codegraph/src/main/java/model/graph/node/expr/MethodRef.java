package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class MethodRef extends ExprNode {
    public MethodRef(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof MethodRef) {
            match = toLabelString().equals(((MethodRef) other).toLabelString());
        }
        return match;
    }
}
