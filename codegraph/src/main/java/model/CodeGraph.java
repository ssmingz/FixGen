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
    protected HashSet<BaseNode> statementNodes = new HashSet<>();

    public CodeGraph(MethodDeclaration md, GraphBuildingContext context, GraphConfiguration configuration) {
        this(context, configuration);
        if (isTooSmall(md))
            return;
        context.addScope();
        context.setMethod(md);
        entryNode = new EntryNode(md, "START");
        nodes.add(entryNode);
        statementNodes.add(entryNode);
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

    public CodeGraph(GraphBuildingContext context, BaseNode node, GraphConfiguration configuration) {
        this(context, configuration);
        init(node);
    }

    private void init(BaseNode node) {
        nodes.add(node);
        statementNodes.add(node);
    }

    private CodeGraph buildPDG(BaseNode control, SingleVariableDeclaration astNode) {
        SimpleName name = astNode.getName();
        String type = JavaASTUtil.getSimpleType(astNode.getType());
        context.addLocalVariable(name.getIdentifier(), "" + name.getStartPosition(), type);
        DataNode node = new DataNode(name, type, name.getIdentifier());
        CodeGraph pdg = new CodeGraph(context, configuration);
        pdg.mergeNode(node);
        return pdg;
    }

    private CodeGraph buildPDG(BaseNode control, Block astNode) {
        if (astNode.statements().size() > 0) {
            context.addScope();
            CodeGraph pdg = buildPDG(control, astNode.statements());
            context.removeScope();
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
        context.addLocalVariable(name.getIdentifier(), "" + name.getStartPosition(), type);
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

    private void mergeNode(BaseNode next) {
        nodes.add(next);
        statementNodes.add(next);
    }

    private void mergeGraph(CodeGraph pdg) {
        if (pdg.statementNodes.isEmpty())
            return;
        // if the left side of the join is empty, the entire right side becomes the result, since there are not
        // sinks to connect to the sources of the right side.
        this.nodes.addAll(pdg.nodes);
        this.statementNodes.addAll(pdg.statementNodes);
        pdg.clear();
    }

    private void clear() {
        nodes.clear();
        statementNodes.clear();
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
