package model.graph.node.stmt;

import org.eclipse.jdt.core.dom.ASTNode;

public class EmptyStmt extends StmtNode{
    public EmptyStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
