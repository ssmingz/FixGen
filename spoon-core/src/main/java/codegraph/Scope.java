package codegraph;

import spoon.reflect.code.CtArrayWrite;
import spoon.support.reflect.code.*;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Scope {
    private Scope parent;
    private Map<String, List<CtElementImpl>> defVars = new LinkedHashMap<>();
    private Map<String, CtElementImpl> usedVars = new LinkedHashMap<>();

    public Scope(Scope p) {
        parent = p;
    }

    public void addDefine(String iden, CtElementImpl node) {
        if (iden == null || node == null)
            return;
        if (defVars.containsKey(iden)) {
            if (inThen(node)) {
                if (noElse(node)) {

                } else {

                }
            } else if (inElse(node)) {

            } else if (inLoopOrCase(node)) {

            }
        } else {
            List<CtElementImpl> defines = new ArrayList<>();
            defines.add(node);
            defVars.put(iden, defines);
        }
    }

    public void addUse(String iden, CtElementImpl node) {
        if(iden == null || node == null)
            return;
        if (defVars.containsKey(iden)) {
            new DefUseEdge(getDefinedVar(defVars.get(iden)), node);
        }
        usedVars.put(iden, node);
    }

    public static CtElementImpl getDefinedVar(CtElementImpl node) {
        if (node instanceof CtAssignmentImpl) {
            return (CtElementImpl) ((CtAssignmentImpl) node).getAssigned();
        } else if (node instanceof CtUnaryOperatorImpl) {
            return (CtElementImpl) ((CtUnaryOperatorImpl) node).getOperand();
        } else if (node instanceof CtVariableWriteImpl) {
            return (CtElementImpl) ((CtVariableWriteImpl) node).getVariable();
        } else if (node instanceof CtCatchVariableImpl) {
            return (CtElementImpl) ((CtCatchVariableImpl) node).getReference();
        } else if (node instanceof CtArrayWriteImpl) {
            return (CtElementImpl) ((CtArrayWriteImpl) node).getTarget();
        } else if (node instanceof CtFieldWriteImpl) {
            return (CtElementImpl) ((CtFieldWriteImpl) node).getTarget();
        } else {
            return null;
        }
    }
}
