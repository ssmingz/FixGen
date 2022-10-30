package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.SwitchCase;

public class CaseStmt extends StmtNode {
    private ExprNode _expression;
    public CaseStmt(SwitchCase astNode, String filePath, int start, int end) {
        super(astNode, filePath, start, end);
    }

    public void setExpression(ExprNode expression) {
        _expression = expression;
        new ASTEdge(this, expression);
    }

    @Override
    public boolean compare(Node other) {
        if(other != null && other instanceof CaseStmt) {
            CaseStmt swCase = (CaseStmt) other;
            if(_expression != null) {
                return _expression.compare(swCase._expression);
            } else {
                return swCase._expression == null;
            }
        }
        return false;
    }
}
