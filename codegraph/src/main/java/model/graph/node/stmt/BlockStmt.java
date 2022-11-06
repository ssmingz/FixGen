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
    public String toLabelString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("{");
        if(_stmtlist.size() > 0){
            stringBuffer.append(_stmtlist.get(0).getClass().toString());
        }
        for(int i = 1; i < _stmtlist.size(); ++i){
            stringBuffer.append("," + _stmtlist.get(i).getClass().toString());
        }
        stringBuffer.append("}");
        return stringBuffer.toString();
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
