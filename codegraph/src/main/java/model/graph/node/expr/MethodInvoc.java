package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class MethodInvoc extends ExprNode{
    private ExprNode _expression;
    private SimpName _name;
    private ExprList _arguments;

    public MethodInvoc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setName(SimpName iden) {
        _name = iden;
        new ASTEdge(this, iden);
    }

    public void setArguments(ExprList exprList) {
        _arguments = exprList;
        new ASTEdge(this, exprList);
    }
}
