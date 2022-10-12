package model;

import org.eclipse.jdt.core.dom.ASTNode;

public class DataNode extends BaseNode{
    protected String dataName;
    protected String dataValue;

    public DataNode(ASTNode astNode, String dataType, String dataName) {
        super(astNode);
        if (astNode.getNodeType() == ASTNode.METHOD_DECLARATION)
            this.dataType = dataType;
        else if (dataType.endsWith(")") || dataType.endsWith(">"))
            this.dataType = "UNKNOWN";
        else
            this.dataType = dataType;

        this.dataName = dataName;
    }

    public DataNode(ASTNode astNode, String dataType, String dataName, String dataValue) {
        this(astNode, dataType, dataName);
        this.dataValue = dataValue;
    }

    @Override
    public String getDataName() {
        return dataName;
    }

    public String getDataValue() {
        return dataValue;
    }

    @Override
    public String getLabel() {
        if (dataValue != null)
            return dataValue;
        if (dataName != null)
            return dataName;
        return dataType;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
