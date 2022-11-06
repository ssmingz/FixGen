package model.graph.node.stmt;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class EmptyStmt extends StmtNode {
    public EmptyStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return null;
    }

    @Override
    public boolean compare(Node other) {
        if(other != null && other instanceof EmptyStmt) {
            return true;
        }
        return false;
    }
}
