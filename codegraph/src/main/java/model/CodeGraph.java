package model;

import model.graph.node.AnonymousClassDecl;
import model.graph.node.CatClause;
import model.graph.node.Node;
import model.graph.node.bodyDecl.FieldDecl;
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
import java.util.List;

public class CodeGraph {
    private final GraphConfiguration configuration;

    private String filePath, name, projectName;
    private GraphBuildingContext context;

    public Node entryNode = null;
    protected List<Node> fieldNodes = new ArrayList<>();
    protected List<Node> statementNodes = new ArrayList<>();

    protected CompilationUnit cu = null;

    public CodeGraph(GraphBuildingContext context, GraphConfiguration configuration) {
        this.context = context;
        this.configuration = configuration;
    }

    public void buildFieldNode(TypeDeclaration astNode) {
        for (FieldDeclaration f : astNode.getFields()) {
            String fieldType = JavaASTUtil.getSimpleType(f.getType());
            for (int i = 0; i < f.fragments().size(); i++) {
                VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(i);
                String fieldName = vdf.getName().getIdentifier();
                for (int j = 0; j < vdf.getExtraDimensions(); j++)
                    fieldType += "[]";

                FieldDecl fieldNode = (FieldDecl) buildNode(f, null, null);
                fieldNodes.add(fieldNode);
            }
        }
        ASTNode p = astNode.getParent();
        if (p != null) {
            if (p instanceof TypeDeclaration) {
                buildFieldNode((TypeDeclaration) p);
            }
        }
    }

    public Node buildNode(ASTNode astNode, Node parent, Node control) {
        if (astNode == null) {
            return null;
        }

        if (astNode instanceof FieldDeclaration) {
            return visit((FieldDeclaration) astNode, parent, control);
        } else if (astNode instanceof MethodDeclaration) {
            return visit((MethodDeclaration) astNode, parent, control);
        } else if (astNode instanceof CatchClause) {
            return visit((CatchClause) astNode, parent, control);
        } else if (astNode instanceof AssertStatement) {
            return visit((AssertStatement) astNode, parent, control);
        } else if (astNode instanceof Block) {
            return visit((Block) astNode, parent, control);
        } else if (astNode instanceof BreakStatement) {
            return visit((BreakStatement) astNode, parent, control);
        } else if (astNode instanceof SwitchCase) {
            return visit((SwitchCase) astNode, parent, control);
        } else if (astNode instanceof ConstructorInvocation) {
            return visit((ConstructorInvocation) astNode, parent, control);
        } else if (astNode instanceof ContinueStatement) {
            return visit((ContinueStatement) astNode, parent, control);
        } else if (astNode instanceof DoStatement) {
            return visit((DoStatement) astNode, parent, control);
        } else if (astNode instanceof EmptyStatement) {
            return visit((EmptyStatement) astNode, parent, control);
        } else if (astNode instanceof EnhancedForStatement) {
            return visit((EnhancedForStatement) astNode, parent, control);
        } else if (astNode instanceof ExpressionStatement) {
            return visit((ExpressionStatement) astNode, parent, control);
        } else if (astNode instanceof ForStatement) {
            return visit((ForStatement) astNode, parent, control);
        } else if (astNode instanceof IfStatement) {
            return visit((IfStatement) astNode, parent, control);
        } else if (astNode instanceof LabeledStatement) {
            return visit((LabeledStatement) astNode, parent, control);
        } else if (astNode instanceof ReturnStatement) {
            return  visit((ReturnStatement) astNode, parent, control);
        } else if (astNode instanceof SuperConstructorInvocation) {
            return visit((SuperConstructorInvocation) astNode, parent, control);
        } else if (astNode instanceof SwitchStatement) {
            return visit((SwitchStatement) astNode, parent, control);
        } else if (astNode instanceof SynchronizedStatement) {
            return visit((SynchronizedStatement) astNode, parent, control);
        } else if (astNode instanceof ThrowStatement) {
            return visit((ThrowStatement) astNode, parent, control);
        } else if (astNode instanceof TryStatement) {
            return visit((TryStatement) astNode, parent, control);
        } else if (astNode instanceof TypeDeclarationStatement) {
            return visit((TypeDeclarationStatement) astNode, parent, control);
        } else if (astNode instanceof VariableDeclarationStatement) {
            return visit((VariableDeclarationStatement) astNode, parent, control);
        } else if (astNode instanceof WhileStatement) {
            return visit((WhileStatement) astNode, parent, control);
        } else if (astNode instanceof Annotation) {
            return visit((Annotation) astNode, parent, control);
        } else if (astNode instanceof ArrayAccess) {
            return visit((ArrayAccess) astNode, parent, control);
        } else if (astNode instanceof ArrayCreation) {
            return visit((ArrayCreation) astNode, parent, control);
        } else if (astNode instanceof ArrayInitializer) {
            return visit((ArrayInitializer) astNode, parent, control);
        } else if (astNode instanceof Assignment) {
            return visit((Assignment) astNode, parent, control);
        } else if (astNode instanceof BooleanLiteral) {
            return visit((BooleanLiteral)astNode, parent, control);
        } else if (astNode instanceof CastExpression) {
            return visit((CastExpression) astNode, parent, control);
        } else if (astNode instanceof CharacterLiteral) {
            return visit((CharacterLiteral) astNode, parent, control);
        } else if (astNode instanceof ClassInstanceCreation) {
            return visit((ClassInstanceCreation) astNode, parent, control);
        } else if (astNode instanceof ConditionalExpression) {
            return visit((ConditionalExpression) astNode, parent, control);
        } else if (astNode instanceof CreationReference) {
            return visit((CreationReference) astNode, parent, control);
        } else if (astNode instanceof ExpressionMethodReference) {
            return visit((ExpressionMethodReference) astNode, parent, control);
        } else if (astNode instanceof FieldAccess) {
            return visit((FieldAccess) astNode, parent, control);
        } else if (astNode instanceof InfixExpression) {
            return visit((InfixExpression) astNode, parent, control);
        } else if (astNode instanceof InstanceofExpression) {
            return visit((InstanceofExpression) astNode, parent, control);
        } else if (astNode instanceof LambdaExpression) {
            return visit((LambdaExpression) astNode, parent, control);
        } else if (astNode instanceof MethodInvocation) {
            return visit((MethodInvocation) astNode, parent, control);
        } else if (astNode instanceof MethodReference) {
            return visit((MethodReference) astNode, parent, control);
        } else if (astNode instanceof NullLiteral) {
            return visit((NullLiteral) astNode, parent, control);
        } else if (astNode instanceof NumberLiteral) {
            return visit((NumberLiteral) astNode, parent, control);
        } else if (astNode instanceof ParenthesizedExpression) {
            return visit((ParenthesizedExpression) astNode, parent, control);
        } else if (astNode instanceof PostfixExpression) {
            return visit((PostfixExpression) astNode, parent, control);
        } else if (astNode instanceof PrefixExpression) {
            return visit((PrefixExpression) astNode, parent, control);
        } else if (astNode instanceof QualifiedName) {
            return visit((QualifiedName) astNode, parent, control);
        } else if (astNode instanceof SimpleName) {
            return visit((SimpleName) astNode, parent, control);
        } else if (astNode instanceof StringLiteral) {
            return visit((StringLiteral) astNode, parent, control);
        } else if (astNode instanceof SuperFieldAccess) {
            return visit((SuperFieldAccess) astNode, parent, control);
        } else if (astNode instanceof SuperMethodInvocation) {
            return visit((SuperMethodInvocation) astNode, parent, control);
        } else if (astNode instanceof SuperMethodReference) {
            return visit((SuperMethodReference) astNode, parent, control);
        } else if (astNode instanceof ThisExpression) {
            return visit((ThisExpression) astNode, parent, control);
        } else if (astNode instanceof TypeMethodReference) {
            return visit((TypeMethodReference) astNode, parent, control);
        } else if (astNode instanceof TypeLiteral) {
            return visit((TypeLiteral) astNode, parent, control);
        } else if (astNode instanceof VariableDeclarationExpression) {
            return visit((VariableDeclarationExpression) astNode, parent, control);
        } else if (astNode instanceof Type) {
            return visit((Type) astNode, parent, control);
        } else if (astNode instanceof SingleVariableDeclaration) {
            return visit((SingleVariableDeclaration) astNode, parent, control);
        } else if (astNode instanceof VariableDeclarationFragment) {
            return visit((VariableDeclarationFragment) astNode, parent, control);
        } else {
            System.out.println("UNKNOWN ASTNode type : " + astNode.toString());
            return null;
        }
    }

    private FieldDecl visit(FieldDeclaration astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        FieldDecl fieldDecl = new FieldDecl(astNode, filePath, start, end);
        fieldDecl.setControlDependency(control);

        TypeNode declType = (TypeNode) buildNode(astNode.getType(), fieldDecl, control);
        fieldDecl.setDeclType(declType);

        List<VarDeclFrag> frags = new ArrayList<>();
        for (Object obj : astNode.fragments()) {
            VarDeclFrag vdf = (VarDeclFrag) buildNode((ASTNode) obj, fieldDecl, control);
            frags.add(vdf);
        }
        fieldDecl.setFrags(frags);

        return fieldDecl;
    }

    private Node visit(MethodDeclaration astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        MethodDecl methodDecl = new MethodDecl(astNode, filePath, start, end);
        methodDecl.setControlDependency(control);
        // modifiers
        List<String> modifiers = new ArrayList<>();
        for (Object obj : astNode.modifiers()) {
            modifiers.add(obj.toString());
        }
        methodDecl.setModifiers(modifiers);
        // return type
        if (astNode.getReturnType2() != null) {
            TypeNode type = (TypeNode) buildNode(astNode.getReturnType2(), methodDecl, control);
            String typeStr = JavaASTUtil.getSimpleType(astNode.getReturnType2());
            methodDecl.setRetType(type, typeStr);
        }
        // method name
        SimpName mname = (SimpName) buildNode(astNode.getName(), methodDecl, control);
        methodDecl.setName(mname);
        // arguments
        List<ExprNode> paras = new ArrayList<>();
        for (Object obj : astNode.parameters()) {
            ExprNode para = (ExprNode) buildNode((ASTNode) obj, methodDecl, control);
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
            BlockStmt blk = (BlockStmt) buildNode(body, methodDecl, control);
            methodDecl.setBody(blk);
        }

        return methodDecl;
    }

    private Node visit(AssertStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AssertStmt assertStmt = new AssertStmt(astNode, filePath, start, end);
        assertStmt.setControlDependency(control);
        // expression
        Expression expression = astNode.getExpression();
        ExprNode expr = (ExprNode) buildNode(expression, assertStmt, control);
        assertStmt.setExpr(expr);
        // message
        if (astNode.getMessage() != null) {
            ExprNode message = (ExprNode) buildNode(astNode.getMessage(), assertStmt, control);
            assertStmt.setMessage(message);
        }

        statementNodes.add(assertStmt);
        return assertStmt;
    }

    private Node visit(Block astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        BlockStmt blk = new BlockStmt(astNode, filePath, start, end);
        blk.setControlDependency(control);
        // stmt list
        List<StmtNode> stmts = new ArrayList<>();
        for (Object object : astNode.statements()) {
            StmtNode stmt = (StmtNode) buildNode((ASTNode) object, blk, control);
            stmts.add(stmt);
        }
        blk.setStatement(stmts);

        statementNodes.add(blk);
        return blk;
    }

    private Node visit(BreakStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        BreakStmt brk = new BreakStmt(astNode, filePath, start, end);
        brk.setControlDependency(control);
        // identifier
        if (astNode.getLabel() != null) {
            SimpName sName = (SimpName) buildNode(astNode.getLabel(), brk, control);
            brk.setIdentifier(sName);
        }

        statementNodes.add(brk);
        return brk;
    }

    private Node visit(SwitchCase astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CaseStmt swCase = new CaseStmt(astNode, filePath, start, end);
        swCase.setControlDependency(control);
        // case expression
        if (astNode.getExpression() != null) {
            ExprNode expression = (ExprNode) buildNode(astNode.getExpression(), swCase, control);
            swCase.setExpression(expression);
        }

        statementNodes.add(swCase);
        return swCase;
    }

    private Node visit(ConstructorInvocation astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ConstructorInvoc consInv = new ConstructorInvoc(astNode, filePath, start, end);
        consInv.setControlDependency(control);
        // arguments
        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> argulist = new ArrayList<>();
        for (Object object : astNode.arguments()) {
            ExprNode expr = (ExprNode) buildNode((ASTNode) object, exprList, control);
            argulist.add(expr);
        }
        exprList.setExprs(argulist);
        consInv.setArguments(exprList);

        statementNodes.add(consInv);
        return consInv;
    }

    private Node visit(ContinueStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ContinueStmt conti = new ContinueStmt(astNode, filePath, start, end);
        conti.setControlDependency(control);
        // identifier
        if (astNode.getLabel() != null) {
            SimpName sName = (SimpName) buildNode(astNode.getLabel(), conti, control);
            conti.setIdentifier(sName);
        }

        statementNodes.add(conti);
        return conti;
    }

    /**
     * do Statement while ( Expression ) ;
     */
    private Node visit(DoStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        DoStmt doStmt = new DoStmt(astNode, filePath, start, end);
        doStmt.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), doStmt, control);
        doStmt.setExpr(expr);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), doStmt, expr);
        doStmt.setBody(body);

        statementNodes.add(doStmt);
        return doStmt;
    }

    private Node visit(EmptyStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        EmptyStmt ept = new EmptyStmt(astNode, filePath, start, end);
        ept.setControlDependency(control);

        statementNodes.add(ept);
        return ept;
    }

    private Node visit(EnhancedForStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        EnhancedForStmt efor = new EnhancedForStmt(astNode, filePath, start, end);
        efor.setControlDependency(control);
        // formal parameter
        SingleVarDecl svd =  (SingleVarDecl) buildNode(astNode.getParameter(), efor, control);
        efor.setSVD(svd);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), efor, control);
        efor.setExpr(expr);
        // body
        StmtNode stmt = wrapBlock(astNode.getBody(), efor, expr);
        // TODO: relation between body and formal parameter
        efor.setBody(stmt);

        statementNodes.add(efor);
        return efor;
    }

    private Node visit(ExpressionStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ExprStmt exprStmt = new ExprStmt(astNode, filePath, start, end);
        exprStmt.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), exprStmt, control);
        exprStmt.setExpr(expr);

        statementNodes.add(exprStmt);
        return exprStmt;
    }

    private Node visit(ForStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ForStmt forStmt = new ForStmt(astNode, filePath, start, end);
        forStmt.setControlDependency(control);
        // initializers
        ExprList initExprList = new ExprList(null, filePath, start, end);
        List<ExprNode> initializers = new ArrayList<>();
        if (!astNode.initializers().isEmpty()) {
            for (Object object : astNode.initializers()) {
                ExprNode initializer = (ExprNode) buildNode((ASTNode) object, initExprList, control);
                initializers.add(initializer);
            }
        }
        initExprList.setExprs(initializers);
        forStmt.setInitializer(initExprList);
        // expression
        if (astNode.getExpression() != null) {
            ExprNode condition = (ExprNode) buildNode(astNode.getExpression(), forStmt, control);
            forStmt.setCondition(condition);
        }
        // updaters
        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> updaters = new ArrayList<>();
        if (!astNode.updaters().isEmpty()) {
            for (Object object : astNode.updaters()) {
                ExprNode update = (ExprNode) buildNode((ASTNode) object, exprList, control);
                updaters.add(update);
            }
        }
        exprList.setExprs(updaters);
        forStmt.setUpdaters(exprList);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), forStmt, forStmt.getCondition());
        forStmt.setBody(body);
        // TODO: relation between initializers and body

        statementNodes.add(forStmt);
        return forStmt;
    }

    private Node visit(IfStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        IfStmt ifstmt = new IfStmt(astNode, filePath, start, end);
        ifstmt.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), ifstmt, control);
        ifstmt.setExpression(expr);
        // then statement
        StmtNode then = wrapBlock(astNode.getThenStatement(), ifstmt, expr);
        ifstmt.setThen(then);
        // else statement
        if (astNode.getElseStatement() != null) {
            StmtNode els = wrapBlock(astNode.getElseStatement(), ifstmt, expr);
            ifstmt.setElse(els);
        }

        statementNodes.add(ifstmt);
        return ifstmt;
    }

    private Node visit(LabeledStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        LabeledStmt labStmt = new LabeledStmt(astNode, filePath, start, end);
        labStmt.setControlDependency(control);
        // label
        SimpName lab = (SimpName) buildNode(astNode.getLabel(), labStmt, control);
        labStmt.setLabel(lab);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), labStmt, control);
        labStmt.setBody(body);

        statementNodes.add(labStmt);
        return labStmt;
    }

    private Node visit(ReturnStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ReturnStmt ret = new ReturnStmt(astNode, filePath, start, end);
        ret.setControlDependency(control);
        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), ret, control);
            ret.setExpr(expr);
        }

        statementNodes.add(ret);
        return ret;
    }

    private Node visit(SuperConstructorInvocation astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperConstructorInvoc spi = new SuperConstructorInvoc(astNode, filePath, start, end);
        spi.setControlDependency(control);
        // expression
        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), spi, control);
            spi.setExpr(expr);
        }
        // parameters
        ExprList arguList = new ExprList(null, filePath, start, end);
        List<ExprNode> argus = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode para = (ExprNode) buildNode((ASTNode) obj, arguList, control);
            argus.add(para);
        }
        arguList.setExprs(argus);
        spi.setArguments(arguList);

        statementNodes.add(spi);
        return spi;
    }

    private Node visit(SwitchStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SwitchStmt swi = new SwitchStmt(astNode, filePath, start, end);
        swi.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), swi, control);
        swi.setExpr(expr);
        // stmt list
        List<StmtNode> stmts = new ArrayList<>();
        Node swiCase = null;
        for (Object obj : astNode.statements()) {
            StmtNode stmt = (StmtNode) buildNode((ASTNode) obj, swi, expr);
            // TODO: relation with each switch case value
            stmts.add(stmt);
            if (stmt instanceof CaseStmt) {
                swiCase = stmt;
            }
        }
        swi.setStatements(stmts);

        statementNodes.add(swi);
        return swi;
    }

    private Node visit(SynchronizedStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SynchronizedStmt syn = new SynchronizedStmt(astNode, filePath, start, end);
        syn.setControlDependency(control);
        // expression
        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), syn, control);
            syn.setExpr(expr);
        }
        // body
        BlockStmt body = (BlockStmt) buildNode(astNode.getBody(), syn, syn.getExpression());
        syn.setBody(body);

        statementNodes.add(syn);
        return syn;
    }

    private Node visit(ThrowStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ThrowStmt throwStmt = new ThrowStmt(astNode, filePath, start, end);
        throwStmt.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), throwStmt, control);
        throwStmt.setExpr(expr);

        statementNodes.add(throwStmt);
        return throwStmt;
    }

    private Node visit(TryStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TryStmt tryStmt = new TryStmt(astNode, filePath, start, end);
        tryStmt.setControlDependency(control);
        // resources
        if (astNode.resources() != null) {
            List<VarDeclExpr> resourceList = new ArrayList<>();
            for (Object obj : astNode.resources()) {
                VariableDeclarationExpression resource = (VariableDeclarationExpression) obj;
                VarDeclExpr vdExpr = (VarDeclExpr) buildNode(resource, tryStmt, control);
                resourceList.add(vdExpr);
            }
            tryStmt.setResources(resourceList);
        }
        BlockStmt blk = (BlockStmt) buildNode(astNode.getBody(), tryStmt, control);
        tryStmt.setBody(blk);
        // catch
        List<CatClause> catches = new ArrayList<>(astNode.catchClauses().size());
        for (Object obj : astNode.catchClauses()) {
            CatchClause catchClause = (CatchClause) obj;
            CatClause catClause = (CatClause) buildNode(catchClause, tryStmt, control);
            catches.add(catClause);
        }
        tryStmt.setCatchClause(catches);
        // finally
        if (astNode.getFinally() != null ){
            BlockStmt finallyBlk = (BlockStmt) buildNode(astNode.getFinally(), tryStmt, control);
            tryStmt.setFinallyBlock(finallyBlk);
        }

        statementNodes.add(tryStmt);
        return tryStmt;
    }

    private CatClause visit(CatchClause astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CatClause cat = new CatClause(astNode, filePath, start, end);
        cat.setControlDependency(control);

        SingleVarDecl svd = (SingleVarDecl) buildNode(astNode.getException(), cat, control);
        cat.setException(svd);

        BlockStmt body = (BlockStmt) buildNode(astNode.getBody(), cat, svd);
        cat.setBody(body);

        statementNodes.add(cat);
        return cat;
    }

    private Node visit(TypeDeclarationStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypeDeclStmt td = new TypeDeclStmt(astNode, filePath, start, end);
        td.setControlDependency(control);
        return td;
    }

    private Node visit(VariableDeclarationStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        VarDeclStmt vdStmt = new VarDeclStmt(astNode, filePath, start, end);
        vdStmt.setControlDependency(control);
        // modifiers
        String modifier = "";
        if (astNode.modifiers() != null && astNode.modifiers().size() > 0) {
            for (Object obj : astNode.modifiers()) {
                modifier += " " + obj.toString();
            }
        }
        vdStmt.setModifier(modifier);
        // type
        TypeNode type = (TypeNode) buildNode(astNode.getType(), vdStmt, control);
        String typeStr = JavaASTUtil.getSimpleType(astNode.getType());
        vdStmt.setDeclType(type, typeStr);
        // fragments
        List<VarDeclFrag> fragments = new ArrayList<>();
        for (Object obj : astNode.fragments()) {
            VarDeclFrag vdf = (VarDeclFrag) buildNode((ASTNode) obj, vdStmt, control);
            vdf.setDeclType(type);
            fragments.add(vdf);
        }
        vdStmt.setFragments(fragments);

        statementNodes.add(vdStmt);
        return vdStmt;
    }

    private Node visit(WhileStatement astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        WhileStmt whi = new WhileStmt(astNode, filePath, start, end);
        whi.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), whi, control);
        whi.setExpr(expr);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), whi, expr);
        whi.setBody(body);

        statementNodes.add(whi);
        return whi;
    }

    private Node visit(Annotation astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AnnotationExpr anno = new AnnotationExpr(astNode, filePath, start, end);
        anno.setControlDependency(control);
        return anno;
    }

    private Node visit(ArrayAccess astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AryAcc aryAcc = new AryAcc(astNode, filePath, start, end);
        aryAcc.setControlDependency(control);
        // array
        ExprNode array = (ExprNode) buildNode(astNode.getArray(), aryAcc, control);
        aryAcc.setArray(array);
        // index
        ExprNode index = (ExprNode) buildNode(astNode.getIndex(), aryAcc, control);
        aryAcc.setIndex(index);
        // type
        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        String typeStr = JavaASTUtil.getSimpleType(type);
        TypeNode typ = (TypeNode) buildNode(type, aryAcc, control);
        aryAcc.setType(typ, typeStr);

        return aryAcc;
    }

    private Node visit(ArrayCreation astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AryCreation arycr = new AryCreation(astNode, filePath, start, end);
        arycr.setControlDependency(control);
        // type
        Type type = typeFromBinding(astNode.getAST(), astNode.getType().getElementType().resolveBinding());
        if (type == null || type instanceof WildcardType) {
            type = astNode.getType().getElementType();
        }
        TypeNode typ = (TypeNode) buildNode(astNode.getType().getElementType(), arycr, control);
        String typStr = JavaASTUtil.getSimpleType(type);
        arycr.setArrayType(typ, typStr);
        arycr.setType(astNode.getType());
        // dimension
        ExprList dimlist = new ExprList(null, filePath, start, end);
        List<ExprNode> dimension = new ArrayList<>();
        for (Object obj : astNode.dimensions()) {
            ExprNode dim = (ExprNode) buildNode((ASTNode) obj, arycr, control);
            dimension.add(dim);
        }
        dimlist.setExprs(dimension);
        arycr.setDimension(dimlist);
        // initializer
        if (astNode.getInitializer() != null) {
            AryInitializer aryinit = (AryInitializer) buildNode(astNode.getInitializer(), arycr, control);
            arycr.setInitializer(aryinit);
        }

        return arycr;
    }

    private AryInitializer visit(ArrayInitializer astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AryInitializer aryinit = new AryInitializer(astNode, filePath, start, end);
        aryinit.setControlDependency(control);
        List<ExprNode> exprs = new ArrayList<>();
        for (Object obj : astNode.expressions()) {
            ExprNode expr = (ExprNode) buildNode((ASTNode) obj, aryinit, control);
            exprs.add(expr);
        }
        aryinit.setExpressions(exprs);

        return aryinit;
    }

    private AssignExpr visit(Assignment astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AssignExpr assign = new AssignExpr(astNode, filePath, start, end);
        assign.setControlDependency(control);
        // left
        ExprNode lhs = (ExprNode) buildNode(astNode.getLeftHandSide(), assign, control);
        assign.setLeftHandSide(lhs);
        // right
        ExprNode rhs = (ExprNode) buildNode(astNode.getRightHandSide(), assign, control);
        assign.setRightHandSide(rhs);
        // operator
        AssignOpr assignOpr = new AssignOpr(null, filePath, start, end);
        assignOpr.setOperator(astNode.getOperator());
        assignOpr.setParent(assign);
        assign.setOperator(assignOpr);

        return assign;
    }

    private BoolLiteral visit(BooleanLiteral astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        BoolLiteral literal = new BoolLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);
        literal.setValue(astNode.booleanValue());
        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        literal.setType(type);

        return literal;
    }

    private CastExpr visit(CastExpression astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CastExpr cast = new CastExpr(astNode, filePath, start, end);
        cast.setControlDependency(control);
        // type
        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), cast, control);
        cast.setCastType(typeNode);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), cast, control);
        cast.setExpression(expr);
        cast.setType(astNode.getType());

        return cast;
    }

    private CharLiteral visit(CharacterLiteral astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CharLiteral literal = new CharLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);
        literal.setValue(astNode.charValue());
        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        literal.setType(type);

        return literal;
    }

    private ClassInstanceCreationExpr visit(ClassInstanceCreation astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ClassInstanceCreationExpr classCreation = new ClassInstanceCreationExpr(astNode, filePath, start, end);
        classCreation.setControlDependency(control);

        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), classCreation, control);
            classCreation.setExpression(expr);
        }

        if (astNode.getAnonymousClassDeclaration() != null) {
            AnonymousClassDecl anony = (AnonymousClassDecl) buildNode(astNode.getAnonymousClassDeclaration(), classCreation, control);
            classCreation.setAnonymousClassDecl(anony);
        }

        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> argus = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode arg = (ExprNode) buildNode((ASTNode) obj, exprList, control);
            argus.add(arg);
        }
        exprList.setExprs(argus);
        classCreation.setArguments(exprList);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), classCreation, control);
        classCreation.setClassType(typeNode);
        classCreation.setType(astNode.getType());

        return classCreation;
    }

    private AnonymousClassDecl visit(AnonymousClassDeclaration astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AnonymousClassDecl anony = new AnonymousClassDecl(astNode, filePath, start, end);
        anony.setControlDependency(control);
        return anony;
    }

    private ConditionalExpr visit(ConditionalExpression astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ConditionalExpr condExpr = new ConditionalExpr(astNode, filePath, start, end);
        condExpr.setControlDependency(control);

        ExprNode condition = (ExprNode) buildNode(astNode.getExpression(), condExpr, control);
        condExpr.setCondition(condition);

        ExprNode thenExpr = (ExprNode) buildNode(astNode.getThenExpression(), condExpr, control);
        condExpr.setThenExpr(thenExpr);

        ExprNode elseExpr = (ExprNode) buildNode(astNode.getElseExpression(), condExpr, control);
        condExpr.setElseExpr(elseExpr);

        return condExpr;
    }

    private CreationRef visit(CreationReference astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CreationRef cref = new CreationRef(astNode, filePath, start, end);
        cref.setControlDependency(control);
        // TODO: parse nodes for CreationReference
        return cref;
    }

    private ExprMethodRef visit(ExpressionMethodReference astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ExprMethodRef exprMethodRef = new ExprMethodRef(astNode, filePath, start, end);
        exprMethodRef.setControlDependency(control);
        // TODO: parse nodes for ExpressionMethodReference
        return exprMethodRef;
    }

    private FieldAcc visit(FieldAccess astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        FieldAcc fieldAcc = new FieldAcc(astNode, filePath, start, end);
        fieldAcc.setControlDependency(control);

        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), fieldAcc, control);
        fieldAcc.setExpression(expr);

        SimpName iden = (SimpName) buildNode(astNode.getName(), fieldAcc, control);
        fieldAcc.setIdentifier(iden);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        fieldAcc.setType(type);

        return fieldAcc;
    }

    private InfixExpr visit(InfixExpression astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        InfixExpr infixExpr = new InfixExpr(astNode, filePath, start, end);
        infixExpr.setControlDependency(control);

        ExprNode lhs = (ExprNode) buildNode(astNode.getLeftOperand(), infixExpr, control);
        infixExpr.setLeftHandSide(lhs);

        ExprNode rhs = (ExprNode) buildNode(astNode.getRightOperand(), infixExpr, control);
        infixExpr.setRightHandSide(rhs);

        InfixOpr infixOpr = new InfixOpr(null, filePath, start, end);
        infixOpr.setOperator(astNode.getOperator());
        infixExpr.setOperatior(infixOpr);

        if (astNode.hasExtendedOperands()) {
            lhs = infixExpr;
            for (Object obj : astNode.extendedOperands()) {
                rhs = (ExprNode) buildNode((Expression) obj, infixExpr, control);
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

    private InstanceofExpr visit(InstanceofExpression astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        InstanceofExpr instanceofExpr = new InstanceofExpr(astNode, filePath, start, end);
        instanceofExpr.setControlDependency(control);

        ExprNode expr = (ExprNode) buildNode(astNode.getLeftOperand(), instanceofExpr, control);
        instanceofExpr.setExpression(expr);

        TypeNode instType = (TypeNode) buildNode(astNode.getRightOperand(), instanceofExpr, control);
        instanceofExpr.setInstanceType(instType);

        return instanceofExpr;
    }

    private LambdaExpr visit(LambdaExpression astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        LambdaExpr lambdaExpr = new LambdaExpr(astNode, filePath, start, end);
        lambdaExpr.setControlDependency(control);
        // TODO: parse nodes for lambda expression
        return lambdaExpr;
    }

    private MethodInvoc visit(MethodInvocation astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        MethodInvoc methodInvoc = new MethodInvoc(astNode, filePath, start, end);
        methodInvoc.setControlDependency(control);

        ExprNode expr = null;
        if (astNode.getExpression() != null) {
            expr = (ExprNode) buildNode(astNode.getExpression(), methodInvoc, control);
            methodInvoc.setExpression(expr);
        }

        SimpName iden = (SimpName) buildNode(astNode.getName(), methodInvoc, control);
        methodInvoc.setName(iden);

        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> args = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode arg = (ExprNode) buildNode((ASTNode) obj, exprList, control);
            args.add(arg);
        }
        exprList.setExprs(args);
        methodInvoc.setArguments(exprList);

        return methodInvoc;
    }

    private MethodRef visit(MethodReference astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        MethodRef mref = new MethodRef(astNode, filePath, start, end);
        mref.setControlDependency(control);
        return mref;
    }

    private SimpName visit(SimpleName astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SimpName sname = new SimpName(astNode, filePath, start, end);
        sname.setControlDependency(control);

        String name = astNode.getFullyQualifiedName();
        sname.setName(name);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        sname.setType(type);

        return sname;
    }

    private QuaName visit(QualifiedName astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        QuaName qname = new QuaName(astNode, filePath, start, end);
        qname.setControlDependency(control);
        // Name . SimpleName
        SimpName sname = (SimpName) buildNode(astNode.getName(), qname, control);
        qname.setName(sname);

        NameExpr name = (NameExpr) buildNode(astNode.getQualifier(), qname, control);
        qname.setQualifier(name);

        return qname;
    }

    private NulLiteral visit(NullLiteral astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        NulLiteral literal = new NulLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);
        return literal;
    }

    private NumLiteral visit(NumberLiteral astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        NumLiteral literal = new NumLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);

        String value = astNode.getToken();
        literal.setValue(value);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        literal.setType(type);

        return literal;
    }

    private ParenExpr visit(ParenthesizedExpression astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ParenExpr parenthesiszedExpr = new ParenExpr(astNode, filePath, start, end);
        parenthesiszedExpr.setControlDependency(control);

        ExprNode expression = (ExprNode) buildNode(astNode.getExpression(), parenthesiszedExpr, control);
        parenthesiszedExpr.setExpr(expression);

        return parenthesiszedExpr;
    }

    private PostfixExpr visit(PostfixExpression astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        PostfixExpr postfixExpr = new PostfixExpr(astNode, filePath, start, end);
        postfixExpr.setControlDependency(control);

        ExprNode expr = (ExprNode) buildNode(astNode.getOperand(), postfixExpr, control);
        postfixExpr.setExpr(expr);

        PostfixOpr postfixOpr = new PostfixOpr(null, filePath, start, end);
        postfixOpr.setOperator(astNode.getOperator());
        postfixExpr.setOpr(postfixOpr);

        return postfixExpr;
    }

    private PrefixExpr visit(PrefixExpression astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        PrefixExpr prefixExpr = new PrefixExpr(astNode, filePath, start, end);
        prefixExpr.setControlDependency(control);

        ExprNode expr = (ExprNode) buildNode(astNode.getOperand(), prefixExpr, control);
        prefixExpr.setExpr(expr);

        PrefixOpr prefixOpr = new PrefixOpr(null, filePath, start, end);
        prefixOpr.setOperator(astNode.getOperator());
        prefixExpr.setOpr(prefixOpr);

        return prefixExpr;
    }

    private StrLiteral visit(StringLiteral astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        StrLiteral literal = new StrLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);

        literal.setLiteralValue(astNode.getLiteralValue());
        literal.setEscapedValue(astNode.getEscapedValue());

        return literal;
    }

    private SuperFieldAcc visit(SuperFieldAccess astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperFieldAcc superFieldAcc = new SuperFieldAcc(astNode, filePath, start, end);
        superFieldAcc.setControlDependency(control);

        SimpName iden = (SimpName) buildNode(astNode.getName(), superFieldAcc, control);
        superFieldAcc.setIdentifier(iden);

        if (astNode.getQualifier() != null) {
            QuaName qname = (QuaName) buildNode(astNode.getQualifier(), superFieldAcc, control);
            superFieldAcc.setQualifier(qname);
        }

        return superFieldAcc;
    }

    private SuperMethodInvoc visit(SuperMethodInvocation astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperMethodInvoc superMethodInvoc = new SuperMethodInvoc(astNode, filePath, start, end);
        superMethodInvoc.setControlDependency(control);

        SimpName name = (SimpName) buildNode(astNode.getName(), superMethodInvoc, control);
        superMethodInvoc.setName(name);

        if (astNode.getQualifier() != null) {
            QuaName qualifier = (QuaName) buildNode(astNode.getQualifier(), superMethodInvoc, control);
            superMethodInvoc.setQualifier(qualifier);
        }

        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> args = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode arg = (ExprNode) buildNode((ASTNode) obj, exprList, control);
            args.add(arg);
        }
        exprList.setExprs(args);
        superMethodInvoc.setArguments(exprList);

        return superMethodInvoc;
    }

    private SuperMethodRef visit(SuperMethodReference astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperMethodRef superMethodRef = new SuperMethodRef(astNode, filePath, start, end);
        superMethodRef.setControlDependency(control);
        // TODO: parse nodes for SuperMethodReference
        return superMethodRef;
    }

    private ThisExpr visit(ThisExpression astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ThisExpr thisExpr = new ThisExpr(astNode, filePath, start, end);
        thisExpr.setControlDependency(control);
        return thisExpr;
    }

    private TypLiteral visit(TypeLiteral astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypLiteral literal = new TypLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), literal, control);
        literal.setValue(typeNode);

        return literal;
    }

    private TypeMethodRef visit(TypeMethodReference astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypeMethodRef typeMethodRef = new TypeMethodRef(astNode, filePath, start, end);
        typeMethodRef.setControlDependency(control);
        // TODO: parse nodes for TypeMethodReference
        return typeMethodRef;
    }

    private VarDeclExpr visit(VariableDeclarationExpression astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        VarDeclExpr varDeclExpr = new VarDeclExpr(astNode, filePath, start, end);
        varDeclExpr.setControlDependency(control);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), varDeclExpr, control);
        varDeclExpr.setDeclType(typeNode);

        List<VarDeclFrag> fragments = new ArrayList<>();
        for (Object obj : astNode.fragments()) {
            VarDeclFrag vdf = (VarDeclFrag) buildNode((ASTNode) obj, varDeclExpr, control);
            vdf.setDeclType(typeNode);
            fragments.add(vdf);
        }
        varDeclExpr.setFragments(fragments);


        return varDeclExpr;
    }

    private VarDeclFrag visit(VariableDeclarationFragment astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        VarDeclFrag vdf = new VarDeclFrag(astNode, filePath, start, end);
        vdf.setControlDependency(control);

        SimpName iden = (SimpName) buildNode(astNode.getName(), vdf, control);
        vdf.setName(iden);

        vdf.setDimensions(astNode.getExtraDimensions());

        if (astNode.getInitializer() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getInitializer(), vdf, control);
            vdf.setExpr(expr);
        }

        return vdf;
    }

    private SingleVarDecl visit(SingleVariableDeclaration astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SingleVarDecl svd = new SingleVarDecl(astNode, filePath, start, end);
        svd.setControlDependency(control);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), svd, control);
        svd.setDeclType(typeNode);

        if (astNode.getInitializer() != null) {
            ExprNode init = (ExprNode) buildNode(astNode.getInitializer(), svd, control);
            svd.setInitializer(init);
        }

        SimpName name = (SimpName) buildNode(astNode.getName(), svd, control);
        svd.setName(name);

        return svd;
    }

    private TypeNode visit(Type astNode, Node parent, Node control) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypeNode typeNode = new TypeNode(astNode, filePath, start, end);
        typeNode.setControlDependency(control);

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

    private BlockStmt wrapBlock(Statement node, Node parent, Node control) {
        BlockStmt blk;
        if(node instanceof Block) {
            blk = (BlockStmt) buildNode(node, parent, control);
        } else {
            int startLine = cu.getLineNumber(node.getStartPosition());
            int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
            blk = new BlockStmt(node, filePath, startLine, endLine);
            List<StmtNode> stmts = new ArrayList<>();
            StmtNode stmt = (StmtNode) buildNode(node, blk, control);
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
