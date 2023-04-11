package builder;

import com.github.gumtreediff.tree.Tree;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import model.CodeGraph;
import model.graph.node.Node;
import model.graph.node.bodyDecl.MethodDecl;
import model.graph.node.stmt.BlockStmt;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.reference.CtExecutableReferenceImpl;
import utils.CtObject;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class Matcher {

    public static boolean equals(CtElement ct1, CtElement ct2) {
        if (ct1 == null || ct2 == null)
            return false;
        if (!ct1.getClass().getSimpleName().equals(ct2.getClass().getSimpleName()))
            return false;
        if (ct1.getMetadataKeys().size() != ct2.getMetadataKeys().size())
            return false;
        if (ct1.getMetadata("isMoved") != null && !ct1.getMetadata("isMoved").equals(ct2.getMetadata("isMoved")))
            return false;
        if (ct2.getMetadata("isMoved") != null && !ct2.getMetadata("isMoved").equals(ct1.getMetadata("isMoved")))
            return false;
        if (ct2.hashCode() != ct1.hashCode())
            return false;
        return ct1.equals(ct2);
    }

    public static boolean equalsInSameSrc(CtElement ct1, CtElement ct2) {
        if (ct1 == null || ct2 == null)
            return false;
        if (!ct1.toString().equals(ct2.toString()))
            return false;
        if (!ct1.getClass().getSimpleName().equals(ct2.getClass().getSimpleName()))
            return false;
        if (ct1.getPosition().isValidPosition() && ct2.getPosition().isValidPosition() && ct1.getPosition().getSourceStart() != ct2.getPosition().getSourceStart())
            return false;
        if (ct1.getPosition().isValidPosition() && ct2.getPosition().isValidPosition() && ct1.getPosition().getSourceEnd() != ct2.getPosition().getSourceEnd())
            return false;
        if (ct1.getMetadataKeys().size() != ct2.getMetadataKeys().size())
            return false;
        if (ct1.getMetadata("isMoved") != null && !ct1.getMetadata("isMoved").equals(ct2.getMetadata("isMoved")))
            return false;
        if (ct2.getMetadata("isMoved") != null && !ct2.getMetadata("isMoved").equals(ct1.getMetadata("isMoved")))
            return false;
        if (ct2.hashCode() != ct1.hashCode())
            return false;
        return ct1.equals(ct2);
    }

    public static BiMap<Node, CtObject> mapCodeGraphAndSpoon(CodeGraph codeGraph, Tree tree) {
        BiMap<Node, CtObject> result = HashBiMap.create();
        // get all in-method CtElement
        List<CtElement> spoonNodes = getMethodCtElements(tree, codeGraph.getStartLine(), codeGraph.getEndLine());
        // map
        List<Node> cgNodes = codeGraph.getNodes();
        for (Node cge : cgNodes) {
            if (cge != null && cge.getASTNode() != null) {
                int cgLine = cge.getStartSourceLine();
                int cgStartPos = cge.getASTNode().getStartPosition();
                int cgLength = cge.getASTNode().getLength();
                for (CtElement cte : spoonNodes) {
                    int ctLine = cte.getPosition().getLine();
                    int ctStartPos = cte.getPosition().getSourceStart();
                    int ctLength = cte.getPosition().getSourceEnd() - cte.getPosition().getSourceStart() + 1;
                    if (cte instanceof CtExecutableReferenceImpl && ctLine == cgLine && cge.getASTNode().toString().equals(((CtExecutableReferenceImpl) cte).getSimpleName())) {
                        result.forcePut(cge, new CtObject(cte, cge.getASTNode().getLocationInParent().getId()));  // TODO: try not to use forcePut()
                        break;
                    } else if (ctLine == cgLine && ctStartPos == cgStartPos && ctLength == cgLength) {  // TODO: accurate lineNo matched and gumtree-spoon/CodeGraph nodeType matched
                        if (cte instanceof CtBlockImpl && !(cge instanceof BlockStmt)) {}
                        else if (result.containsValue(new CtObject(cte, cge.getASTNode().getLocationInParent().getId()))) {}  // to handle InfixExpression
                        else {
                            result.put(cge, new CtObject(cte, cge.getASTNode().getLocationInParent().getId()));
                            break;
                        }
                    } else if (ctLine == cgLine && ctStartPos == cgStartPos && cge.getASTNode().toString().equals(cte.prettyprint())) {
                        result.put(cge, new CtObject(cte, cge.getASTNode().getLocationInParent().getId()));
                        break;
                    } else if (cte instanceof CtMethodImpl && cge instanceof MethodDecl && ctLine == cgLine) {
                        // handle special case like same lineNo but different length
                        result.put(cge, new CtObject(cte, cge.getASTNode().getLocationInParent().getId()));
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * get all CtElement inside a method declaration according to line number
     */
    private static List<CtElement> getMethodCtElements(Tree tree, int startLine, int endLine) {
        List<CtElement> results = new ArrayList<>();
        for (Tree curTree : tree.breadthFirst()) {
            if (curTree.getMetadata("spoon_object") != null) {
                CtElement curCtE = (CtElement) curTree.getMetadata("spoon_object");
                if (curCtE.getPosition().isValidPosition()) {
                    int spoon_start = curCtE.getPosition().getLine();
                    if (spoon_start >= startLine && spoon_start <= endLine) {
                        results.add(curCtE);
                        for (CtElement child : curCtE.getDirectChildren()) {
                            if (child instanceof CtExecutableReferenceImpl) {
                                child.setPosition(curCtE.getPosition());
                                results.add(child);
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    private static List<Field> getAllFields(Object obj) {
        Class clazz = obj.getClass();
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()).stream().filter(p -> !p.getName().equals("serialVersionUID")).collect(Collectors.toList()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
