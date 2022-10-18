package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class BlockStmt extends StmtNode {
    private List<StmtNode> _stmtlist;
    public BlockStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setStatement(List<StmtNode> stmts) {
        _stmtlist = stmts;
        for (StmtNode obj : stmts) {
            new ASTEdge(this, obj);
        }
    }
}
