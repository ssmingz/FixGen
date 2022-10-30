package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.type.TypeNode;
import model.graph.node.varDecl.VarDeclFrag;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class VarDeclExpr extends ExprNode {
    private String _declType;
    private List<VarDeclFrag> _fragments;

    public VarDeclExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setDeclType(String type) {
        _declType = type;
    }

    public void setFragments(List<VarDeclFrag> fragments) {
        _fragments = fragments;
        for (VarDeclFrag frag : fragments) {
            new ASTEdge(this, frag);
        }
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof VarDeclExpr) {
            VarDeclExpr varDeclarationExpr = (VarDeclExpr) other;
            match = _declType.equals(varDeclarationExpr._declType);
            match = match && (_fragments.size() == varDeclarationExpr._fragments.size());
            for (int i = 0; match && i < _fragments.size(); i++) {
                match = match && _fragments.get(i).compare(varDeclarationExpr._fragments.get(i));
            }
        }
        return match;
    }
}
