package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class AryAcc extends ExprNode{
    private ExprNode _array;
    private ExprNode _index;
    private TypeNode _type;
    private String _typeStr;

    public AryAcc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setArray(ExprNode array) {
        _array = array;
        new ASTEdge(this, array);
    }

    public void setIndex(ExprNode index) {
        _index = index;
        new ASTEdge(this, index);
    }

    public void setType(TypeNode typ, String typeStr) {
        _type = typ;
        _typeStr = typeStr;
        new ASTEdge(this, typ);
    }
}
