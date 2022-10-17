package model.graph.node.bodyDecl;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.SimpName;
import model.graph.node.stmt.BlockStmt;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Type;

import java.util.ArrayList;
import java.util.List;

public class MethodDecl extends Node {
    /**
     * return type
     */
    private TypeNode _retType;
    private String _retTypeStr;

    private List<String> _modifiers = new ArrayList<>();

    private SimpName _name;

    private List<ExprNode> _parameters;

    private List<String> _throws;

    private BlockStmt _body;

    private List<FieldDecl> _fieldVariables = new ArrayList<>();
    public MethodDecl(ASTNode astNode, String fileName, int startLine, int endLine) {
        super(astNode, fileName, startLine, endLine);
        _retType = null;
    }

    public void setModifiers(List<String> modifiers) {
        _modifiers = modifiers;
    }

    public void setRetType(TypeNode type, String typeStr) {
        _retType = type;
        Edge.createEdge(this, type, new ASTEdge(this, type));
        _retTypeStr = typeStr;
    }

    public void setName(SimpName name) {
        _name = name;
        Edge.createEdge(this, name, new ASTEdge(this, name));
    }

    public void setParameters(List<ExprNode> parameters) {
        _parameters = parameters;
        for (Object obj : parameters) {
            Edge.createEdge(this, (ExprNode) obj, new ASTEdge(this, (ExprNode) obj));
        }
    }

    public void setThrows(List<String> throwTypes) {
        _throws = throwTypes;
    }

    public void setBody(BlockStmt blk) {
        _body = blk;
        Edge.createEdge(this, blk, new ASTEdge(this, blk));
    }

    public void setFieldDecl(FieldDecl fieldNode) {
        _fieldVariables.add(fieldNode);
        Edge.createEdge(this, fieldNode, new ASTEdge(this, fieldNode));
    }
}
