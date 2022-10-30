package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.AnonymousClassDecl;
import model.graph.node.Node;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class ClassInstanceCreationExpr extends ExprNode {
    private ExprNode _expression;
    private AnonymousClassDecl _anonymousClassDeclaration;
    private ExprList _arguments;
    private String _classType;

    public ClassInstanceCreationExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    @Override
    public String toLabelString() {
        return _astNode.toString();
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        new ASTEdge(this, expr);
    }

    public void setAnonymousClassDecl(AnonymousClassDecl anony) {
        _anonymousClassDeclaration = anony;
        new ASTEdge(this, anony);
    }

    public void setArguments(ExprList exprList) {
        _arguments = exprList;
        new ASTEdge(this, exprList);
    }

    public void setClassType(String typeStr) {
        _classType = typeStr;
    }

    @Override
    public boolean compare(Node other) {
        boolean match = false;
        if (other != null && other instanceof ClassInstanceCreationExpr) {
            ClassInstanceCreationExpr classInstCreation = (ClassInstanceCreationExpr) other;
            match = _expression == null ? (classInstCreation._expression == null) :
                    _expression.compare(classInstCreation._expression);
            match = match && _classType.equals(classInstCreation._classType) && _arguments.compare(classInstCreation._arguments);
            if (_anonymousClassDeclaration == null) {
                match = match && (classInstCreation._anonymousClassDeclaration == null);
            } else {
                match = match && _anonymousClassDeclaration.compare(classInstCreation._anonymousClassDeclaration);
            }
        }
        return match;
    }
}
