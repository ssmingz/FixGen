package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public class VarDeclExpr extends ExprNode {
    public VarDeclExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
