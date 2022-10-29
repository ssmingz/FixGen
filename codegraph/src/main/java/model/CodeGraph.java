package model;

import model.graph.Scope;
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
    protected List<Node> allNodes = new ArrayList<>();

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

                FieldDecl fieldNode = (FieldDecl) buildNode(f, null, new Scope(null));
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

    public Node buildNode(ASTNode astNode, Node control, Scope scope) {
        if (astNode == null) {
            return null;
        }

        Node node = null;
        if (astNode instanceof FieldDeclaration) {
            node = visit((FieldDeclaration) astNode, control, scope);
        } else if (astNode instanceof MethodDeclaration) {
            node = visit((MethodDeclaration) astNode, control, scope);
        } else if (astNode instanceof CatchClause) {
            node = visit((CatchClause) astNode, control, scope);
        } else if (astNode instanceof AssertStatement) {
            node = visit((AssertStatement) astNode, control, scope);
        } else if (astNode instanceof Block) {
            node = visit((Block) astNode, control, scope);
        } else if (astNode instanceof BreakStatement) {
            node = visit((BreakStatement) astNode, control, scope);
        } else if (astNode instanceof SwitchCase) {
            node = visit((SwitchCase) astNode, control, scope);
        } else if (astNode instanceof ConstructorInvocation) {
            node = visit((ConstructorInvocation) astNode, control, scope);
        } else if (astNode instanceof ContinueStatement) {
            node = visit((ContinueStatement) astNode, control, scope);
        } else if (astNode instanceof DoStatement) {
            node = visit((DoStatement) astNode, control, scope);
        } else if (astNode instanceof EmptyStatement) {
            node = visit((EmptyStatement) astNode, control, scope);
        } else if (astNode instanceof EnhancedForStatement) {
            node = visit((EnhancedForStatement) astNode, control, scope);
        } else if (astNode instanceof ExpressionStatement) {
            node = visit((ExpressionStatement) astNode, control, scope);
        } else if (astNode instanceof ForStatement) {
            node = visit((ForStatement) astNode, control, scope);
        } else if (astNode instanceof IfStatement) {
            node = visit((IfStatement) astNode, control, scope);
        } else if (astNode instanceof LabeledStatement) {
            node = visit((LabeledStatement) astNode, control, scope);
        } else if (astNode instanceof ReturnStatement) {
            node =  visit((ReturnStatement) astNode, control, scope);
        } else if (astNode instanceof SuperConstructorInvocation) {
            node = visit((SuperConstructorInvocation) astNode, control, scope);
        } else if (astNode instanceof SwitchStatement) {
            node = visit((SwitchStatement) astNode, control, scope);
        } else if (astNode instanceof SynchronizedStatement) {
            node = visit((SynchronizedStatement) astNode, control, scope);
        } else if (astNode instanceof ThrowStatement) {
            node = visit((ThrowStatement) astNode, control, scope);
        } else if (astNode instanceof TryStatement) {
            node = visit((TryStatement) astNode, control, scope);
        } else if (astNode instanceof TypeDeclarationStatement) {
            node = visit((TypeDeclarationStatement) astNode, control, scope);
        } else if (astNode instanceof VariableDeclarationStatement) {
            node = visit((VariableDeclarationStatement) astNode, control, scope);
        } else if (astNode instanceof WhileStatement) {
            node = visit((WhileStatement) astNode, control, scope);
        } else if (astNode instanceof Annotation) {
            node = visit((Annotation) astNode, control, scope);
        } else if (astNode instanceof ArrayAccess) {
            node = visit((ArrayAccess) astNode, control, scope);
        } else if (astNode instanceof ArrayCreation) {
            node = visit((ArrayCreation) astNode, control, scope);
        } else if (astNode instanceof ArrayInitializer) {
            node = visit((ArrayInitializer) astNode, control, scope);
        } else if (astNode instanceof Assignment) {
            node = visit((Assignment) astNode, control, scope);
        } else if (astNode instanceof BooleanLiteral) {
            node = visit((BooleanLiteral) astNode, control, scope);
        } else if (astNode instanceof CastExpression) {
            node = visit((CastExpression) astNode, control, scope);
        } else if (astNode instanceof CharacterLiteral) {
            node = visit((CharacterLiteral) astNode, control, scope);
        } else if (astNode instanceof ClassInstanceCreation) {
            node = visit((ClassInstanceCreation) astNode, control, scope);
        } else if (astNode instanceof ConditionalExpression) {
            node = visit((ConditionalExpression) astNode, control, scope);
        } else if (astNode instanceof CreationReference) {
            node = visit((CreationReference) astNode, control, scope);
        } else if (astNode instanceof ExpressionMethodReference) {
            node = visit((ExpressionMethodReference) astNode, control, scope);
        } else if (astNode instanceof FieldAccess) {
            node = visit((FieldAccess) astNode, control, scope);
        } else if (astNode instanceof InfixExpression) {
            node = visit((InfixExpression) astNode, control, scope);
        } else if (astNode instanceof InstanceofExpression) {
            node = visit((InstanceofExpression) astNode, control, scope);
        } else if (astNode instanceof LambdaExpression) {
            node = visit((LambdaExpression) astNode, control, scope);
        } else if (astNode instanceof MethodInvocation) {
            node = visit((MethodInvocation) astNode, control, scope);
        } else if (astNode instanceof MethodReference) {
            node = visit((MethodReference) astNode, control, scope);
        } else if (astNode instanceof NullLiteral) {
            node = visit((NullLiteral) astNode, control, scope);
        } else if (astNode instanceof NumberLiteral) {
            node = visit((NumberLiteral) astNode, control, scope);
        } else if (astNode instanceof ParenthesizedExpression) {
            node = visit((ParenthesizedExpression) astNode, control, scope);
        } else if (astNode instanceof PostfixExpression) {
            node = visit((PostfixExpression) astNode, control, scope);
        } else if (astNode instanceof PrefixExpression) {
            node = visit((PrefixExpression) astNode, control, scope);
        } else if (astNode instanceof QualifiedName) {
            node = visit((QualifiedName) astNode, control, scope);
        } else if (astNode instanceof SimpleName) {
            node = visit((SimpleName) astNode, control, scope);
        } else if (astNode instanceof StringLiteral) {
            node = visit((StringLiteral) astNode, control, scope);
        } else if (astNode instanceof SuperFieldAccess) {
            node = visit((SuperFieldAccess) astNode, control, scope);
        } else if (astNode instanceof SuperMethodInvocation) {
            node = visit((SuperMethodInvocation) astNode, control, scope);
        } else if (astNode instanceof SuperMethodReference) {
            node = visit((SuperMethodReference) astNode, control, scope);
        } else if (astNode instanceof ThisExpression) {
            node = visit((ThisExpression) astNode, control, scope);
        } else if (astNode instanceof TypeMethodReference) {
            node = visit((TypeMethodReference) astNode, control, scope);
        } else if (astNode instanceof TypeLiteral) {
            node = visit((TypeLiteral) astNode, control, scope);
        } else if (astNode instanceof VariableDeclarationExpression) {
            node = visit((VariableDeclarationExpression) astNode, control, scope);
        } else if (astNode instanceof Type) {
            node = visit((Type) astNode, control, scope);
        } else if (astNode instanceof SingleVariableDeclaration) {
            node = visit((SingleVariableDeclaration) astNode, control, scope);
        } else if (astNode instanceof VariableDeclarationFragment) {
            node = visit((VariableDeclarationFragment) astNode, control, scope);
        } else {
            System.out.println("UNKNOWN ASTNode type : " + astNode.toString());
            node = null;
        }
        allNodes.add(node);
        return node;
    }

    private FieldDecl visit(FieldDeclaration astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        FieldDecl fieldDecl = new FieldDecl(astNode, filePath, start, end);
        fieldDecl.setControlDependency(control);

        TypeNode declType = (TypeNode) buildNode(astNode.getType(), control, scope);
        fieldDecl.setDeclType(declType);

        List<VarDeclFrag> frags = new ArrayList<>();
        for (Object obj : astNode.fragments()) {
            VarDeclFrag vdf = (VarDeclFrag) buildNode((ASTNode) obj, control, scope);
            frags.add(vdf);
        }
        fieldDecl.setFrags(frags);

        fieldDecl.setScope(scope);
        return fieldDecl;
    }

    private Node visit(MethodDeclaration astNode, Node control, Scope scope) {
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
            TypeNode type = (TypeNode) buildNode(astNode.getReturnType2(), control, scope);
            String typeStr = JavaASTUtil.getSimpleType(astNode.getReturnType2());
            methodDecl.setRetType(type, typeStr);
        }
        // method name
        SimpName mname = (SimpName) buildNode(astNode.getName(), control, scope);
        methodDecl.setName(mname);
        // arguments
        List<ExprNode> paras = new ArrayList<>();
        for (Object obj : astNode.parameters()) {
            ExprNode para = (ExprNode) buildNode((ASTNode) obj, control, scope);
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
            BlockStmt blk = (BlockStmt) buildNode(body, control, scope);
            methodDecl.setBody(blk);
        }

        methodDecl.setScope(scope);
        return methodDecl;
    }

    private Node visit(AssertStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AssertStmt assertStmt = new AssertStmt(astNode, filePath, start, end);
        assertStmt.setControlDependency(control);
        // expression
        Expression expression = astNode.getExpression();
        ExprNode expr = (ExprNode) buildNode(expression, control, scope);
        assertStmt.setExpr(expr);
        // message
        if (astNode.getMessage() != null) {
            ExprNode message = (ExprNode) buildNode(astNode.getMessage(), control, scope);
            assertStmt.setMessage(message);
        }

        assertStmt.setScope(scope);
        statementNodes.add(assertStmt);
        return assertStmt;
    }

    private Node visit(Block astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        BlockStmt blk = new BlockStmt(astNode, filePath, start, end);
        blk.setControlDependency(control);
        // stmt list
        List<StmtNode> stmts = new ArrayList<>();
        for (Object object : astNode.statements()) {
            StmtNode stmt = (StmtNode) buildNode((ASTNode) object, control, scope);
            stmts.add(stmt);
        }
        blk.setStatement(stmts);

        blk.setScope(scope);
        statementNodes.add(blk);
        return blk;
    }

    private Node visit(BreakStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        BreakStmt brk = new BreakStmt(astNode, filePath, start, end);
        brk.setControlDependency(control);
        // identifier
        if (astNode.getLabel() != null) {
            SimpName sName = (SimpName) buildNode(astNode.getLabel(), control, scope);
            brk.setIdentifier(sName);
        }

        brk.setScope(scope);
        statementNodes.add(brk);
        return brk;
    }

    private Node visit(SwitchCase astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CaseStmt swCase = new CaseStmt(astNode, filePath, start, end);
        swCase.setControlDependency(control);
        // case expression
        if (astNode.getExpression() != null) {
            ExprNode expression = (ExprNode) buildNode(astNode.getExpression(), control, scope);
            swCase.setExpression(expression);
        }

        swCase.setScope(scope);
        statementNodes.add(swCase);
        return swCase;
    }

    private Node visit(ConstructorInvocation astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ConstructorInvoc consInv = new ConstructorInvoc(astNode, filePath, start, end);
        consInv.setControlDependency(control);
        // arguments
        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> argulist = new ArrayList<>();
        for (Object object : astNode.arguments()) {
            ExprNode expr = (ExprNode) buildNode((ASTNode) object, control, scope);
            argulist.add(expr);
        }
        exprList.setExprs(argulist);
        consInv.setArguments(exprList);

        consInv.setScope(scope);
        statementNodes.add(consInv);
        return consInv;
    }

    private Node visit(ContinueStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ContinueStmt conti = new ContinueStmt(astNode, filePath, start, end);
        conti.setControlDependency(control);
        // identifier
        if (astNode.getLabel() != null) {
            SimpName sName = (SimpName) buildNode(astNode.getLabel(), control, scope);
            conti.setIdentifier(sName);
        }

        conti.setScope(scope);
        statementNodes.add(conti);
        return conti;
    }

    /**
     * do Statement while ( Expression ) ;
     */
    private Node visit(DoStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        DoStmt doStmt = new DoStmt(astNode, filePath, start, end);
        doStmt.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        doStmt.setExpr(expr);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), expr, scope);
        doStmt.setBody(body);

        doStmt.setScope(scope);
        statementNodes.add(doStmt);
        return doStmt;
    }

    private Node visit(EmptyStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        EmptyStmt ept = new EmptyStmt(astNode, filePath, start, end);
        ept.setControlDependency(control);

        ept.setScope(scope);
        statementNodes.add(ept);
        return ept;
    }

    private Node visit(EnhancedForStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        EnhancedForStmt efor = new EnhancedForStmt(astNode, filePath, start, end);
        efor.setControlDependency(control);
        // formal parameter
        SingleVarDecl svd =  (SingleVarDecl) buildNode(astNode.getParameter(), control, scope);
        efor.setSVD(svd);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        efor.setExpr(expr);
        // body
        StmtNode stmt = wrapBlock(astNode.getBody(), expr, scope);
        // TODO: relation between body and formal parameter
        efor.setBody(stmt);

        efor.setScope(scope);
        statementNodes.add(efor);
        return efor;
    }

    private Node visit(ExpressionStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ExprStmt exprStmt = new ExprStmt(astNode, filePath, start, end);
        exprStmt.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        exprStmt.setExpr(expr);

        exprStmt.setScope(scope);
        statementNodes.add(exprStmt);
        return exprStmt;
    }

    private Node visit(ForStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ForStmt forStmt = new ForStmt(astNode, filePath, start, end);
        forStmt.setControlDependency(control);
        // initializers
        ExprList initExprList = new ExprList(null, filePath, start, end);
        List<ExprNode> initializers = new ArrayList<>();
        if (!astNode.initializers().isEmpty()) {
            for (Object object : astNode.initializers()) {
                ExprNode initializer = (ExprNode) buildNode((ASTNode) object, control, scope);
                initializers.add(initializer);
            }
        }
        initExprList.setExprs(initializers);
        forStmt.setInitializer(initExprList);
        // expression
        if (astNode.getExpression() != null) {
            ExprNode condition = (ExprNode) buildNode(astNode.getExpression(), control, scope);
            forStmt.setCondition(condition);
        }
        // updaters
        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> updaters = new ArrayList<>();
        if (!astNode.updaters().isEmpty()) {
            for (Object object : astNode.updaters()) {
                ExprNode update = (ExprNode) buildNode((ASTNode) object, control, scope);
                updaters.add(update);
            }
        }
        exprList.setExprs(updaters);
        forStmt.setUpdaters(exprList);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), forStmt.getCondition(), scope);
        forStmt.setBody(body);
        // TODO: relation between initializers and body

        forStmt.setScope(scope);
        statementNodes.add(forStmt);
        return forStmt;
    }

    private Node visit(IfStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        IfStmt ifstmt = new IfStmt(astNode, filePath, start, end);
        ifstmt.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        ifstmt.setExpression(expr);
        // then statement
        StmtNode then = wrapBlock(astNode.getThenStatement(), expr, scope);
        ifstmt.setThen(then);
        // else statement
        if (astNode.getElseStatement() != null) {
            StmtNode els = wrapBlock(astNode.getElseStatement(), expr, scope);
            ifstmt.setElse(els);
        }

        ifstmt.setScope(scope);
        statementNodes.add(ifstmt);
        return ifstmt;
    }

    private Node visit(LabeledStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        LabeledStmt labStmt = new LabeledStmt(astNode, filePath, start, end);
        labStmt.setControlDependency(control);
        // label
        SimpName lab = (SimpName) buildNode(astNode.getLabel(), control, scope);
        labStmt.setLabel(lab);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), control, scope);
        labStmt.setBody(body);

        labStmt.setScope(scope);
        statementNodes.add(labStmt);
        return labStmt;
    }

    private Node visit(ReturnStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ReturnStmt ret = new ReturnStmt(astNode, filePath, start, end);
        ret.setControlDependency(control);
        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
            ret.setExpr(expr);
        }

        ret.setScope(scope);
        statementNodes.add(ret);
        return ret;
    }

    private Node visit(SuperConstructorInvocation astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperConstructorInvoc spi = new SuperConstructorInvoc(astNode, filePath, start, end);
        spi.setControlDependency(control);
        // expression
        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
            spi.setExpr(expr);
        }
        // parameters
        ExprList arguList = new ExprList(null, filePath, start, end);
        List<ExprNode> argus = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode para = (ExprNode) buildNode((ASTNode) obj, control, scope);
            argus.add(para);
        }
        arguList.setExprs(argus);
        spi.setArguments(arguList);

        spi.setScope(scope);
        statementNodes.add(spi);
        return spi;
    }

    private Node visit(SwitchStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SwitchStmt swi = new SwitchStmt(astNode, filePath, start, end);
        swi.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        swi.setExpr(expr);
        // stmt list
        List<StmtNode> stmts = new ArrayList<>();
        Node swiCase = null;
        for (Object obj : astNode.statements()) {
            StmtNode stmt = (StmtNode) buildNode((ASTNode) obj, expr, scope);
            // TODO: relation with each switch case value
            stmts.add(stmt);
            if (stmt instanceof CaseStmt) {
                swiCase = stmt;
            }
        }
        swi.setStatements(stmts);

        swi.setScope(scope);
        statementNodes.add(swi);
        return swi;
    }

    private Node visit(SynchronizedStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SynchronizedStmt syn = new SynchronizedStmt(astNode, filePath, start, end);
        syn.setControlDependency(control);
        // expression
        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
            syn.setExpr(expr);
        }
        // body
        BlockStmt body = (BlockStmt) buildNode(astNode.getBody(), syn.getExpression(), scope);
        syn.setBody(body);

        syn.setScope(scope);
        statementNodes.add(syn);
        return syn;
    }

    private Node visit(ThrowStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ThrowStmt throwStmt = new ThrowStmt(astNode, filePath, start, end);
        throwStmt.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        throwStmt.setExpr(expr);

        throwStmt.setScope(scope);
        statementNodes.add(throwStmt);
        return throwStmt;
    }

    private Node visit(TryStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TryStmt tryStmt = new TryStmt(astNode, filePath, start, end);
        tryStmt.setControlDependency(control);
        // resources
        if (astNode.resources() != null) {
            List<VarDeclExpr> resourceList = new ArrayList<>();
            for (Object obj : astNode.resources()) {
                VariableDeclarationExpression resource = (VariableDeclarationExpression) obj;
                VarDeclExpr vdExpr = (VarDeclExpr) buildNode(resource, control, scope);
                resourceList.add(vdExpr);
            }
            tryStmt.setResources(resourceList);
        }
        BlockStmt blk = (BlockStmt) buildNode(astNode.getBody(), control, scope);
        tryStmt.setBody(blk);
        // catch
        List<CatClause> catches = new ArrayList<>(astNode.catchClauses().size());
        for (Object obj : astNode.catchClauses()) {
            CatchClause catchClause = (CatchClause) obj;
            CatClause catClause = (CatClause) buildNode(catchClause, control, scope);
            catches.add(catClause);
        }
        tryStmt.setCatchClause(catches);
        // finally
        if (astNode.getFinally() != null ){
            BlockStmt finallyBlk = (BlockStmt) buildNode(astNode.getFinally(), control, scope);
            tryStmt.setFinallyBlock(finallyBlk);
        }

        tryStmt.setScope(scope);
        statementNodes.add(tryStmt);
        return tryStmt;
    }

    private CatClause visit(CatchClause astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CatClause cat = new CatClause(astNode, filePath, start, end);
        cat.setControlDependency(control);

        SingleVarDecl svd = (SingleVarDecl) buildNode(astNode.getException(), control, scope);
        cat.setException(svd);

        BlockStmt body = (BlockStmt) buildNode(astNode.getBody(), svd, scope);
        cat.setBody(body);

        cat.setScope(scope);
        statementNodes.add(cat);
        return cat;
    }

    private Node visit(TypeDeclarationStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypeDeclStmt td = new TypeDeclStmt(astNode, filePath, start, end);
        td.setControlDependency(control);
        td.setScope(scope);
        return td;
    }

    private Node visit(VariableDeclarationStatement astNode, Node control, Scope scope) {
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
        TypeNode type = (TypeNode) buildNode(astNode.getType(), control, scope);
        String typeStr = JavaASTUtil.getSimpleType(astNode.getType());
        vdStmt.setDeclType(type, typeStr);
        // fragments
        List<VarDeclFrag> fragments = new ArrayList<>();
        for (Object obj : astNode.fragments()) {
            VarDeclFrag vdf = (VarDeclFrag) buildNode((ASTNode) obj, control, scope);
            vdf.setDeclType(type);
            fragments.add(vdf);
        }
        vdStmt.setFragments(fragments);

        vdStmt.setScope(scope);
        statementNodes.add(vdStmt);
        return vdStmt;
    }

    private Node visit(WhileStatement astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        WhileStmt whi = new WhileStmt(astNode, filePath, start, end);
        whi.setControlDependency(control);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        whi.setExpr(expr);
        // body
        StmtNode body = wrapBlock(astNode.getBody(), expr, scope);
        whi.setBody(body);

        whi.setScope(scope);
        statementNodes.add(whi);
        return whi;
    }

    private Node visit(Annotation astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AnnotationExpr anno = new AnnotationExpr(astNode, filePath, start, end);
        anno.setControlDependency(control);
        anno.setScope(scope);
        return anno;
    }

    private Node visit(ArrayAccess astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AryAcc aryAcc = new AryAcc(astNode, filePath, start, end);
        aryAcc.setControlDependency(control);
        // array
        ExprNode array = (ExprNode) buildNode(astNode.getArray(), control, scope);
        aryAcc.setArray(array);
        // index
        ExprNode index = (ExprNode) buildNode(astNode.getIndex(), control, scope);
        aryAcc.setIndex(index);
        // type
        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        String typeStr = JavaASTUtil.getSimpleType(type);
        TypeNode typ = (TypeNode) buildNode(type, control, scope);
        aryAcc.setType(typ, typeStr);

        aryAcc.setScope(scope);
        return aryAcc;
    }

    private Node visit(ArrayCreation astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AryCreation arycr = new AryCreation(astNode, filePath, start, end);
        arycr.setControlDependency(control);
        // type
        Type type = typeFromBinding(astNode.getAST(), astNode.getType().getElementType().resolveBinding());
        if (type == null || type instanceof WildcardType) {
            type = astNode.getType().getElementType();
        }
        TypeNode typ = (TypeNode) buildNode(astNode.getType().getElementType(), control, scope);
        String typStr = JavaASTUtil.getSimpleType(type);
        arycr.setArrayType(typ, typStr);
        arycr.setType(astNode.getType());
        // dimension
        ExprList dimlist = new ExprList(null, filePath, start, end);
        List<ExprNode> dimension = new ArrayList<>();
        for (Object obj : astNode.dimensions()) {
            ExprNode dim = (ExprNode) buildNode((ASTNode) obj, control, scope);
            dimension.add(dim);
        }
        dimlist.setExprs(dimension);
        arycr.setDimension(dimlist);
        // initializer
        if (astNode.getInitializer() != null) {
            AryInitializer aryinit = (AryInitializer) buildNode(astNode.getInitializer(), control, scope);
            arycr.setInitializer(aryinit);
        }

        arycr.setScope(scope);
        return arycr;
    }

    private AryInitializer visit(ArrayInitializer astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AryInitializer aryinit = new AryInitializer(astNode, filePath, start, end);
        aryinit.setControlDependency(control);
        List<ExprNode> exprs = new ArrayList<>();
        for (Object obj : astNode.expressions()) {
            ExprNode expr = (ExprNode) buildNode((ASTNode) obj, control, scope);
            exprs.add(expr);
        }
        aryinit.setExpressions(exprs);

        aryinit.setScope(scope);
        return aryinit;
    }

    private AssignExpr visit(Assignment astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AssignExpr assign = new AssignExpr(astNode, filePath, start, end);
        assign.setControlDependency(control);
        // left
        ExprNode lhs = (ExprNode) buildNode(astNode.getLeftHandSide(), control, scope);
        assign.setLeftHandSide(lhs);
        // right
        ExprNode rhs = (ExprNode) buildNode(astNode.getRightHandSide(), control, scope);
        assign.setRightHandSide(rhs);
        // operator
        AssignOpr assignOpr = new AssignOpr(null, filePath, start, end);
        assignOpr.setOperator(astNode.getOperator());
        assign.setOperator(assignOpr);

        scope.addDefine(lhs.toNameString(), assign);
        lhs.setDataDependency(rhs);
        assign.setScope(scope);
        return assign;
    }

    private BoolLiteral visit(BooleanLiteral astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        BoolLiteral literal = new BoolLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);
        literal.setValue(astNode.booleanValue());
        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        literal.setType(type);

        literal.setScope(scope);
        return literal;
    }

    private CastExpr visit(CastExpression astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CastExpr cast = new CastExpr(astNode, filePath, start, end);
        cast.setControlDependency(control);
        // type
        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), control, scope);
        cast.setCastType(typeNode);
        // expression
        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        cast.setExpression(expr);
        cast.setType(astNode.getType());

        cast.setScope(scope);
        return cast;
    }

    private CharLiteral visit(CharacterLiteral astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CharLiteral literal = new CharLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);
        literal.setValue(astNode.charValue());
        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        literal.setType(type);

        literal.setScope(scope);
        return literal;
    }

    private ClassInstanceCreationExpr visit(ClassInstanceCreation astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ClassInstanceCreationExpr classCreation = new ClassInstanceCreationExpr(astNode, filePath, start, end);
        classCreation.setControlDependency(control);

        if (astNode.getExpression() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
            classCreation.setExpression(expr);
        }

        if (astNode.getAnonymousClassDeclaration() != null) {
            AnonymousClassDecl anony = (AnonymousClassDecl) buildNode(astNode.getAnonymousClassDeclaration(), control, scope);
            classCreation.setAnonymousClassDecl(anony);
        }

        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> argus = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode arg = (ExprNode) buildNode((ASTNode) obj, control, scope);
            argus.add(arg);
        }
        exprList.setExprs(argus);
        classCreation.setArguments(exprList);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), control, scope);
        classCreation.setClassType(typeNode);
        classCreation.setType(astNode.getType());

        classCreation.setScope(scope);
        return classCreation;
    }

    private AnonymousClassDecl visit(AnonymousClassDeclaration astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        AnonymousClassDecl anony = new AnonymousClassDecl(astNode, filePath, start, end);
        anony.setControlDependency(control);
        anony.setScope(scope);
        return anony;
    }

    private ConditionalExpr visit(ConditionalExpression astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ConditionalExpr condExpr = new ConditionalExpr(astNode, filePath, start, end);
        condExpr.setControlDependency(control);

        ExprNode condition = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        condExpr.setCondition(condition);

        ExprNode thenExpr = (ExprNode) buildNode(astNode.getThenExpression(), control, scope);
        condExpr.setThenExpr(thenExpr);

        ExprNode elseExpr = (ExprNode) buildNode(astNode.getElseExpression(), control, scope);
        condExpr.setElseExpr(elseExpr);

        condExpr.setScope(scope);
        return condExpr;
    }

    private CreationRef visit(CreationReference astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        CreationRef cref = new CreationRef(astNode, filePath, start, end);
        cref.setControlDependency(control);
        // TODO: parse nodes for CreationReference
        cref.setScope(scope);
        return cref;
    }

    private ExprMethodRef visit(ExpressionMethodReference astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ExprMethodRef exprMethodRef = new ExprMethodRef(astNode, filePath, start, end);
        exprMethodRef.setControlDependency(control);
        // TODO: parse nodes for ExpressionMethodReference
        exprMethodRef.setScope(scope);
        return exprMethodRef;
    }

    private FieldAcc visit(FieldAccess astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        FieldAcc fieldAcc = new FieldAcc(astNode, filePath, start, end);
        fieldAcc.setControlDependency(control);

        ExprNode expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        fieldAcc.setExpression(expr);

        SimpName iden = (SimpName) buildNode(astNode.getName(), control, scope);
        fieldAcc.setIdentifier(iden);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        fieldAcc.setType(type);

        fieldAcc.setScope(scope);
        return fieldAcc;
    }

    private InfixExpr visit(InfixExpression astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        InfixExpr infixExpr = new InfixExpr(astNode, filePath, start, end);
        infixExpr.setControlDependency(control);

        ExprNode lhs = (ExprNode) buildNode(astNode.getLeftOperand(), control, scope);
        infixExpr.setLeftHandSide(lhs);

        ExprNode rhs = (ExprNode) buildNode(astNode.getRightOperand(), control, scope);
        infixExpr.setRightHandSide(rhs);

        InfixOpr infixOpr = new InfixOpr(null, filePath, start, end);
        infixOpr.setOperator(astNode.getOperator());
        infixExpr.setOperatior(infixOpr);

        if (astNode.hasExtendedOperands()) {
            lhs = infixExpr;
            for (Object obj : astNode.extendedOperands()) {
                rhs = (ExprNode) buildNode((Expression) obj, control, scope);
                infixExpr = new InfixExpr((ASTNode) obj, filePath, start, end);
                infixExpr.setLeftHandSide(lhs);
                infixExpr.setRightHandSide(rhs);

                infixOpr = new InfixOpr(null, filePath, start, end);
                infixOpr.setOperator(astNode.getOperator());

                lhs = infixExpr;
            }
        }

        infixExpr.setScope(scope);
        return infixExpr;
    }

    private InstanceofExpr visit(InstanceofExpression astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        InstanceofExpr instanceofExpr = new InstanceofExpr(astNode, filePath, start, end);
        instanceofExpr.setControlDependency(control);

        ExprNode expr = (ExprNode) buildNode(astNode.getLeftOperand(), control, scope);
        instanceofExpr.setExpression(expr);

        TypeNode instType = (TypeNode) buildNode(astNode.getRightOperand(), control, scope);
        instanceofExpr.setInstanceType(instType);

        instanceofExpr.setScope(scope);
        return instanceofExpr;
    }

    private LambdaExpr visit(LambdaExpression astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        LambdaExpr lambdaExpr = new LambdaExpr(astNode, filePath, start, end);
        lambdaExpr.setControlDependency(control);
        // TODO: parse nodes for lambda expression
        lambdaExpr.setScope(scope);
        return lambdaExpr;
    }

    private MethodInvoc visit(MethodInvocation astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        MethodInvoc methodInvoc = new MethodInvoc(astNode, filePath, start, end);
        methodInvoc.setControlDependency(control);

        ExprNode expr = null;
        if (astNode.getExpression() != null) {
            expr = (ExprNode) buildNode(astNode.getExpression(), control, scope);
            methodInvoc.setExpression(expr);
        }

        SimpName iden = (SimpName) buildNode(astNode.getName(), control, scope);
        methodInvoc.setName(iden);

        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> args = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode arg = (ExprNode) buildNode((ASTNode) obj, control, scope);
            args.add(arg);
        }
        exprList.setExprs(args);
        methodInvoc.setArguments(exprList);

        methodInvoc.setScope(scope);
        return methodInvoc;
    }

    private MethodRef visit(MethodReference astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        MethodRef mref = new MethodRef(astNode, filePath, start, end);
        mref.setControlDependency(control);
        mref.setScope(scope);
        return mref;
    }

    private SimpName visit(SimpleName astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SimpName sname = new SimpName(astNode, filePath, start, end);
        sname.setControlDependency(control);

        String name = astNode.getFullyQualifiedName();
        sname.setName(name);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        sname.setType(type);

        sname.setScope(scope);
        scope.addUse(sname.toNameString(), sname);
        return sname;
    }

    private QuaName visit(QualifiedName astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        QuaName qname = new QuaName(astNode, filePath, start, end);
        qname.setControlDependency(control);
        // Name . SimpleName
        SimpName sname = (SimpName) buildNode(astNode.getName(), control, scope);
        qname.setName(sname);

        NameExpr name = (NameExpr) buildNode(astNode.getQualifier(), control, scope);
        qname.setQualifier(name);

        qname.setScope(scope);
        scope.addUse(qname.toNameString(), qname);
        return qname;
    }

    private NulLiteral visit(NullLiteral astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        NulLiteral literal = new NulLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);
        literal.setScope(scope);
        return literal;
    }

    private NumLiteral visit(NumberLiteral astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        NumLiteral literal = new NumLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);

        String value = astNode.getToken();
        literal.setValue(value);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveTypeBinding());
        literal.setType(type);

        literal.setScope(scope);
        return literal;
    }

    private ParenExpr visit(ParenthesizedExpression astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ParenExpr parenthesiszedExpr = new ParenExpr(astNode, filePath, start, end);
        parenthesiszedExpr.setControlDependency(control);

        ExprNode expression = (ExprNode) buildNode(astNode.getExpression(), control, scope);
        parenthesiszedExpr.setExpr(expression);

        parenthesiszedExpr.setScope(scope);
        return parenthesiszedExpr;
    }

    private PostfixExpr visit(PostfixExpression astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        PostfixExpr postfixExpr = new PostfixExpr(astNode, filePath, start, end);
        postfixExpr.setControlDependency(control);

        ExprNode expr = (ExprNode) buildNode(astNode.getOperand(), control, scope);
        postfixExpr.setExpr(expr);

        PostfixOpr postfixOpr = new PostfixOpr(null, filePath, start, end);
        postfixOpr.setOperator(astNode.getOperator());
        postfixExpr.setOpr(postfixOpr);

        switch (postfixOpr.toString()) {
            case "++":
            case "--":
                scope.addDefine(expr.toNameString(), postfixExpr);
                // TODO: build data dependency but can have a loop
                postfixExpr.setDataDependency(expr);
        }

        postfixExpr.setScope(scope);
        return postfixExpr;
    }

    private PrefixExpr visit(PrefixExpression astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        PrefixExpr prefixExpr = new PrefixExpr(astNode, filePath, start, end);
        prefixExpr.setControlDependency(control);

        ExprNode expr = (ExprNode) buildNode(astNode.getOperand(), control, scope);
        prefixExpr.setExpr(expr);

        PrefixOpr prefixOpr = new PrefixOpr(null, filePath, start, end);
        prefixOpr.setOperator(astNode.getOperator());
        prefixExpr.setOpr(prefixOpr);

        switch (prefixExpr.toNameString()) {
            case "++":
            case "--":
                scope.addDefine(expr.toNameString(), prefixExpr);
                // TODO: build data dependency but can have a loop
                prefixExpr.setDataDependency(expr);
        }

        prefixExpr.setScope(scope);
        return prefixExpr;
    }

    private StrLiteral visit(StringLiteral astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        StrLiteral literal = new StrLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);

        literal.setLiteralValue(astNode.getLiteralValue());
        literal.setEscapedValue(astNode.getEscapedValue());

        literal.setScope(scope);
        return literal;
    }

    private SuperFieldAcc visit(SuperFieldAccess astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperFieldAcc superFieldAcc = new SuperFieldAcc(astNode, filePath, start, end);
        superFieldAcc.setControlDependency(control);

        SimpName iden = (SimpName) buildNode(astNode.getName(), control, scope);
        superFieldAcc.setIdentifier(iden);

        if (astNode.getQualifier() != null) {
            QuaName qname = (QuaName) buildNode(astNode.getQualifier(), control, scope);
            superFieldAcc.setQualifier(qname);
        }

        superFieldAcc.setScope(scope);
        return superFieldAcc;
    }

    private SuperMethodInvoc visit(SuperMethodInvocation astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperMethodInvoc superMethodInvoc = new SuperMethodInvoc(astNode, filePath, start, end);
        superMethodInvoc.setControlDependency(control);

        SimpName name = (SimpName) buildNode(astNode.getName(), control, scope);
        superMethodInvoc.setName(name);

        if (astNode.getQualifier() != null) {
            QuaName qualifier = (QuaName) buildNode(astNode.getQualifier(), control, scope);
            superMethodInvoc.setQualifier(qualifier);
        }

        ExprList exprList = new ExprList(null, filePath, start, end);
        List<ExprNode> args = new ArrayList<>();
        for (Object obj : astNode.arguments()) {
            ExprNode arg = (ExprNode) buildNode((ASTNode) obj, control, scope);
            args.add(arg);
        }
        exprList.setExprs(args);
        superMethodInvoc.setArguments(exprList);

        superMethodInvoc.setScope(scope);
        return superMethodInvoc;
    }

    private SuperMethodRef visit(SuperMethodReference astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SuperMethodRef superMethodRef = new SuperMethodRef(astNode, filePath, start, end);
        superMethodRef.setControlDependency(control);
        // TODO: parse nodes for SuperMethodReference
        superMethodRef.setScope(scope);
        return superMethodRef;
    }

    private ThisExpr visit(ThisExpression astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        ThisExpr thisExpr = new ThisExpr(astNode, filePath, start, end);
        thisExpr.setControlDependency(control);
        thisExpr.setScope(scope);
        return thisExpr;
    }

    private TypLiteral visit(TypeLiteral astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypLiteral literal = new TypLiteral(astNode, filePath, start, end);
        literal.setControlDependency(control);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), control, scope);
        literal.setValue(typeNode);

        literal.setScope(scope);
        return literal;
    }

    private TypeMethodRef visit(TypeMethodReference astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypeMethodRef typeMethodRef = new TypeMethodRef(astNode, filePath, start, end);
        typeMethodRef.setControlDependency(control);
        // TODO: parse nodes for TypeMethodReference
        typeMethodRef.setScope(scope);
        return typeMethodRef;
    }

    private VarDeclExpr visit(VariableDeclarationExpression astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        VarDeclExpr varDeclExpr = new VarDeclExpr(astNode, filePath, start, end);
        varDeclExpr.setControlDependency(control);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), control, scope);
        varDeclExpr.setDeclType(typeNode);

        List<VarDeclFrag> fragments = new ArrayList<>();
        for (Object obj : astNode.fragments()) {
            VarDeclFrag vdf = (VarDeclFrag) buildNode((ASTNode) obj, control, scope);
            vdf.setDeclType(typeNode);
            fragments.add(vdf);
        }
        varDeclExpr.setFragments(fragments);

        varDeclExpr.setScope(scope);
        return varDeclExpr;
    }

    private VarDeclFrag visit(VariableDeclarationFragment astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        VarDeclFrag vdf = new VarDeclFrag(astNode, filePath, start, end);
        vdf.setControlDependency(control);

        SimpName iden = (SimpName) buildNode(astNode.getName(), control, scope);
        vdf.setName(iden);

        vdf.setDimensions(astNode.getExtraDimensions());

        if (astNode.getInitializer() != null) {
            ExprNode expr = (ExprNode) buildNode(astNode.getInitializer(), control, scope);
            vdf.setExpr(expr);
            iden.setDataDependency(expr);
        }

        scope.addDefine(iden.toNameString(), vdf);

        vdf.setScope(scope);
        return vdf;
    }

    private SingleVarDecl visit(SingleVariableDeclaration astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        SingleVarDecl svd = new SingleVarDecl(astNode, filePath, start, end);
        svd.setControlDependency(control);

        TypeNode typeNode = (TypeNode) buildNode(astNode.getType(), control, scope);
        svd.setDeclType(typeNode);

        if (astNode.getInitializer() != null) {
            ExprNode init = (ExprNode) buildNode(astNode.getInitializer(), control, scope);
            svd.setInitializer(init);
        }

        SimpName name = (SimpName) buildNode(astNode.getName(), control, scope);
        svd.setName(name);

        scope.addDefine(name.toNameString(), svd);

        svd.setScope(scope);
        return svd;
    }

    private TypeNode visit(Type astNode, Node control, Scope scope) {
        int start = cu.getLineNumber(astNode.getStartPosition());
        int end = cu.getLineNumber(astNode.getStartPosition() + astNode.getLength());
        TypeNode typeNode = new TypeNode(astNode, filePath, start, end);
        typeNode.setControlDependency(control);

        Type type = typeFromBinding(astNode.getAST(), astNode.resolveBinding());
        if (type == null || type instanceof WildcardType) {
            type = astNode;
        }
        typeNode.setType(type);

        typeNode.setScope(scope);
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

    private BlockStmt wrapBlock(Statement node, Node control, Scope scope) {
        BlockStmt blk;
        if(node instanceof Block) {
            blk = (BlockStmt) buildNode(node, control, scope);
        } else {
            int startLine = cu.getLineNumber(node.getStartPosition());
            int endLine = cu.getLineNumber(node.getStartPosition() + node.getLength());
            blk = new BlockStmt(node, filePath, startLine, endLine);
            List<StmtNode> stmts = new ArrayList<>();
            StmtNode stmt = (StmtNode) buildNode(node, control, scope);
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

    public String getFilePath() {
        return filePath;
    }

    public String getGraphName() {
        return name;
    }

    public List<Node> getNodes() {
        return allNodes;
    }
}
