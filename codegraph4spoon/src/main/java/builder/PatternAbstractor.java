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

    public static Pattern buildWithoutAbstract(Pattern pat) {
        collectAttributes(pat);
        voteAttributes(pat);
        return pat;
    }

    /**
     * collect feature values for each element in a pattern
     */
    public static void collectAttributes(Pattern pat) {
        // node attributes
        for (PatternNode pn : pat.getNodes()) {
            Attribute attr1 = new Attribute("locationInParent");
            Attribute attr2 = new Attribute("nodeType");
            Attribute attr3 = new Attribute("value");
            Attribute attr4 = new Attribute("value2");  // replace name with type
            Attribute attr5 = new Attribute("valueType");  // type of the value
            for (Map.Entry<CtWrapper, CodeGraph> entry : pn.getInstance().entrySet()) {
                CtWrapper n = entry.getKey();
                CodeGraph g = entry.getValue();

                attr1.addValue(Attribute.computeLocationInParent(n), g);
                attr2.addValue(Attribute.computeNodeType(n), g);
                attr3.addValue(Attribute.computeValue(n), g);
                attr4.addValue(Attribute.computeValue2(n), g);
                attr5.addValue(Attribute.computeValueType(n), g);

                pn.position.addValue(Attribute.computePosition(n), g);
                pn.listSize.addValue(Attribute.computeListSize(n), g);
                pn.listIndex.addValue(Attribute.computeListIndex(n), g);
                pn.implicit.addValue(Attribute.computeImplicit(n), g);
            }
            pn.setComparedAttribute(attr1);
            pn.setComparedAttribute(attr2);
            pn.setComparedAttribute(attr3);
            pn.setComparedAttribute(attr4);
            pn.setComparedAttribute(attr5);
//            pn.setComparedAttribute(pn.position);
//            pn.setComparedAttribute(pn.listSize);
//            pn.setComparedAttribute(pn.listIndex);
//            pn.setComparedAttribute(pn.implicit);
        }
    }

    /**
     * sort features by instances size
     */
    public static void voteAttributes(Pattern pat) {
        // node
        for (PatternNode pn : pat.getNodes()) {
            for (Attribute a : pn.getComparedAttributes()) {
                vote4Attribute(a);
            }
            // other features
            vote4Attribute(pn.position);
            vote4Attribute(pn.listSize);
            vote4Attribute(pn.listIndex);
            vote4Attribute(pn.implicit);
        }
    }

    public static void vote4Attribute(Attribute attr) {
        List<Map.Entry<Object, Integer>> sorted = new ArrayList<>(attr.sort().entrySet());
        if (sorted.size() != 0)
            attr.setTag(sorted.get(0).getKey());
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
            pn.getComparedAttributes().forEach(a -> {
                if (a.getSupport(a.getTag()) < threshold)
                    a.setAbstract(true);
            });
            if (pn.getAttribute("nodeType").isAbstract()) {
                Attribute nodeTypeAttr = pn.getAttribute("nodeType");
                nodeTypeAttr.clear();
                for (Map.Entry<CtWrapper, CodeGraph> entry : pn.getInstance().entrySet()) {
                    CtWrapper n = entry.getKey();
                    CodeGraph g = entry.getValue();
                    nodeTypeAttr.addValue(Attribute.computeNodeType2(n), g);
                }
                vote4Attribute(nodeTypeAttr);
                if (nodeTypeAttr.getSupport(nodeTypeAttr.getTag())<threshold) {
                    nodeTypeAttr.clear();
                    for (Map.Entry<CtWrapper, CodeGraph> entry : pn.getInstance().entrySet()) {
                        CtWrapper n = entry.getKey();
                        CodeGraph g = entry.getValue();
                        nodeTypeAttr.addValue(Attribute.computeNodeType3(n), g);
                    }
                    vote4Attribute(nodeTypeAttr);
                    if (nodeTypeAttr.getSupport(nodeTypeAttr.getTag())<threshold)
                        nodeTypeAttr.setAbstract(true);
                }
            }
            if (pn.getComparedAttributes().stream().allMatch(Attribute::isAbstract)) {
                Iterator<PatternEdge> eItr = pn.inEdges().iterator();
                while (eItr.hasNext()) {
                    PatternEdge pe = eItr.next();
                    pe.setAbstract(true);
                }
                eItr = pn.outEdges().iterator();
                while (eItr.hasNext()) {
                    PatternEdge pe = eItr.next();
                    pe.setAbstract(true);
                }
                pn.setAbstract(true);
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
                    pe.setAbstract(true);
                }
                eItr = pn.outEdges().iterator();
                while (eItr.hasNext()) {
                    PatternEdge pe = eItr.next();
                    pe.setAbstract(true);
                }
                pn.setAbstract(true);
            }
        }
    }

    private void extendOneEdge(PatternNode start, Set<PatternNode> reached) {
        if (reached.contains(start))
            return;
        reached.add(start);
        for (PatternEdge ie : start.inEdges()) {
            if (!ie.isAbstract())
                extendOneEdge(ie.getSource(), reached);
        }
        for (PatternEdge oe : start.outEdges()) {
            if (!oe.isAbstract())
                extendOneEdge(oe.getTarget(), reached);
        }
    }

}
