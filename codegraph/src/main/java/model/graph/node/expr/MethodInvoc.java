package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class MethodInvoc extends ExprNode {
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof MethodInvoc) {
            MethodInvoc methodInv = (MethodInvoc) other;
            match = _name.compare(methodInv._name);
            if (match) {
                match = (_expression == null ? (methodInv._expression == null)
                        : _expression.compare(methodInv._expression)) && _arguments.compare(methodInv._arguments);
            }
        }
        return match;
    }
}
