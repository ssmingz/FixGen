package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class AryAcc extends ExprNode{
    private ExprNode _array;
    private ExprNode _index;
    private String _type;

    public AryAcc(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toNameString() {
        return _array.toNameString();
    }

    public void setArray(ExprNode array) {
        _array = array;
        new ASTEdge(this, array);
    }

    public void setIndex(ExprNode index) {
        _index = index;
        new ASTEdge(this, index);
    }

    public void setType(String typeStr) {
        _type = typeStr;
    }
}
