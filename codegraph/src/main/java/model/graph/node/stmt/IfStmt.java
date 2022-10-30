package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class IfStmt extends StmtNode {
    private ExprNode _expression;
    private StmtNode _then;
    private StmtNode _else;

    public IfStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setThen(StmtNode then) {
        _then = then;
        new ASTEdge(this, then);
    }

    public void setElse(StmtNode els) {
        _else = els;
        new ASTEdge(this, els);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if(other != null && other instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) other;
            match = _expression.compare(ifStmt._expression) && _then.compare(ifStmt._then);
            if(_else == null) {
                match = match && (ifStmt._else == null);
            } else {
                match = match && _else.compare(ifStmt._else);
            }
        }
        return match;
    }
}
