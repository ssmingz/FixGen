package model;

import codegraph.ASTEdge;
import codegraph.Scope;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.*;
import spoon.support.reflect.declaration.*;
import spoon.support.reflect.reference.*;

import java.util.ArrayList;
import java.util.List;

public class CodeGraph {
    private String _name;
    private CtMethodImpl _ctMethod;
    private ArrayList<CtWrapper> _allNodes = new ArrayList<>();
    private CtElementImpl _entryNode;
    public CodeGraph() {}

    public void setCtMethod(CtMethodImpl ctMethod) {
        _ctMethod = ctMethod;
    }

    public void setName(String name) {
        _name = name;
    }

    public void buildNode(Object ctNode, CtElementImpl control, Scope scope) {
        if (ctNode == null) {
            return;
        }
        // connect AST children
        if (ctNode instanceof CtElementImpl) {
            for (CtElement ch : ((CtElementImpl) ctNode).getDirectChildren()) {
                new ASTEdge((CtElementImpl) ctNode, (CtElementImpl) ch);
            }
        }
        /* The structural part contains the declarations of the program elements, such as interface, class, variable, method, annotation, and enum declarations. */
        if (ctNode instanceof CtMethodImpl) {
            visit((CtMethodImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtAnonymousExecutableImpl) {
            // do not handle
        } else if (ctNode instanceof CtConstructorImpl) {
            visit((CtConstructorImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtEnumImpl) {
            visit((CtEnumImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtEnumValueImpl) {
            visit((CtEnumValueImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtFieldImpl) {
            visit((CtFieldImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtParameterImpl) {
            visit((CtParameterImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtTypeParameterImpl) {
            visit((CtTypeParameterImpl) ctNode, control, scope);
        }
        /* The code part contains the executable Java code, such as the one found in method bodies. */
        else if (ctNode instanceof CtAnnotationFieldAccessImpl) {
            // do not handle
        } else if (ctNode instanceof CtAnnotationImpl) {
            // do not handle
        } else if (ctNode instanceof CtArrayReadImpl) {
            visit((CtArrayReadImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtArrayWriteImpl) {
            visit((CtArrayWriteImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtAssertImpl) {
            visit((CtAssertImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtAssignmentImpl) {
            visit((CtAssignmentImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtBinaryOperatorImpl) {
            visit((CtBinaryOperatorImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtBlockImpl) {
            visit((CtBlockImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtBreakImpl) {
            visit((CtBreakImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtCaseImpl) {
            visit((CtCaseImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtCatchImpl) {
            visit((CtCatchImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtCatchVariableImpl) {
            visit((CtCatchVariableImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtCommentImpl) {
            // do not handle
        } else if (ctNode instanceof CtConditionalImpl) {
            visit((CtConditionalImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtConstructorCallImpl) {
            visit((CtConstructorCallImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtContinueImpl) {
            // do not handle
        } else if (ctNode instanceof CtDoImpl) {
            visit((CtDoImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtExecutableReferenceExpressionImpl) {
            visit((CtExecutableReferenceExpressionImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtFieldReadImpl) {
            visit((CtFieldReadImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtFieldWriteImpl) {
            visit((CtFieldWriteImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtForEachImpl) {
            visit((CtForEachImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtForImpl) {
            visit((CtForImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtIfImpl) {
            visit((CtIfImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtInvocationImpl) {
            visit((CtInvocationImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtLambdaImpl) {
            visit((CtLambdaImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtLiteralImpl) {
            visit((CtLiteralImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtLocalVariableImpl) {
            visit((CtLocalVariableImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtNewArrayImpl) {
            visit((CtNewArrayImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtReturnImpl) {
            visit((CtReturnImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtStatementListImpl) {
            visit((CtStatementListImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtSuperAccessImpl) {
            visit((CtSuperAccessImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtSwitchExpressionImpl) {
            visit((CtSwitchExpressionImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtSwitchImpl) {
            visit((CtSwitchImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtSynchronizedImpl) {
            visit((CtSynchronizedImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtThisAccessImpl) {
            visit((CtThisAccessImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtThrowImpl) {
            visit((CtThrowImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtTryImpl) {
            visit((CtTryImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtTypeAccessImpl) {
            visit((CtTypeAccessImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtUnaryOperatorImpl) {
            visit((CtUnaryOperatorImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtVariableReadImpl) {
            visit((CtVariableReadImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtVariableWriteImpl) {
            visit((CtVariableWriteImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtWhileImpl) {
            visit((CtWhileImpl) ctNode, control, scope);
        }
        /* The reference part models the references to program elements (for instance a reference to a type). */
        else if (ctNode instanceof CtArrayTypeReferenceImpl) {
            visit((CtArrayTypeReferenceImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtCatchVariableReferenceImpl) {
            visit((CtCatchVariableReferenceImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtExecutableReferenceImpl) {
            visit((CtExecutableReferenceImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtFieldReferenceImpl) {
            visit((CtFieldReferenceImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtIntersectionTypeReferenceImpl) {
            // do not handle
        } else if (ctNode instanceof CtLocalVariableReferenceImpl) {
            visit((CtLocalVariableReferenceImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtParameterReferenceImpl) {
            visit((CtParameterReferenceImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtTypeParameterReferenceImpl) {
            visit((CtTypeParameterReferenceImpl) ctNode, control, scope);
        }
        else {
            System.out.println("UNKNOWN ctNode type : " + ctNode.toString());
        }
        CtWrapper ctwrapper = new CtWrapper((CtElementImpl) ctNode);
        _allNodes.add(ctwrapper);
    }

    private void visit(CtMethodImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: modifiers, ModifierKind (enum type)
        // return type
        if (ctNode.getType() != null) {
            buildNode(ctNode.getType(), control, scope);
        }
        // TODO: method name, simpleName (string type)
        // arguments
        for (Object para : ctNode.getParameters()) {
            buildNode(para, control, scope);
        }
        // throws type
        for (Object throwt : ctNode.getThrownTypes()) {
            buildNode(throwt, control, scope);
        }
        // method body
        if (ctNode.getBody() != null) {
            buildNode(ctNode.getBody(), control, scope);
        }
    }

    private void visit(CtConstructorImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: record name
        // arguments
        for (Object arg : ctNode.getParameters()) {
            buildNode(arg, control, scope);
        }
        // body
        buildNode(ctNode.getBody(), control, scope);
    }

    private void visit(CtEnumImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        for (Object enu : ctNode.getEnumValues()) {
            buildNode(enu, control, scope);
            scope.addDefine(enu.toString(), ctNode);
        }
    }

    private void visit(CtEnumValueImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: record value
        buildNode(ctNode.getAssignment(), control, scope);
    }

    private void visit(CtFieldImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: record value
        buildNode(ctNode.getAssignment(), control, scope);
    }

    private void visit(CtParameterImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
    }

    private void visit(CtTypeParameterImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
    }

    private void visit(CtArrayReadImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // array
        buildNode(ctNode.getTarget(), control, scope);
        // index
        buildNode(ctNode.getIndexExpression(), control, scope);

        scope.addUse(ctNode.getTarget().toString(), ctNode);
    }

    private void visit(CtArrayWriteImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // array
        buildNode(ctNode.getTarget(), control, scope);
        // index
        buildNode(ctNode.getIndexExpression(), control, scope);
        // type
        buildNode(ctNode.getType(), control, scope);
        // add define
        scope.addDefine(ctNode.getTarget().toString(), ctNode);
        ((CtElementImpl) ctNode.getTarget()).setDataDependency(ctNode);
    }

    private void visit(CtAssertImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        buildNode(ctNode.getExpression(), control, scope);
        // TODO: assert message
    }

    private void visit(CtAssignmentImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // left
        buildNode(ctNode.getAssigned(), control, scope);
        // right
        buildNode(ctNode.getAssignment(), control, scope);
        // type
        buildNode(ctNode.getType(), control, scope);
        // add define
        scope.addDefine(ctNode.getAssigned().toString(), ctNode);
        ((CtElementImpl) ctNode.getAssigned()).setDataDependency((CtElementImpl) ctNode.getAssignment());
        // TODO: type casts
        // TODO: operator
    }

    private void visit(CtBinaryOperatorImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: operator kind (BinaryOperatorKind type)
        // left
        buildNode(ctNode.getLeftHandOperand(), control, scope);
        // right
        buildNode(ctNode.getRightHandOperand(), control, scope);
    }

    private void visit(CtBlockImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // statements
        for (Object stmt : ctNode.getStatements()) {
            buildNode(stmt, control, scope);
        }
    }

    private void visit(CtBreakImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: label(string type), from which the control flow breaks (null if no label defined)
        // labelled statement
        if (ctNode.getLabelledStatement() != null) {
            buildNode(ctNode.getLabelledStatement(), control, scope);
        }
    }

    private void visit(CtCaseImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // case expression
        if (ctNode.getCaseExpression() != null) {
            buildNode(ctNode.getCaseExpression(), control, scope);
        }
        // statements
        for (Object stmt : ctNode.getStatements()) {
            buildNode(stmt, control, scope);
        }
    }

    private void visit(CtCatchImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // throwable
        buildNode(ctNode.getParameter(), control, scope);
        // body
        buildNode(ctNode.getBody(), (CtElementImpl) ctNode.getParameter(), scope);
    }

    private void visit(CtCatchVariableImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        scope.addDefine(ctNode.getSimpleName(), ctNode);
        ctNode.setScope(scope);
        // TODO: name (string type)
        // type
        buildNode(ctNode.getType(), control, scope);
        // initializer
        buildNode(ctNode.getDefaultExpression(), control, scope);
    }

    // 1==0 ? "foo" : "bar" // <-- ternary conditional
    private void visit(CtConditionalImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // condition
        buildNode(ctNode.getCondition(), control, scope);
        // then
        buildNode(ctNode.getThenExpression(), (CtElementImpl) ctNode.getCondition(), scope);
        // else
        buildNode(ctNode.getElseExpression(), (CtElementImpl) ctNode.getCondition(), scope);
    }

    private void visit(CtConstructorCallImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // arguments
        for (Object arg : ctNode.getArguments()) {
            buildNode(arg, control, scope);
        }
    }

    private void visit(CtDoImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        buildNode(ctNode.getLoopingExpression(), control, scope);
        // body
        // TODO: check whether need to wrap block
        buildNode(ctNode.getBody(), (CtElementImpl) ctNode.getLoopingExpression(), scope);
    }

    // * Example: java.util.function.Supplier p = Object::new;
    private void visit(CtExecutableReferenceExpressionImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expresssion
        buildNode(ctNode.getExecutable(), control, scope);
    }

    private void visit(CtFieldReadImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: use getTarget() or getVariable()??
        buildNode(ctNode.getTarget(), control, scope);

        scope.addUse(ctNode.getVariable().getSimpleName(), ctNode);
    }

    private void visit(CtFieldWriteImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        buildNode(ctNode.getVariable(), control, scope);
        scope.addDefine(ctNode.getVariable().toString(), ctNode);
    }

    private void visit(CtForEachImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // formal parameter
        buildNode(ctNode.getVariable(), control, scope);
        // expression
        buildNode(ctNode.getExpression(), control, scope);
        // body
        buildNode(ctNode.getBody(), (CtElementImpl) ctNode.getExpression(), scope);
    }

    private void visit(CtForImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // initializers
        for (Object init : ctNode.getForInit()) {
            buildNode(init, control, scope);
        }
        // expression
        if (ctNode.getExpression() != null) {
            buildNode(ctNode.getExpression(), control, scope);
        }
        // updaters
        for (Object upd : ctNode.getForUpdate()) {
            buildNode(upd, control, scope);
        }
        // body
        // TODO: check whether to wrap block
        buildNode(ctNode.getBody(), (CtElementImpl) ctNode.getExpression(), scope);
    }

    private void visit(CtIfImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        buildNode(ctNode.getCondition(), control, scope);
        // then
        // TODO: check whether to wrap block
        buildNode(ctNode.getThenStatement(), (CtElementImpl) ctNode.getCondition(), scope);
        // else
        // TODO: check whether to wrap block
        buildNode(ctNode.getElseStatement(), (CtElementImpl) ctNode.getCondition(), scope);
    }

    private void visit(CtInvocationImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        if (ctNode.getExecutable() != null) {
            buildNode(ctNode.getExecutable(), control, scope);
        }
        // name
        // TODO: check whether to use getTarget()
        if (ctNode.getTarget() != null) {
            buildNode(ctNode.getTarget(), control, scope);
        }
        // arguments
        for (Object arg : ctNode.getArguments()) {
            buildNode(arg, control, scope);
        }
    }

    private void visit(CtLambdaImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        if (ctNode.getExpression() != null) {
            buildNode(ctNode.getExpression(), control, scope);
        } else if (ctNode.getBody() != null) {
            buildNode(ctNode.getBody(), control, scope);
        }
    }

    private void visit(CtLiteralImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: record literal value
    }

    private void visit(CtLocalVariableImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: record name
    }

    private void visit(CtNewArrayImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: element list
        // TODO: dimension expressions
    }

    private void visit(CtReturnImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expr
        buildNode(ctNode.getReturnedExpression(), control, scope);
    }

    private void visit(CtStatementListImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        for (Object stmt : ctNode.getStatements()) {
            buildNode(stmt, control, scope);
        }
    }

    private void visit(CtSuperAccessImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        buildNode(ctNode.getTarget(), control, scope);
    }

    private void visit(CtSwitchExpressionImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        buildNode(ctNode.getSelector(), control, scope);
        // cases
        for (Object c : ctNode.getCases()) {
            buildNode(c, (CtElementImpl) ctNode.getSelector(), scope);
        }
    }

    private void visit(CtSwitchImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        buildNode(ctNode.getSelector(), control, scope);
        // cases
        for (Object c : ctNode.getCases()) {
            buildNode(c, (CtElementImpl) ctNode.getSelector(), scope);
        }
    }

    private void visit(CtSynchronizedImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        buildNode(ctNode.getExpression(), control, scope);
        // body
        buildNode(ctNode.getBlock(), (CtElementImpl) ctNode.getExpression(), scope);
    }

    private void visit(CtThisAccessImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        buildNode(ctNode.getTarget(), control, scope);
    }

    private void visit(CtThrowImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        buildNode(ctNode.getThrownExpression(), control, scope);
    }

    private void visit(CtTryImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // body
        buildNode(ctNode.getBody(), control, scope);
        // catch
        for (Object cat : ctNode.getCatchers()) {
            buildNode(cat, control, scope);
        }
        // final
        buildNode(ctNode.getFinalizer(), control, scope);
    }

    private void visit(CtTypeAccessImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // TODO: check how to use
    }

    private void visit(CtUnaryOperatorImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // operand
        buildNode(ctNode.getOperand(), control, scope);
        // add define
        if (ctNode.getKind() == UnaryOperatorKind.POSTDEC || ctNode.getKind() == UnaryOperatorKind.POSTINC
                || ctNode.getKind() == UnaryOperatorKind.PREDEC || ctNode.getKind() == UnaryOperatorKind.PREINC ) {
            scope.addDefine(ctNode.getOperand().toString(), ctNode);
            ctNode.setDataDependency((CtElementImpl) ctNode.getOperand());
        }
    }

    private void visit(CtVariableReadImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // variable
        scope.addUse(ctNode.getVariable().getSimpleName(), ctNode);
    }

    private void visit(CtVariableWriteImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // variable
        scope.addDefine(ctNode.getVariable().getSimpleName(), ctNode);
    }

    private void visit(CtWhileImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // expression
        buildNode(ctNode.getLoopingExpression(), control, scope);
        // body
        buildNode(ctNode.getBody(), (CtElementImpl) ctNode.getLoopingExpression(), scope);
    }

    // a reference to an array
    private void visit(CtArrayTypeReferenceImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        // can get array type, component type, dimension count, array name
    }

    private void visit(CtCatchVariableReferenceImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        buildNode(ctNode.getDeclaration(), control, scope);
    }

    private void visit(CtExecutableReferenceImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        buildNode(ctNode.getDeclaration(), control, scope);
    }

    private void visit(CtFieldReferenceImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        buildNode(ctNode.getDeclaration(), control, scope);
    }

    private void visit(CtLocalVariableReferenceImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        buildNode(ctNode.getDeclaration(), control, scope);
    }

    private void visit(CtParameterReferenceImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        buildNode(ctNode.getDeclaration(), control, scope);
    }

    private void visit(CtTypeParameterReferenceImpl ctNode, CtElementImpl control, Scope scope) {
        ctNode.setControlDependency(control);
        ctNode.setScope(scope);
        buildNode(ctNode.getDeclaration(), control, scope);
    }

    public void setEntryNode(CtElementImpl buildNode) {
        _entryNode = buildNode;
    }

    public String getGraphName() {
        return _name;
    }

    public List<CtWrapper> getNodes() {
        return _allNodes;
    }
}
