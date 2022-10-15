package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class ExprList extends Node {
    private List<ExprNode> _exprs;

    public ExprList(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExprs(List<ExprNode> exprs) {
        _exprs = exprs;
        for (ExprNode expr : exprs) {
            Edge.createEdge(this, expr, new ASTEdge(this, expr));
        }
    }
}
