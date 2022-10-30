package model.graph.node.bodyDecl;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import model.graph.node.type.TypeNode;
import model.graph.node.varDecl.VarDeclFrag;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class FieldDecl extends ExprNode {
    private String _declType = null;
    private List<VarDeclFrag> _fragments = new ArrayList<>();

    public FieldDecl(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return null;
    }

    public void setDeclType(String declType) {
        _declType = declType;
    }

    public void setFrags(List<VarDeclFrag> frags) {
        _fragments = frags;
        for (VarDeclFrag frag : frags) {
            new ASTEdge(this, frag);
        }
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof FieldDecl) {
            FieldDecl fieldDecl = (FieldDecl) other;
            match = _declType.equals(fieldDecl._declType);
            match = match && (_fragments.size() == fieldDecl._fragments.size());
            for (int i=0; match && i<_fragments.size(); i++) {
                match = match && _fragments.get(i).compare(fieldDecl._fragments.get(i));
            }
        }
        return match;
    }
}
