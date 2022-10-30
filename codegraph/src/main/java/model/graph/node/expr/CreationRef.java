package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class CreationRef extends ExprNode {
    public CreationRef(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return _astNode.toString();
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof CreationRef) {
            CreationRef creationRef = (CreationRef) other;
            match = toLabelString().equals(creationRef.toLabelString());
        }
        return match;
    }
}
