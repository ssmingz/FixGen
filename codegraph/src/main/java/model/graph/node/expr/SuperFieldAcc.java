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

    @Override
    public String toNameString() {
        return _identifier.getName();
    }

    public void setIdentifier(SimpName iden) {
        _identifier = iden;
        new ASTEdge(this, iden);
    }

    public void setQualifier(QuaName qname) {
        _qualifier = qname;
        new ASTEdge(this, qname);
    }
}
