package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
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
    public String toLabelString() {
        StringBuffer buf = new StringBuffer();
        buf.append(_array.toLabelString());
        buf.append("[");
        buf.append(_index.toLabelString());
        buf.append("]");
        return buf.toString();
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

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof AryAcc) {
            match = _array.compare(((AryAcc) other)._array)
                    && _index.compare(((AryAcc) other)._index)
                    && _type.equals(((AryAcc) other)._type);
        }
        return match;
    }
}
