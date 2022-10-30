package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.SimpName;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.BreakStatement;

public class BreakStmt extends StmtNode {
    private SimpName _identifier;
    public BreakStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setIdentifier(SimpName sName) {
        _identifier = sName;
        new ASTEdge(this, sName);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof BreakStmt) {
            BreakStmt breakStmt = (BreakStmt) other;
            match = _identifier == null ? (breakStmt._identifier == null) : _identifier.compare(breakStmt._identifier);
        }
        return match;
    }
}
