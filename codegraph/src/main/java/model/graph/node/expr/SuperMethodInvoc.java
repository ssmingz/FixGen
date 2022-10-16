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

    public void setName(SimpName name) {
        _name = name;
        Edge.createEdge(this, name, new ASTEdge(this, name));
    }

    public void setQualifier(QuaName qualifier) {
        _qualifier = qualifier;
        Edge.createEdge(this, qualifier, new ASTEdge(this, qualifier));
    }

    public void setArguments(ExprList exprList) {
        _arguments = exprList;
        Edge.createEdge(this, exprList, new ASTEdge(this, exprList));
    }
}
