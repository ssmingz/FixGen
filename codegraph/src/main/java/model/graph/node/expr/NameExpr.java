package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public abstract class NameExpr extends ExprNode{
    public NameExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
