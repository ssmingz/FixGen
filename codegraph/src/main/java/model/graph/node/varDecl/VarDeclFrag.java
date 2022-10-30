package model.graph.node.varDecl;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.SimpName;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class VarDeclFrag extends ExprNode {
    private String _type;
    private SimpName _name;
    private int _dimensions;
    private ExprNode _expression;


    public VarDeclFrag(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setDeclType(String type) {
        _type = type;
    }

    public void setName(SimpName iden) {
        _name = iden;
        new ASTEdge(this, iden);
    }

    public Node getName() {
        return _name;
    }

    public void setDimensions(int extraDimensions) {
        _dimensions = extraDimensions;
    }

    public void setExpr(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof VarDeclFrag) {
            VarDeclFrag vdf = (VarDeclFrag) other;
            match = (_dimensions == vdf._dimensions) && _name.compare(vdf._name);
            match = match && _type.equals(vdf._type);
            if (_expression == null) {
                match = match && (vdf._expression == null);
            } else {
                match = match && _expression.compare(vdf._expression);
            }
        }
        return match;
    }
}
