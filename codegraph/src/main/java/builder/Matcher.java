package builder;

import gumtree.spoon.diff.operations.*;
import model.CodeGraph;
import model.graph.node.Node;
import model.graph.node.actions.*;
import model.graph.node.stmt.StmtNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import pattern.Pair;
import spoon.reflect.declaration.CtElement;
import utils.JavaASTUtil;
import visitor.MethodDeclCollector;

import java.util.*;

public class Matcher {

    public static Map<CtElement, Node> mapSpoonToCodeGraph(List<Node> cgNodes, List<CtElement> spoonNodes) {
        Map<CtElement, Node> mappings = new LinkedHashMap<>();
        for (CtElement cte : spoonNodes) {
            int ctLine = cte.getPosition().getLine();
            int ctLength = cte.getPosition().getSourceEnd() - cte.getPosition().getSourceStart() + 1;
            for (Node cge : cgNodes) {
                if (cge != null) {
                    int cgLine = cge.getStartSourceLine();
                    int cgLength = cge.getASTNode().getLength();
                    if (ctLine == cgLine && ctLength == cgLength) {
                        mappings.put(cte, cge);
                        break;
                    }
                }
            }
        }
        return mappings;
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

    public static Map<Node, Node> mapSrcToDst(Map<CtElement, Node> src_matcher, Map<CtElement, Node> dst_matcher, Map<CtElement, CtElement> mappings) {
        Map<Node, Node> src_to_dst = new LinkedHashMap<>();
        for (Map.Entry<CtElement, CtElement> entry : mappings.entrySet()) {
            CtElement ctSrc = entry.getKey();
            CtElement ctDst = entry.getValue();
            if (src_matcher.containsKey(ctSrc) && dst_matcher.containsKey(ctDst)) {
                src_to_dst.put(src_matcher.get(ctSrc), dst_matcher.get(ctDst));
            }
        }
        return src_to_dst;
    }

    public static List<Node> mapOperationToCodeGraph(InsertOperation operation, CodeGraph srcGraph, Map<CtElement, Node> src_matcher) {
        List<Node> changedNodes = new ArrayList<>();
        CtElement ctParent = operation.getParent();
        CtElement ctInsert = operation.getNode();
        if (src_matcher.containsKey(ctParent)) {
            Node cgParent = src_matcher.get(ctParent);
            Node insertNode = rebuild(ctInsert);
            Insert insertAction = new Insert(cgParent, insertNode);
            changedNodes.add(insertAction);
            src_matcher.putAll(mapStructure(ctInsert, insertNode)); // also need to map their children and so on
        } else {
            System.err.println("No mapped parent " + ctParent.getClass() + " : " + ctParent.getPosition().getLine());
        }
        return changedNodes;
    }

    public static List<Node> mapOperationToCodeGraph(MoveOperation operation, CodeGraph srcGraph, Map<CtElement, Node> src_matcher) {
        List<Node> changedNodes = new ArrayList<>();
        CtElement ctParent = operation.getParent();
        if (src_matcher.containsKey(ctParent)) {
            CtElement ctMove = operation.getNode();
            if (src_matcher.containsKey(ctMove)) {
                Node cgParent = src_matcher.get(ctParent);
                Node moveNode = src_matcher.get(ctMove);
                Move moveAction = new Move(cgParent, moveNode);
                changedNodes.add(moveAction);
            } else {
                System.err.println("No mapped move node " + ctMove.getClass() + " : " + ctMove.getPosition().getLine());
            }
        } else {
            System.err.println("No mapped parent " + ctParent.getClass() + " : " + ctParent.getPosition().getLine());
        }
        return changedNodes;
    }

    public static List<Node> mapOperationToCodeGraph(DeleteOperation operation, CodeGraph srcGraph, Map<CtElement, Node> src_matcher) {
        List<Node> changedNodes = new ArrayList<>();
        CtElement ctDelete = operation.getNode();
        if (src_matcher.containsKey(ctDelete)) {
            Node delNode = src_matcher.get(ctDelete);
            Delete delAction = new Delete(null, delNode);
            changedNodes.add(delAction);
        } else {
            System.err.println("No mapped delete node " + ctDelete.getClass() + " : " + ctDelete.getPosition().getLine());
        }
        return changedNodes;
    }

    public static List<Node> mapOperationToCodeGraph(UpdateOperation operation, CodeGraph srcGraph, Map<CtElement, Node> src_matcher) {
        List<Node> changedNodes = new ArrayList<>();
        CtElement ctBefore = operation.getSrcNode();
        CtElement ctAfter = operation.getDstNode();
        if (src_matcher.containsKey(ctBefore)) {
            Node cgBefore = src_matcher.get(ctBefore);
            Node cgAfter = rebuild(ctAfter);
            Update updateAction = new Update(cgBefore, cgAfter);
            changedNodes.add(updateAction);
            src_matcher.putAll(mapStructure(ctAfter, cgAfter));
        } else {
            System.err.println("No mapped parent " + ctBefore.getClass() + " : " + ctBefore.getPosition().getLine());
        }
        return changedNodes;
    }

    private static Map<CtElement, Node> mapStructure(CtElement ctNode, Node cgNode) {
        Map<CtElement, Node> newMappings = new LinkedHashMap<>();
        newMappings.put(ctNode, cgNode);
        // TODO: recursion for their children
        return newMappings;
    }

    private static Node rebuild(CtElement destElement) {
        // TODO: consider for different types
        return null;
    }
}
