package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.CatClause;
import model.graph.node.Node;
import model.graph.node.expr.VarDeclExpr;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class TryStmt extends StmtNode {
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if(other != null && other instanceof TryStmt) {
            TryStmt tryStmt = (TryStmt) other;
            if(_resources == null) {
                match = (tryStmt._resources == null);
            } else {
                if(tryStmt._resources == null) {
                    match = false;
                } else {
                    match = (_resources.size() == tryStmt._resources.size());
                    for(int i = 0; match && i < _resources.size(); i++) {
                        match = match && _resources.get(i).compare(tryStmt._resources.get(i));
                    }
                }
            }
            // body
            match = match && _body.compare(tryStmt._body);
            // catch clause
            if(_catches != null) {
                if(tryStmt._catches != null) {
                    match = match && (_catches.size() == tryStmt._catches.size());
                    for(int i = 0; match && i < _catches.size(); i ++) {
                        match = match && _catches.get(i).compare(tryStmt._catches.get(i));
                    }
                } else {
                    match = false;
                }
            } else {
                match = match && (tryStmt._catches == null);
            }
            // finally block
            if(_finally == null) {
                match = match && (tryStmt._finally == null);
            } else {
                if(tryStmt._finally == null) {
                    match = false;
                } else {
                    match = match && _finally.compare(tryStmt._finally);
                }
            }
        }
        return match;
    }
}
