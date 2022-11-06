package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.VarDeclExpr;
import model.graph.node.type.TypeNode;
import model.graph.node.varDecl.VarDeclFrag;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class VarDeclStmt extends StmtNode {
    private String _modifier;
    private String _declType;
    private List<VarDeclFrag> _fragments;

    public VarDeclStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return _astNode.toString();
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof VarDeclStmt) {
            VarDeclStmt varDeclarationStmt = (VarDeclStmt) other;
            match = _declType.equals(varDeclarationStmt._declType);
            if (_modifier == null) {
                match = match && (varDeclarationStmt._modifier == null);
            } else {
                if (varDeclarationStmt._modifier == null) {
                    match = false;
                } else {
                    match = match && _modifier.equals(varDeclarationStmt._modifier);
                }
            }
            match = match && (_fragments.size() == varDeclarationStmt._fragments.size());
            for (int i = 0; match && i < _fragments.size(); i++) {
                match = match && _fragments.get(i).compare(varDeclarationStmt._fragments.get(i));
            }
        }
        return match;
    }
}
