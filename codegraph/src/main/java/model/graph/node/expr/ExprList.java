package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Expression;

import java.util.List;

public class ExprList extends Node {
    private List<ExprNode> _exprs;
    private List<ASTNode> _oriNodeList;

    public ExprList(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setASTNodeList(List<ASTNode> nodeList) {
        _oriNodeList = nodeList;
    }

    public void setExprs(List<ExprNode> exprs) {
        _exprs = exprs;
        for (ExprNode expr : exprs) {
            new ASTEdge(this, expr);
        }
    }

    @Override
    public String toLabelString() {
        StringBuffer buf = new StringBuffer();
        if (_exprs.size() > 0) {
            buf.append(_exprs.get(0).toLabelString());
            for (int i = 1; i < _exprs.size(); i++) {
                buf.append(",");
                buf.append(_exprs.get(i).toLabelString());
            }
        }
        return buf.toString();
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof ExprList) {
            ExprList exprList = (ExprList) other;
            if (_exprs.size() == exprList._exprs.size()) {
                for (int i = 0; i < _exprs.size(); i++) {
                    if (!_exprs.get(i).compare(exprList._exprs.get(i))) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
