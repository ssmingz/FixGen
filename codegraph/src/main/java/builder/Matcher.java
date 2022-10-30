package builder;

import model.CodeGraph;
import model.graph.node.Node;
import model.graph.node.stmt.StmtNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import pattern.Pair;
import utils.JavaASTUtil;
import visitor.MethodDeclCollector;

import java.util.*;

public class Matcher {
    public static List<Pair<MethodDeclaration, MethodDeclaration>> match(CompilationUnit bug, CompilationUnit fix) {
        List<Pair<MethodDeclaration, MethodDeclaration>> pairs = new LinkedList<>();

        MethodDeclCollector methodDeclCollector = new MethodDeclCollector();
        methodDeclCollector.init();
        bug.accept(methodDeclCollector);
        List<MethodDeclaration> bugMethods = methodDeclCollector.getAllMethodDecl();
        methodDeclCollector.init();
        fix.accept(methodDeclCollector);
        List<MethodDeclaration> fixMethods = methodDeclCollector.getAllMethodDecl();

        for (MethodDeclaration bugm : bugMethods) {
            boolean notMatch = true;
            for (int i=0; i< fixMethods.size(); i++) {
                MethodDeclaration fixm = fixMethods.get(i);
                DiffType diff = compareSignature(bugm, fixm);
                switch (diff) {
                    case SAME:
                        pairs.add(new Pair<>(bugm, fixm));
                        fixMethods.remove(fixm);
                        notMatch = false;
                        break;
                    default:
                }
            }
            if (notMatch) {
                System.out.println("No match for method declaration : " + JavaASTUtil.buildSignature(bugm));
                return pairs;
            }
        }
        return pairs;
    }

    @SuppressWarnings("unchecked")
    private static DiffType compareSignature(MethodDeclaration sm, MethodDeclaration tm) {
        // modifier
        int smdf = sm.getModifiers();
        int tmdf = tm.getModifiers();
        if ((smdf & tmdf) != smdf)
            return DiffType.DIFF_MODIFIER;
        // name
        if (!sm.getName().getFullyQualifiedName().equals(tm.getName().getFullyQualifiedName()))
            return DiffType.DIFF_NAME;
        // return type
        String sType = sm.getReturnType2() == null ? "?" : sm.getReturnType2().toString();
        String tType = tm.getReturnType2() == null ? "?" : tm.getReturnType2().toString();
        if (!sType.equals(tType))
            return DiffType.DIFF_RETURN;
        // parameters
        List<SingleVariableDeclaration> sp = sm.parameters();
        List<SingleVariableDeclaration> tp = tm.parameters();
        if (sp.size() != tp.size())
            return DiffType.DIFF_PARAM;
        for (int i = 0; i < sp.size(); i++) {
            if (!sp.get(i).getType().toString().equals(tp.get(i).getType().toString())) {
                return DiffType.DIFF_PARAM;
            }
        }
        // otherwise, same
        return DiffType.SAME;
    }

    public static boolean match(CodeGraph bugGraph, CodeGraph fixGraph) {
        List<StmtNode> bugStmts = bugGraph.getAllStmtNodes();
        List<StmtNode> fixStmts = fixGraph.getAllStmtNodes();

        List<StmtNode> bugNotMatched = new ArrayList<>();
        List<StmtNode> fixNotMatched = new ArrayList<>();
        Set<Integer> fixMatched = new HashSet<>();
        for (int i=0; i< bugStmts.size(); i++) {
            boolean notMatched = true;
            if (anyAncestorMatch(bugStmts.get(i))) {
                notMatched = false;
            } else {
                for (int j=0; j< fixStmts.size(); j++) {
                    if (!fixMatched.contains(j) && fixStmts.get(j).getBindingNode() == null
                            && bugStmts.get(i).compare(fixStmts.get(j))) {
                        bugStmts.get(i).setBindingNode(fixStmts.get(j));
                        fixMatched.add(j);
                        notMatched = false;
                        break;
                    }
                }
            }
            if (notMatched) {
                bugNotMatched.add(bugStmts.get(i));
            }
        }
        for (int i=0; i< fixStmts.size(); i++) {
            if (fixStmts.get(i).getBindingNode() == null) {
                fixNotMatched.add(fixStmts.get(i));
            }
        }

        if (bugNotMatched.isEmpty() && fixNotMatched.isEmpty()) {
            return false;
        }

        // TODO: match sub-expressions
        System.out.println("stop");

        return true;
    }

    private static boolean anyAncestorMatch(Node node) {
        while (node.getParent() != null) {
            node = node.getParent();
            if (node.getBindingNode() != null) {
                return true;
            }
        }
        return false;
    }

    enum DiffType {
        DIFF_MODIFIER("different modifiers"),
        DIFF_NAME("different names"),
        DIFF_RETURN("different return types"),
        DIFF_PARAM("different parameters"),
        SAME("same");

        private String message;

        DiffType(String msg) {
            message = msg;
        }

        public String toString() {
            return message;
        }
    }
}
