package builder;

import model.CodeGraph;
import model.graph.edge.ASTEdge;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.PatchNode;
import model.graph.node.actions.*;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.eclipse.jdt.core.dom.*;
import java.lang.reflect.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class PatternExtractor {
    int MAX_NODE_SIZE;
    static int MAX_EXTEND_LEVEL = 3;  // default is 3

    public static List<Pattern> extractPattern(CodeGraph refer, CodeGraph compared) {
        List<Pattern> patternList = new ArrayList<>();
        for (ActionNode ap1 : refer.getActions()) {
            PatternNode start = new PatternNode(ap1.getParent(), refer, loc(ap1.getParent()), getASTNodeType(ap1.getParent()));
            Pattern pattern = new Pattern(start);
            start.setPattern(pattern);
            pattern.getNodeMapping().put(ap1.getParent(), start);
            patternList.add(pattern);
            for (ActionNode ap2 : compared.getActions()) {
                if (similar(ap1, ap2)) {  // TODO: design algorithm to check whether these two actions can be group together
                    pattern.getStart().addInstance(ap2.getParent(), compared);
                    // extending process
                    extendPattern(ap1.getParent(), refer, start, pattern, 1, new HashSet<>());
                    extendPattern(ap2.getParent(), compared, start, pattern, 1, new HashSet<>());
                }
            }
        }
        return patternList;
    }

    private static String getASTNodeType(Node n) {
        ASTNode an = n.getASTNode();
        if (an != null) {
            return ASTNode.nodeClassForType(an.getNodeType()).getSimpleName();
        }
        return "";
    }

    public static void extendPattern(Node aNode, CodeGraph aGraph, PatternNode extendPoint, Pattern pattern, int extendLevel, Set<Edge> ignore) {
        if (extendLevel > MAX_EXTEND_LEVEL)
            return;
        // in edges of aNode
        for (Edge ai : aNode.inEdges) {
            if (ignore.contains(ai))
                continue;
            Node pre = ai.getSource();
            // same AST structure into one PatternNode
            if (ai instanceof ASTEdge) {
                List<PatternEdge> astPE = extendPoint.inEdges().stream().filter(p -> p.type == PatternEdge.EdgeType.AST).collect(Collectors.toList());
                if (astPE.size() > 1) {  // there must be only on in-AST edge
                    System.out.println("[WARN]there are more than one AST edge for pattern");
                } else if (astPE.size() == 1) {  // same type PatternNode already exists
                    PatternNode astPN = astPE.get(0).getSource();
                    astPN.addInstance(pre, aGraph);
                    // extend recursively
                    ignore.add(ai);
                    extendPattern(pre, aGraph, astPN, pattern, ++extendLevel, ignore);
                } else {  // create new PatterNode
                    PatternNode extendPN = new PatternNode(pre, aGraph, loc(pre), getASTNodeType(pre));
                    extendPN.setPattern(pattern);
                    pattern.addNode(extendPN, pre);
                    pattern.addEdge(extendPN, extendPoint, PatternEdge.EdgeType.AST);
                    // extend recursively
                    ignore.add(ai);
                    extendPattern(pre, aGraph, extendPN, pattern, ++extendLevel, ignore);
                }
            }
        }
        // out edges of aNode
        for (Edge ao : aNode.outEdges) {
            if (ignore.contains(ao))
                continue;
            Node post = ao.getTarget();
            // same AST structure into one PatternNode
            if (ao instanceof ASTEdge) {
                List<PatternEdge> astPE = extendPoint.outEdges().stream().filter(p -> p.type == PatternEdge.EdgeType.AST).collect(Collectors.toList());
                List<PatternEdge> exist = astPE.stream().filter(p -> loc(post).equals(p.getTarget().getLocationInParent())).collect(Collectors.toList());
                if (!exist.isEmpty()) {  // same type PatternNode already exists
                    if (exist.size() == 1) {
                        PatternNode pn = exist.get(0).getTarget();  // same type PatternNode already exists
                        pn.addInstance(post, aGraph);
                        // extend recursively
                        ignore.add(ao);
                        extendPattern(post, aGraph, pn, pattern, ++extendLevel, ignore);
                    } else {
                        System.out.println("[WARN]more than one same location pattern node exist");
                    }
                } else {  // create new PatterNode
                    PatternNode extendPN = new PatternNode(post, aGraph, loc(post), getASTNodeType(post));
                    extendPN.setPattern(pattern);
                    pattern.addNode(extendPN, post);
                    pattern.addEdge(extendPoint, extendPN, PatternEdge.EdgeType.AST);
                    // extend recursively
                    ignore.add(ao);
                    extendPattern(post, aGraph, extendPN, pattern, ++extendLevel, ignore);
                }
            }
        }
        // add edges other than AST
        List<Edge> otherIn = aNode.inEdges.stream().filter(p -> !(p instanceof ASTEdge)).collect(Collectors.toList());
        List<Edge> otherOut = aNode.outEdges.stream().filter(p -> !(p instanceof ASTEdge)).collect(Collectors.toList());
        for (Edge oi : otherIn) {
            if (ignore.contains(oi))
                continue;
            Node pre = oi.getSource();
            if (pattern.getNodeMapping().containsKey(pre)) {  // node already in the pattern
                if (!pattern.hasEdge(pattern.getNodeMapping().get(pre), extendPoint, PatternEdge.getEdgeType(oi.type)))
                    pattern.addEdge(pattern.getNodeMapping().get(pre), extendPoint, PatternEdge.getEdgeType(oi.type));
            } else {
                // if not mapped in current pattern, skip
            }
        }
        for (Edge oo : otherOut) {
            if (ignore.contains(oo))
                continue;
            Node post = oo.getTarget();
            if (pattern.getNodeMapping().containsKey(post)) {  // node already in the pattern
                if (!pattern.hasEdge(extendPoint, pattern.getNodeMapping().get(post), PatternEdge.getEdgeType(oo.type)))
                    pattern.addEdge(extendPoint, pattern.getNodeMapping().get(post), PatternEdge.getEdgeType(oo.type));
            } else {
                // if not mapped in current pattern, skip
            }
        }
    }

    /**
     * return the node type and the relation with its parent
     */
    public static String loc(Node n) {
        ASTNode an = n.getASTNode();
        if (an != null) {
            String aType =  ASTNode.nodeClassForType(an.getNodeType()).getSimpleName();
            StructuralPropertyDescriptor rel = an.getLocationInParent();
            String propertyId = rel.getId();
            ASTNode aParent = an.getParent();
            if (propertyId.equals("arguments")) {
                int index = -1;
                if (aParent instanceof MethodInvocation) {
                    index = ((MethodInvocation) aParent).arguments().indexOf(an);
                } else if (aParent instanceof ConstructorInvocation) {
                    index = ((ConstructorInvocation) aParent).arguments().indexOf(an);
                } else if (aParent instanceof SuperMethodInvocation) {
                    index = ((SuperMethodInvocation) aParent).arguments().indexOf(an);
                } else if (aParent instanceof SuperConstructorInvocation) {
                    index = ((SuperConstructorInvocation) aParent).arguments().indexOf(an);
                } else {
                    System.out.println("[WARN]aParent type of arguments not handled in loc(Node n)");
                }
                propertyId = "argument " + index;
            } else if (propertyId.equals("statements")) {
                int index = -1;
                if (aParent instanceof Block) {
                    index = ((Block) aParent).statements().indexOf(an);
                } else {
                    System.out.println("[WARN]aParent type of statements not handled in loc(Node n)");
                }
                propertyId = "statement " + index;
            }
            String parentType = ASTNode.nodeClassForType(aParent.getNodeType()).getSimpleName();
            return aType + " in " + parentType + " as " + propertyId;
        }
        return "";
    }

    public static boolean similar(ActionNode an1, ActionNode an2) {
        // action type
        if (an1.getType() == an2.getType()) {
            switch (an1.getType()) {
                case DELETE:
                    Node del1 = ((Delete) an1).getDelete();
                    Node del2 = ((Delete) an2).getDelete();
                    return sameType(del1, del2);
                case MOVE:
                    Node movefrom1 = ((Move) an1).getMove();
                    Node movefrom2 = ((Move) an2).getMove();
                    Node moveto1 = an1.getParent();
                    Node moveto2 = an2.getParent();
                    return sameType(movefrom1, movefrom2) && sameType(moveto1, moveto2);
                case INSERT:
                    Node ins1 = ((Insert) an1).getInsert();
                    Node ins2 = ((Insert) an2).getInsert();
                    Node insto1 = an1.getParent();
                    Node insto2 = an2.getParent();
                    return sameType(ins1, ins2) && sameType(insto1, insto2);
                case UPDATE:
                    Node updBef1 = ((Update) an1).getBefore();
                    Node updBef2 = ((Update) an2).getBefore();
                    Node updAft1 = ((Update) an1).getAfter();
                    Node updAft2 = ((Update) an2).getAfter();
                    return sameType(updBef1, updBef2) && sameType(updAft1, updAft2);
            }
        }
        return false;
    }

    public static boolean sameType(Node nodeA, Node nodeB) {
        if (nodeA instanceof PatchNode && nodeB instanceof PatchNode) {
            String aType = ((PatchNode) nodeA).getSpoonNode().getClass().getTypeName();
            String bType = ((PatchNode) nodeB).getSpoonNode().getClass().getTypeName();
            if (aType.equals(bType)) {
                return true;
            } else {
                // same ancestor type
                Type aSupType = ((PatchNode) nodeA).getSpoonNode().getClass().getAnnotatedSuperclass().getType();
                Type bSupType = ((PatchNode) nodeB).getSpoonNode().getClass().getAnnotatedSuperclass().getType();
                if (aSupType == bSupType) {
                    return true;
                } else {
                    // special case
                    String aStr = ((PatchNode) nodeA).getSpoonNode().toString();
                    String bStr = ((PatchNode) nodeB).getSpoonNode().toString();
                    if ((aStr.equals("==") && bStr.equals("!="))||(aStr.equals("!=") && bStr.equals("==")))
                        return true;
                    return false;
                }
            }
        }
        if (nodeA.getASTNode() == null || nodeB.getASTNode() == null) {
            System.out.println("[WARN]no attached ASTNode");
            return false;
        }
        int aType = nodeA.getASTNode().getNodeType();
        int bType = nodeB.getASTNode().getNodeType();
        if (aType == bType) {
            return true;
        } else {
            // same ancestor type
            Type aSupType = (Type) nodeA.getASTNode().getClass().getAnnotatedSuperclass().getType();
            Type bSupType = (Type) nodeB.getASTNode().getClass().getAnnotatedSuperclass().getType();
            if (aSupType == bSupType) {
                return true;
            } else {
                // special case
                String aStr = nodeA.getASTNode().toString();
                String bStr = nodeB.getASTNode().toString();
                if ((aStr.equals("==") && bStr.equals("!="))||(aStr.equals("!=") && bStr.equals("==")))
                    return true;
                return false;
            }
        }
    }

    public static List<Pattern> combineGraphs(List<CodeGraph> cgs) {
        List<Pattern> patternList = new ArrayList<>();
        Set<ActionNode> traversedSet = new LinkedHashSet<>();
        for (CodeGraph cg : cgs) {
            for (ActionNode ap1 : cg.getActions()) {
                if (traversedSet.contains(ap1)) continue;
                PatternNode start = new PatternNode(ap1.getParent(), cg, loc(ap1.getParent()), getASTNodeType(ap1.getParent()));
                Pattern pattern = new Pattern(start);
                start.setPattern(pattern);
                patternList.add(pattern);
                traversedSet.add(ap1);
                for (CodeGraph compared : cgs) {
                    if (compared == cg) continue;
                    for (ActionNode ap2 : compared.getActions()) {
                        if (similar(ap1, ap2)) {
                            traversedSet.add(ap2);
                            pattern.getStart().addInstance(ap2.getParent(), compared);
                            // extending process
                            extendPattern(ap1.getParent(), cg, start, pattern, 1, new HashSet<>());
                            extendPattern(ap2.getParent(), compared, start, pattern, 1, new HashSet<>());
                        }
                    }
                }
            }
        }
        return patternList;
    }
}
