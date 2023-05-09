package model;

import codegraph.Scope;
import spoon.support.reflect.code.*;
import spoon.support.reflect.declaration.*;
import spoon.support.reflect.reference.*;

import java.util.ArrayList;

public class CodeGraph {
    private String _name;
    private CtMethodImpl _ctMethod;
    private ArrayList<CtElementImpl> _allNodes = new ArrayList<>();
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
        /* The structural part contains the declarations of the program elements, such as interface, class, variable, method, annotation, and enum declarations. */
        if (ctNode instanceof CtMethodImpl) {
            visit((CtMethodImpl) ctNode, control, scope);
        } else if (ctNode instanceof CtAnonymousExecutableImpl) {

        } else if (ctNode instanceof CtConstructorImpl) {

        } else if (ctNode instanceof CtEnumImpl) {

        } else if (ctNode instanceof CtEnumValueImpl) {

        } else if (ctNode instanceof CtFieldImpl) {

        } else if (ctNode instanceof CtParameterImpl) {

        } else if (ctNode instanceof CtTypeParameterImpl) {

        }
        /* The code part contains the executable Java code, such as the one found in method bodies. */
        else if (ctNode instanceof CtAnnotationFieldAccessImpl) {

        } else if (ctNode instanceof CtAnnotationImpl) {

        } else if (ctNode instanceof CtArrayAccessImpl) {

        } else if (ctNode instanceof CtAssertImpl) {

        } else if (ctNode instanceof CtAssignmentImpl) {

        } else if (ctNode instanceof CtBinaryOperatorImpl) {

        } else if (ctNode instanceof CtBlockImpl) {

        } else if (ctNode instanceof CtBreakImpl) {

        } else if (ctNode instanceof CtCaseImpl) {

        } else if (ctNode instanceof CtCatchImpl) {

        } else if (ctNode instanceof CtCatchVariableImpl) {

        } else if (ctNode instanceof CtCommentImpl) {

        } else if (ctNode instanceof CtConditionalImpl) {

        } else if (ctNode instanceof CtConstructorCallImpl) {

        } else if (ctNode instanceof CtContinueImpl) {

        } else if (ctNode instanceof CtDoImpl) {

        } else if (ctNode instanceof CtExecutableReferenceExpressionImpl) {

        } else if (ctNode instanceof CtFieldAccessImpl) {

        } else if (ctNode instanceof CtForEachImpl) {

        } else if (ctNode instanceof CtForImpl) {

        } else if (ctNode instanceof CtIfImpl) {

        } else if (ctNode instanceof CtInvocationImpl) {

        } else if (ctNode instanceof CtLambdaImpl) {

        } else if (ctNode instanceof CtLiteralImpl) {

        } else if (ctNode instanceof CtLocalVariableImpl) {

        } else if (ctNode instanceof CtNewArrayImpl) {

        } else if (ctNode instanceof CtReturnImpl) {

        } else if (ctNode instanceof CtStatementListImpl) {

        } else if (ctNode instanceof CtSuperAccessImpl) {

        } else if (ctNode instanceof CtSwitchExpressionImpl) {

        } else if (ctNode instanceof CtSwitchImpl) {

        } else if (ctNode instanceof CtSynchronizedImpl) {

        } else if (ctNode instanceof CtThisAccessImpl) {

        } else if (ctNode instanceof CtThrowImpl) {

        } else if (ctNode instanceof CtTryImpl) {

        } else if (ctNode instanceof CtTypeAccessImpl) {

        } else if (ctNode instanceof CtUnaryOperatorImpl) {

        } else if (ctNode instanceof CtVariableReadImpl) {

        } else if (ctNode instanceof CtVariableWriteImpl) {

        } else if (ctNode instanceof CtWhileImpl) {

        }
        /* The reference part models the references to program elements (for instance a reference to a type). */
        else if (ctNode instanceof CtArrayTypeReferenceImpl) {

        } else if (ctNode instanceof CtCatchVariableReferenceImpl) {

        } else if (ctNode instanceof CtExecutableReferenceImpl) {

        } else if (ctNode instanceof CtFieldReferenceImpl) {

        } else if (ctNode instanceof CtIntersectionTypeReferenceImpl) {

        } else if (ctNode instanceof CtLocalVariableReferenceImpl) {

        } else if (ctNode instanceof CtParameterReferenceImpl) {

        } else if (ctNode instanceof CtTypeParameterReferenceImpl) {

        }
        else {
            System.out.println("UNKNOWN ctNode type : " + ctNode.toString());
        }
        if (ctNode != null) {
            _allNodes.add((CtElementImpl) ctNode);
        }
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


    public void setEntryNode(CtElementImpl buildNode) {
        _entryNode = buildNode;
    }

}
