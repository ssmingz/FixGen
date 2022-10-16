package model;

import model.graph.node.AnonymousClassDecl;
import model.graph.node.CatClause;
import model.graph.node.Node;
import model.graph.node.bodyDecl.MethodDecl;
import model.graph.node.expr.*;
import model.graph.node.expr.MethodRef;
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

    public Node entryNode = null;

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
            vdf.setDeclType(type);
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

    private Node visit(Annotation astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AnnotationExpr anno = new AnnotationExpr(astNode, filePath, start, end);
        return anno;
    }

    private Node visit(ArrayAccess astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AryAcc aryAcc = new AryAcc(astNode, filePath, start, end);
        // array
        ExprNode array = (ExprNode) buildNode(astNode.getArray(), aryAcc);
        aryAcc.setArray(array);
        // index
        ExprNode index = (ExprNode) buildNode(astNode.getIndex(), aryAcc);
        aryAcc.setIndex(index);
        // type
        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        String typeStr = JavaASTUtil.getSimpleType(type);
        TypeNode typ = (TypeNode) buildNode(type, aryAcc);
        aryAcc.setType(typ, typeStr);

        return aryAcc;
    }

    private Node visit(ArrayCreation astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AryCreation arycr = new AryCreation(astNode, filePath, start, end);
        // type
        Type type = typeFromBinding(astNode.getAST(), astNode.getType().getElementType().resolveBinding());
        if (type == null || type instanceof WildcardType) {
            type = astNode.getType().getElementType();
        }
        TypeNode typ = (TypeNode) buildNode(astNode.getType().getElementType(), arycr);
        String typStr = JavaASTUtil.getSimpleType(type);
        arycr.setArrayType(typ, typStr);
        arycr.setType(astNode.getType());
        // dimension
        ExprList dimlist = new ExprList(null, filePath, start, end);
        List<ExprNode> dimension = new ArrayList<>();
        for (Object obj : astNode.dimensions()) {
            ExprNode dim = (ExprNode) buildNode((ASTNode) obj, arycr);
            dimension.add(dim);
        }
        dimlist.setExprs(dimension);
        arycr.setDimension(dimlist);
        // initializer
        if (astNode.getInitializer() != null) {
            AryInitializer aryinit = (AryInitializer) buildNode(astNode.getInitializer(), arycr);
            arycr.setInitializer(aryinit);
        }

        return arycr;
    }

    private AryInitializer visit(ArrayInitializer astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AryInitializer aryinit = new AryInitializer(astNode, filePath, start, end);
        List<ExprNode> exprs = new ArrayList<>();
        for (Object obj : astNode.expressions()) {
            ExprNode expr = (ExprNode) buildNode((ASTNode) obj, aryinit);
            exprs.add(expr);
        }
        aryinit.setExpressions(exprs);

        return aryinit;
    }

    private AssignExpr visit(Assignment astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AssignExpr assign = new AssignExpr(astNode, filePath, start, end);
        // left
        ExprNode lhs = (ExprNode) buildNode(astNode.getLeftHandSide(), assign);
        assign.setLeftHandSide(lhs);
        // right
        ExprNode rhs = (ExprNode) buildNode(astNode.getRightHandSide(), assign);
        assign.setRightHandSide(rhs);
        // operator
        AssignOpr assignOpr = new AssignOpr(null, filePath, start, end);
        assignOpr.setOperator(astNode.getOperator());
        assignOpr.setParent(assign);
        assign.setOperator(assignOpr);

        return assign;
    }

    private BoolLiteral visit(BooleanLiteral astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        BoolLiteral literal = new BoolLiteral(astNode, filePath, start, end);
        literal.setValue(astNode.booleanValue());
        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        literal.setType(type);

        return literal;
    }

    private CastExpr visit(CastExpression astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CastExpr cast = new CastExpr(astNode, filePath, start, end);
        // type
        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), cast);
        cast.setCastType(typeNode);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), cast);
        cast.setExpression(expr);
        cast.setType(astNode.getType());

        return cast;
    }

    private CharLiteral visit(CharacterLiteral astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CharLiteral literal = new CharLiteral(astNode, filePath, start, end);
        literal.setValue(astNode.charValue());
        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        literal.setType(type);

        return literal;
    }

    private ClassInstanceCreationExpr visit(ClassInstanceCreation astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ClassInstanceCreationExpr classCreation = new ClassInstanceCreationExpr(astNode, filePath, start, end);

        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), classCreation);
            classCreation.setExpression(expr);
        }

        if (astNode.getAnonymousClassDeclaration() != null) {
            AnonymousClassDecl anony = (AnonymousClassDecl) buildNode(astNode.getAnonymousClassDeclaration(), classCreation);
            classCreation.setAnonymousClassDecl(anony);
        }

        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> argus = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode arg = (ExprNode) buildNode((ASTNode) obj, exprList);
            argus.add(arg);
        }
        exprList.setExprs(argus);
        classCreation.setArguments(exprList);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), classCreation);
        classCreation.setClassType(typeNode);
        classCreation.setType(astNode.getType());

        return classCreation;
    }

    private AnonymousClassDecl visit(AnonymousClassDeclaration astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AnonymousClassDecl anony = new AnonymousClassDecl(astNode, filePath, start, end);
        return anony;
    }

    private ConditionalExpr visit(ConditionalExpression astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ConditionalExpr condExpr = new ConditionalExpr(astNode, filePath, start, end);

        ExprNode condition = (ExprNode) buildNode(astNode.getExpression(), condExpr);
        condExpr.setCondition(condition);

        ExprNode thenExpr = (ExprNode) buildNode(astNode.getThenExpression(), condExpr);
        condExpr.setThenExpr(thenExpr);

        ExprNode elseExpr = (ExprNode) buildNode(astNode.getElseExpression(), condExpr);
        condExpr.setElseExpr(elseExpr);

        return condExpr;
    }

    private CreationRef visit(CreationReference astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CreationRef cref = new CreationRef(astNode, filePath, start, end);
        // TODO: parse nodes for CreationReference
        return cref;
    }

    private ExprMethodRef visit(ExpressionMethodReference astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ExprMethodRef exprMethodRef = new ExprMethodRef(astNode, filePath, start, end);
        // TODO: parse nodes for ExpressionMethodReference
        return exprMethodRef;
    }

    private FieldAcc visit(FieldAccess astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        FieldAcc fieldAcc = new FieldAcc(astNode, filePath, start, end);

        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), fieldAcc);
        fieldAcc.setExpression(expr);

        SimpName iden = (SimpName) buildNode(astNode.getName(), fieldAcc);
        fieldAcc.setIdentifier(iden);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        fieldAcc.setType(type);

        return fieldAcc;
    }

    private InfixExpr visit(InfixExpression astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        InfixExpr infixExpr = new InfixExpr(astNode, filePath, start, end);

        ExprNode lhs = (ExprNode) buildNode(astNode.getLeftOperand(), infixExpr);
        infixExpr.setLeftHandSide(lhs);

        ExprNode rhs = (ExprNode) buildNode(astNode.getRightOperand(), infixExpr);
        infixExpr.setRightHandSide(rhs);

        InfixOpr infixOpr = new InfixOpr(null, filePath, start, end);
        infixOpr.setOperator(astNode.getOperator());
        infixExpr.setOperatior(infixOpr);

        if (astNode.hasExtendedOperands()) {
            lhs = infixExpr;
            for (Object obj : astNode.extendedOperands()) {
                rhs = (ExprNode) buildNode((Expression) obj, infixExpr);
                infixExpr = new InfixExpr((ASTNode) obj, filePath, start, end);
                infixExpr.setLeftHandSide(lhs);
                infixExpr.setRightHandSide(rhs);

                infixOpr = new InfixOpr(null, filePath, start, end);
                infixOpr.setOperator(astNode.getOperator());

                lhs = infixExpr;
            }
        }

        return infixExpr;
    }

    private InstanceofExpr visit(InstanceofExpression astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        InstanceofExpr instanceofExpr = new InstanceofExpr(astNode, filePath, start, end);

        ExprNode expr = (ExprNode) buildNode(astNode.getLeftOperand(), instanceofExpr);
        instanceofExpr.setExpression(expr);

        TypeNode instType = (TypeNode) buildNode(astNode.getRightOperand(), instanceofExpr);
        instanceofExpr.setInstanceType(instType);

        return instanceofExpr;
    }

    private LambdaExpr visit(LambdaExpression astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        LambdaExpr lambdaExpr = new LambdaExpr(astNode, filePath, start, end);
        // TODO: parse nodes for lambda expression
        return lambdaExpr;
    }

    private MethodInvoc visit(MethodInvocation astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        MethodInvoc methodInvoc = new MethodInvoc(astNode, filePath, start, end);

        ExprNode expr = null;
        if (astNode.getExpression() != null) {
            expr = (ExprNode) buildNode(astNode.getExpression(), methodInvoc);
            methodInvoc.setExpression(expr);
        }

        SimpName iden = (SimpName) buildNode(astNode.getName(), methodInvoc);
        methodInvoc.setName(iden);

        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> args = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode arg = (ExprNode) buildNode((ASTNode) obj, exprList);
            args.add(arg);
        }
        exprList.setExprs(args);
        methodInvoc.setArguments(exprList);

        return methodInvoc;
    }

    private MethodRef visit(MethodReference astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        MethodRef mref = new MethodRef(astNode, filePath, start, end);
        return mref;
    }

    private SimpName visit(SimpleName astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SimpName sname = new SimpName(astNode, filePath, start, end);

        String name = astNode.getFullyQualifiedName();
        sname.setName(name);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        sname.setType(type);

        return sname;
    }

    private QuaName visit(QualifiedName astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        QuaName qname = new QuaName(astNode, filePath, start, end);
        // Name . SimpleName
        SimpName sname = (SimpName) buildNode(astNode.getName(), qname);
        qname.setName(sname);

        NameExpr name = (NameExpr) buildNode(astNode.getQualifier(), qname);
        qname.setQualifier(name);

        return qname;
    }

    private NulLiteral visit(NullLiteral astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        NulLiteral literal = new NulLiteral(astNode, filePath, start, end);
        return literal;
    }

    private NumLiteral visit(NumberLiteral astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        NumLiteral literal = new NumLiteral(astNode, filePath, start, end);

        String value = astNode.getToken();
        literal.setValue(value);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        literal.setType(type);

        return literal;
    }

    private ParenExpr visit(ParenthesizedExpression astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ParenExpr parenthesiszedExpr = new ParenExpr(astNode, filePath, start, end);

        ExprNode expression = (ExprNode) buildNode(astNode.getExpression(), parenthesiszedExpr);
        parenthesiszedExpr.setExpr(expression);

        return parenthesiszedExpr;
    }

    private PostfixExpr visit(PostfixExpression astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        PostfixExpr postfixExpr = new PostfixExpr(astNode, filePath, start, end);

        ExprNode expr = (ExprNode) buildNode(astNode.getOperand(), postfixExpr);
        postfixExpr.setExpr(expr);

        PostfixOpr postfixOpr = new PostfixOpr(null, filePath, start, end);
        postfixOpr.setOperator(astNode.getOperator());
        postfixExpr.setOpr(postfixOpr);

        return postfixExpr;
    }

    private PrefixExpr visit(PrefixExpression astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        PrefixExpr prefixExpr = new PrefixExpr(astNode, filePath, start, end);

        ExprNode expr = (ExprNode) buildNode(astNode.getOperand(), prefixExpr);
        prefixExpr.setExpr(expr);

        PrefixOpr prefixOpr = new PrefixOpr(null, filePath, start, end);
        prefixOpr.setOperator(astNode.getOperator());
        prefixExpr.setOpr(prefixOpr);

        return prefixExpr;
    }

    private StrLiteral visit(StringLiteral astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        StrLiteral literal = new StrLiteral(astNode, filePath, start, end);

        literal.setLiteralValue(astNode.getLiteralValue());
        literal.setEscapedValue(astNode.getEscapedValue());

        return literal;
    }

    private SuperFieldAcc visit(SuperFieldAccess astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperFieldAcc superFieldAcc = new SuperFieldAcc(astNode, filePath, start, end);

        SimpName iden = (SimpName) buildNode(astNode.getName(), superFieldAcc);
        superFieldAcc.setIdentifier(iden);

        if (astNode.getQualifier() != null) {
            QuaName qname = (QuaName) buildNode(astNode.getQualifier(), superFieldAcc);
            superFieldAcc.setQualifier(qname);
        }

        return superFieldAcc;
    }

    private SuperMethodInvoc visit(SuperMethodInvocation astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperMethodInvoc superMethodInvoc = new SuperMethodInvoc(astNode, filePath, start, end);

        SimpName name = (SimpName) buildNode(astNode.getName(), superMethodInvoc);
        superMethodInvoc.setName(name);

        if (astNode.getQualifier() != null) {
            QuaName qualifier = (QuaName) buildNode(astNode.getQualifier(), superMethodInvoc);
            superMethodInvoc.setQualifier(qualifier);
        }

        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> args = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode arg = (ExprNode) buildNode((ASTNode) obj, exprList);
            args.add(arg);
        }
        exprList.setExprs(args);
        superMethodInvoc.setArguments(exprList);

        return superMethodInvoc;
    }

    private SuperMethodRef visit(SuperMethodReference astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperMethodRef superMethodRef = new SuperMethodRef(astNode, filePath, start, end);
        // TODO: parse nodes for SuperMethodReference
        return superMethodRef;
    }

    private ThisExpr visit(ThisExpression astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ThisExpr thisExpr = new ThisExpr(astNode, filePath, start, end);
        return thisExpr;
    }

    private TypLiteral visit(TypeLiteral astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypLiteral literal = new TypLiteral(astNode, filePath, start, end);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), literal);
        literal.setValue(typeNode);

        return literal;
    }

    private TypeMethodRef visit(TypeMethodReference astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypeMethodRef typeMethodRef = new TypeMethodRef(astNode, filePath, start, end);
        // TODO: parse nodes for TypeMethodReference
        return typeMethodRef;
    }

    private VarDeclExpr visit(VariableDeclarationExpression astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        VarDeclExpr varDeclExpr = new VarDeclExpr(astNode, filePath, start, end);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), varDeclExpr);
        varDeclExpr.setDeclType(typeNode);

        List<VarDeclFrag> fragments = new ArrayList<>();
        for (Object obj : astNode.fragments()) {
            VarDeclFrag vdf = (VarDeclFrag) buildNode((ASTNode) obj, varDeclExpr);
            vdf.setDeclType(typeNode);
            fragments.add(vdf);
        }
        varDeclExpr.setFragments(fragments);


        return varDeclExpr;
    }

    private VarDeclFrag visit(VariableDeclarationFragment astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        VarDeclFrag vdf = new VarDeclFrag(astNode, filePath, start, end);

        SimpName iden = (SimpName) buildNode(astNode.getName(), vdf);
        vdf.setName(iden);

        vdf.setDimensions(astNode.getExtraDimensions());

        if (astNode.getInitializer() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getInitializer(), vdf);
            vdf.setExpr(expr);
        }

        return vdf;
    }

    private SingleVarDecl visit(SingleVariableDeclaration astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SingleVarDecl svd = new SingleVarDecl(astNode, filePath, start, end);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), svd);
        svd.setDeclType(typeNode);

        if (astNode.getInitializer() != null) {
            ExprNode init = (ExprNode) buildNode(astNode.getInitializer(), svd);
            svd.setInitializer(init);
        }

        SimpName name = (SimpName) buildNode(astNode.getName(), svd);
        svd.setName(name);

        return svd;
    }

    private TypeNode visit(Type astNode, Node parent) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypeNode typeNode = new TypeNode(astNode, filePath, start, end);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveBinding());
        if (type == null || type instanceof WildcardType) {
            type = astNode;
        }
        typeNode.setType(type);

        return typeNode;
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

    private static Type typeFromBinding(AST ast, ITypeBinding typeBinding) {
        if (typeBinding == null) {
            return ast.newWildcardType();
        }

        if (typeBinding.isPrimitive()) {
            return ast.newPrimitiveType(
                    PrimitiveType.toCode(typeBinding.getName()));
        }

        if (typeBinding.isCapture()) {
            ITypeBinding wildCard = typeBinding.getWildcard();
            WildcardType capType = ast.newWildcardType();
            ITypeBinding bound = wildCard.getBound();
            if (bound != null) {
                capType.setBound(typeFromBinding(ast, bound),
                        wildCard.isUpperbound());
            }
            return capType;
        }

        if (typeBinding.isArray()) {
            Type elType = typeFromBinding(ast, typeBinding.getElementType());
            return ast.newArrayType(elType, typeBinding.getDimensions());
        }

        if (typeBinding.isParameterizedType()) {
            ParameterizedType type = ast.newParameterizedType(
                    typeFromBinding(ast, typeBinding.getErasure()));

            @SuppressWarnings("unchecked")
            List<Type> newTypeArgs = type.typeArguments();
            for (ITypeBinding typeArg : typeBinding.getTypeArguments()) {
                newTypeArgs.add(typeFromBinding(ast, typeArg));
            }

            return type;
        }

        if (typeBinding.isWildcardType()) {
            WildcardType type = ast.newWildcardType();
            if (typeBinding.getBound() != null) {
                type.setBound(typeFromBinding(ast, typeBinding.getBound()));
            }
            return type;
        }

        // simple or raw type
        String qualName = typeBinding.getName();
        if ("".equals(qualName)) {
            return ast.newWildcardType();
        }
        try {
            return ast.newSimpleType(ast.newName(qualName));
        } catch (Exception e) {
            return ast.newWildcardType();
        }
    }
}
