package model.graph.node.stmt;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public abstract class StmtNode extends Node {
    public StmtNode(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        StringBuffer buf = new StringBuffer();
        buf.append(ASTNode.nodeClassForType(_astNode.getNodeType()).toString());
        return buf.toString();
    }
}
