package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.ExprList;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.List;

public class ConstructorInvoc extends StmtNode {
    private ExprList _argulist;

    public ConstructorInvoc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setArguments(ExprList argulist) {
        _argulist = argulist;
        new ASTEdge(this, argulist);
    }
}
