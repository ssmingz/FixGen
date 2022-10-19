package model.graph.node.expr;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;

public class AssignOpr extends ExprNode{
    private Assignment.Operator _operator;

    public AssignOpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return null;
    }

    public void setOperator(Assignment.Operator operator) {
        _operator = operator;
    }
}
