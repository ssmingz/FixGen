package model.graph.node;

import org.eclipse.jdt.core.dom.ASTNode;

public class AnonymousClassDecl extends Node{
    public AnonymousClassDecl(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
