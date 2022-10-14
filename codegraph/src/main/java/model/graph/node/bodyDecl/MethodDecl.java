package model.graph.node.bodyDecl;

import model.graph.node.Node;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.SimpName;
import model.graph.node.stmt.BlockStmt;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Type;

import java.util.ArrayList;
import java.util.List;

public class MethodDecl extends Node {
    /**
     * return type
     */
    private transient Type _retType;
    private String _retTypeStr;

    private List<String> _modifiers = new ArrayList<>();

    private SimpName _name;

    private List<ExprNode> _parameters;

    private List<String> _throws;

    private BlockStmt _body;
    public MethodDecl(ASTNode astNode, String fileName, int startLine, int endLine) {
        super(astNode, fileName, startLine, endLine);
        _retType = null;
    }

    public void setModifiers(List<String> modifiers) {
        _modifiers = modifiers;
    }

    public void setRetType(Type type, String typeStr) {
        _retType = type;
        _retTypeStr = typeStr;
    }

    public void setName(SimpName name) {
        _name = name;
    }

    public void setParameters(List<ExprNode> parameters) {
        _parameters = parameters;
    }

    public void setThrows(List<String> throwTypes) {
        _throws = throwTypes;
    }

    public void setBody(BlockStmt blk) {
        _body = blk;
    }
}
