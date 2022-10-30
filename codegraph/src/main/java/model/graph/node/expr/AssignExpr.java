package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class AssignExpr extends ExprNode {
    private ExprNode _lhs;
    private ExprNode _rhs;
    private AssignOpr _opr;

    public AssignExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        StringBuffer buf = new StringBuffer();
        buf.append(_lhs.toLabelString());
        buf.append(_opr.toLabelString());
        buf.append(_rhs.toLabelString());
        return buf.toString();
    }

    public void setLeftHandSide(ExprNode lhs) {
        _lhs = lhs;
        new ASTEdge(this, lhs);
    }

    public Node getLeftHandSide() {
        return _lhs;
    }

    public void setRightHandSide(ExprNode rhs) {
        _rhs = rhs;
        new ASTEdge(this, rhs);
    }

    public void setOperator(AssignOpr assignOpr) {
        _opr = assignOpr;
        new ASTEdge(this, assignOpr);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof AssignExpr) {
            AssignExpr assign = (AssignExpr) other;
            match = _opr.compare(assign._opr) && _lhs.compare(assign._lhs) && _rhs.compare(assign._rhs);
        }
        return match;
    }
}
