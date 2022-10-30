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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof BlockStmt) {
            BlockStmt blk = (BlockStmt) other;
            match = (_stmtlist.size() == blk._stmtlist.size());
            for (int i = 0; match && i < _stmtlist.size(); i++) {
                match = match && _stmtlist.get(i).compare(blk._stmtlist.get(i));
            }
        }
        return match;
    }
}
