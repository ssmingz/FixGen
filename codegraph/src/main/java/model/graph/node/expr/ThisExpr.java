package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class ThisExpr extends ExprNode {
    public ThisExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof ThisExpr) {
            return toLabelString().equals(((ThisExpr) other).toLabelString());
        }
        return false;
    }
}
