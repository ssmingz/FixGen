package builder;

import codegraph.ASTEdge;
import codegraph.CtVirtualElement;
import codegraph.Edge;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.ActionEdge;
import model.actions.ActionNode;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.apache.commons.collections4.SetUtils;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtStatementImpl;
import spoon.support.reflect.declaration.CtElementImpl;
import utils.ObjectUtil;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class PatternExtractor {
    static int MAX_EXTEND_LEVEL = 3;  // default is 3
    static Map<CtWrapper, PatternNode> _nodeMap = new LinkedHashMap<>();
    static Map<Map<CtWrapper, CtWrapper>, Double> _mappingsByScore = new LinkedHashMap<>();

    public static List<Pattern> combineGraphs(List<CodeGraph> ags) {
        List<Pattern> patternList = new ArrayList<>();
        if (ags == null || ags.isEmpty()) {
            return patternList;
        }
        List<List<CtWrapper>> nodeLists = getActionLinks(ags.get(0));
        // init patterns
        for (List<CtWrapper> l : nodeLists) {
            PatternNode start = new PatternNode(l.get(0), ags.get(0));
            Pattern pat = new Pattern(start);
            pat.addNode(start, l.get(0));
            start.setPattern(pat);
            patternList.add(pat);
            for (int i=1; i<l.size(); i++) {
                CtWrapper n = l.get(i);
                PatternNode pn = new PatternNode(n, ags.get(0));
                pn.setPattern(pat);
                pat.addNode(pn, n);
            }
            for (CtWrapper n : l) {
                for (Edge e : n.getCtElementImpl()._inEdges) {
                    PatternNode srcPN = pat.getNodeMapping().get(ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(e.getSource())));
                    PatternNode tarPN = pat.getNodeMapping().get(n);
                    if (srcPN != null && tarPN!=null && !pat.hasEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type))) {
                        pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, ags.get(0));
                    }
                }
                for (Edge e : n.getCtElementImpl()._outEdges) {
                    PatternNode srcPN = pat.getNodeMapping().get(n);
                    PatternNode tarPN = pat.getNodeMapping().get(ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(e.getTarget())));
                    if (srcPN != null && tarPN!=null && !pat.hasEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type))) {
                        pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, ags.get(0));
                    }
                }
            }
        }

        for(int i=1; i<ags.size(); i++) {
            CodeGraph aGraph = ags.get(i);
            List<List<CtWrapper>> nodeListsComps = getActionLinks(aGraph);
            for (List<CtWrapper> nodeList: nodeLists) {
                _mappingsByScore.clear();
                double[] scores = new double[nodeListsComps.size()];
                for (int j=0; j<nodeListsComps.size(); j++) {
                    List<CtWrapper> nodeListComp = nodeListsComps.get(j);
                    // calculate similarity score for each pair
                    Map<CtWrapper, Map<CtWrapper, Double>> orderBySimScore = calSimScore(nodeList, nodeListComp);
                    scores[j] = matchBySimScore(nodeList, 0, nodeListComp, 0, new LinkedHashMap<>(), orderBySimScore);      //两种匹配方法
//                    match(nodeList, 0, nodeListComp, 0, new LinkedHashMap<>());
                    // group by ags.get(0)
                }
                if (_mappingsByScore.size() > 0) {
                    double maxScore = 0.0;
                    int maxIndex = -1;
                    int counter = -1;
                    Map<CtWrapper, CtWrapper> result = null;
                    for (Map.Entry<Map<CtWrapper, CtWrapper>, Double> entry : _mappingsByScore.entrySet()) {
                        counter++;
                        if (entry.getValue() > maxScore) {
                            maxScore = entry.getValue();
                            maxIndex = counter;
                            result = entry.getKey();
                        }
                    }
                    if (result==null) continue;
                    // add to pattern
                    Pattern pat = patternList.get(nodeLists.indexOf(nodeList));
                    for (Map.Entry<CtWrapper, CtWrapper> entry : result.entrySet()) {
                        CtWrapper oNode = entry.getKey();
                        CtWrapper cNode = entry.getValue();
                        pat.getNodeMapping().get(oNode).addInstance(cNode, aGraph);
                        // add edges
                        for (Edge e : cNode.getCtElementImpl()._inEdges) {
                            PatternNode srcPN = pat.getNodeMapping().get(ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(e.getSource())));
                            PatternNode tarPN = pat.getNodeMapping().get(oNode);
                            if (srcPN != null && tarPN!=null) {
                                pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, aGraph);
                            }
                        }
                        for (Edge e : cNode.getCtElementImpl()._outEdges) {
                            PatternNode srcPN = pat.getNodeMapping().get(oNode);
                            PatternNode tarPN = pat.getNodeMapping().get(ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(e.getTarget())));
                            if (srcPN != null && tarPN!=null) {
                                pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, aGraph);
                            }
                        }
                    }
                    // extra not mapped
                    Map<CtWrapper, CtWrapper> finalResult = result;
                    Set<CtWrapper> notMapped = nodeListsComps.get(maxIndex).stream().filter(s -> !finalResult.containsValue(s)).collect(Collectors.toSet());
                    for (CtWrapper n : notMapped) {
                        // build node
                        PatternNode pnNew = new PatternNode(n, aGraph);
                        pnNew.setPattern(pat);
                        pat.addNode(pnNew, n);
                    }
                    for (CtWrapper n : notMapped) {
                        PatternNode pn = pat.getNodeMapping().get(n);
                        // add edges
                        for (Edge e : n.getCtElementImpl()._inEdges) {
                            CtElementImpl src = e.getSource();
                            CtWrapper findKey = ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(src));
                            if (findKey != null) {
                                pat.addEdge(pat.getNodeMapping().get(findKey), pn, PatternEdge.getEdgeType(e.type), e, aGraph);
                            }
                        }
                        for (Edge e : n.getCtElementImpl()._outEdges) {
                            CtElementImpl tar = e.getTarget();
                            CtWrapper findKey = ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(tar));
                            if (findKey != null) {
                                pat.addEdge(pn, pat.getNodeMapping().get(findKey), PatternEdge.getEdgeType(e.type), e, aGraph);
                            }
                        }
                    }
                    nodeList.addAll(notMapped);
                }
            }
        }
        return patternList;
    }

    private static List<List<CtWrapper>> getActionLinks(CodeGraph codeGraph) {
        List<List<CtWrapper>> result = new ArrayList<>();
        Set<Object> traversed_actions = new LinkedHashSet<>();
        for (ActionNode action : codeGraph.getActions()) {
            if (traversed_actions.contains(action)) continue;
            Set<CtWrapper> traversed = new LinkedHashSet<>();
            List<CtWrapper> nodes = extendLinks(new CtWrapper(action), 0, traversed, codeGraph.getMapping(), false); // start from action
            traversed_actions.addAll(nodes.stream().filter(n -> n.getCtElementImpl() instanceof ActionNode).map(CtWrapper::getCtElementImpl).collect(Collectors.toSet()));
            if (!nodes.isEmpty())
                result.add(nodes);
        }
        return result;
    }

    private static List<CtWrapper> extendLinks(CtWrapper node, int extendLevel, Set<CtWrapper> traversed, Map<CtWrapper, CtWrapper> mapping, boolean isPatch) {
        List<CtWrapper> nodes = new ArrayList<>();
        if (ObjectUtil.findCtKeyInSet(traversed, node)!=null || extendLevel > MAX_EXTEND_LEVEL)
            return nodes;
        nodes.add(node);
        // update traversed
        traversed.add(node);
        CtWrapper mapped = mapping.get(ObjectUtil.findCtKeyInSet(mapping.keySet(), node));
        if (mapped != null && node.toLabelString().equals(mapped.toLabelString())) {
            traversed.add(mapped);
        }
        for (Edge ie : node.getCtElementImpl()._inEdges) {
            if (ie instanceof ActionEdge && !(node.getCtElementImpl() instanceof ActionNode)) {
                isPatch = true;
                break;
            }
        }
        for (Edge ie : node.getCtElementImpl()._inEdges) {
            // continue extending source
            if (isPatch && ie instanceof ASTEdge)  // do not extend AST relationship in dst tree
                continue;
            nodes.addAll(extendLinks(new CtWrapper(ie.getSource()), extendLevel+1, traversed, mapping, isPatch).stream().filter(n -> !nodes.contains(n)).collect(Collectors.toList()));
        }
        for (Edge oe : node.getCtElementImpl()._outEdges) {
            // continue extending target
            if (isPatch && oe instanceof ASTEdge)  // do not extend AST relationship in dst tree
                continue;
            if (oe.getTarget() instanceof CtStatementImpl)
                nodes.addAll(extendLinks(new CtWrapper(oe.getTarget()), extendLevel+1, traversed, mapping, isPatch).stream().filter(n -> !nodes.contains(n)).collect(Collectors.toList()));
            else
                nodes.addAll(extendLinks(new CtWrapper(oe.getTarget()), extendLevel+1, traversed, mapping, isPatch).stream().filter(n -> !nodes.contains(n)).collect(Collectors.toList()));

        }
        return nodes;
    }

    /**
     * 优先匹配相似度分数最高的
     */
    private static double matchBySimScore(List<CtWrapper> nodeList, int index, List<CtWrapper> nodeListComp, double score, Map<CtWrapper, CtWrapper> mapping, Map<CtWrapper, Map<CtWrapper, Double>> simScoreMap) {
        if (index == nodeList.size()) {
            _mappingsByScore.put(mapping, score);
            return score;
        }
        CtWrapper node = nodeList.get(index);
        if (simScoreMap.containsKey(node)) {
            Iterator<CtWrapper> itr = simScoreMap.get(node).keySet().iterator();
            CtWrapper bestSim = null;
            while (itr.hasNext()) {
                CtWrapper aNode = itr.next();
                if (isMatch(node, aNode, mapping)) {
                    bestSim = aNode;
                    break;
                }
            }
            if (bestSim != null) {
                double simScore = simScoreMap.get(node).get(bestSim);
                List<CtWrapper> nodeListCopy = new ArrayList<>(nodeListComp);
                nodeListCopy.remove(bestSim);
                Map<CtWrapper, CtWrapper> mappingNew = new LinkedHashMap<>(mapping);
                mappingNew.put(node, bestSim);
                // remove itself
                simScoreMap.remove(bestSim);
                // remove related
                Iterator<Map.Entry<CtWrapper, Map<CtWrapper, Double>>> iterator = simScoreMap.entrySet().iterator();
                while(iterator.hasNext()) {
                    Map<CtWrapper, Double> scoreMap = iterator.next().getValue();
                    scoreMap.remove(bestSim);
                    if (scoreMap.isEmpty()) {
                        iterator.remove();
                    }
                }
                return matchBySimScore(nodeList, index+1, nodeListCopy, score+simScore, mappingNew, simScoreMap);
            } else {
                return matchBySimScore(nodeList, index+1, nodeListComp, score, mapping, simScoreMap);
            }
        } else {
            return matchBySimScore(nodeList, index+1, nodeListComp, score, mapping, simScoreMap);
        }
    }

    /**
     * hard rules for matching
     */
    private static boolean isMatch(CtWrapper nodeA, CtWrapper nodeB, Map<CtWrapper, CtWrapper> mapping) {
        // action nodes must have same type
        if (nodeA.getCtElementImpl() instanceof ActionNode && nodeB.getCtElementImpl() instanceof ActionNode && !nodeA.toLabelString().equals(nodeB.toLabelString()))
            return false;
        // compare type
        if (!sameType(nodeA, nodeB))
            return false;
//        if (!getLocationInParent(nodeA).equals(getLocationInParent(nodeB)))
//            return false;
        // TODO: other comparing rules
        for (Map.Entry<CtWrapper, CtWrapper> entry : mapping.entrySet()) {
            CtWrapper key = entry.getKey();
            CtWrapper value = entry.getValue();
            // key -> nodeA and value -> nodeB
            if (key.equals(new CtWrapper((CtElementImpl) nodeA.getCtElementImpl().getParent())) != value.equals(new CtWrapper((CtElementImpl) nodeB.getCtElementImpl().getParent())))
                return false;
            if (nodeA.getCtElementImpl().isDependOn(key.getCtElementImpl()) != nodeB.getCtElementImpl().isDependOn(value.getCtElementImpl()))
                return false;
            if (ObjectUtil.hasEdge(nodeA.getCtElementImpl(), key.getCtElementImpl()) != ObjectUtil.hasEdge(nodeB.getCtElementImpl(), value.getCtElementImpl()))
                return false;
        }
        return true;
    }

    private static String getLocationInParent(CtWrapper n) {
        if (n.isVirtual()) {
            return ((CtVirtualElement) n.getCtElementImpl()).getLocationInParent();
        } else if (n.getCtElementImpl() instanceof ActionNode) {
            return "ACTION";
        } else {
            return n.getCtElementImpl().getRoleInParent().name();
        }
    }

    public static boolean sameType(CtWrapper nodeA, CtWrapper nodeB) {
        CtElementImpl ctA = nodeA.getCtElementImpl();
        CtElementImpl ctB = nodeB.getCtElementImpl();
        if (ctA.getClass().getSimpleName().equals(ctB.getClass().getSimpleName()))
            return true;
        Type ancA = ctA.getClass().getAnnotatedSuperclass().getType();
        Type ancB = ctB.getClass().getAnnotatedSuperclass().getType();
        if (ancA == ancB && (ctA instanceof CtBlockImpl) == (ctB instanceof CtBlockImpl)) {
            // same ancestor type
            return true;
        } else if (similarType(ancA, ancB)) {
            return true;
        } else if (isVar(ctA) && isVar(ctB)) {
            return true;
        } else {
            // special case
            String aStr = ctA.prettyprint();
            String bStr = ctB.prettyprint();
            return (aStr.equals("==") && bStr.equals("!=")) || (aStr.equals("!=") && bStr.equals("=="));
        }
    }

    private static boolean isVar(CtElementImpl ct) {
        boolean isVirtualName = ct instanceof CtVirtualElement && ((CtVirtualElement) ct).getLocationInParent().contains("VAR_NAME");
        String clazz = ct.getClass().getSimpleName();
        String[] varRelate = {"CtFieldReadImpl", "CtFieldWriteImpl", "CtArrayReadImpl",
                "CtArrayWriteImpl", "CtLocalVariableImpl", "CtThisAccessImpl",
                "CtVariableReadImpl", "CtVariableWriteImpl",
                "CtCatchVariableReferenceImpl", "CtFieldReferenceImpl", "CtLocalVariableReferenceImpl", "CtParameterReferenceImpl"};
        return Arrays.asList(varRelate).contains(clazz) || isVirtualName;
    }

    private static boolean similarType(Type ancA, Type ancB) {
        String tA = ancA.getTypeName();
        String tB = ancB.getTypeName();
        return (tA.contains("CtExpressionImpl") && tB.contains("CtFieldAccessImpl")) || (tA.contains("CtFieldAccessImpl") && tB.contains("CtExpressionImpl"));
    }

    private static Map<CtWrapper, Map<CtWrapper, Double>> calSimScore(List<CtWrapper> nodeList, List<CtWrapper> nodeListComp) {
        Map<CtWrapper, Map<CtWrapper, Double>> result = new LinkedHashMap<>();
        for (CtWrapper nodeA : nodeList) {
            if (!result.containsKey(nodeA)) {
                result.put(nodeA, new LinkedHashMap<>());
            } else if (result.get(nodeA).size() == nodeListComp.size()){
                continue;
            }
            for (CtWrapper nodeB : nodeListComp) {
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
        double cos = cos(a, b);
        return Double.isNaN(cos) ? 0F : cos;
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
}
