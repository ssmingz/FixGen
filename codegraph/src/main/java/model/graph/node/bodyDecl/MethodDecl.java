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
    private String _retType;

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

    @Override
    public String toLabelString() {
        StringBuffer buf = new StringBuffer();
        for (Object modifier : _modifiers) {
            buf.append(modifier.toString() + " ");
        }
        if (_retType != null && !_retType.equals("?")) {
            buf.append(_retType + " ");
        }
        buf.append(_name.toLabelString());
        buf.append("(");
        if (_parameters != null && _parameters.size() > 0) {
            buf.append(_parameters.get(0).toLabelString());
            for (int i = 1; i < _parameters.size(); i++) {
                buf.append("," + _parameters.get(i).toLabelString());
            }
        }
        buf.append(")");
        if (_throws != null && _throws.size() > 0) {
            buf.append(" throws " + _throws.get(0));
            for (int i = 1; i < _throws.size(); i++) {
                buf.append("," + _throws.get(i));
            }
        }
        return buf.toString();
    }

    public void setModifiers(List<String> modifiers) {
        _modifiers = modifiers;
    }

    public void setRetType(String typeStr) {
        _retType = typeStr;
    }

    public void setName(SimpName name) {
        _name = name;
        new ASTEdge(this, name);
    }

    public void setParameters(List<ExprNode> parameters) {
        _parameters = parameters;
        for (Object obj : parameters) {
            new ASTEdge(this, (ExprNode) obj);
        }
    }

    public void setThrows(List<String> throwTypes) {
        _throws = throwTypes;
    }

    public void setBody(BlockStmt blk) {
        _body = blk;
        new ASTEdge(this, blk);
    }

    public void setFieldDecl(FieldDecl fieldNode) {
        _fieldVariables.add(fieldNode);
        new ASTEdge(this, fieldNode);
    }
}
