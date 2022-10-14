package model;

import org.eclipse.jdt.core.dom.*;
import utils.JavaASTUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CodeGraph {
    private final GraphConfiguration configuration;

    private String filePath, name, projectName;
    private GraphBuildingContext context;
    protected BaseNode entryNode;
    protected HashSet<BaseNode> nodes = new HashSet<>();

    public CodeGraph(MethodDeclaration md, GraphBuildingContext context, GraphConfiguration configuration) {
        this(context, configuration);
        if (isTooSmall(md))
            return;
        context.setMethod(md);
        entryNode = new EntryNode(md, "START");
        nodes.add(entryNode);
        // parameters
        for (int i = 0; i < md.parameters().size(); i++) {
            SingleVariableDeclaration d = (SingleVariableDeclaration) md.parameters().get(i);
            CodeGraph pg = buildPDG(entryNode, d);
            mergeGraph(pg);
        }
        // body
        if (md.getBody() != null) {
            Block block = md.getBody();
            if (!block.statements().isEmpty()) {
                CodeGraph pdg = buildPDG(entryNode, block);
                mergeGraph(pdg);
            }
        }
    }

    public CodeGraph(GraphBuildingContext context, GraphConfiguration configuration) {
        this.context = context;
        this.configuration = configuration;
    }

    public CodeGraph buildCG(ASTNode astNode, ASTNode parent) {
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

    private CodeGraph buildPDG(BaseNode control, SingleVariableDeclaration astNode) {
        SimpleName name = astNode.getName();
        String type = JavaASTUtil.getSimpleType(astNode.getType());
        DataNode node = new DataNode(name, type, name.getIdentifier());
        CodeGraph pdg = new CodeGraph(context, configuration);
        pdg.mergeNode(node);
        return pdg;
    }

    private CodeGraph buildPDG(BaseNode control, Block astNode) {
        if (astNode.statements().size() > 0) {
            CodeGraph pdg = buildPDG(control, astNode.statements());
            return pdg;
        }
        return new CodeGraph(context, configuration);
    }

    private CodeGraph buildPDG(BaseNode control, List<?> list) {
        CodeGraph g = new CodeGraph(context, configuration);
        for (Object s : list) {
            if (s instanceof EmptyStatement) continue;
            CodeGraph pdg = buildPDG(control, (ASTNode) s);
            if (!pdg.isEmpty()) {
                g.mergeGraph(pdg);
            }
        }
        return g;
    }

    private CodeGraph buildPDG(BaseNode control, ASTNode node) {
        if (node instanceof VariableDeclarationStatement) {
            return buildPDG(control, (VariableDeclarationStatement) node);
        } else if (node instanceof VariableDeclarationFragment) {
            return buildPDG(control, (VariableDeclarationFragment) node);
        } else if (node instanceof SimpleName) {
            return buildPDG(control, (SimpleName) node);
        } else if (node instanceof NumberLiteral) {
            return buildPDG(control, (NumberLiteral) node);
        } else {
            return new CodeGraph(context, configuration);
        }
    }

    private CodeGraph buildPDG(BaseNode control, NumberLiteral astNode) {
        String type = "number";
        if (astNode.resolveTypeBinding() != null)
            type = astNode.resolveTypeBinding().getName();
        CodeGraph pdg = new CodeGraph(context, new DataNode(astNode, type, null, astNode.getToken()), configuration);
        return pdg;
    }

    private CodeGraph buildPDG(BaseNode control, VariableDeclarationStatement astNode) {
        CodeGraph pdg = buildPDG(control, (ASTNode) astNode.fragments().get(0));
        for (int i = 1; i < astNode.fragments().size(); i++)
            pdg.mergeGraph(buildPDG(control, (ASTNode) astNode.fragments().get(i)));
        return pdg;
    }

    private CodeGraph buildPDG(BaseNode control, VariableDeclarationFragment astNode) {
        SimpleName name = astNode.getName();
        String type = JavaASTUtil.getSimpleType(astNode);
        DataNode node = new DataNode(name, type, name.getIdentifier());
        CodeGraph pdg = buildPDG(control, astNode.getInitializer());
        pdg.mergeNode(node);
        return pdg;
    }

    private CodeGraph buildPDG(BaseNode control, SimpleName astNode) {
        String constantName = null, constantValue = null, type = null;
        int astNodeType = ASTNode.SIMPLE_NAME;
        IBinding b = astNode.resolveBinding();
        if (b instanceof IVariableBinding) {
            IVariableBinding vb = (IVariableBinding) b;
            vb = vb.getVariableDeclaration();
            if (vb.getType().getTypeDeclaration() != null) {
                type = vb.getType().getTypeDeclaration().getName();
            } else {
                type = vb.getType().getName();
            }
            if (JavaASTUtil.isConstant(vb)) {
                if (vb.getDeclaringClass().getTypeDeclaration() != null) {
                    constantName = vb.getDeclaringClass().getTypeDeclaration().getName() + "." + vb.getName();
                } else {
                    constantName = vb.getName();
                }
                if (vb.getConstantValue() != null) {
                    ITypeBinding tb = vb.getType();
                    if (tb.isPrimitive()) {
                        constantValue = vb.getConstantValue().toString();
                        astNodeType = getPrimitiveConstantType(tb);
                    } else if (tb.getName().equals("String")) {
                        constantValue = vb.getConstantValue().toString();
                        astNodeType = ASTNode.STRING_LITERAL;
                    } else if (tb.getName().equals("Boolean")) {
                        constantValue = vb.getConstantValue().toString();
                        astNodeType = ASTNode.BOOLEAN_LITERAL;
                    } else if (tb.getName().equals("Character")) {
                        constantValue = vb.getConstantValue().toString();
                        astNodeType = ASTNode.CHARACTER_LITERAL;
                    } else if (tb.getSuperclass() != null && tb.getSuperclass().getName().equals("Number")) {
                        constantValue = vb.getConstantValue().toString();
                        astNodeType = ASTNode.NUMBER_LITERAL;
                    }
                }
            }
        }
        if (constantName != null) {
            DataNode node = new DataNode(astNode, type, constantName, constantValue);
            return new CodeGraph(context, node, configuration);
        }
        String name = astNode.getIdentifier();
        if (astNode.resolveTypeBinding() != null) {
            type = astNode.resolveTypeBinding().getTypeDeclaration().getName();
        }
        String[] info = context.getLocalVariableInfo(name);
        if (info != null) {
            CodeGraph pdg = new CodeGraph(context, new DataNode(
                    astNode, type == null ? info[1] : type, astNode.getIdentifier()), configuration);
            return pdg;
        }
        if (type == null)
            type = context.getFieldType(astNode);
        if (type != null) {
            CodeGraph pdg = new CodeGraph(context, new DataNode(astNode, type, name), configuration);
            return pdg;
        }
        if (name.equals(name.toUpperCase()))
            return new CodeGraph(context, new DataNode(astNode, name, name, null), configuration);
        if (astNodeType == -1) {
            CodeGraph pdg = new CodeGraph(context, new DataNode(null, context.getType(), "this"), configuration);
            pdg.mergeNode(new DataNode(astNode, "UNKNOWN", name));
            return pdg;
        } else {
            CodeGraph pdg = new CodeGraph(context, new DataNode(astNode, "UNKNOWN", name), configuration);
            return pdg;
        }
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
}
