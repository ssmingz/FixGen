package model.graph.node.stmt;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public class TypeDeclStmt extends StmtNode{
    public TypeDeclStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if(other != null && other instanceof TypeDeclStmt) {
            match = toLabelString().equals(((TypeDeclStmt) other).toLabelString());
        }
        return match;
    }
}
