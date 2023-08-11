package builder;

import codegraph.CtVirtualElement;
import codegraph.Edge;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.ActionNode;
import model.pattern.Attribute;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.SetUtils;
import org.javatuples.Pair;
import spoon.support.reflect.code.CtBlockImpl;
import spoon.support.reflect.code.CtCodeElementImpl;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.reference.CtReferenceImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;
import utils.ObjectUtil;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class PatternExtractor {
    static int MAX_EXTEND_LEVEL = 3;  // default is 3
    static Map<CtWrapper, PatternNode> _nodeMap = new LinkedHashMap<>();
    static Map<Map<CtWrapper, CtWrapper>, Double> _mappingsByScore = new LinkedHashMap<>();


    public static List<Pattern> combineGraphs(List<CodeGraph> ags, String runType) {
        List<Pattern> patternList = new ArrayList<>();
        if (ags == null || ags.isEmpty()) {
            return patternList;
        }
        List<List<CtWrapper>> nodeLists = getActionLinks(ags.get(0));
        // init patterns
        for (List<CtWrapper> l : nodeLists) {
            PatternNode start = new PatternNode(l.get(0), ags.get(0));
            Pattern pat = new Pattern(start, l.get(0));
            start.setPattern(pat);
            patternList.add(pat);
            for (int i = 1; i < l.size(); i++) {
                CtWrapper n = l.get(i);
                PatternNode pn = new PatternNode(n, ags.get(0));
                pn.setPattern(pat);
                pat.addNode(pn, n);
            }
            for (CtWrapper n : l) {
                for (Edge e : n.getCtElementImpl()._inEdges) {
                    PatternNode srcPN = pat.getNodeMapping().get(ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(e.getSource())));
                    PatternNode tarPN = pat.getNodeMapping().get(n);
                    if (srcPN != null && tarPN != null && !pat.hasEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type))) {
                        pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, ags.get(0));
                    }
                }
                for (Edge e : n.getCtElementImpl()._outEdges) {
                    PatternNode srcPN = pat.getNodeMapping().get(n);
                    PatternNode tarPN = pat.getNodeMapping().get(ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(e.getTarget())));
                    if (srcPN != null && tarPN != null && !pat.hasEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type))) {
                        pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, ags.get(0));
                    }
                }
            }
        }

        for (int i = 1; i < ags.size(); i++) {
            CodeGraph aGraph = ags.get(i);
            List<List<CtWrapper>> nodeListsComps = getActionLinks(aGraph);
            for (List<CtWrapper> nodeList : nodeLists) {
                _mappingsByScore.clear();
                double[] scores = new double[nodeListsComps.size()];
                for (int j = 0; j < nodeListsComps.size(); j++) {
                    List<CtWrapper> nodeListComp = nodeListsComps.get(j);

                    // Begin the traversal from the parent nodes
                    Collections.reverse(nodeList);
                    Collections.reverse(nodeListComp);
                    // calculate similarity score for each pair
                    Map<CtWrapper, Map<CtWrapper, Double>> orderBySimScore = calSimScore(nodeList, nodeListComp);

                    if (runType.equals("old")) {
                        scores[j] = matchBySimScore(nodeList, 0, nodeListComp, 0, new LinkedHashMap<>(), orderBySimScore);      //两种匹配方法
                    } else if (runType.equals("new")) {
                        Set<Pair<Map<CtWrapper, CtWrapper>, Double>> resScores = matchTiedNodeBySimScore(new HashSet<>(), nodeList, 0, nodeListComp, 0, new LinkedHashMap<>(), orderBySimScore);      //两种匹配方法

                        List<Pair<Map<CtWrapper, CtWrapper>, Double>> scoresList = new ArrayList<>(resScores);
                        scoresList.sort((p1, p2) -> Double.compare(p2.getValue1(), p1.getValue1()));
                        scores[j] = scoresList.get(0).getValue1() / nodeList.size();
                        _mappingsByScore.put(scoresList.get(0).getValue0(), scores[j]);
                    }

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
                    if (result == null) continue;
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
                            if (srcPN != null && tarPN != null) {
                                pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, aGraph);
                            }
                        }
                        for (Edge e : cNode.getCtElementImpl()._outEdges) {
                            PatternNode srcPN = pat.getNodeMapping().get(oNode);
                            PatternNode tarPN = pat.getNodeMapping().get(ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(e.getTarget())));
                            if (srcPN != null && tarPN != null) {
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


    public static List<Pattern> combineGraphs(List<CodeGraph> ags) {
        List<Pattern> patternList = new ArrayList<>();
        if (ags == null || ags.isEmpty()) {
            return patternList;
        }
        List<List<CtWrapper>> nodeLists = getActionLinks(ags.get(0));
        // init patterns
        for (List<CtWrapper> l : nodeLists) {
            PatternNode start = new PatternNode(l.get(0), ags.get(0));
            Pattern pat = new Pattern(start, l.get(0));
            start.setPattern(pat);
            patternList.add(pat);
            for (int i = 1; i < l.size(); i++) {
                CtWrapper n = l.get(i);
                PatternNode pn = new PatternNode(n, ags.get(0));
                pn.setPattern(pat);
                pat.addNode(pn, n);
            }
            for (CtWrapper n : l) {
                for (Edge e : n.getCtElementImpl()._inEdges) {
                    PatternNode srcPN = pat.getNodeMapping().get(ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(e.getSource())));
                    PatternNode tarPN = pat.getNodeMapping().get(n);
                    if (srcPN != null && tarPN != null && !pat.hasEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type))) {
                        pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, ags.get(0));
                    }
                }
                for (Edge e : n.getCtElementImpl()._outEdges) {
                    PatternNode srcPN = pat.getNodeMapping().get(n);
                    PatternNode tarPN = pat.getNodeMapping().get(ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(e.getTarget())));
                    if (srcPN != null && tarPN != null && !pat.hasEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type))) {
                        pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, ags.get(0));
                    }
                }
            }
        }

        for (int i = 1; i < ags.size(); i++) {
            CodeGraph aGraph = ags.get(i);
            List<List<CtWrapper>> nodeListsComps = getActionLinks(aGraph);
            for (List<CtWrapper> nodeList : nodeLists) {
                _mappingsByScore.clear();
                double[] scores = new double[nodeListsComps.size()];
                for (int j = 0; j < nodeListsComps.size(); j++) {
                    List<CtWrapper> nodeListComp = nodeListsComps.get(j);
                    // getActionLinks is a recursive function, so nodes are down up, reverse for to match them up down
                    Collections.reverse(nodeList);
                    Collections.reverse(nodeListsComps);

                    // calculate similarity score for each pair
                    Map<CtWrapper, Map<CtWrapper, Double>> orderBySimScore = calSimScore(nodeList, nodeListComp);


//                    scores[j] = matchBySimScore(nodeList, 0, nodeListComp, 0, new LinkedHashMap<>(), orderBySimScore);      //两种匹配方法

                    Set<Pair<Map<CtWrapper, CtWrapper>, Double>> resScores = matchTiedNodeBySimScore(nodeList, 0, nodeListComp, 0, new LinkedHashMap<>(), orderBySimScore);      //两种匹配方法

                    List<Pair<Map<CtWrapper, CtWrapper>, Double>> scoresList = new ArrayList<>(resScores);
                    scoresList.sort((p1, p2) -> Double.compare(p2.getValue1(), p1.getValue1()));

                    scores[j] = scoresList.get(0).getValue1();
                    _mappingsByScore.put(scoresList.get(0).getValue0(), scoresList.get(0).getValue1());

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
                    if (result == null) continue;
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
                            if (srcPN != null && tarPN != null) {
                                pat.addEdge(srcPN, tarPN, PatternEdge.getEdgeType(e.type), e, aGraph);
                            }
                        }
                        for (Edge e : cNode.getCtElementImpl()._outEdges) {
                            PatternNode srcPN = pat.getNodeMapping().get(oNode);
                            PatternNode tarPN = pat.getNodeMapping().get(ObjectUtil.findCtKeyInSet(pat.getNodeMapping().keySet(), new CtWrapper(e.getTarget())));
                            if (srcPN != null && tarPN != null) {
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
            Set<CtWrapper> extendFromControlDep = new HashSet<>();
            List<CtWrapper> nodes = extendLinks(new CtWrapper(action), 0, traversed, codeGraph, extendFromControlDep); // start from action

            // remove extended-by-control-dep if action-related nodes cannot reach
            Set<CtWrapper> actionRelated = nodes.stream().filter(n -> n.getCtElementImpl().isActionRelated()
                    && canReach(action, n.getCtElementImpl(), new HashSet<>()) != 99999).collect(Collectors.toSet());
            for (CtWrapper c : extendFromControlDep) {
                if (actionRelated.stream().noneMatch(a -> canReach(a.getCtElementImpl(), c.getCtElementImpl(), new HashSet<>()) <= MAX_EXTEND_LEVEL
                        || canReach(c.getCtElementImpl(), a.getCtElementImpl(), new HashSet<>()) <= MAX_EXTEND_LEVEL)) {
                    nodes.removeIf(n -> n.getCtElementImpl() == c.getCtElementImpl());
                }
            }
            traversed_actions.addAll(nodes.stream().map(CtWrapper::getCtElementImpl).filter(ctElementImpl -> ctElementImpl instanceof ActionNode).collect(Collectors.toSet()));
            if (!nodes.isEmpty())
                result.add(nodes);
        }
        return result;
    }

    private static int canReach(CtElementImpl src, CtElementImpl tar, Set<CtWrapper> traversed) {
        int result = 99999;
        if (src == tar)
            return 0;
        if (src._outEdges.isEmpty())
            return result;
        traversed.add(new CtWrapper(src));
        for (Edge oe : src._outEdges) {
            if (ObjectUtil.findCtKeyInSet(traversed, new CtWrapper(oe.getTarget())) == null) {
                int path = 1 + canReach(oe.getTarget(), tar, traversed);
                if (result > path)
                    result = path;
            }
        }
        return result;
    }

    private static List<CtWrapper> extendLinks(CtWrapper node, int extendLevel, Set<CtWrapper> traversed, CodeGraph cg, Set<CtWrapper> extendFromControlDep) {
        List<CtWrapper> nodes = new ArrayList<>();
        boolean isPatch = node.getCtElementImpl().isActionRelated() && !(node.getCtElementImpl() instanceof ActionNode);
        if (ObjectUtil.findCtKeyInSet(traversed, node) != null || (!isPatch && extendLevel > MAX_EXTEND_LEVEL))
            return nodes;

        // Find the mapping one in srcGraph if is the dstGraph node. If cannot find, add only if it is in srcGraph.allNodes, or else skip.
        if (cg.getNodes().stream().anyMatch(e -> e.getCtElementImpl() == node.getCtElementImpl())) {
            nodes.add(node);
        } else {
            CtElementImpl nodeInSrc = ObjectUtil.findMappedNodeInSrcGraph(node.getCtElementImpl(), cg);
            if (nodeInSrc != null) {
                nodes.add(new CtWrapper(nodeInSrc));
            } else {
//          System.out.printf("[warn]Unable to find the extended node in srcGraph for:%s\n", node.getCtElementImpl().prettyprint());
            }
        }

        // update traversed
        traversed.add(node);
        for (Edge ie : node.getCtElementImpl()._inEdges) {
            // continue extending source
            if (isPatch && ie.type == Edge.EdgeType.AST)  // do not extend AST relationship in dst tree (already add it in action graph building)
                continue;
            if (ObjectUtil.findCtKeyInSet(extendFromControlDep, new CtWrapper(node.getCtElementImpl())) != null)
                extendFromControlDep.add(new CtWrapper(ie.getSource()));
            nodes.addAll(extendLinks(new CtWrapper(ie.getSource()), extendLevel + 1, traversed, cg, extendFromControlDep).stream().filter(n -> !nodes.contains(n)).collect(Collectors.toList()));
        }
        for (Edge oe : node.getCtElementImpl()._outEdges) {
            // continue extending target
            if (ObjectUtil.findCtKeyInSet(extendFromControlDep, new CtWrapper(node.getCtElementImpl())) != null
                    || oe.type == Edge.EdgeType.CONTROL_DEP)
                extendFromControlDep.add(new CtWrapper(oe.getTarget()));
            nodes.addAll(extendLinks(new CtWrapper(oe.getTarget()), extendLevel + 1, traversed, cg, extendFromControlDep).stream().filter(n -> !nodes.contains(n)).collect(Collectors.toList()));
        }
        return nodes;
    }

    /**
     * 优先匹配相似度分数最高的
     */
    public static double matchBySimScore(List<CtWrapper> nodeList, int index, List<CtWrapper> nodeListComp, double score, Map<CtWrapper, CtWrapper> mapping, Map<CtWrapper, Map<CtWrapper, Double>> simScoreMap) {
        if (index == nodeList.size()) {
            score = score / nodeList.size();
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
                while (iterator.hasNext()) {
                    Map<CtWrapper, Double> scoreMap = iterator.next().getValue();
                    scoreMap.remove(bestSim);
                    if (scoreMap.isEmpty()) {
                        iterator.remove();
                    }
                }
                return matchBySimScore(nodeList, index + 1, nodeListCopy, score + simScore, mappingNew, simScoreMap);
            } else {
                return matchBySimScore(nodeList, index + 1, nodeListComp, score, mapping, simScoreMap);
            }
        } else {
            return matchBySimScore(nodeList, index + 1, nodeListComp, score, mapping, simScoreMap);
        }
    }

    public static Set<Pair<Map<CtWrapper, CtWrapper>, Double>> matchTiedNodeBySimScore(Set<CtWrapper> traversed, List<CtWrapper> nodeList, int index, List<CtWrapper> nodeListComp, double score, Map<CtWrapper, CtWrapper> mapping, Map<CtWrapper, Map<CtWrapper, Double>> simScoreMap) {
        if (traversed.size() == nodeList.size())
            return Collections.singleton(new Pair<>(new HashMap<>(mapping), score));
        //if (index == nodeList.size()) {
//            _mappingsByScore.put(mapping, score);
        //    return Collections.singleton(new Pair<>(new HashMap<>(mapping), score));
//            return score;
        //}
        index = index % nodeList.size();
        CtWrapper node = nodeList.get(index);
        while (ObjectUtil.findCtKeyInSet(traversed, node) != null) {
            index++;
            index = index % nodeList.size();
            node = nodeList.get(index);
        }
        if (ObjectUtil.findCtKeyInSet(simScoreMap.keySet(), new CtWrapper((CtElementImpl) node.getCtElementImpl().getParent())) != null
                && ObjectUtil.findCtKeyInSet(traversed, new CtWrapper((CtElementImpl) node.getCtElementImpl().getParent())) == null)
            return matchTiedNodeBySimScore(traversed, nodeList, index + 1, nodeListComp, score, mapping, simScoreMap);
        traversed.add(node);
        if (simScoreMap.containsKey(node)) {
            List<CtWrapper> topNCandidates = new ArrayList<>();
            double highestScore = -1;
            for (CtWrapper candidate : simScoreMap.get(node).keySet()) {
                double simScore = simScoreMap.get(node).get(candidate);
                if (simScore <= 0) {
                    break;
                }
                if (simScore > highestScore) {
                    highestScore = simScore;
                }
            }

            simScoreMap.replace(node, sortByValue(simScoreMap.get(node)));
            double bestSimScore = -1;
            for (CtWrapper candidate : simScoreMap.get(node).keySet()) {
                double simScore = simScoreMap.get(node).get(candidate);
                if (simScore <= 0 || simScore < bestSimScore) {
                    break;
                }
                if (node.toLabelString().equals("!fails.fTornDown") && candidate.toLabelString().equals("fails.fTornDown") && !isMatch(node, candidate, mapping)) {
                    isMatch(node, candidate, mapping);
                    System.out.println("debug");
                }

                if (simScore >= bestSimScore && isMatch(node, candidate, mapping)) {
                    //if (simScore == highestScore && isMatch(node, candidate, mapping)) {
                    topNCandidates.add(candidate);
                    bestSimScore = simScore;
                }
            }

            Set<Pair<Map<CtWrapper, CtWrapper>, Double>> allPairs = new HashSet<>();
            if (!topNCandidates.isEmpty()) {
                for (CtWrapper bestSim : topNCandidates) {
                    List<CtWrapper> nodeListCopy = new ArrayList<>(nodeListComp);
                    Map<CtWrapper, CtWrapper> mappingCopy = new HashMap<>(mapping);
                    Map<CtWrapper, Map<CtWrapper, Double>> simScoreMapCopy = deepCopyCtSimScoreMap(simScoreMap);

                    double simScore = simScoreMap.get(node).get(bestSim);

                    nodeListCopy.remove(bestSim);
                    mappingCopy.put(node, bestSim);
                    simScoreMapCopy.remove(node);

                    for (Map<CtWrapper, Double> scoreMap : simScoreMapCopy.values()) {
                        scoreMap.remove(bestSim);
                    }

                    allPairs.addAll(matchTiedNodeBySimScore(traversed, nodeList, index + 1, nodeListCopy, score + simScore, mappingCopy, simScoreMapCopy));
                }
                return allPairs;
            } else {
                return matchTiedNodeBySimScore(traversed, nodeList, index + 1, nodeListComp, score, mapping, simScoreMap);
            }

        } else {
            return matchTiedNodeBySimScore(traversed, nodeList, index + 1, nodeListComp, score, mapping, simScoreMap);
        }
    }

    public static Set<Pair<Map<CtWrapper, CtWrapper>, Double>> matchTiedNodeBySimScore(List<CtWrapper> nodeList, int index, List<CtWrapper> nodeListComp, double score, Map<CtWrapper, CtWrapper> mapping, Map<CtWrapper, Map<CtWrapper, Double>> simScoreMap) {
        if (index == nodeList.size()) {
//            _mappingsByScore.put(mapping, score);
            return Collections.singleton(new Pair<>(new HashMap<>(mapping), score));
//            return score;
        }
        CtWrapper node = nodeList.get(index);
        if (simScoreMap.containsKey(node)) {
            List<CtWrapper> topNCandidates = new ArrayList<>();

            simScoreMap.replace(node, sortByValue(simScoreMap.get(node)));
            double bestScore = -1;
            for (CtWrapper candidate : simScoreMap.get(node).keySet()) {
                double simScore = simScoreMap.get(node).get(candidate);
                if (simScore <= 0 || simScore < bestScore) {
                    break;
                }
                if (simScore >= bestScore && isMatch(node, candidate, mapping)) {
                    topNCandidates.add(candidate);
                    bestScore = simScore;
                }
            }

            Set<Pair<Map<CtWrapper, CtWrapper>, Double>> allPairs = new HashSet<>();
            if (!topNCandidates.isEmpty()) {
                for (CtWrapper bestSim : topNCandidates) {
                    List<CtWrapper> nodeListCopy = new ArrayList<>(nodeListComp);
                    Map<CtWrapper, CtWrapper> mappingCopy = new HashMap<>(mapping);
                    Map<CtWrapper, Map<CtWrapper, Double>> simScoreMapCopy = deepCopyCtSimScoreMap(simScoreMap);

                    double simScore = simScoreMap.get(node).get(bestSim);

                    nodeListCopy.remove(bestSim);
                    mappingCopy.put(node, bestSim);
                    simScoreMapCopy.remove(node);

                    for (Map<CtWrapper, Double> scoreMap : simScoreMapCopy.values()) {
                        scoreMap.remove(bestSim);
                    }

                    allPairs.addAll(matchTiedNodeBySimScore(nodeList, index + 1, nodeListComp, score + simScore, mappingCopy, simScoreMapCopy));
                }
                return allPairs;
            } else {
                return matchTiedNodeBySimScore(nodeList, index + 1, nodeListComp, score, mapping, simScoreMap);
            }

        } else {
            return matchTiedNodeBySimScore(nodeList, index + 1, nodeListComp, score, mapping, simScoreMap);
        }
    }

    public static double matchBySimScorePattern(List<PatternNode> pnList, int index, List<CtWrapper> cgList, double score, Map<PatternNode, CtWrapper> mapping, Map<PatternNode, Map<CtWrapper, Double>> simScoreMap) {
        if (index == pnList.size()) {
            return score;
        }
        PatternNode node = pnList.get(index);
        if (simScoreMap.containsKey(node)) {
            Iterator<CtWrapper> itr = simScoreMap.get(node).keySet().iterator();
            CtWrapper bestSim = null;
            while (itr.hasNext()) {
                CtWrapper aNode = itr.next();
                if (simScoreMap.get(node).get(aNode) <= 0)
                    break;
                if (isMatch(node, aNode, mapping)) {
                    bestSim = aNode;
                    break;
                }
            }
            if (bestSim != null) {
                double simScore = simScoreMap.get(node).get(bestSim);
                List<CtWrapper> nodeListCopy = new ArrayList<>(cgList);
                nodeListCopy.remove(bestSim);
                mapping.put(node, bestSim);
                // remove itself
                simScoreMap.remove(bestSim);
                // remove related
                Iterator<Map.Entry<PatternNode, Map<CtWrapper, Double>>> iterator = simScoreMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map<CtWrapper, Double> scoreMap = iterator.next().getValue();
                    scoreMap.remove(bestSim);
                    if (scoreMap.isEmpty()) {
                        iterator.remove();
                    }
                }
                return matchBySimScorePattern(pnList, index + 1, nodeListCopy, score + simScore, mapping, simScoreMap);
            } else {
                return matchBySimScorePattern(pnList, index + 1, cgList, score, mapping, simScoreMap);
            }
        } else {
            return matchBySimScorePattern(pnList, index + 1, cgList, score, mapping, simScoreMap);
        }
    }

    public static Set<Pair<Map<PatternNode, CtWrapper>, Double>> matchTopTiedNodeBySimScorePattern2(List<PatternNode> pnList, List<CtWrapper> cgList, Map<PatternNode, Map<CtWrapper, Double>> simScoreMap) {
        class Choice {
            Set<PatternNode> traveledNodes;
            CtWrapper cgNode;
            Map<PatternNode, CtWrapper> track;

            double score;

            Choice(Set<PatternNode> traveledNodes, CtWrapper cgNode, Map<PatternNode, CtWrapper> track, double score) {
                this.traveledNodes = traveledNodes;
                this.cgNode = cgNode;
                this.track = track;
                this.score = score;
            }

        }

        Set<Pair<Map<PatternNode, CtWrapper>, Double>> result = new HashSet<>();


        Stack<Choice> stack = new Stack<>();
        stack.push(new Choice(new HashSet<>(), null, new HashMap<>(), 0)); // 初始化

        while (!stack.isEmpty()) {
            Choice choice = stack.pop();

            Set<PatternNode> traversed = choice.traveledNodes;
            Map<PatternNode, CtWrapper> track = choice.track;
            double score = choice.score;

            List<PatternNode> tempPatternNodes = pnList.stream()
                    .filter(patternNode -> !traversed.contains(patternNode))
                    .collect(Collectors.toList());

            int index = 0;
            PatternNode node = null;
            while (!traversed.contains(node)) {
                node = tempPatternNodes.get(index);
                // 如果一个node的parent没有遍历过，先遍历他的parent
                if (ObjectUtil.findPtKeyInSet(simScoreMap.keySet(), node.getParent()) != null && ObjectUtil.findPtKeyInSet(traversed, node.getParent()) == null) {
                    index = (++index) % tempPatternNodes.size();
                    continue;
                }
                traversed.add(node);
            }

            // 当前patternNode存在多个可匹配CtWrapper
            if (simScoreMap.containsKey(node)) {
                List<CtWrapper> topNCandidates = new ArrayList<>();

                // 已经排好序了
                simScoreMap.replace(node, sortByValue(simScoreMap.get(node)));
                double bestScore = -1;
                for (CtWrapper candidate : simScoreMap.get(node).keySet()) {
                    double simScore = simScoreMap.get(node).get(candidate);
                    if (simScore <= 0 || simScore < bestScore) {
                        break;
                    }

                    if (simScore >= bestScore && isMatch(node, candidate, track)) {
                        topNCandidates.add(candidate);
                        bestScore = simScore;
                    }
                }


                if (!topNCandidates.isEmpty()) {
                    for (CtWrapper bestSim : topNCandidates) {

                        Map<PatternNode, CtWrapper> mapping = new HashMap<>();
                        mapping.put(node, bestSim);
                        double simScore = simScoreMap.get(node).get(bestSim);

                        traversed.add(node);

                        track.put(node, bestSim);
                        stack.push(new Choice(traversed, bestSim, new HashMap<>(track), score + simScore));
                        track.remove(node, bestSim);

                    }
                }
            }

            if (traversed.size() == pnList.size()) {
                while (!stack.isEmpty()) {
                    Choice choice1 = stack.pop();
                    result.add(new Pair<>(choice1.track, choice1.score));
                }
                return result;
            }

        }
        return result;
    }

    public static Set<Pair<Map<PatternNode, CtWrapper>, Double>> matchTopTiedNodeBySimScorePattern(Set<PatternNode> traversed, List<PatternNode> pnList, int index, List<CtWrapper> cgList, double score, Map<PatternNode, CtWrapper> mapping, Map<PatternNode, Map<CtWrapper, Double>> simScoreMap) {
        if (traversed.size() == pnList.size())
            return Collections.singleton(new Pair<>(new HashMap<>(mapping), score));
        //if (index == nodeList.size()) {
//            _mappingsByScore.put(mapping, score);
        //    return Collections.singleton(new Pair<>(new HashMap<>(mapping), score));
//            return score;
        //}
        index = index % pnList.size();
        PatternNode node = pnList.get(index);

        while (ObjectUtil.findPtKeyInSet(traversed, node) != null) {
            index++;
            index = index % pnList.size();
            node = pnList.get(index);
        }
        if (ObjectUtil.findPtKeyInSet(simScoreMap.keySet(), node.getParent()) != null && ObjectUtil.findPtKeyInSet(traversed, node.getParent()) == null)
            return matchTopTiedNodeBySimScorePattern(traversed, pnList, index + 1, cgList, score, mapping, simScoreMap);
        traversed.add(node);

        if (simScoreMap.containsKey(node)) {

            List<CtWrapper> topNCandidates = new ArrayList<>();

            simScoreMap.replace(node, sortByValue(simScoreMap.get(node)));
            double bestScore = -1;
            for (CtWrapper candidate : simScoreMap.get(node).keySet()) {
                double simScore = simScoreMap.get(node).get(candidate);
                if (simScore <= 0 || simScore < bestScore) {
                    break;
                }
                if (simScore >= bestScore && isMatch(node, candidate, mapping)) {
                    topNCandidates.add(candidate);
                    bestScore = simScore;
                }
            }

            Set<Pair<Map<PatternNode, CtWrapper>, Double>> allPairs = new HashSet<>();
            if (!topNCandidates.isEmpty()) {
                for (CtWrapper bestSim : topNCandidates) {
                    List<CtWrapper> nodeListCopy = new ArrayList<>(cgList);
                    Map<PatternNode, CtWrapper> mappingCopy = new HashMap<>(mapping);
                    Map<PatternNode, Map<CtWrapper, Double>> simScoreMapCopy = deepCopySimScoreMap(simScoreMap);

                    double simScore = simScoreMap.get(node).get(bestSim);

                    nodeListCopy.remove(bestSim);
                    mappingCopy.put(node, bestSim);
                    simScoreMapCopy.remove(node);

                    for (Map<CtWrapper, Double> scoreMap : simScoreMapCopy.values()) {
                        scoreMap.remove(bestSim);
                    }

                    allPairs.addAll(matchTopTiedNodeBySimScorePattern(traversed, pnList, index + 1, nodeListCopy, score + simScore, mappingCopy, simScoreMapCopy));
                }
                return allPairs;
            } else {
                return matchTopTiedNodeBySimScorePattern(traversed, pnList, index + 1, cgList, score, mapping, simScoreMap);
            }
        } else {
            return matchTopTiedNodeBySimScorePattern(traversed, pnList, index + 1, cgList, score, mapping, simScoreMap);
        }
    }


    public static Set<Pair<Map<PatternNode, CtWrapper>, Double>> matchTopTiedNodeBySimScorePattern(List<PatternNode> pnList, int index, List<CtWrapper> cgList, double score, Map<PatternNode, CtWrapper> mapping, Map<PatternNode, Map<CtWrapper, Double>> simScoreMap) {
        if (index == pnList.size()) {
            return Collections.singleton(new Pair<>(new HashMap<>(mapping), score));
        }

        PatternNode node = pnList.get(index);
        if (simScoreMap.containsKey(node)) {

            List<CtWrapper> topNCandidates = new ArrayList<>();

            simScoreMap.replace(node, sortByValue(simScoreMap.get(node)));
            double bestScore = -1;
            for (CtWrapper candidate : simScoreMap.get(node).keySet()) {
                double simScore = simScoreMap.get(node).get(candidate);
                if (simScore <= 0 || simScore < bestScore) {
                    break;
                }
                if (simScore >= bestScore && isMatch(node, candidate, mapping)) {
                    topNCandidates.add(candidate);
                    bestScore = simScore;
                }
            }

            Set<Pair<Map<PatternNode, CtWrapper>, Double>> allPairs = new HashSet<>();
            if (!topNCandidates.isEmpty()) {
                for (CtWrapper bestSim : topNCandidates) {
                    List<CtWrapper> nodeListCopy = new ArrayList<>(cgList);
                    Map<PatternNode, CtWrapper> mappingCopy = new HashMap<>(mapping);
                    Map<PatternNode, Map<CtWrapper, Double>> simScoreMapCopy = deepCopySimScoreMap(simScoreMap);

                    double simScore = simScoreMap.get(node).get(bestSim);

                    nodeListCopy.remove(bestSim);
                    mappingCopy.put(node, bestSim);
                    simScoreMapCopy.remove(node);

                    for (Map<CtWrapper, Double> scoreMap : simScoreMapCopy.values()) {
                        scoreMap.remove(bestSim);
                    }

                    allPairs.addAll(matchTopTiedNodeBySimScorePattern(pnList, index + 1, nodeListCopy, score + simScore, mappingCopy, simScoreMapCopy));
                }
                return allPairs;
            } else {
                return matchTopTiedNodeBySimScorePattern(pnList, index + 1, cgList, score, mapping, simScoreMap);
            }
        } else {
            return matchTopTiedNodeBySimScorePattern(pnList, index + 1, cgList, score, mapping, simScoreMap);
        }
    }


    // Helper function to create a deep copy of the simScoreMap
    private static Map<PatternNode, Map<CtWrapper, Double>> deepCopySimScoreMap(Map<PatternNode, Map<CtWrapper, Double>> original) {
        Map<PatternNode, Map<CtWrapper, Double>> copy = new HashMap<>();
        for (Map.Entry<PatternNode, Map<CtWrapper, Double>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copy;
    }

    private static Map<CtWrapper, Map<CtWrapper, Double>> deepCopyCtSimScoreMap(Map<CtWrapper, Map<CtWrapper, Double>> original) {
        Map<CtWrapper, Map<CtWrapper, Double>> copy = new HashMap<>();
        for (Map.Entry<CtWrapper, Map<CtWrapper, Double>> entry : original.entrySet()) {
            copy.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        return copy;
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
        if (nodeA.isVirtual() && nodeB.isVirtual() &&
                !Objects.equals(((CtVirtualElement) nodeA.getCtElementImpl()).getLocationInParent(), ((CtVirtualElement) nodeB.getCtElementImpl()).getLocationInParent()))
            return false;
        if (nodeA.getCtElementImpl() instanceof CtTypeReferenceImpl && nodeB.getCtElementImpl() instanceof CtTypeReferenceImpl &&
                !Objects.equals((nodeA.getCtElementImpl()).getRoleInParent(), (nodeB.getCtElementImpl()).getRoleInParent()))
            return false;
        if (nodeA.getCtElementImpl().isActionRelated() != nodeB.getCtElementImpl().isActionRelated())
            return false;
        // TODO: other comparing rules
        for (Map.Entry<CtWrapper, CtWrapper> entry : mapping.entrySet()) {
            CtWrapper key = entry.getKey();
            CtWrapper value = entry.getValue();
            // key -> nodeA and value -> nodeB
            if (key.equals(new CtWrapper((CtElementImpl) nodeA.getCtElementImpl().getParent())) != value.equals(new CtWrapper((CtElementImpl) nodeB.getCtElementImpl().getParent())))
                return false;
            if (nodeA.getCtElementImpl().hasDependencyRelation(key.getCtElementImpl()) != nodeB.getCtElementImpl().hasDependencyRelation(value.getCtElementImpl()))
                return false;
            if (key.getCtElementImpl().hasDependencyRelation(nodeA.getCtElementImpl()) != value.getCtElementImpl().hasDependencyRelation(nodeB.getCtElementImpl()))
                return false;
            if (ObjectUtil.hasEdge(nodeA.getCtElementImpl(), key.getCtElementImpl()) != ObjectUtil.hasEdge(nodeB.getCtElementImpl(), value.getCtElementImpl()))
                return false;
        }
        return true;
    }

    private static boolean isMatch(PatternNode pn, CtWrapper cgn, Map<PatternNode, CtWrapper> mapping) {
        // compare type
        Class pType = (pn.getAttribute("nodeType") != null) ? (Class) pn.getAttribute("nodeType").getTag() : null;
        Class gType = Attribute.computeNodeType(cgn);
        if (!sameType(pn, cgn) || (pType.equals(CtBlockImpl.class)) != (gType.equals(CtBlockImpl.class)))
            return false;
        // other rules
        for (Map.Entry<PatternNode, CtWrapper> entry : mapping.entrySet()) {
            PatternNode key = entry.getKey();
            CtWrapper value = entry.getValue();
            // key -> pn and value -> cgn
            if (pn.hasInEdge(key, PatternEdge.EdgeType.AST) != value.equals(new CtWrapper((CtElementImpl) cgn.getCtElementImpl().getParent())))
                return false;
            if (pn.hasInEdge(key, PatternEdge.EdgeType.CONTROL_DEP) != cgn.getCtElementImpl().hasInEdge(value.getCtElementImpl(), Edge.EdgeType.CONTROL_DEP))
                return false;
            if (pn.hasInEdge(key, PatternEdge.EdgeType.DATA_DEP) != cgn.getCtElementImpl().hasInEdge(value.getCtElementImpl(), Edge.EdgeType.DATA_DEP))
                return false;
            if (pn.hasInEdge(key, PatternEdge.EdgeType.DEF_USE) != cgn.getCtElementImpl().hasInEdge(value.getCtElementImpl(), Edge.EdgeType.DEF_USE))
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
        if (ctA.getClass().equals(ctB.getClass()))
            return true;
        Type ancA = ctA.getClass().getAnnotatedSuperclass().getType();
        Type ancB = ctB.getClass().getAnnotatedSuperclass().getType();
        if (sameAncType(ctA.getClass(), ctB.getClass()) && (ctA instanceof CtBlockImpl) == (ctB instanceof CtBlockImpl)) {
            // same ancestor type
            return true;
        } else if (similarType(ancA, ancB)) {
            return true;
        } else if (isVar(ctA) && isVar(ctB)) {
            return true;
        } else if (isVarRef(ctA) && isVarRef(ctB)) {
            return true;
        } else {
            // special case
            String aStr = ctA.prettyprint();
            String bStr = ctB.prettyprint();
            return (aStr.equals("==") && bStr.equals("!=")) || (aStr.equals("!=") && bStr.equals("=="));
        }
    }

    public static boolean sameType(PatternNode pn, CtWrapper cgn) {
        Class a = (pn.getAttribute("nodeType") != null) ? (Class) pn.getAttribute("nodeType").getTag() : null;
        Class b = Attribute.computeNodeType(cgn);
        if (a.equals(b))
            return true;
        Type ancA = a.getAnnotatedSuperclass().getType();
        Type ancB = b.getAnnotatedSuperclass().getType();
        if (sameAncType(a, b) && (a.equals(CtBlockImpl.class) == b.equals(CtBlockImpl.class))) {
            // same ancestor type
            return true;
        } else if (similarType(ancA, ancB)) {
            return true;
        } else if (isVar(pn) && isVar(cgn)) {
            return true;
        } else if (isVarRef(pn) && isVarRef(cgn)) {
            return true;
        } else {
            // special case
            String aStr = pn.getAttribute("value") != null ? pn.getAttribute("value").getTag().toString() : null;
            String bStr = cgn.toLabelString();
            return ("==".equals(aStr) && "!=".equals(bStr)) || ("!=".equals(aStr) && "==".equals(bStr));
        }
    }

    private static boolean sameAncType(Class<? extends CtElementImpl> a, Class<? extends CtElementImpl> b) {
        Set<Class> aClassList = new LinkedHashSet<>();
        Set<Class> bClassList = new LinkedHashSet<>();
        while (a.getSuperclass() != null) {
            if (!a.getPackageName().contains("spoon.support.reflect") || a.equals(CtCodeElementImpl.class) || a.equals(CtReferenceImpl.class) || a.equals(CtElementImpl.class))
                break;
            aClassList.add(a);
            a = (Class<? extends CtElementImpl>) a.getSuperclass();
        }
        while (b.getSuperclass() != null) {
            if (b.equals(CtCodeElementImpl.class) || b.equals(CtReferenceImpl.class) || b.equals(CtElementImpl.class))
                break;
            bClassList.add(b);
            b = (Class<? extends CtElementImpl>) b.getSuperclass();
        }
        return CollectionUtils.intersection(aClassList, bClassList).size() > 0;
    }

    public static boolean isVar(Object ct) {
        boolean isVirtualName = isVirtualName(ct);
        String clazz = ct.getClass().getSimpleName();
        String[] varRelate = {
                "CtFieldReadImpl", "CtFieldWriteImpl", "CtArrayReadImpl", "CtArrayWriteImpl",
                "CtVariableReadImpl", "CtVariableWriteImpl",
                "CtThisAccessImpl"
        };
        return Arrays.asList(varRelate).contains(clazz) || isVirtualName;
    }

    private static boolean isVirtualName(Object obj) {
        boolean isVirtualName = false;
        if (obj instanceof CtElementImpl)
            isVirtualName = obj instanceof CtVirtualElement && ((CtVirtualElement) obj).getLocationInParent().contains("VAR_NAME");
        else if (obj instanceof PatternNode) {
            Class nodeType = ((PatternNode) obj).getAttribute("nodeType") != null ? (Class) ((PatternNode) obj).getAttribute("nodeType").getTag() : null;
            String location = ((PatternNode) obj).getAttribute("locationInParent") != null ? (String) ((PatternNode) obj).getAttribute("locationInParent").getTag() : null;
            isVirtualName = (nodeType != null) && (location != null) && nodeType.equals(CtVirtualElement.class) && location.contains("VAR_NAME");
        }
        return isVirtualName;
    }

    public static boolean isVarRef(Object ct) {
        boolean isVirtualName = isVirtualName(ct);
        String clazz = ct.getClass().getSimpleName();
        String[] varRelate = {
                "CtCatchVariableReferenceImpl", "CtFieldReferenceImpl", "CtLocalVariableReferenceImpl", "CtParameterReferenceImpl"};
        return Arrays.asList(varRelate).contains(clazz) || isVirtualName;
    }

    private static boolean similarType(Type ancA, Type ancB) {
        String tA = ancA.getTypeName();
        String tB = ancB.getTypeName();
        return (tA.contains("CtExpressionImpl") && tB.contains("CtFieldAccessImpl")) || (tA.contains("CtFieldAccessImpl") && tB.contains("CtExpressionImpl"));
    }

    private static double calSimScore(CtElementImpl a, CtElementImpl b) {
        double text_score = calContextSim(a.prettyprint(), b.prettyprint());
        double parent_score = 0;
        if (a.getParent() != null && b.getParent() != null)
            parent_score = calContextSim(a.getParent().prettyprint(), b.getParent().prettyprint());
        else if (a.getParent() == null && b.getParent() == null)
            parent_score = 1.0;
        double role_score = 0;
        String aRole = a instanceof CtVirtualElement ? ((CtVirtualElement) a).getLocationInParent() : (a.getRoleInParent() == null ? null : a.getRoleInParent().toString());
        String bRole = b instanceof CtVirtualElement ? ((CtVirtualElement) b).getLocationInParent() : (b.getRoleInParent() == null ? null : b.getRoleInParent().toString());
        role_score = calContextSim(aRole, bRole);

        double score = (text_score + parent_score + role_score) / 3;
        return score;
    }

    private static Map<CtWrapper, Map<CtWrapper, Double>> calSimScore(List<CtWrapper> nodeList, List<CtWrapper> nodeListComp) {
        Map<CtWrapper, Map<CtWrapper, Double>> result = new LinkedHashMap<>();
        for (CtWrapper nodeA : nodeList) {
            if (!result.containsKey(nodeA)) {
                result.put(nodeA, new LinkedHashMap<>());
            } else if (result.get(nodeA).size() == nodeListComp.size()) {
                continue;
            }
            for (CtWrapper nodeB : nodeListComp) {
                if (result.get(nodeA).containsKey(nodeB)) continue;
                double score = calSimScore(nodeA.getCtElementImpl(), nodeB.getCtElementImpl());
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

    public static Map<PatternNode, Map<CtWrapper, Double>> calSimScorePattern(List<PatternNode> pNodeList, List<CtWrapper> cgNodeList) {
        Map<PatternNode, Map<CtWrapper, Double>> result = new LinkedHashMap<>();
        for (PatternNode nodeA : pNodeList) {
            if (!result.containsKey(nodeA)) {
                result.put(nodeA, new LinkedHashMap<>());
            } else if (result.get(nodeA).size() == cgNodeList.size()) {
                continue;
            }
            for (CtWrapper nodeB : cgNodeList) {
                if (result.get(nodeA).containsKey(nodeB)) continue;
                double score = calContextSimPattern(nodeA, nodeB);
                result.get(nodeA).put(nodeB, score);
            }
            result.replace(nodeA, sortByValue(result.get(nodeA)));
        }
        return result;
    }

    private static double calContextSimPattern(PatternNode pn, CtWrapper cgn) {
        List<Double> scores = new ArrayList<>();
        for (Attribute a : pn.getComparedAttributes()) {
            if (a.isAbstract())
                continue;
            Object comp = "";
            switch (a.getName()) {
                case "locationInParent":
                    comp = Attribute.computeLocationInParent(cgn);
                    break;
                case "nodeType":
                    comp = Attribute.computeNodeType(cgn);
                    if (!a.getTag().equals(comp)) {
                        comp = Attribute.computeNodeType2(cgn);
                        if (!a.getTag().equals(comp))
                            comp = Attribute.computeNodeType3(cgn);
                    }
                    break;
                case "value":
                    comp = Attribute.computeValue(cgn);
                    break;
                case "value2":
                    comp = Attribute.computeValue2(cgn);
                    break;
                case "valueType":
                    comp = Attribute.computeValueType(cgn);
                    break;
            }

            scores.add(calContextSim(String.valueOf(a.getTag()), comp == null ? null : String.valueOf(comp)));

        }
        return scores.isEmpty() ? 0.0 : scores.stream().mapToDouble(n -> n).average().getAsDouble();
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
