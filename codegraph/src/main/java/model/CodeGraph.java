package model;

import model.graph.node.Node;
import model.graph.node.bodyDecl.MethodDecl;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.SimpName;
import model.graph.node.stmt.BlockStmt;
import org.eclipse.jdt.core.dom.*;
import utils.JavaASTUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CodeGraph {
    private final GraphConfiguration configuration;

    private String filePath, name, projectName;
    private GraphBuildingContext context;
    protected BaseNode entryNode;
    protected HashSet<BaseNode> nodes = new HashSet<>();

    protected CompilationUnit cu = null;

    public CodeGraph(GraphBuildingContext context, GraphConfiguration configuration) {
        this.context = context;
        this.configuration = configuration;
    }

    public Node buildNode(ASTNode astNode, Node parent) {
        if (astNode == null) {
            return null;
        }
        if (astNode instanceof MethodDeclaration) {
            return visit((MethodDeclaration) astNode, parent);
        } else if (astNode instanceof AssertStatement) {
            return visit((AssertStatement) astNode, parent);
        } else if (astNode instanceof Block) {
            return visit((Block) astNode, parent);
        } else if (astNode instanceof BreakStatement) {
            return visit((BreakStatement) astNode, parent);
        } else if (astNode instanceof SwitchCase) {
            return visit((SwitchCase) astNode, parent);
        } else if (astNode instanceof ConstructorInvocation) {
            return visit((ConstructorInvocation) astNode, parent);
        } else if (astNode instanceof ContinueStatement) {
            return visit((ContinueStatement) astNode, parent);
        } else if (astNode instanceof DoStatement) {
            return visit((DoStatement) astNode, parent);
        } else if (astNode instanceof EmptyStatement) {
            return visit((EmptyStatement) astNode, parent);
        } else if (astNode instanceof EnhancedForStatement) {
            return visit((EnhancedForStatement) astNode, parent);
        } else if (astNode instanceof ExpressionStatement) {
            return visit((ExpressionStatement) astNode, parent);
        } else if (astNode instanceof ForStatement) {
            return visit((ForStatement) astNode, parent);
        } else if (astNode instanceof IfStatement) {
            return visit((IfStatement) astNode, parent);
        } else if (astNode instanceof LabeledStatement) {
            return visit((LabeledStatement) astNode, parent);
        } else if (astNode instanceof ReturnStatement) {
            return  visit((ReturnStatement) astNode, parent);
        } else if (astNode instanceof SuperConstructorInvocation) {
            return visit((SuperConstructorInvocation) astNode, parent);
        } else if (astNode instanceof SwitchStatement) {
            return visit((SwitchStatement) astNode, parent);
        } else if (astNode instanceof SynchronizedStatement) {
            return visit((SynchronizedStatement) astNode, parent);
        } else if (astNode instanceof ThrowStatement) {
            return visit((ThrowStatement) astNode, parent);
        } else if (astNode instanceof TryStatement) {
            return visit((TryStatement) astNode, parent);
        } else if (astNode instanceof TypeDeclarationStatement) {
            return visit((TypeDeclarationStatement) astNode, parent);
        } else if (astNode instanceof VariableDeclarationStatement) {
            return visit((VariableDeclarationStatement) astNode, parent);
        } else if (astNode instanceof WhileStatement) {
            return visit((WhileStatement) astNode, parent);
        } else if (astNode instanceof Annotation) {
            return visit((Annotation) astNode, parent);
        } else if (astNode instanceof ArrayAccess) {
            return visit((ArrayAccess) astNode, parent);
        } else if (astNode instanceof ArrayCreation) {
            return visit((ArrayCreation) astNode, parent);
        } else if (astNode instanceof ArrayInitializer) {
            return visit((ArrayInitializer) astNode, parent);
        } else if (astNode instanceof Assignment) {
            return visit((Assignment) astNode, parent);
        } else if (astNode instanceof BooleanLiteral) {
            return visit((BooleanLiteral)astNode, parent);
        } else if (astNode instanceof CastExpression) {
            return visit((CastExpression) astNode, parent);
        } else if (astNode instanceof CharacterLiteral) {
            return visit((CharacterLiteral) astNode, parent);
        } else if (astNode instanceof ClassInstanceCreation) {
            return visit((ClassInstanceCreation) astNode, parent);
        } else if (astNode instanceof ConditionalExpression) {
            return visit((ConditionalExpression) astNode, parent);
        } else if (astNode instanceof CreationReference) {
            return visit((CreationReference) astNode, parent);
        } else if (astNode instanceof ExpressionMethodReference) {
            return visit((ExpressionMethodReference) astNode, parent);
        } else if (astNode instanceof FieldAccess) {
            return visit((FieldAccess) astNode, parent);
        } else if (astNode instanceof InfixExpression) {
            return visit((InfixExpression) astNode, parent);
        } else if (astNode instanceof LambdaExpression) {
            return visit((LambdaExpression) astNode, parent);
        } else if (astNode instanceof MethodInvocation) {
            return visit((MethodInvocation) astNode, parent);
        } else if (astNode instanceof MethodReference) {
            return visit((MethodReference) astNode, parent);
        } else if (astNode instanceof Name) {
            return visit((Name) astNode, parent);
        } else if (astNode instanceof NullLiteral) {
            return visit((NullLiteral) astNode, parent);
        } else if (astNode instanceof NumberLiteral) {
            return visit((NumberLiteral) astNode, parent);
        } else if (astNode instanceof ParenthesizedExpression) {
            return visit((ParenthesizedExpression) astNode, parent);
        } else if (astNode instanceof PostfixExpression) {
            return visit((PostfixExpression) astNode, parent);
        } else if (astNode instanceof PrefixExpression) {
            return visit((PrefixExpression) astNode, parent);
        } else if (astNode instanceof QualifiedName) {
            return visit((QualifiedName) astNode, parent);
        } else if (astNode instanceof SimpleName) {
            return visit((SimpleName) astNode, parent);
        } else if (astNode instanceof StringLiteral) {
            return visit((StringLiteral) astNode, parent);
        } else if (astNode instanceof SuperFieldAccess) {
            return visit((SuperFieldAccess) astNode, parent);
        } else if (astNode instanceof SuperMethodInvocation) {
            return visit((SuperMethodInvocation) astNode, parent);
        } else if (astNode instanceof SuperMethodReference) {
            return visit((SuperMethodReference) astNode, parent);
        } else if (astNode instanceof ThisExpression) {
            return visit((ThisExpression) astNode, parent);
        } else if (astNode instanceof TypeMethodReference) {
            return visit((TypeMethodReference) astNode, parent);
        } else if (astNode instanceof TypeLiteral) {
            return visit((TypeLiteral) astNode, parent);
        } else if (astNode instanceof VariableDeclarationExpression) {
            return visit((VariableDeclarationExpression) astNode, parent);
        } else if (astNode instanceof Type) {
            return visit((Type) astNode, parent);
        } else if (astNode instanceof VariableDeclaration) {
            return visit((VariableDeclaration) astNode, parent);
        } else if (astNode instanceof SingleVariableDeclaration) {
            return visit((SingleVariableDeclaration) astNode, parent);
        } else if (astNode instanceof VariableDeclarationFragment) {
            return visit((VariableDeclarationFragment) astNode, parent);
        } else {
            System.out.println("UNKNOWN ASTNode type : " + astNode.toString());
            return null;
        }
    }

    private Node visit(MethodDeclaration astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        MethodDecl methodDecl = new MethodDecl(astNode, filePath, start, end);
        // modifiers
        List<String> modifiers = new ArrayList<>();
        for (Object obj : astNode.modifiers()) {
            modifiers.add(obj.toString());
        }
        methodDecl.setModifiers(modifiers);
        // return type
        if (astNode.getReturnType2() != null) {
            Type type = astNode.getReturnType2();
            String typeStr = JavaASTUtil.getSimpleType(type);
            methodDecl.setRetType(type, typeStr);
        }
        // method name
        SimpName mname = (SimpName) buildNode(astNode.getName(), methodDecl);
        methodDecl.setName(mname);
        // arguments
        List<ExprNode> paras = new ArrayList<>();
        for (Object obj : astNode.parameters()) {
            ExprNode para = (ExprNode) buildNode((ASTNode) obj, methodDecl);
            paras.add(para);
        }
        methodDecl.setParameters(paras);
        // throws types
        List<String> throwTypes = new ArrayList<>();
        for (Object obj : astNode.thrownExceptionTypes()) {
            Type throwType = (Type) obj;
            String throwTypeStr = JavaASTUtil.getSimpleType(throwType);
            throwTypes.add(throwTypeStr);
        }
        methodDecl.setThrows(throwTypes);
        // method body
        Block body = astNode.getBody();
        if (body != null) {
            BlockStmt blk = (BlockStmt) buildNode(body, methodDecl);
            methodDecl.setBody(blk);
        }

        return methodDecl;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setFilePath(String filepath) {
        this.filePath = filepath;
    }

    private int count = 0;
    private boolean isTooSmall(MethodDeclaration md) {
        md.accept(new ASTVisitor(false) {
            @Override
            public boolean preVisit2(ASTNode node) {
                if (node instanceof Statement) {
                    count++;
                }
                return true;
            }
        });
        return count < configuration.minStatements;
    }

    private boolean isEmpty() {
        return nodes.isEmpty();
    }

    private int getPrimitiveConstantType(ITypeBinding tb) {
        String type = tb.getName();
        if (type.equals("boolean"))
            return ASTNode.BOOLEAN_LITERAL;
        if (type.equals("char"))
            return ASTNode.CHARACTER_LITERAL;
        if (type.equals("void"))
            return -1;
        return ASTNode.NUMBER_LITERAL;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setCompilationUnit(CompilationUnit currentCU) {
        this.cu = currentCU;
    }
}
