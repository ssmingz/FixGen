package model;

import model.graph.Scope;
import model.graph.node.Node;
import spoon.support.reflect.code.*;
import spoon.support.reflect.declaration.*;
import spoon.support.reflect.reference.*;

import java.util.ArrayList;
import java.util.List;

public class CodeGraph {
    private String _name;
    private CtMethodImpl _ctMethod;
    private List<Node> _allNodes = new ArrayList<>();
    private Node _entryNode;
    public CodeGraph() {}

    public void setCtMethod(CtMethodImpl ctMethod) {
        _ctMethod = ctMethod;
    }

    public void setName(String name) {
        _name = name;
    }

    public Node buildNode(Object ctNode, Node control, Scope scope) {
        if (ctNode == null) {
            return null;
        }
        Node node = null;
        /* The structural part contains the declarations of the program elements, such as interface, class, variable, method, annotation, and enum declarations. */
        if (ctNode instanceof CtMethodImpl) {
            node = visit((CtMethodImpl) ctNode, control, scope);
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
            node = null;
        }
        if (node != null) {
            _allNodes.add(node);
        }
        return node;
    }

    private Node visit(CtMethodImpl ctNode, Node control, Scope scope) {
        Node method = Node.getNode(ctNode);
        method.setControlDependency(control);
        return method;
    }

    public void setEntryNode(Node buildNode) {
        _entryNode = buildNode;
    }

}
