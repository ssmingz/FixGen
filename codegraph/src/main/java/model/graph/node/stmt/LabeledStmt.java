package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.SimpName;
import org.eclipse.jdt.core.dom.ASTNode;

public class LabeledStmt extends StmtNode{
    private SimpName _label;
    private StmtNode _body;

    public LabeledStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setLabel(SimpName lab) {
        _label = lab;
        Edge.createEdge(this, lab, new ASTEdge(this, lab));
    }

    public void setBody(StmtNode body) {
        _body = body;
        Edge.createEdge(this, body, new ASTEdge(this, body));
    }
}
