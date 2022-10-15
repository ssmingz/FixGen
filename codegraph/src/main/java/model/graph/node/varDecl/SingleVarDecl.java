package model.graph.node.varDecl;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class SingleVarDecl extends Node {
    public SingleVarDecl(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
