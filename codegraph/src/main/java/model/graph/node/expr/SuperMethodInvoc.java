package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class SuperMethodInvoc extends ExprNode {
    private SimpName _name;
    private QuaName _qualifier;
    private ExprList _arguments;

    public SuperMethodInvoc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return _name.getName();
    }

    public void setName(SimpName name) {
        _name = name;
        new ASTEdge(this, name);
    }

    public void setQualifier(QuaName qualifier) {
        _qualifier = qualifier;
        new ASTEdge(this, qualifier);
    }

    public void setArguments(ExprList exprList) {
        _arguments = exprList;
        new ASTEdge(this, exprList);
    }
}
