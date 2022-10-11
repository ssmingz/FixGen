package model;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.util.ArrayList;
import java.util.Optional;

public abstract class BaseNode {
    public static int numOfNodes = 0;
    protected int id;
    protected ASTNode astNode;
    private int sourceLineNumber;
    protected CodeGraph graph;
    protected String dataType;
    protected ArrayList<BaseEdge> inEdges = new ArrayList<BaseEdge>();
    protected ArrayList<BaseEdge> outEdges = new ArrayList<BaseEdge>();
    public BaseNode(ASTNode astNode) {
        this.id = ++numOfNodes;
        this.astNode = astNode;
        this.sourceLineNumber = -1;
        ASTNode node = astNode;
        while (node != null) {
            if (node instanceof CompilationUnit) {
                this.sourceLineNumber = ((CompilationUnit) node).getLineNumber(astNode.getStartPosition());
            }
            node = node.getParent();
        }
    }

    public void setGraph(CodeGraph g) {
        this.graph = g;
    }

    public int getId() {
        return id;
    }

    public CodeGraph getGraph() {
        return graph;
    }

    public String getDataType() {
        return dataType;
    }

    public String getDataName() {
        if (this instanceof DataNode)
            return ((DataNode) this).getDataName();
        return null;
    }

    public ASTNode getAstNode() {
        return astNode;
    }

    public Optional<Integer> getSourceLineNumber() {
        return sourceLineNumber == -1 ? Optional.empty() : Optional.of(sourceLineNumber);
    }

    public ArrayList<BaseEdge> getInEdges() {
        return inEdges;
    }

    public ArrayList<BaseEdge> getOutEdges() {
        return outEdges;
    }

    public void addOutEdge(BaseEdge edge) {
        outEdges.add(edge);
    }

    public void addInEdge(BaseEdge edge) {
        inEdges.add(edge);
    }

    abstract public String getLabel();
}
