package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public class SimpName extends ExprNode {
    public SimpName(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
