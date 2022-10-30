package model.graph.node;

import org.eclipse.jdt.core.dom.ASTNode;

public class AnonymousClassDecl extends Node {
    public AnonymousClassDecl(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
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
