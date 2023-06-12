package model.pattern;

import builder.PatternExtractor;
import codegraph.Edge;
import model.CodeGraph;
import model.CtWrapper;
import org.javatuples.Pair;

import java.util.*;

public class Pattern {
    private Set<PatternNode> _patternNodes = new LinkedHashSet<>();
    private Set<PatternEdge> _patternEdges = new LinkedHashSet<>();
    private PatternNode _start;
    private Map<CtWrapper, PatternNode> _patternNodeByNode = new LinkedHashMap<>();
    private Set<String> _attributes = new LinkedHashSet<>();

    private HashMap<Integer, Object> _idPattern = new LinkedHashMap<>();
    private int _idCounter = -1;

    public Pattern(PatternNode aNode) {
        _start = aNode;
        _patternNodes.add(aNode);
    }

    public HashMap<Integer, Object> getIdPattern() { return _idPattern; }

    public Set<PatternNode> getNodeSet() { return _patternNodes; }

    public Set<PatternEdge> getEdgeSet() { return _patternEdges; }

    public void addNode(PatternNode pNode, CtWrapper node) {
        _patternNodes.add(pNode);
        _idPattern.put(++_idCounter, pNode);
        _patternNodeByNode.put(node, pNode);
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
                // delete edges
                Iterator<PatternEdge> eItr = pn.inEdges().iterator();
                while(eItr.hasNext()) {
                    eItr.next();
                    eItr.remove();
                }
                eItr = pn.outEdges().iterator();
                while(eItr.hasNext()) {
                    eItr.next();
                    eItr.remove();
                }
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
}