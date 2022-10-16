package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.type.TypeNode;
import model.graph.node.varDecl.VarDeclFrag;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class VarDeclExpr extends ExprNode {
    private TypeNode _declType;
    private List<VarDeclFrag> _fragments;

    public VarDeclExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setDeclType(TypeNode typeNode) {
        _declType = typeNode;
        Edge.createEdge(this, typeNode, new ASTEdge(this, typeNode));
    }

    public void setFragments(List<VarDeclFrag> fragments) {
        _fragments = fragments;
        for (VarDeclFrag frag : fragments) {
            Edge.createEdge(this, frag, new ASTEdge(this, frag));
        }
    }
}
