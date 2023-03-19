package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.PrefixExpression;

public class PrefixOpr extends ExprNode {
    private PrefixExpression.Operator _prefixOperator;

    public PrefixOpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setOperator(PrefixExpression.Operator operator) {
        _prefixOperator = operator;
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof PrefixOpr) {
            return _prefixOperator.equals(((PrefixOpr) other)._prefixOperator);
        }
        return false;
    }

    @Override
    public String toLabelString() {
        return _prefixOperator.toString();
    }
}
