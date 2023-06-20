package builder;

import model.CodeGraph;
import model.CtWrapper;
import model.pattern.Attribute;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;

import java.util.*;
import java.util.stream.Collectors;

public class PatternAbstractor {
    double THRESHOLD = 0;

    public PatternAbstractor(double thr) {
        THRESHOLD = thr;
    }

    public Pattern abstractPattern(Pattern pat) {
        collectAttributes(pat);
        voteAttributes(pat);
        filterAttributes(pat, THRESHOLD);
        return pat;
    }

    /**
     * collect feature values for each element in a pattern
     */
    private void collectAttributes(Pattern pat) {
        // node attributes
        for (PatternNode pn : pat.getNodes()) {
            Attribute attr1 = new Attribute("locationInParent");
            Attribute attr2 = new Attribute("nodeType");
            Attribute attr3 = new Attribute("value");
            for (Map.Entry<CtWrapper, CodeGraph> entry : pn.getInstance().entrySet()) {
                CtWrapper n = entry.getKey();
                CodeGraph g = entry.getValue();

                attr1.addValue(Attribute.computeLocationInParent(n), g);
                attr2.addValue(Attribute.computeNodeType(n), g);
                attr3.addValue(Attribute.computeValue(n), g);
            }
            pn.setComparedAttribute(attr1);
            pn.setComparedAttribute(attr2);
            pn.setComparedAttribute(attr3);
        }
        // edge attributes
    }

    /**
     * sort features by instances size
     */
    private void voteAttributes(Pattern pat) {
        // node
        for (PatternNode pn : pat.getNodes()) {
            for (Attribute a : pn.getComparedAttributes()) {
                List<Map.Entry<String, Integer>> sorted = new ArrayList<>(a.sort().entrySet());
                if (sorted.size() != 0)
                    a.setTag(sorted.get(0).getKey());
            }
        }
        // edge
    }

    /**
     * remove features by threshold
     */
    private void filterAttributes(Pattern pat, double threshold) {
        // node
        Iterator<PatternNode> it = pat.getNodeSet().iterator();
        while(it.hasNext()){
            PatternNode pn = it.next();
            // remove the attribute by setAbstract instead removing
//            pn.getComparedAttributes().removeIf(a -> a.getSupport(a.getTag()) < threshold || a.getTag().equals("?"));
            pn.getComparedAttributes().forEach(a -> {
                if (a.getSupport(a.getTag()) < threshold || a.getTag().equals("?"))
                    a.setAbstract(true);
            });
            if (pn.getComparedAttributes().stream().allMatch(Attribute::isAbstract)) {
                Iterator<PatternEdge> eItr = pn.inEdges().iterator();
                while (eItr.hasNext()) {
                    PatternEdge pe = eItr.next();
                    pe.getSource().outEdges().remove(pe);
                    eItr.remove();
                }
                eItr = pn.outEdges().iterator();
                while (eItr.hasNext()) {
                    PatternEdge pe = eItr.next();
                    pe.getTarget().inEdges().remove(pe);
                    eItr.remove();
                }
                pn.inEdges().removeIf(Objects::nonNull);
                pn.outEdges().removeIf(Objects::nonNull);
                it.remove();
            }
        }
        // recheck for unreached nodes with action
        Set<PatternNode> reached = new HashSet<>();
        Set<PatternNode> actions = pat.getNodeSet().stream().filter(pn -> pn.getAttribute("locationInParent")!=null
                        && "ACTION".equals(pn.getAttribute("locationInParent").getTag())).collect(Collectors.toSet());
        for (PatternNode start : actions) {
            extendOneEdge(start, reached);
        }
        it = pat.getNodeSet().iterator();
        while (it.hasNext()) {
            PatternNode pn = it.next();
            if (!reached.contains(pn)) {
                Iterator<PatternEdge> eItr = pn.inEdges().iterator();
                while (eItr.hasNext()) {
                    PatternEdge pe = eItr.next();
                    pe.getSource().outEdges().remove(pe);
                    eItr.remove();
                }
                eItr = pn.outEdges().iterator();
                while (eItr.hasNext()) {
                    PatternEdge pe = eItr.next();
                    pe.getTarget().inEdges().remove(pe);
                    eItr.remove();
                }
                pn.inEdges().removeIf(Objects::nonNull);
                pn.outEdges().removeIf(Objects::nonNull);
                it.remove();
            }
        }
        int i=1;
    }

    private void extendOneEdge(PatternNode start, Set<PatternNode> reached) {
        if (reached.contains(start))
            return;
        reached.add(start);
        for (PatternEdge ie : start.inEdges()) {
            extendOneEdge(ie.getSource(), reached);
        }
        for (PatternEdge oe : start.outEdges()) {
            extendOneEdge(oe.getTarget(), reached);
        }
    }

}
