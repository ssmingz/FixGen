package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.SimpName;
import org.eclipse.jdt.core.dom.ASTNode;

public class ContinueStmt extends StmtNode{
    private SimpName _identifier;

    public ContinueStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setIdentifier(SimpName sName) {
        _identifier = sName;
        Edge.createEdge(this, sName, new ASTEdge(this, sName));
    }
}
