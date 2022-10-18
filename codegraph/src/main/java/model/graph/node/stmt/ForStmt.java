package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.ExprList;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class ForStmt extends StmtNode{
    private ExprList _initializer;
    private ExprNode _condition;
    private ExprList _updater;
    private StmtNode _body;

    public ForStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setInitializer(ExprList initExprList) {
        _initializer = initExprList;
        new ASTEdge(this, initExprList);
    }

    public void setCondition(ExprNode condition) {
        _condition = condition;
        new ASTEdge(this, condition);
    }

    public void setUpdaters(ExprList exprList) {
        _updater = exprList;
        new ASTEdge(this, exprList);
    }

    public void setBody(StmtNode body) {
        _body = body;
        new ASTEdge(this, body);
    }

    public ExprNode getCondition() {
        return _condition;
    }
}
