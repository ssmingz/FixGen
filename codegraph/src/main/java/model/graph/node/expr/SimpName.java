package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class SimpName extends NameExpr {
    private String _name;
    private String _type;

    public SimpName(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setName(String name) {
        _name = name;
    }

    public String getName() { return _name; }

    @Override
    public String toLabelString() {
        return _name;
    }

    public void setType(String typeStr) { _type = typeStr; }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof SimpName) {
            SimpName sName = (SimpName) other;
            return _name.equals(sName._name) && _type.equals(sName._type);
        }
        return false;
    }
}
