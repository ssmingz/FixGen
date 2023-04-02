package builder;

import model.CodeGraph;
import model.graph.edge.Edge;
import model.graph.node.Node;
import model.graph.node.PatchNode;
import model.graph.node.actions.*;
import model.graph.node.expr.*;
import model.pattern.Attribute;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.apache.commons.collections4.SetUtils;
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
        } else if (nodeA instanceof PrefixOpr && nodeB instanceof PrefixOpr) {
            return true;
        } else if (nodeA instanceof PostfixOpr && nodeB instanceof PostfixOpr) {
            return true;
        } else if (nodeA instanceof InfixOpr && nodeB instanceof InfixOpr) {
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
        for (List<Node> l : nodeLists) {
            PatternNode start = new PatternNode(l.get(0), cgs.get(0));
            Pattern pat = new Pattern(start);
            pat.addNode(start, l.get(0));
            start.setPattern(pat);
            patternList.add(pat);
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

        for(int i=1; i<cgs.size(); i++) {
            CodeGraph aGraph = cgs.get(i);
            List<List<Node>> nodeListsComps = getActionLinks(aGraph);
            for (List<Node> nodeList: nodeLists) {
                for (List<Node> nodeListComp : nodeListsComps) {
                    _mappingsByScore.clear();
                    // calculate similarity score for each pair
                    Map<Node, Map<Node, Double>> orderBySimScore = calSimScore(nodeList, nodeListComp);
                    matchBySimScore(nodeList, 0, nodeListComp, 0, new LinkedHashMap<>(), orderBySimScore);      //两种匹配方法
//                    match(nodeList, 0, nodeListComp, 0, new LinkedHashMap<>());
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
                        Pattern pat = patternList.get(nodeLists.indexOf(nodeList));
                        for (Map.Entry<Node, Node> entry : result.entrySet()) {
                            Node oNode = entry.getKey();
                            Node cNode = entry.getValue();
                            pat.getNodeMapping().get(oNode).addInstance(cNode, aGraph);
                            // add edges
                            for (Edge e : cNode.inEdges) {
                                Node src = e.getSource();
                                if (pat.getNodeMapping().containsKey(src)) {
                                    pat.addEdge(pat.getNodeMapping().get(src), pat.getNodeMapping().get(oNode), PatternEdge.getEdgeType(e.type), e, aGraph);
                                }
                            }
                            for (Edge e : cNode.outEdges) {
                                Node tar = e.getTarget();
                                if (pat.getNodeMapping().containsKey(tar)) {
                                    pat.addEdge(pat.getNodeMapping().get(oNode), pat.getNodeMapping().get(tar), PatternEdge.getEdgeType(e.type), e, aGraph);
                                }
                            }
                        }
                        // extra not mapped
                        Map<Node, Node> finalResult = result;
                        Set<Node> notMapped = nodeListComp.stream().filter(s -> !finalResult.containsValue(s)).collect(Collectors.toSet());
                        for (Node n : notMapped) {
                            // build node
                            PatternNode pnNew = new PatternNode(n, aGraph);
                            pnNew.setPattern(pat);
                            pat.addNode(pnNew, n);
                        }
                        for (Node n : notMapped) {
                            PatternNode pn = pat.getNodeMapping().get(n);
                            // add edges
                            for (Edge e : n.inEdges) {
                                Node src = e.getSource();
                                if (pat.getNodeMapping().containsKey(src)) {
                                    pat.addEdge(pat.getNodeMapping().get(src), pn, PatternEdge.getEdgeType(e.type), e, aGraph);
                                }
                            }
                            for (Edge e : n.outEdges) {
                                Node tar = e.getTarget();
                                if (pat.getNodeMapping().containsKey(tar)) {
                                    pat.addEdge(pn, pat.getNodeMapping().get(tar), PatternEdge.getEdgeType(e.type), e, aGraph);
                                }
                            }
                        }
                        nodeList.addAll(notMapped);
                    }
                }
            }
        }
        return patternList;
    }

    private static Map<Node, Map<Node, Double>> calSimScore(List<Node> nodeList, List<Node> nodeListComp) {
        Map<Node, Map<Node, Double>> result = new LinkedHashMap<>();
        for (Node nodeA : nodeList) {
            if (!result.containsKey(nodeA)) {
                result.put(nodeA, new LinkedHashMap<>());
            } else if (result.get(nodeA).size() == nodeListComp.size()){
                continue;
            }
            for (Node nodeB : nodeListComp) {
                if (result.get(nodeA).containsKey(nodeB)) continue;
                double score = calContextSim(nodeA.toLabelString(), nodeB.toLabelString());
                result.get(nodeA).put(nodeB, score);
                if (!result.containsKey(nodeB)) {
                    result.put(nodeB, new LinkedHashMap<>());
                }
                result.get(nodeB).put(nodeA, score);
            }
            result.replace(nodeA, sortByValue(result.get(nodeA)));
        }
        return result;
    }

    /**
     * 优先匹配相似度分数最高的
     */
    private static void matchBySimScore(List<Node> nodeList, int index, List<Node> nodeListComp, double score, Map<Node, Node> mapping, Map<Node, Map<Node, Double>> simScoreMap) {
        if (index == nodeList.size()) {
            _mappingsByScore.put(mapping, score);
            return;
        }
        Node node = nodeList.get(index);
        if (simScoreMap.containsKey(node)) {
            Iterator<Node> itr = simScoreMap.get(node).keySet().iterator();
            Node bestSim = null;
            while (itr.hasNext()) {
                Node aNode = itr.next();
                if (isMatch(node, aNode, mapping)) {
                    bestSim = aNode;
                    break;
                }
            }
            if (bestSim != null) {
                double simScore = simScoreMap.get(node).get(bestSim);
                List<Node> nodeListCopy = new ArrayList<>(nodeListComp);
                nodeListCopy.remove(bestSim);
                Map<Node, Node> mappingNew = new LinkedHashMap<>(mapping);
                mappingNew.put(node, bestSim);
                // remove itself
                simScoreMap.remove(bestSim);
                // remove related
                Iterator<Map.Entry<Node, Map<Node, Double>>> iterator = simScoreMap.entrySet().iterator();
                while(iterator.hasNext()) {
                    Map<Node, Double> scoreMap = iterator.next().getValue();
                    scoreMap.remove(bestSim);
                    if (scoreMap.isEmpty()) {
                        iterator.remove();
                    }
                }
                matchBySimScore(nodeList, index+1, nodeListCopy, score+simScore, mappingNew, simScoreMap);
            } else {
                matchBySimScore(nodeList, index+1, nodeListComp, score, mapping, simScoreMap);
            }
        } else {
            matchBySimScore(nodeList, index+1, nodeListComp, score, mapping, simScoreMap);
        }
    }

    /**
     * 考虑所有匹配可能性
     */
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

    public static float cos(String a, String b) {
        if (a == null || b == null) {
            return 0F;
        }
        Set<Integer> aChar = a.chars().boxed().collect(Collectors.toSet());
        Set<Integer> bChar = b.chars().boxed().collect(Collectors.toSet());

        // 统计字频
        Map<Integer, Integer> aMap = new HashMap<>();
        Map<Integer, Integer> bMap = new HashMap<>();
        for (Integer a1 : aChar) {
            aMap.put(a1, aMap.getOrDefault(a1, 0) + 1);
        }
        for (Integer b1 : bChar) {
            bMap.put(b1, bMap.getOrDefault(b1, 0) + 1);
        }

        // 向量化
        Set<Integer> union = SetUtils.union(aChar, bChar);
        int[] aVec = new int[union.size()];
        int[] bVec = new int[union.size()];
        List<Integer> collect = new ArrayList<>(union);
        for (int i = 0; i < collect.size(); i++) {
            aVec[i] = aMap.getOrDefault(collect.get(i), 0);
            bVec[i] = bMap.getOrDefault(collect.get(i), 0);
        }

        // 分别计算三个参数
        int p1 = 0;
        for (int i = 0; i < aVec.length; i++) {
            p1 += (aVec[i] * bVec[i]);
        }

        float p2 = 0f;
        for (int i : aVec) {
            p2 += (i * i);
        }
        p2 = (float) Math.sqrt(p2);

        float p3 = 0f;
        for (int i : bVec) {
            p3 += (i * i);
        }
        p3 = (float) Math.sqrt(p3);

        return ((float) p1) / (p2 * p3);
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
//        double editDistance = levenshtein(a, b);
//        return 1 - (editDistance / Math.max(a.length(), b.length()));
        return cos(a, b);
    }

    /**
     * sort by value
     */
    public static <K, V extends Comparable> Map<K, V> sortByValue(Map<K, V> aMap) {
        HashMap<K, V> sorted = new LinkedHashMap<>();
        aMap.entrySet()
                .stream()
                .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .collect(Collectors.toList()).forEach(ele -> sorted.put(ele.getKey(), ele.getValue()));
        return sorted;
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
            if (key.equals(nodeA.getParent()) != value.equals(nodeB.getParent()))
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
            List<Node> nodes = extendLinks(action, 0, traversed); // start from action
//            List<Node> nodes = extendLinks(action.getParent(), 1, traversed); // start from action.parent
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
                // continue extending source
                nodes.addAll(extendLinks(e.getSource(), extendLevel+1, traversed).stream().filter(n -> !nodes.contains(n)).collect(Collectors.toList()));
            } else if (e.getSource().equals(node)) {
                // continue extending target
                nodes.addAll(extendLinks(e.getTarget(), extendLevel+1, traversed).stream().filter(n -> !nodes.contains(n)).collect(Collectors.toList()));
            }
        }
        return nodes;
    }
}
