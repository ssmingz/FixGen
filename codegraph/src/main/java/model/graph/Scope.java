package model.graph;

import model.graph.edge.DefUseEdge;
import model.graph.node.Node;
import model.graph.node.expr.AssignExpr;
import model.graph.node.expr.PostfixExpr;
import model.graph.node.expr.PrefixExpr;
import model.graph.node.expr.PrefixOpr;
import model.graph.node.varDecl.SingleVarDecl;
import model.graph.node.varDecl.VarDeclFrag;
import utils.JavaASTUtil;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {
    private Scope parent;
    private Map<String, Node> defVars = new LinkedHashMap<>();
    private Map<String, Node> usedVars = new LinkedHashMap<>();

    public Scope(Scope p) {
        parent = p;
    }

    public void addDefine(String iden, Node node) {
        if (iden == null || node == null)
            return;
        defVars.put(iden, node);
    }

    public Node getDefine(String iden) {
        if (iden == null)
            return null;
        Node node = defVars.get(iden);
        if (node == null && parent != null)
            return parent.getDefine(iden);
        return node;
    }

    public void addUse(String iden, Node node) {
        if(iden == null || node == null)
            return;
        if (defVars.containsKey(iden)) {
            new DefUseEdge(getDefinedVar(defVars.get(iden)), node);
        }
        usedVars.put(iden, node);
    }

    public Node getUse(String iden) {
        if (iden == null)
            return null;
        Node node = usedVars.get(iden);
        if (node == null && parent != null)
            return parent.getUse(iden);
        return node;
    }

    public static Node getDefinedVar(Node node) {
        if (node instanceof AssignExpr) {
            return ((AssignExpr) node).getLeftHandSide();
        } else if (node instanceof PostfixExpr) {
            return ((PostfixExpr) node).getExpression();
        } else if (node instanceof PrefixExpr) {
            return ((PrefixExpr) node).getExpression();
        } else if (node instanceof VarDeclFrag) {
            return ((VarDeclFrag) node).getName();
        } else if (node instanceof SingleVarDecl) {
            return ((SingleVarDecl) node).getName();
        } else {
            return null;
        }
    }
}
