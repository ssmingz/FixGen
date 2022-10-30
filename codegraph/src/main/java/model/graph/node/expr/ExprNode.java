package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Type;

public abstract class ExprNode extends Node {
    protected String _exprTypeStr = "?";

    public ExprNode(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setType(String exprType) {
        if (exprType == null) {
            _exprTypeStr = "?";
        } else {
            _exprTypeStr = exprType.toString();
        }
    }

    @Override
    public String toLabelString() {
        return _astNode.toString();
    }
}
