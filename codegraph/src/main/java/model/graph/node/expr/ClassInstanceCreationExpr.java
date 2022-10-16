package model.graph.node.expr;

import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.AnonymousClassDecl;
import model.graph.node.type.TypeNode;
import org.eclipse.jdt.core.dom.ASTNode;

public class ClassInstanceCreationExpr extends ExprNode {
    private ExprNode _expression;
    private AnonymousClassDecl _anonymousClassDeclaration;
    private ExprList _arguments;
    private TypeNode _classType;

    public ClassInstanceCreationExpr(ASTNode oriNode, String fileName, int startLine, int endLine) {
        super(oriNode, fileName, startLine, endLine);
    }

    public void setExpression(ExprNode expr) {
        _expression = expr;
        Edge.createEdge(this, expr, new ASTEdge(this, expr));
    }

    public void setAnonymousClassDecl(AnonymousClassDecl anony) {
        _anonymousClassDeclaration = anony;
        Edge.createEdge(this, anony, new ASTEdge(this, anony));
    }

    public void setArguments(ExprList exprList) {
        _arguments = exprList;
        Edge.createEdge(this, exprList, new ASTEdge(this, exprList));
    }

    public void setClassType(TypeNode typeNode) {
        _classType = typeNode;
        Edge.createEdge(this, typeNode, new ASTEdge(this, typeNode));
    }
}
