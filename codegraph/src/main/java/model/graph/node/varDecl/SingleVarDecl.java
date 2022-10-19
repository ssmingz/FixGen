package model.graph.node.varDecl;

import model.graph.edge.ASTEdge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.SimpName;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class SingleVarDecl extends ExprNode {
    private TypeNode _declType;
    private ExprNode _initializer;
    private SimpName _name;

    public SingleVarDecl(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return _name.getName();
    }

    public void setDeclType(TypeNode typeNode) {
        _declType = typeNode;
        new ASTEdge(this, typeNode);
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

}
