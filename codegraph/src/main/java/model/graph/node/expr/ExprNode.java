package model.graph.node.expr;

import model.graph.node.Node;
import org.eclipse.jdt.core.dom.ASTNode;

public abstract class ExprNode extends Node {
    public ExprNode(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }
}
