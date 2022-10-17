package model;

import model.graph.node.expr.SimpName;
import org.eclipse.jdt.core.dom.*;
import utils.JavaASTUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class GraphBuildingContext {
    public static HashMap<String, HashMap<String, String>> typeFieldType = new HashMap<>();
    public static HashMap<String, HashMap<String, HashSet<String>>> typeMethodExceptions = new HashMap<>();
    public static HashMap<String, HashSet<String>> exceptionHierarchy = new HashMap<>();
    private Stack<HashMap<String, String>> localVariables = new Stack<>(), localVariableTypes = new Stack<>();
    private HashMap<String, SimpName> fieldTypes = new HashMap<>();

    public String[] getLocalVariableInfo(String identifier) {
        for (int i = localVariables.size() - 1; i >= 0; i--) {
            HashMap<String, String> variables = this.localVariables.get(i);
            if (variables.containsKey(identifier))
                return new String[]{variables.get(identifier), this.localVariableTypes.get(i).get(identifier)};
        }
        return null;
    }

    private ArrayList<String> getQualifiedTypes(String stype, CompilationUnit cu) {
        ArrayList<String> qts = new ArrayList<>();
        int index = stype.indexOf('.');
        String qual = null;
        if (index > -1) {
            if (Character.isLowerCase(stype.charAt(0))) {
                qts.add(stype);
                return qts;
            }
            qual = stype.substring(0, index);
        }
        for (int i = 0; i < cu.imports().size(); i++) {
            ImportDeclaration id = (ImportDeclaration) cu.imports().get(i);
            if (id.isOnDemand()) {
                qts.add(id.getName().getFullyQualifiedName() + "." + stype);
            } else if (!id.isStatic()) {
                String qn = id.getName().getFullyQualifiedName();
                if (qn.endsWith("." + stype)) {
                    qts.add(qn);
                    return qts;
                }
                if (qual != null && qn.endsWith("." + qual)) {
                    qts.add(qn + stype.substring(qual.length()));
                    return qts;
                }
            }
        }
        String pkg = "";
        if (cu.getPackage() != null)
            pkg = cu.getPackage().getName().getFullyQualifiedName();
        qts.add(0, pkg + "." + stype);
        return qts;
    }
}
