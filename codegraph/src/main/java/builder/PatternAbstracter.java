package builder;

import model.CodeGraph;
import model.graph.node.Node;
import model.pattern.Attribute;
import model.pattern.Pattern;
import model.pattern.PatternNode;

import java.util.*;

public class PatternAbstracter {
    int _threshold = 0;

    public PatternAbstracter(int thr) {
        _threshold = thr;
    }

    public int getThreshold() {
        return _threshold;
    }

    public Pattern abstractPattern(Pattern pat, int threshold) {
        collectAttributes(pat);
        voteAttributes(pat);
        filterAttributes(pat, threshold);
        return pat;
    }

    /**
     * remove features by threshold
     * @param pat
     */
    private void filterAttributes(Pattern pat, int threshold) {
        // node
        Iterator<PatternNode> it = pat.getNodeSet().iterator();
        while(it.hasNext()){
            PatternNode pn = it.next();
            // remove the attribute
            pn.getComparedAttributes().removeIf(a -> a.getSupport(a.getTag()) < threshold || a.getTag().equals("?"));
            if (pn.getComparedAttributes().size() == 0 || isNoMeaningful(pn)) {
                // remove the node and its attached edges
                pn.inEdges().removeIf(Objects::nonNull);
                pn.outEdges().removeIf(Objects::nonNull);
                it.remove();
            }
        }
        // edge
    }

    private boolean isNoMeaningful(PatternNode pn) {
        // 1. when no locationInParent in attribute
        if (pn.getAttribute("locationInParent") == null) {
            return true;
        }
        // 2. when no valueName if it is expression
        if (pn.getAttribute("nodeTypeHighLevel") != null) {
            if (pn.getAttribute("nodeTypeHighLevel").getTag().equals("expression")
                    && pn.getAttribute("valueName") == null && pn.getAttribute("valueType") == null) {
                return true;
            }
        }
        return false;
    }

    /**
     * collect feature values for each element in a pattern
     * @param pat
     */
    private void collectAttributes(Pattern pat) {
        // node attributes
        for (PatternNode pn : pat.getNodes()) {
            Attribute attr1 = new Attribute("locationInParent");
            Attribute attr2 = new Attribute("nodeType");
            Attribute attr3 = new Attribute("nodeTypeHighLevel");
            Attribute attr4 = new Attribute("valueName");
            Attribute attr5 = new Attribute("valueType");
            for (Map.Entry<Node,CodeGraph> entry : pn.getInstance().entrySet()) {
                Node n = entry.getKey();
                CodeGraph cg = entry.getValue();

                attr1.computeLocationInParent(n);
                attr2.computeNodeType(n);
                attr3.computeNodeTypeHighLevel(n);
                attr4.computeValueName(n, cg);
                attr5.computeValueType(n, cg);
            }
            pn.setComparedAttribute(attr1);
            pn.setComparedAttribute(attr2);
            pn.setComparedAttribute(attr3);
            pn.setComparedAttribute(attr4);
            pn.setComparedAttribute(attr5);

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
                if (sorted.size() == 0)
                    continue;
                else
                    a.setTag(sorted.get(0).getKey());
            }
        }
        // edge
    }
    
    
}
