package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Type;

import java.util.List;

public class AryCreation extends ExprNode{
    private TypeNode _elementType;
    private String _elementTypeStr;
    private ExprList _dimension;
    private AryInitializer _initializer;

    public AryCreation(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setArrayType(TypeNode typ, String typStr) {
        _elementType = typ;
        _elementTypeStr = typStr;
        Edge.createEdge(this, typ, new ASTEdge(this, typ));
    }

    public void setDimension(ExprList dimension) {
        _dimension = dimension;
        Edge.createEdge(this, dimension, new ASTEdge(this, dimension));
    }

    public void setInitializer(AryInitializer aryinit) {
        _initializer = aryinit;
        Edge.createEdge(this, aryinit, new ASTEdge(this, aryinit));
    }
}
