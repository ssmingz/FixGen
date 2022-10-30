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
    private String _declType;
    private List<VarDeclFrag> _fragments;

    public VarDeclStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setModifier(String modifier) {
        _modifier = modifier;
    }

    public void setDeclType(String type) {
        _declType = type;
    }

    public void setFragments(List<VarDeclFrag> fragments) {
        _fragments = fragments;
        for (VarDeclFrag obj : fragments) {
            new ASTEdge(this, obj);
        }
    }
}
