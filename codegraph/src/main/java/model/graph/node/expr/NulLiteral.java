package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public class NulLiteral extends ExprNode{
    public NulLiteral(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
