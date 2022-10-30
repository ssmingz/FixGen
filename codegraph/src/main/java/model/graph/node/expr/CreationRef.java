package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public class CreationRef extends ExprNode {
    public CreationRef(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return _astNode.toString();
    }
}
