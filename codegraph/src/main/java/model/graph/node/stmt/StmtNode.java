package model.graph.node.stmt;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public abstract class StmtNode extends Node {
    public StmtNode(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
