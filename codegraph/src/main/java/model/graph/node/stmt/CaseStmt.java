package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.SwitchCase;

public class CaseStmt extends StmtNode{
    private ExprNode _expression;
    public CaseStmt(SwitchCase astNode, String filePath, int start, int end) {
        super(astNode, filePath, start, end);
    }

    public void setExpression(ExprNode expression) {
        _expression = expression;
        Edge.createEdge(this, expression, new ASTEdge(this, expression));
    }
}
