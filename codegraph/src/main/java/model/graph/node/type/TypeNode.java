package model.graph.node.type;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class TypeNode extends Node {
    public TypeNode(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
