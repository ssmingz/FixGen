package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.SimpName;
import org.eclipse.jdt.core.dom.ASTNode;

public class ContinueStmt extends StmtNode {
    private SimpName _identifier;

    public ContinueStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setIdentifier(SimpName sName) {
        _identifier = sName;
        new ASTEdge(this, sName);
    }

    @Override
    public String toLabelString() {
        return _astNode.toString();
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof ContinueStmt) {
            ContinueStmt continueStmt = (ContinueStmt) other;
            match = (_identifier == null) ? (continueStmt._identifier == null)
                    : _identifier.compare(continueStmt._identifier);
        }
        return match;
    }
}
