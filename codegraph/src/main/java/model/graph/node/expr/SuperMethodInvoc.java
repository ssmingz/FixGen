package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof SuperMethodInvoc) {
            SuperMethodInvoc superMethodInv = (SuperMethodInvoc) other;
            match = (_qualifier == null) ? (superMethodInv._qualifier == null) : _qualifier.compare(superMethodInv._qualifier);
            match = match && _name.compare(superMethodInv._name) && _arguments.compare(superMethodInv._arguments);
        }
        return match;
    }
}
