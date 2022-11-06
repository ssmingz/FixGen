package model.graph.node.stmt;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.SimpName;
import org.eclipse.jdt.core.dom.ASTNode;

public class LabeledStmt extends StmtNode {
    private SimpName _label;
    private StmtNode _body;

    public LabeledStmt(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(_label.toLabelString());
        stringBuffer.append(" : " + _body.toLabelString());
        return stringBuffer.toString();
    }

    public void setLabel(SimpName lab) {
        _label = lab;
        new ASTEdge(this, lab);
    }

    public void setBody(StmtNode body) {
        _body = body;
        new ASTEdge(this, body);
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if(other != null && other instanceof LabeledStmt) {
            match = _label.compare(((LabeledStmt) other)._label)
                    && _body.compare(((LabeledStmt) other)._body);
        }
        return match;
    }
}
