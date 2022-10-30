package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import org.eclipse.jdt.core.dom.ASTNode;

public class QuaName extends NameExpr {
    private SimpName _name;
    private NameExpr _qualifier;

    public QuaName(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setName(SimpName sname) {
        _name = sname;
        new ASTEdge(this, sname);
    }

    public void setQualifier(NameExpr name) {
        _qualifier = name;
        new ASTEdge(this, name);
    }

}
