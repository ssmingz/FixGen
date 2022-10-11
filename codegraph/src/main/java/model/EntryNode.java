package model;

import org.eclipse.jdt.core.dom.ASTNode;

public class EntryNode extends BaseNode {
    private String label;
    public EntryNode(ASTNode astNode, String label) {
        super(astNode);
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
