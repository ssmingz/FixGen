package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class SuperFieldAcc extends ExprNode {
    private SimpName _identifier;
    private QuaName _qualifier;

    public SuperFieldAcc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setIdentifier(SimpName iden) {
        _identifier = iden;
        new ASTEdge(this, iden);
    }

    public void setQualifier(QuaName qname) {
        _qualifier = qname;
        new ASTEdge(this, qname);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof SuperFieldAcc) {
            SuperFieldAcc superFieldAcc = (SuperFieldAcc) other;
            match = (_qualifier == null) ? (superFieldAcc._qualifier == null) : _qualifier.compare(superFieldAcc._qualifier);
            if (match) {
                match = match && _identifier.compare(superFieldAcc._identifier);
            }
        }
        return match;
    }
}
