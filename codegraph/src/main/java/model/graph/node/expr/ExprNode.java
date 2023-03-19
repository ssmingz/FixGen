package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.PrefixExpression;
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

    public String getType() {
        return _exprTypeStr;
    }

    @Override
    public String toLabelString() {
        if (_astNode == null)
            return "";
        return _astNode.toString();
    }
}
