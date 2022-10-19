package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public class ThisExpr extends ExprNode{
    public ThisExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return null;
    }
}
