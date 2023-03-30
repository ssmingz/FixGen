package builder;

import model.CodeGraph;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.PatchNode;
import model.graph.node.actions.*;
import model.graph.node.expr.ExprList;
import model.graph.node.expr.SimpName;
import model.pattern.Attribute;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.eclipse.jdt.core.dom.*;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class PatternExtractor {
    int MAX_NODE_SIZE;
    static int MAX_EXTEND_LEVEL = 3;  // default is 3
    static Map<Node, PatternNode>  _nodeMap = new LinkedHashMap<>();
    static Map<Map<Node, Node>, Double> _mappingsByScore = new LinkedHashMap<>();

    public static List<Pattern> extractPattern(CodeGraph refer, CodeGraph compared) {
        List<Pattern> patternList = new ArrayList<>();
        for (ActionNode ap1 : refer.getActions()) {
            PatternNode start = new PatternNode(ap1.getParent(), refer, getLocationInParent(ap1.getParent()), getNodeType(ap1.getParent()));
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

    private static String getNodeType(Node n) {
        String type = "";
        if (n.getASTNode() != null) {
            type = n.getASTNode().getClass().getSimpleName();
        } else if (n instanceof PatchNode) {
            type = ((PatchNode)n).getSpoonNode().getClass().getSimpleName();
        } else if (n instanceof ActionNode) {
            type = ((ActionNode) n).getType().name();
        } else {
            type = Attribute.getType(n);
            if (type.equals("")) {
                System.out.println("not attached AST node or spoon node: " + n.toLabelString());
            }
        }
        return type;
    }

    private static String getLocationInParent(Node n) {
        String loc = "";
        if (n.getASTNode() != null) {
            loc = loc(n.getASTNode());
        } else if (n instanceof PatchNode) {
            loc = ((PatchNode)n).getSpoonNode().getRoleInParent().getCamelCaseName();
        } else if (n instanceof ActionNode) {
            loc = "action";
        } else {
            loc = Attribute.getRoleInParent(n);
            if (!loc.equals("")) {
                if (loc.startsWith("_"))
                    loc = loc.substring(1);
            } else {
                System.out.println("not attached AST node or spoon node: " + n.toLabelString());
            }
        }
        return loc;
    }

    /**
     * return the node according to the nodeLabel around root node, return null if not exist
     */
    public static PatternNode findNeighbor(PatternNode root, PatternEdge.EdgeType edgeType, String nodeLabel, String direction) {
        List<PatternEdge> edges;
        if (direction.equals("in")) {
            edges = root.inEdges().stream().filter(p -> p.type == edgeType).collect(Collectors.toList());
            //edges = edges.stream().filter(s -> s.getSource().getType().equals(nodeLabel)).collect(Collectors.toList());
            edges = edges.stream().filter(s -> s.getSource().getLocationInParent().equals(nodeLabel)).collect(Collectors.toList());
            if (edges.size() == 1)
                return edges.get(0).getSource();
        } else if (direction.equals("out")) {
            edges = root.outEdges().stream().filter(p -> p.type == edgeType).collect(Collectors.toList());
            //edges = edges.stream().filter(s -> s.getTarget().getType().equals(nodeLabel)).collect(Collectors.toList());
            edges = edges.stream().filter(s -> s.getTarget().getLocationInParent().equals(nodeLabel)).collect(Collectors.toList());
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
            //String nodeLabel = getNodeType(pre);
            String nodeLabel = getLocationInParent(pre);
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
                    PatternNode extendPN = new PatternNode(pre, aGraph, getLocationInParent(pre), getNodeType(pre));
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
            if(post.getASTNode()!=null && post.getASTNode().getLocationInParent().getId().equals("elseStatement"))
                post.getASTNode();
            // TODO: check node type or location in parent
            //String nodeLabel = getNodeType(post);
            String nodeLabel = getLocationInParent(post);
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
                    PatternNode extendPN = new PatternNode(post, aGraph, getLocationInParent(post), getNodeType(post));
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
     * return the relation with its parent of an ASTNode
     */
    public static String loc(ASTNode an) {
        if (an != null) {
//            String aType =  ASTNode.nodeClassForType(an.getNodeType()).getSimpleName();
            StructuralPropertyDescriptor rel = an.getLocationInParent();
            String propertyId = rel.getId();
            ASTNode aParent = an.getParent();
            if (rel.isChildListProperty() && !propertyId.equals("statements")) {
                Object listObj =  getFieldValueByObject(aParent, propertyId);
                int index = -1;
                if (listObj instanceof List) {
                    index = ((List) listObj).indexOf(an);
                } else {
                    System.out.println("[WARN]couldn't find list property index in loc(Node n)");
                }
                propertyId += " " +  index;
            }
//            String parentType = ASTNode.nodeClassForType(aParent.getNodeType()).getSimpleName();
//            return aType + " in " + parentType + " as " + propertyId;
            return propertyId;
        }
        return "";
    }

    /**
     * get field value by given target object and field name
     */
    public static Object getFieldValueByObject(Object object, String targetFieldName) {
        Class<?> objClass = object.getClass();
        Object result = null;
        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            String currentFieldName = field.getName();
            try {
                if (currentFieldName.equals(targetFieldName)) {
                    field.setAccessible(true);
                    result = field.get(object);
                    return result;
                }
            } catch (SecurityException e) {
                // 安全性异常
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // 非法参数
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // 无访问权限
                e.printStackTrace();
            }
        }
        return result;
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
        if (!nodeA.getClass().getSimpleName().equals(nodeB.getClass().getSimpleName()))
            return false;
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
        } else if (nodeA instanceof ActionNode && nodeB instanceof ActionNode) {
            String aType = ((ActionNode) nodeA).getType().name();
            String bType = ((ActionNode) nodeB).getType().name();
            if (aType.equals(bType))
                return true;
        } else if (nodeA instanceof ExprList && nodeB instanceof ExprList) {
           if(((ExprList) nodeA).getExprs().size() != ((ExprList) nodeB).getExprs().size()) {
               return false;
           }
           for (int i=0; i<((ExprList) nodeA).getExprs().size(); i++) {
               Node expA = ((ExprList) nodeA).getExprs().get(i);
               Node expB = ((ExprList) nodeB).getExprs().get(i);
               if (!sameType(expA, expB)) {
                   return false;
               }
           }
           return true;
        } else if (nodeA.getASTNode() == null || nodeB.getASTNode() == null) {
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
        if (cgs == null || cgs.isEmpty()) {
            return patternList;
        }
        List<List<Node>> nodeLists = getActionLinks(cgs.get(0));
        // init patterns
        Map<List<Node>, Pattern> initPatterns = new LinkedHashMap<>();
        for (List<Node> l : nodeLists) {
            PatternNode start = new PatternNode(l.get(0), cgs.get(0));
            Pattern pat = new Pattern(start);
            pat.addNode(start, l.get(0));
            start.setPattern(pat);
            initPatterns.put(l, pat);
            for (int i=1; i<l.size(); i++) {
                Node n = l.get(i);
                PatternNode pn = new PatternNode(n, cgs.get(0));
                pn.setPattern(pat);
                pat.addNode(pn, n);
            }
            for (Node n : l) {
                for (Edge e : n.inEdges) {
                    PatternNode srcPN = pat.getNodeMapping().get(e.getSource());
                    PatternNode tarPN = pat.getNodeMapping().get(n);
                    if (srcPN != null && tarPN!=null && !pat.hasEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type))) {
                        pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, cgs.get(0));
                    }
                }
                for (Edge e : n.outEdges) {
                    PatternNode srcPN = pat.getNodeMapping().get(n);
                    PatternNode tarPN = pat.getNodeMapping().get(e.getTarget());
                    if (srcPN != null && tarPN!=null && !pat.hasEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type))) {
                        pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, cgs.get(0));
                    }
                }
            }
        }

        Map<Node, List<Node>> nodeGroups = new LinkedHashMap<>();
        for(int i=1; i<cgs.size(); i++) {
            CodeGraph aGraph = cgs.get(i);
            List<List<Node>> nodeListsComps = getActionLinks(aGraph);
            for (List<Node> nodeList: nodeLists) {
                for (List<Node> nodeListComp : nodeListsComps) {
                    _mappingsByScore.clear();
                    match(nodeList, 0, nodeListComp, 0, new LinkedHashMap<>());
                    // group by cgs.get(0)
                    if (_mappingsByScore.size() > 0) {
                        // sort
//                        List<Map.Entry<Map<Node, Node>, Double>> temp = new ArrayList<>(_mappingsByScore.entrySet());
//                        temp.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
                        double maxScore = 0.0;
                        Map<Node, Node> result = null;
                        for (Map.Entry<Map<Node, Node>, Double> entry : _mappingsByScore.entrySet()) {
                            if (entry.getValue() > maxScore) {
                                maxScore = entry.getValue();
                                result = entry.getKey();
                            }
                        }
                        // add to pattern
                        Pattern pat = initPatterns.get(nodeList);
                        for (Map.Entry<Node, Node> entry : result.entrySet()) {
                            Node oNode = entry.getKey();
                            Node cNode = entry.getValue();
                            if (nodeGroups.containsKey(oNode)) {
                                nodeGroups.get(oNode).add(cNode);
                            } else {
                                List<Node> tmp = new ArrayList<>();
                                tmp.add(cNode);
                                nodeGroups.put(oNode, tmp);
                            }
                            pat.getNodeMapping().get(oNode).addInstance(cNode, aGraph);
                        }
                    }
                }
            }
        }
        patternList = new ArrayList<>(initPatterns.values());
        return patternList;

//        for (CodeGraph cg : cgs) {
//            for (ActionNode ap : cg.getActions()) {
//                if (_nodeMap.containsKey(ap))
//                    continue;
//                boolean find = false;
//                for (Map.Entry<ActionNode, Pattern> entry : traversedMap.entrySet()) {
//                    if (similar(ap, entry.getKey())) {
//                        traversedMap.put(ap, entry.getValue());
//                        entry.getValue().getStart().addInstance(ap.getParent(), cg);
//                        _nodeMap.put(ap.getParent(), entry.getValue().getStart());
//                        extendPattern(ap.getParent(), cg, entry.getValue().getStart(), entry.getValue(), 1, new HashSet<>());
//                        find = true;
//                        break;
//                    }
//                }
//                if (!find) {
//                    PatternNode start = new PatternNode(ap.getParent(), cg, getLocationInParent(ap.getParent()), getNodeType(ap.getParent()));
//                    Pattern pattern = new Pattern(start);
//                    start.setPattern(pattern);
//                    patternList.add(pattern);
//                    traversedMap.put(ap, pattern);
//                    _nodeMap.put(ap.getParent(), start);
//                    extendPattern(ap.getParent(), cg, start, pattern, 1, new HashSet<>());
//                }
//            }
//        }
//        return patternList;
    }

    private static void match(List<Node> nodeList, int index, List<Node> nodeListComp, double score, Map<Node, Node> mapping) {
        if (index == nodeList.size()) {
            _mappingsByScore.put(mapping, score);
            return;
        }
        for (Node nodeComp : nodeListComp) {
            if (isMatch(nodeList.get(index), nodeComp, mapping)) {
                List<Node> nodeListCopy = new ArrayList<>(nodeListComp);
                Map<Node, Node> mappingNew = new LinkedHashMap<>(mapping);
                nodeListCopy.remove(nodeComp);
                mappingNew.put(nodeList.get(index), nodeComp);
                match(nodeList, index+1, nodeListCopy, score + calculate(nodeList.get(index), nodeComp), mappingNew);
            }
        }
        match(nodeList, index+1, nodeListComp, score, mapping);
    }

    /**
     * soft rules for matching
     */
    private static double calculate(Node node, Node nodeComp) {
        // compare the similarity between two strings by using "Levenshtein distance"
        double score = calContextSim(node.toLabelString(), nodeComp.toLabelString());
        // TODO: other comparing rules
        return score;
    }

    public static float levenshtein(String a, String b) {
        int aLen = a.length();
        int bLen = b.length();

        if (aLen == 0) return aLen;
        if (bLen == 0) return bLen;

        int[][] v = new int[aLen + 1][bLen + 1];
        for (int i = 0; i <= aLen; ++i) {
            for (int j = 0; j <= bLen; ++j) {
                if (i == 0) {
                    v[i][j] = j;
                } else if (j == 0) {
                    v[i][j] = i;
                } else if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    v[i][j] = v[i - 1][j - 1];
                } else {
                    v[i][j] = 1 + Math.min(v[i - 1][j - 1], Math.min(v[i][j - 1], v[i - 1][j]));
                }
            }
        }
        return v[aLen][bLen];
    }

    private static double calContextSim(String a, String b) {
        if (a == null && b == null) {
            return 1f;
        }
        if (a == null || b == null) {
            return 0F;
        }
        if (a.equals(b)) {
            return 1f;
        }
        double editDistance = levenshtein(a, b);
        return 1 - (editDistance / Math.max(a.length(), b.length()));
    }

    /**
     * hard rules for matching
     */
    private static boolean isMatch(Node nodeA, Node nodeB, Map<Node, Node> mapping) {
        // compare type
        if (!sameType(nodeA, nodeB))
            return false;
        if (!getLocationInParent(nodeA).equals(getLocationInParent(nodeB)))
            return false;
        // TODO: other comparing rules
        for (Map.Entry<Node, Node> entry : mapping.entrySet()) {
            Node key = entry.getKey();
            Node value = entry.getValue();
            // key -> nodeA and value -> nodeB
            if (!mapping.containsKey(nodeA.getParent()))
                return false;
            if (nodeA.getParent().equals(key) != nodeB.getParent().equals(value))
                return false;
            if (nodeA.isDependOn(key) != nodeB.isDependOn(value))
                return false;
        }
        return true;
    }

    private static List<List<Node>> getActionLinks(CodeGraph codeGraph) {
        List<List<Node>> result = new ArrayList<>();
        Set<Node> traversed = new LinkedHashSet<>();
        for (ActionNode action : codeGraph.getActions()) {
            if (traversed.contains(action)) continue;
            traversed.add(action);
            List<Node> nodes = extendLinks(action.getParent(), 1, traversed);
            if (!nodes.isEmpty())
                result.add(nodes);
        }
        return result;
    }

    private static List<Node> extendLinks(Node node, int extendLevel, Set<Node> traversed) {
        List<Node> nodes = new ArrayList<>();
        if (extendLevel > MAX_EXTEND_LEVEL)
            return nodes;
        nodes.add(node);
        traversed.add(node);
        for (Edge e : node.getEdges()) {
            if (e.getTarget().equals(node)) {
                // continue extend source
                nodes.addAll(extendLinks(e.getSource(), extendLevel+1, traversed).stream().filter(n -> !nodes.contains(n)).collect(Collectors.toList()));
            } else if (e.getSource().equals(node)) {
                // continue extend target
                nodes.addAll(extendLinks(e.getTarget(), extendLevel+1, traversed).stream().filter(n -> !nodes.contains(n)).collect(Collectors.toList()));
            }
        }
        return nodes;
    }

}
