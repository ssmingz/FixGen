package model;

import org.eclipse.jdt.core.dom.*;
import utils.JavaASTUtil;

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
        context.setMethod(md);
        entryNode = new EntryNode(md, "START");
        nodes.add(entryNode);
        statementNodes.add(entryNode);
        // parameters
        for (int i = 0; i < md.parameters().size(); i++) {
            SingleVariableDeclaration d = (SingleVariableDeclaration) md.parameters().get(i);
            CodeGraph pg = buildPDG(entryNode, d);
            this.nodes.addAll(pg.nodes);
        }
        // body
        if (md.getBody() != null) {
            Block block = md.getBody();
            if (!block.statements().isEmpty()) {
                CodeGraph pdg = buildPDG(entryNode, block);
            }
        }
    }

    public CodeGraph(GraphBuildingContext context, GraphConfiguration configuration) {
        this.context = context;
        this.configuration = configuration;
    }

    private CodeGraph buildPDG(BaseNode control, SingleVariableDeclaration astNode) {
        SimpleName name = astNode.getName();
        String type = JavaASTUtil.getSimpleType(astNode.getType());
        context.addLocalVariable(name.getIdentifier(), "" + name.getStartPosition(), type);
        DataNode node = new DataNode(name, type, name.getIdentifier());
        CodeGraph pdg = new CodeGraph(context, configuration);
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
                g.mergeSequential(pdg);
            }
            if (s instanceof ReturnStatement || s instanceof ThrowStatement) {
                g.clearDefStore();
                return g;
            }
        }
        return g;
    }

    private void mergeSequential(CodeGraph pdg) {
        if (pdg.statementNodes.isEmpty())
            return;
        if (this.isEmpty()) {
            // if the left side of the join is empty, the entire right side becomes the result, since there are not
            // sinks to connect to the sources of the right side.
            this.statementSources.addAll(pdg.statementSources);
        }
        connectSinksToSourcesOf(pdg);
        for (BaseNode sink : pdg.sinks)
            sink.consumeDefStore(this);
        for (BaseNode sink : statementSinks) {
            for (BaseNode source : pdg.statementSources) {
                new BaseEdge(sink, source, DEPENDENCE);
            }
        }
        this.dataSources.addAll(pdg.dataSources);
        this.sinks.clear();
        this.sinks.addAll(pdg.sinks);
        this.statementSinks.clear();
        this.statementSinks.addAll(pdg.statementSinks);
        this.nodes.addAll(pdg.nodes);
        this.statementNodes.addAll(pdg.statementNodes);
        this.breaks.addAll(pdg.breaks);
        this.returns.addAll(pdg.returns);
        pdg.clear();
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
}
