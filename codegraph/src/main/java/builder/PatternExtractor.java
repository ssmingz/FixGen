package builder;

import model.CodeGraph;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.PatchNode;
import model.graph.node.actions.*;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.eclipse.jdt.core.dom.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class PatternExtractor {
    int MAX_NODE_SIZE;
    static int MAX_EXTEND_LEVEL = 3;  // default is 3
    static Map<Node, PatternNode>  _nodeMap = new LinkedHashMap<>();

    public static List<Pattern> extractPattern(CodeGraph refer, CodeGraph compared) {
        List<Pattern> patternList = new ArrayList<>();
        for (ActionNode ap1 : refer.getActions()) {
            PatternNode start = new PatternNode(ap1.getParent(), refer, getASTNodeType(ap1.getParent()));
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

    /**
     * return the node according to the nodeLabel around root node, return null if not exist
     */
    public static PatternNode findNeighbor(PatternNode root, PatternEdge.EdgeType edgeType, String nodeLabel, String direction) {
        List<PatternEdge> edges;
        if (direction.equals("in")) {
            edges = root.inEdges().stream().filter(p -> p.type == edgeType).collect(Collectors.toList());
            edges = edges.stream().filter(s -> s.getSource().getASTType().equals(nodeLabel)).collect(Collectors.toList());
            if (edges.size() == 1)
                return edges.get(0).getSource();
        } else if (direction.equals("out")) {
            edges = root.outEdges().stream().filter(p -> p.type == edgeType).collect(Collectors.toList());
            edges = edges.stream().filter(s -> s.getTarget().getASTType().equals(nodeLabel)).collect(Collectors.toList());
            if (edges.size() == 1)
                return edges.get(0).getTarget();
        } else {
            System.out.println("[ERROR]more than one matched patternNode");
        }
        return null;
    }

    public static void extendPattern(Node aNode, CodeGraph aGraph, PatternNode extendPoint, Pattern pattern, int extendLevel, Set<Edge> ignore) {
        if (extendLevel > MAX_EXTEND_LEVEL)
            return;
        Set<PatternNode> candidates = new LinkedHashSet<>();
        // in edges of aNode
        for (Edge ai : aNode.inEdges) {
            if (ignore.contains(ai))
                continue;
            Node pre = ai.getSource();
            // TODO: check node type or location in parent
            String nodeLabel = getASTNodeType(pre);
            PatternNode prePN = findNode(pre, aGraph);
            if (prePN != null) {  // by early traversing, node exists but edge not
                pattern.addEdge(prePN, extendPoint, PatternEdge.getEdgeType(ai.type), ai, aGraph);
            } else {
                prePN = findNeighbor(extendPoint, PatternEdge.getEdgeType(ai.type), nodeLabel, "in");
                if (prePN != null) {  // same type already exists
                    prePN.addInstance(pre, aGraph);
                    pattern.addEdge(prePN, extendPoint, PatternEdge.getEdgeType(ai.type), ai, aGraph);
                    _nodeMap.put(pre, prePN);
                    ignore.add(ai);
                    candidates.add(prePN);
                } else {
                    PatternNode extendPN = new PatternNode(pre, aGraph, getASTNodeType(pre));
                    _nodeMap.put(pre, extendPN);
                    extendPN.setPattern(pattern);
                    pattern.addNode(extendPN, pre);
                    pattern.addEdge(extendPN, extendPoint, PatternEdge.getEdgeType(ai.type), ai, aGraph);
                    ignore.add(ai);
                    candidates.add(extendPN);
                }
            }
        }
        // out edges of aNode
        for (Edge ao : aNode.outEdges) {
            if (ignore.contains(ao))
                continue;
            Node post = ao.getTarget();
            // TODO: check node type or location in parent
            String nodeLabel = getASTNodeType(post);
            PatternNode postPN = findNode(post, aGraph);
            if (postPN != null) {  // by early traversing, node exists but edge not
                pattern.addEdge(extendPoint, postPN, PatternEdge.getEdgeType(ao.type), ao, aGraph);
            } else {
                postPN = findNeighbor(extendPoint, PatternEdge.getEdgeType(ao.type), nodeLabel, "out");
                if (postPN != null) {  // same type already exists
                    postPN.addInstance(post, aGraph);
                    pattern.addEdge(extendPoint, postPN, PatternEdge.getEdgeType(ao.type), ao, aGraph);
                    _nodeMap.put(post, postPN);
                    ignore.add(ao);
                    candidates.add(postPN);
                } else {
                    PatternNode extendPN = new PatternNode(post, aGraph, getASTNodeType(post));
                    _nodeMap.put(post, extendPN);
                    extendPN.setPattern(pattern);
                    pattern.addNode(extendPN, post);
                    pattern.addEdge(extendPoint, extendPN, PatternEdge.getEdgeType(ao.type), ao, aGraph);
                    ignore.add(ao);
                    candidates.add(extendPN);
                }
            }
        }
        // extend recursively
        for (PatternNode c : candidates) {
            for (Node n : _nodeMap.keySet()) {
                if (_nodeMap.get(n).equals(c) && _nodeMap.get(n).getInstance().get(n).getGraphName().equals(aGraph.getGraphName())) {
                    extendPattern(n, aGraph, c, pattern,extendLevel+1, ignore);
                    break;
                }
            }
        }
    }

    private static PatternNode findNode(Node post, CodeGraph cg) {
        if (_nodeMap.containsKey(post))
            return _nodeMap.get(post);
        else {
            for (Map.Entry<Node, PatternNode> entry : _nodeMap.entrySet()) {
                for (Node ins : entry.getValue().getInstance().keySet()) {
                    String label1 = entry.getValue().getInstance().get(ins) + "#" + entry.getKey().getStartSourceLine() + ":" + entry.getKey().toLabelString();
                    String label2 = cg.getGraphName() + "#" + post.getStartSourceLine() + ":" + post.toLabelString();
                    if (label1.equals(label2))
                        return entry.getValue();
                }
            }
        }
        return null;
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
        Map<ActionNode, Pattern> traversedMap = new LinkedHashMap<>();
        for (CodeGraph cg : cgs) {
            for (ActionNode ap : cg.getActions()) {
                if (_nodeMap.containsKey(ap))
                    continue;
                boolean find = false;
                for (Map.Entry<ActionNode, Pattern> entry : traversedMap.entrySet()) {
                    if (similar(ap, entry.getKey())) {
                        traversedMap.put(ap, entry.getValue());
                        entry.getValue().getStart().addInstance(ap.getParent(), cg);
                        _nodeMap.put(ap.getParent(), entry.getValue().getStart());
                        extendPattern(ap.getParent(), cg, entry.getValue().getStart(), entry.getValue(), 1, new HashSet<>());
                        find = true;
                        break;
                    }
                }
                if (!find) {
                    PatternNode start = new PatternNode(ap.getParent(), cg, getASTNodeType(ap.getParent()));
                    Pattern pattern = new Pattern(start);
                    start.setPattern(pattern);
                    patternList.add(pattern);
                    traversedMap.put(ap, pattern);
                    _nodeMap.put(ap.getParent(), start);
                    extendPattern(ap.getParent(), cg, start, pattern, 1, new HashSet<>());
                }
            }
        }
        return patternList;
    }
}
