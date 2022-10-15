package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.VarDeclExpr;
import model.graph.node.type.TypeNode;
import model.graph.node.varDecl.VarDeclFrag;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class VarDeclStmt extends StmtNode{
    private String _modifier;
    private String _typeStr;
    private TypeNode _declType;
    private List<VarDeclFrag> _fragments;

    public VarDeclStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setModifier(String modifier) {
        _modifier = modifier;
    }

    public void setDeclType(TypeNode type, String typeStr) {
        _declType = type;
        _typeStr = typeStr;
        Edge.createEdge(this, type, new ASTEdge(this, type));
    }

    public void setFragments(List<VarDeclFrag> fragments) {
        _fragments = fragments;
        for (VarDeclFrag obj : fragments) {
            Edge.createEdge(this, obj, new ASTEdge(this, obj));
        }
    }
}
