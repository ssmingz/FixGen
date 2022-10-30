package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof QuaName) {
            QuaName qName = (QuaName) other;
            match = _name.compare(qName._name)
                    && _qualifier.compare(qName._qualifier);
        }
        return match;
    }
}
