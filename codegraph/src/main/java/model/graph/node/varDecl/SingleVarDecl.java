package model.graph.node.varDecl;

import model.graph.edge.ASTEdge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.SimpName;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class SingleVarDecl extends ExprNode {
    private String _declType;
    private ExprNode _initializer;
    private SimpName _name;

    public SingleVarDecl(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setDeclType(String type) {
        _declType = type;
    }

    public void setInitializer(ExprNode init) {
        _initializer = init;
        new ASTEdge(this, init);
    }

    public void setName(SimpName name) {
        _name = name;
        new ASTEdge(this, name);
    }

    public Node getName() {
        return _name;
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof SingleVarDecl) {
            SingleVarDecl svd = (SingleVarDecl) other;
            match = _declType.equals(svd._declType);
            match = match && _name.compare(svd._name);
            if (_initializer == null) {
                match = match && (svd._initializer == null);
            } else {
                match = match && _initializer.compare(svd._initializer);
            }
        }

        return match;
    }
}
