package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.CatClause;
import model.graph.node.expr.VarDeclExpr;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class TryStmt extends StmtNode{
    private List<VarDeclExpr> _resources;
    private BlockStmt _body;
    private List<CatClause> _catches;
    private BlockStmt _finally;

    public TryStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setResources(List<VarDeclExpr> resourceList) {
        _resources = resourceList;
        for (VarDeclExpr obj : resourceList) {
            new ASTEdge(this, obj);
        }
    }

    public void setBody(BlockStmt blk) {
        _body = blk;
        new ASTEdge(this, blk);
    }

    public void setCatchClause(List<CatClause> catches) {
        _catches = catches;
        for (CatClause obj : catches) {
            new ASTEdge(this, obj);
        }
    }

    public void setFinallyBlock(BlockStmt finallyBlk) {
        _finally = finallyBlk;
        new ASTEdge(this, finallyBlk);
    }
}
