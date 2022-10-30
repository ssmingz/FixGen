package model.graph.node.type;

import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class TypeNode extends ExprNode {
    public TypeNode(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return _exprTypeStr;
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof TypeNode) {
            return toLabelString().equals(((TypeNode) other).toLabelString());
        }
        return false;
    }
}
