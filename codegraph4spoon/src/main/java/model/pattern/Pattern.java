package model.pattern;

import builder.PatternExtractor;
import codegraph.Edge;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.ActionNode;
import org.javatuples.Pair;

import java.io.Serializable;
import java.util.*;

public class Pattern implements Serializable {
    private Set<PatternNode> _patternNodes = new LinkedHashSet<>();
    private Set<PatternEdge> _patternEdges = new LinkedHashSet<>();
    private PatternNode _start;
    private Map<CtWrapper, PatternNode> _patternNodeByNode = new LinkedHashMap<>();
    private Set<String> _attributes = new LinkedHashSet<>();
    private Set<PatternNode> _actions = new HashSet<>();  // srcNode:actionNode

    private HashMap<Integer, Object> _idPattern = new LinkedHashMap<>();
    private int _idCounter = -1;

    public Pattern(PatternNode pNode, CtWrapper node) {
        _start = pNode;
        addNode(pNode, node);
    }

    public HashMap<Integer, Object> getIdPattern() { return _idPattern; }

    public Set<PatternNode> getNodeSet() { return _patternNodes; }

    public Set<PatternEdge> getEdgeSet() { return _patternEdges; }

    public Set<PatternNode> getActionSet() { return _actions; }

    public void addNode(PatternNode pNode, CtWrapper node) {
        _patternNodes.add(pNode);
        _idPattern.put(++_idCounter, pNode);
        _patternNodeByNode.put(node, pNode);
        // record actions
        if (node.getCtElementImpl() instanceof ActionNode) {
            _actions.add(pNode);
        }
    }

    public void addEdge(PatternNode src, PatternNode target, PatternEdge.EdgeType type, Edge e, CodeGraph cg) {
        PatternEdge edge = findEdge(src, target, type);
        if (edge == null) {
            PatternEdge anEdge = new PatternEdge(src, target, type);
            anEdge.addInstance(e, cg);
            _patternEdges.add(anEdge);
            _idPattern.put(++_idCounter, anEdge);
        } else {
            edge.addInstance(e, cg);
        }
    }

    public boolean hasEdge(PatternNode src, PatternNode tar, PatternEdge.EdgeType type) {
        for (PatternEdge e : _patternEdges) {
            if (e.getSource().equals(src) && e.getTarget().equals(tar) && e.type == type)
                return true;
        }
        return false;
    }

    public PatternEdge findEdge(PatternNode src, PatternNode tar, PatternEdge.EdgeType type) {
        for (PatternEdge e : _patternEdges) {
            if (e.getSource().equals(src) && e.getTarget().equals(tar) && e.type == type)
                return e;
        }
        return null;
    }

    public Map<CtWrapper, PatternNode> getNodeMapping() {
        return _patternNodeByNode;
    }

    // TODO: set name for a pattern
    public String getPatternName() {
        return "Pattern";
    }

    public List<PatternNode> getNodes() {
        return new ArrayList<>(_patternNodes);
    }

    public PatternNode getStart() {
        return _start;
    }

    public void deleteActionRelated() {
        Iterator<PatternNode> nItr = _patternNodes.iterator();
        while(nItr.hasNext()) {
            PatternNode pn = nItr.next();
            // set new pattern.start
            if(pn.isPatternStart()) {
                for(PatternEdge ie : pn.inEdges()) {
                    if (ie.type == PatternEdge.EdgeType.ACTION) {
                        _start = ie.getSource();
                        break;
                    }
                }
            }
            if(pn.isActionRelated()) {
//                // delete edges
//                Iterator<PatternEdge> eItr = pn.inEdges().iterator();
//                while(eItr.hasNext()) {
//                    eItr.next();
//                    eItr.remove();
//                }
//                eItr = pn.outEdges().iterator();
//                while(eItr.hasNext()) {
//                    eItr.next();
//                    eItr.remove();
//                }
                // delete node
                nItr.remove();
            }
        }
    }

    public Pair<Map<PatternNode, CtWrapper>, Double> compareCG(CodeGraph aGraph) {
        Map<PatternNode, Map<CtWrapper, Double>> orderBySimScore = PatternExtractor.calSimScorePattern(_patternNodes, aGraph.getNodes());
        Map<PatternNode, CtWrapper> mapping = new LinkedHashMap<>();
        double score = PatternExtractor.matchBySimScorePattern(Arrays.asList(_patternNodes.toArray(new PatternNode[0])), 0, aGraph.getNodes(), 0, mapping, orderBySimScore);
        return new Pair<>(mapping, score);
    }

    public PatternNode getPatternNodeByCGNode(CtWrapper n) {
        for (Map.Entry<CtWrapper, PatternNode> entry : _patternNodeByNode.entrySet()) {
            if (entry.getKey().getCtElementImpl() == n.getCtElementImpl()) {
                return entry.getValue();
            }
        }
        return null;
    }

    public PatternNode getPatternNodeByCGElementId(String graphName, int id) {
        for (PatternNode pn : _patternNodes) {
            for (CtWrapper n : pn.getInstance().keySet()) {
                if (n.getCtElementImpl()._graphId == id && n.getCtElementImpl()._graphName.equals(graphName))
                    return pn;
            }
        }
        System.out.printf("[warn]pattern vertex (%d# in codegraph@%s) not exist\n", id, graphName);
        return null;
    }

    public PatternEdge getPatternEdgeByCGElementId(String graphName, int id) {
        for (PatternEdge pe : _patternEdges) {
            for (Edge n : pe.getInstance().keySet()) {
                if (n._graphId == id && n._graphName.equals(graphName))
                    return pe;
            }
        }
        System.out.printf("[warn]pattern edge (%d# in codegraph@%s) not exist\n", id, graphName);
        return null;
    }

    public PatternEdge getPatternEdgeByCGElementId(String graphName, int v1, int v2) {
        for (PatternEdge pe : _patternEdges) {
            for (Edge n : pe.getInstance().keySet()) {
                if (n._graphName.equals(graphName) &&
                        ((n.getSource()._graphId == v1 && n.getTarget()._graphId == v2) ||
                        (n.getTarget()._graphId == v1 && n.getSource()._graphId == v2)))
                    return pe;
            }
        }
        System.out.printf("[warn]pattern edge (vId %d# and vId %d# in codegraph@%s) not exist\n", v1, v2, graphName);
        return null;
    }
}