package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class AryInitializer extends ExprNode {
    private List<ExprNode> _expressions;

    public AryInitializer(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpressions(List<ExprNode> exprs) {
        _expressions = exprs;
        for (ExprNode expr : exprs) {
            Edge.createEdge(this, expr, new ASTEdge(this, expr));
        }
    }
}
