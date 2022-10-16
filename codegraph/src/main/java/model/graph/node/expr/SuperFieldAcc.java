package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class SuperFieldAcc extends ExprNode {
    private SimpName _identifier;
    private QuaName _qualifier;

    public SuperFieldAcc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setIdentifier(SimpName iden) {
        _identifier = iden;
        Edge.createEdge(this, iden, new ASTEdge(this, iden));
    }

    public void setQualifier(QuaName qname) {
        _qualifier = qname;
        Edge.createEdge(this, qname, new ASTEdge(this, qname));
    }
}
