package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprList;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class SuperConstructorInvoc extends StmtNode {
    private ExprNode _expression;
    private ExprList _argulist;

    public SuperConstructorInvoc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setArguments(ExprList arguList) {
        _argulist = arguList;
        new ASTEdge(this, arguList);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if(other != null && other instanceof SuperConstructorInvoc) {
            SuperConstructorInvoc superConstructorInv = (SuperConstructorInvoc) other;
            if(_expression == null) {
                match = (superConstructorInv._expression == null);
            } else {
                match = _expression.compare(superConstructorInv._expression);
            }

            match = match && _argulist.compare(superConstructorInv._argulist);
        }
        return match;
    }
}
