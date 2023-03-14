package model.graph.node;

import model.graph.edge.ASTEdge;
import model.graph.node.bodyDecl.MethodDecl;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.ArrayList;
import java.util.List;

public class AnonymousClassDecl extends Node {
    List<Node> _bodyDecls = new ArrayList<>();

    public AnonymousClassDecl(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setMethodDecl(MethodDecl md) {
        _bodyDecls.add(md);
        new ASTEdge(this, md);
    }

    @Override
    public String toLabelString() {
        return _astNode.toString();
    }

    @Override
    public boolean compare(Node other) {
        if (other != null && other instanceof AnonymousClassDecl) {
            return true;
        }
        return false;
    }
}
