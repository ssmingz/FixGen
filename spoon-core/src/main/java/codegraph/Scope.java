package codegraph;

import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.code.*;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.*;

import static spoon.reflect.path.CtRole.BODY;
import static spoon.reflect.path.CtRole.CASE;

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
        List<CtElementImpl> defines;
        if (defVars.containsKey(iden)) {
            if (allSiblingBranchDefine(node, iden)) {
                defines = new ArrayList<>();
                defines.add(node);
            } else {
                defines = defVars.get(iden);
                defines.add(node);
            }
        } else {
            defines = new ArrayList<>();
            defines.add(node);
        }
        defVars.put(iden, defines);
    }

    private boolean allSiblingBranchDefine(CtElementImpl node, String iden) {
        // TODO
        // get self role: THEN,ELSE,BODY,CASE
        Set<CtRole> special = new HashSet<>();
        special.add(CtRole.THEN);
        special.add(CtRole.ELSE);
        special.add(BODY);
        special.add(CASE);
        CtElementImpl cur = node;
        while (!special.contains(cur.getRoleInParent())) {
            cur = (CtElementImpl) cur.getParent();
        }
        switch (cur.getRoleInParent()) {
            case BODY:
                // for loop
                return true;
            case CASE:
                // check other case
                CtSwitchImpl swi = (CtSwitchImpl) cur.getParent();
                for (Object c : swi.getCases()) {
                    if (c!=cur && !checkChildDefine((CtElementImpl) c, iden, cur)) {
                        return false;
                    }
                }
            case THEN:
            case ELSE:
                // check then or else
                return checkChildDefine((CtElementImpl) cur.getParent(), iden, cur);
        }
        return true;
    }

    /**
     * check other siblings in parent except target to see if has definition of iden
     */
    private boolean checkChildDefine(CtElementImpl parent, String iden, CtElementImpl target) {
        for (Object ch : parent.getDirectChildren()) {
            if (ch!=target) {
                CtElementImpl def = getDefinedVar((CtElementImpl) ch);
                if (def!=null && def.toString().equals(iden)) {
                    return true;
                } else {
                    return checkChildDefine((CtElementImpl) ch, iden, target);
                }
            }
        }
        return false;
    }

    public void addUse(String iden, CtElementImpl node) {
        if(iden == null || node == null)
            return;
        if (defVars.containsKey(iden)) {
            for (CtElementImpl def : defVars.get(iden)) {
                new DefUseEdge(getDefinedVar(def), node);
            }
        }
        usedVars.put(iden, node);
    }

    public static CtElementImpl getDefinedVar(CtElementImpl node) {
        if (node instanceof CtAssignmentImpl) {
            return (CtElementImpl) ((CtAssignmentImpl) node).getAssigned();
        } else if (node instanceof CtUnaryOperatorImpl) {
            if (((CtUnaryOperatorImpl) node).getKind() == UnaryOperatorKind.POSTDEC || ((CtUnaryOperatorImpl) node).getKind() == UnaryOperatorKind.POSTINC
                    || ((CtUnaryOperatorImpl) node).getKind() == UnaryOperatorKind.PREDEC || ((CtUnaryOperatorImpl) node).getKind() == UnaryOperatorKind.PREINC )
                return (CtElementImpl) ((CtUnaryOperatorImpl) node).getOperand();
        } else if (node instanceof CtVariableWriteImpl) {
            return (CtElementImpl) ((CtVariableWriteImpl) node).getVariable();
        } else if (node instanceof CtCatchVariableImpl) {
            return (CtElementImpl) ((CtCatchVariableImpl) node).getReference();
        } else if (node instanceof CtArrayWriteImpl) {
            return (CtElementImpl) ((CtArrayWriteImpl) node).getTarget();
        } else if (node instanceof CtFieldWriteImpl) {
            return (CtElementImpl) ((CtFieldWriteImpl) node).getVariable();
        }
        return null;
    }
}
