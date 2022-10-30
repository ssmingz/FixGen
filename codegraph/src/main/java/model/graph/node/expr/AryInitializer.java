package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class AryInitializer extends ExprNode {
    private List<ExprNode> _expressions;

    public AryInitializer(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return _astNode.toString();
    }

    public void setExpressions(List<ExprNode> exprs) {
        _expressions = exprs;
        for (ExprNode expr : exprs) {
            new ASTEdge(this, expr);
        }
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof AryInitializer) {
            AryInitializer aryInitializer = (AryInitializer) other;
            match = (_expressions.size() == aryInitializer._expressions.size());
            for (int i=0; match && i<_expressions.size(); i++) {
                match = match && _expressions.get(i).compare(aryInitializer._expressions.get(i));
            }
        }
        return match;
    }
}
