package model.graph.node.stmt;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class BlockStmt extends StmtNode {
    public BlockStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
