package model.graph.node.expr;

import model.graph.node.Node;
import model.graph.node.bodyDecl.MethodDecl;
import org.eclipse.jdt.core.dom.ASTNode;

public class AnnotationExpr extends ExprNode {
    private String _comment;

    public AnnotationExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
        _comment = oriNode.toString();
    }

    @Override
    public String toLabelString() {
        return null;
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof AnnotationExpr) {
            AnnotationExpr annotationExpr = (AnnotationExpr) other;
            match = _comment.equals(annotationExpr._comment);
        }
        return match;
    }
}
