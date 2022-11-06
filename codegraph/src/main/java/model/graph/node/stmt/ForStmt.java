package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprList;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class ForStmt extends StmtNode {
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

    @Override
    public String toLabelString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("for(");
        if(_initializer != null){
            stringBuffer.append(_initializer.toLabelString());
        }
        stringBuffer.append(";");
        if(_condition!= null){
            stringBuffer.append(_condition.toLabelString());
        }
        stringBuffer.append(";");
        if(_updater != null){
            stringBuffer.append(_updater.toLabelString());
        }
        stringBuffer.append(")");
        stringBuffer.append(_body.toLabelString());
        return stringBuffer.toString();
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof ForStmt) {
            ForStmt forStmt = (ForStmt) other;
            match = _initializer.compare(forStmt._initializer);
            if (_condition != null) {
                match = match && _condition.compare(forStmt._condition);
            } else {
                match = match && (forStmt._condition == null);
            }
            match = match && _updater.compare(forStmt._updater);
            match = match && _body.compare(forStmt._body);
        }
        return match;
    }
}
