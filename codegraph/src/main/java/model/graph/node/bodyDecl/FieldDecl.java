package model.graph.node.bodyDecl;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
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
    public String toNameString() {
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
}
