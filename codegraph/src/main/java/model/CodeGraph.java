package model;

import model.graph.node.CatClause;
import model.graph.node.Node;
import model.graph.node.bodyDecl.MethodDecl;
import model.graph.node.expr.ExprList;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.SimpName;
import model.graph.node.expr.VarDeclExpr;
import model.graph.node.stmt.*;
import model.graph.node.type.TypeNode;
import model.graph.node.varDecl.SingleVarDecl;
import model.graph.node.varDecl.VarDeclFrag;
import org.eclipse.jdt.core.dom.*;
import utils.JavaASTUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CodeGraph {
    private final GraphConfiguration configuration;

    private String filePath, name, projectName;
    private GraphBuildingContext context;
    protected HashSet<Node> nodes = new HashSet<>();

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
        } else if (astNode instanceof CatchClause) {
            return visit((CatchClause) astNode, parent);
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
            TypeNode type = (TypeNode) buildNode(astNode.getReturnType2(), methodDecl);
            String typeStr = JavaASTUtil.getSimpleType(astNode.getReturnType2());
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

    private Node visit(AssertStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AssertStmt assertStmt = new AssertStmt(astNode, filePath, start, end);
        // expression
        Expression expression = astNode.getExpression();
        ExprNode expr = (ExprNode) buildNode(expression, assertStmt);
        assertStmt.setExpr(expr);
        // message
        if (astNode.getMessage() != null) {
            ExprNode message = (ExprNode) buildNode(astNode.getMessage(), assertStmt);
            assertStmt.setMessage(message);
        }

        return assertStmt;
    }

    private Node visit(Block astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        BlockStmt blk = new BlockStmt(astNode, filePath, start, end);
        // stmt list
        List<StmtNode> stmts = new ArrayList<>();
        for (Object object : astNode.statements()) {
            StmtNode stmt = (StmtNode) buildNode((ASTNode) object, blk);
            stmts.add(stmt);
        }
        blk.setStatement(stmts);
        return blk;
    }

    private Node visit(BreakStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        BreakStmt brk = new BreakStmt(astNode, filePath, start, end);
        // identifier
        if (astNode.getLabel() != null) {
            SimpName sName = (SimpName) buildNode(astNode.getLabel(), brk);
            brk.setIdentifier(sName);
        }
        return brk;
    }

    private Node visit(SwitchCase astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CaseStmt swCase = new CaseStmt(astNode, filePath, start, end);
        // case expression
        if (astNode.getExpression() != null) {
            ExprNode expression = (ExprNode) buildNode(astNode.getExpression(), swCase);
            swCase.setExpression(expression);
        }
        return swCase;
    }

    private Node visit(ConstructorInvocation astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ConstructorInvoc consInv = new ConstructorInvoc(astNode, filePath, start, end);
        // arguments
        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> argulist = new ArrayList<>();
        for (Object object : astNode.arguments()) {
            ExprNode expr = (ExprNode) buildNode((ASTNode) object, exprList);
            argulist.add(expr);
        }
        exprList.setExprs(argulist);
        consInv.setArguments(exprList);

        return consInv;
    }

    private Node visit(ContinueStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ContinueStmt conti = new ContinueStmt(astNode, filePath, start, end);
        // identifier
        if (astNode.getLabel() != null) {
            SimpName sName = (SimpName) buildNode(astNode.getLabel(), conti);
            conti.setIdentifier(sName);
        }
        return conti;
    }

    private Node visit(DoStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        DoStmt doStmt = new DoStmt(astNode, filePath, start, end);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), doStmt);
        doStmt.setExpr(expr);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), doStmt);
        doStmt.setBody(body);

        return doStmt;
    }

    private Node visit(EmptyStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        EmptyStmt ept = new EmptyStmt(astNode, filePath, start, end);
        return ept;
    }

    private Node visit(EnhancedForStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        EnhancedForStmt efor = new EnhancedForStmt(astNode, filePath, start, end);
        // formal parameter
        SingleVarDecl svd =  (SingleVarDecl) buildNode(astNode.getParameter(), efor);
        efor.setSVD(svd);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), efor);
        efor.setExpr(expr);
        // body
        StmtNode stmt = wrapBlock(astNode.getBody(), efor);
        efor.setBody(stmt);

        return efor;
    }

    private Node visit(ExpressionStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ExprStmt exprStmt = new ExprStmt(astNode, filePath, start, end);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), exprStmt);
        exprStmt.setExpr(expr);

        return exprStmt;
    }

    private Node visit(ForStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ForStmt forStmt = new ForStmt(astNode, filePath, start, end);
        // initializers
        ExprList initExprList = new ExprList(null, filePath, start, end);
        List<ExprNode> initializers = new ArrayList<>();
        if (!astNode.initializers().isEmpty()) {
            for (Object object : astNode.initializers()) {
                ExprNode initializer = (ExprNode) buildNode((ASTNode) object, initExprList);
                initializers.add(initializer);
            }
        }
        initExprList.setExprs(initializers);
        forStmt.setInitializer(initExprList);
        // expression
        if (astNode.getExpression() != null) {
            ExprNode condition = (ExprNode) buildNode(astNode.getExpression(), forStmt);
            forStmt.setCondition(condition);
        }
        // updaters
        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> updaters = new ArrayList<>();
        if (!astNode.updaters().isEmpty()) {
            for (Object object : astNode.updaters()) {
                ExprNode update = (ExprNode) buildNode((ASTNode) object, exprList);
                updaters.add(update);
            }
        }
        exprList.setExprs(updaters);
        forStmt.setUpdaters(exprList);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), forStmt);
        forStmt.setBody(body);

        return forStmt;
    }

    private Node visit(IfStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        IfStmt ifstmt = new IfStmt(astNode, filePath, start, end);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), ifstmt);
        ifstmt.setExpression(expr);
        // then statement
        StmtNode then = wrapBlock(astNode.getThenStatement(), ifstmt);
        ifstmt.setThen(then);
        // else statement
        if (astNode.getElseStatement() != null) {
            StmtNode els = wrapBlock(astNode.getElseStatement(), ifstmt);
            ifstmt.setElse(els);
        }
        return ifstmt;
    }

    private Node visit(LabeledStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        LabeledStmt labStmt = new LabeledStmt(astNode, filePath, start, end);
        // label
        SimpName lab = (SimpName) buildNode(astNode.getLabel(), labStmt);
        labStmt.setLabel(lab);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), labStmt);
        labStmt.setBody(body);
        return labStmt;
    }

    private Node visit(ReturnStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ReturnStmt ret = new ReturnStmt(astNode, filePath, start, end);
        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), ret);
            ret.setExpr(expr);
        }
        return ret;
    }

    private Node visit(SuperConstructorInvocation astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperConstructorInvoc spi = new SuperConstructorInvoc(astNode, filePath, start, end);
        // expression
        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), spi);
            spi.setExpr(expr);
        }
        // parameters
        ExprList arguList = new ExprList(null, filePath, start, end);
        List<ExprNode> argus = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode para = (ExprNode) buildNode((ASTNode) obj, arguList);
            argus.add(para);
        }
        arguList.setExprs(argus);
        spi.setArguments(arguList);

        return spi;
    }

    private Node visit(SwitchStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SwitchStmt swi = new SwitchStmt(astNode, filePath, start, end);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), swi);
        swi.setExpr(expr);
        // stmt list
        List<StmtNode> stmts = new ArrayList<>();
        Node swiCase = null;
        for (Object obj : astNode.statements()) {
            StmtNode stmt = (StmtNode) buildNode((ASTNode) obj, swi);
            // TODO: set variable scope for each switch case
            stmts.add(stmt);
            if (stmt instanceof CaseStmt) {
                swiCase = stmt;
            }
        }
        swi.setStatements(stmts);
        return swi;
    }

    private Node visit(SynchronizedStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SynchronizedStmt syn = new SynchronizedStmt(astNode, filePath, start, end);
        // expression
        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), syn);
            syn.setExpr(expr);
        }
        // body
        BlockStmt body = (BlockStmt) buildNode(astNode.getBody(), syn);
        syn.setBody(body);

        return syn;
    }

    private Node visit(ThrowStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ThrowStmt throwStmt = new ThrowStmt(astNode, filePath, start, end);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), throwStmt);
        throwStmt.setExpr(expr);

        return throwStmt;
    }

    private Node visit(TryStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TryStmt tryStmt = new TryStmt(astNode, filePath, start, end);
        // resources
        if (astNode.resources() != null) {
            List<VarDeclExpr> resourceList = new ArrayList<>();
            for (Object obj : astNode.resources()) {
                VariableDeclarationExpression resource = (VariableDeclarationExpression) obj;
                VarDeclExpr vdExpr = (VarDeclExpr) buildNode(resource, tryStmt);
                resourceList.add(vdExpr);
            }
            tryStmt.setResources(resourceList);
        }
        BlockStmt blk = (BlockStmt) buildNode(astNode.getBody(), tryStmt);
        tryStmt.setBody(blk);
        // catch
        List<CatClause> catches = new ArrayList<>(astNode.catchClauses().size());
        for (Object obj : astNode.catchClauses()) {
            CatchClause catchClause = (CatchClause) obj;
            CatClause catClause = (CatClause) buildNode(catchClause, tryStmt);
            catches.add(catClause);
        }
        tryStmt.setCatchClause(catches);
        // finally
        if (astNode.getFinally() != null ){
            BlockStmt finallyBlk = (BlockStmt) buildNode(astNode.getFinally(), tryStmt);
            tryStmt.setFinallyBlock(finallyBlk);
        }

        return tryStmt;
    }

    private CatClause visit(CatchClause astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CatClause cat = new CatClause(astNode, filePath, start, end);

        SingleVarDecl svd = (SingleVarDecl) buildNode(astNode.getException(), cat);
        cat.setException(svd);

        BlockStmt body = (BlockStmt) buildNode(astNode.getBody(), cat);
        cat.setBody(body);

        return cat;
    }

    private Node visit(TypeDeclarationStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypeDeclStmt td = new TypeDeclStmt(astNode, filePath, start, end);
        return td;
    }

    private Node visit(VariableDeclarationStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        VarDeclStmt vdStmt = new VarDeclStmt(astNode, filePath, start, end);
        // modifiers
        String modifier = "";
        if (astNode.modifiers() != null && astNode.modifiers().size() > 0) {
            for (Object obj : astNode.modifiers()) {
                modifier += " " + obj.toString();
            }
        }
        vdStmt.setModifier(modifier);
        // type
        TypeNode type = (TypeNode) buildNode(astNode.getType(), vdStmt);
        String typeStr = JavaASTUtil.getSimpleType(astNode.getType());
        vdStmt.setDeclType(type, typeStr);
        // fragments
        List<VarDeclFrag> fragments = new ArrayList<>();
        for (Object obj : astNode.fragments()) {
            VarDeclFrag vdf = (VarDeclFrag) buildNode((ASTNode) obj, vdStmt);
            vdf.setType(type, typeStr);
            fragments.add(vdf);
        }
        vdStmt.setFragments(fragments);

        return vdStmt;
    }

    private Node visit(WhileStatement astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        WhileStmt whi = new WhileStmt(astNode, filePath, start, end);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), whi);
        whi.setExpr(expr);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), whi);
        whi.setBody(body);

        return whi;
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

    private BlockStmt wrapBlock(Statement node, Node parent) {
        BlockStmt blk;
        if(node instanceof Block) {
            blk = (BlockStmt) buildNode(node, parent);
        } else {
            int startLine = cu.getLineNumber(node.getStartPosition());
            int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
            blk = new BlockStmt(node, filePath, startLine, endLine);
            List<StmtNode> stmts = new ArrayList<>();
            StmtNode stmt = (StmtNode) buildNode(node, blk);
            stmts.add(stmt);
            blk.setStatement(stmts);
        }
        return blk;
    }
}
