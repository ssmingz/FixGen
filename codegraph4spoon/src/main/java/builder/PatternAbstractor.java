package builder;

import model.CodeGraph;
import model.CtWrapper;
import model.pattern.Attribute;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;

import java.util.*;

public class PatternAbstractor {
    int THRESHOLD = 0;

    public PatternAbstractor(int thr) {
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

                attr1.computeLocationInParent(n, g);
                attr2.computeNodeType(n, g);
                attr3.computeValue(n, g);
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
    private void filterAttributes(Pattern pat, int threshold) {
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
            if (pn.getComparedAttributes().stream().anyMatch(a -> !a.isAbstract())) {
                // remove the node and its attached edges
                pn.inEdges().removeIf(Objects::nonNull);
                pn.outEdges().removeIf(Objects::nonNull);
                it.remove();
            }
        }
        // recheck for unreached nodes
        it = pat.getNodeSet().iterator();
        while (it.hasNext()) {
            PatternNode pn = it.next();
            boolean canReach = false;
            for (PatternEdge e : pn.inEdges()) {
                if (pat.getNodes().contains(e.getSource())) {
                    canReach = true;
                    break;
                }
            }
            if (canReach) continue;
            for (PatternEdge e : pn.outEdges()) {
                if (pat.getNodes().contains(e.getTarget())) {
                    canReach = true;
                    break;
                }
            }
            if (!canReach) it.remove();
        }
        // edge
    }

}
