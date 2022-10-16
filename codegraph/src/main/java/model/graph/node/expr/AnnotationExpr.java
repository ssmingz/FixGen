package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;

public class AnnotationExpr extends ExprNode {
    private String _comment;

    public AnnotationExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
        _comment = oriNode.toString();
    }
}
