package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class SuperMethodRef extends ExprNode {
    public SuperMethodRef(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof SuperMethodRef) {
            match = toLabelString().equals(((SuperMethodRef) other).toLabelString());
        }
        return match;
    }
}
