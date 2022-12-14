package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;

public class AssignOpr extends ExprNode{
    private Assignment.Operator _operator;

    public AssignOpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return _operator.toString();
    }

    public void setOperator(Assignment.Operator operator) {
        _operator = operator;
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof AssignOpr) {
            return _operator.equals(((AssignOpr) other)._operator);
        }
        return false;
    }
}
