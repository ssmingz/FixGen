package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.PrefixExpression;

public class PrefixOpr extends ExprNode {
    private PrefixExpression.Operator _operator;

    public PrefixOpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setOperator(PrefixExpression.Operator operator) {
        _operator = operator;
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof PrefixOpr) {
            return _operator.equals(((PrefixOpr) other)._operator);
        }
        return false;
    }
}
