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
            Attribute attr3 = new Attribute("nodeType2");  // a more general one, e.g. WhileStmt and IfStmt --> Stmt
            Attribute attr4 = new Attribute("nodeType3");
            Attribute attr5 = new Attribute("value");
            Attribute attr6 = new Attribute("value2");  // replace name with type
            Attribute attr7 = new Attribute("position");
            Attribute attr8 = new Attribute("listSize");  // list length if is list type, or else -1
            Attribute attr9 = new Attribute("listIndex");  // list index if is list type, or else -1
            Attribute attr10 = new Attribute("valueType");  // type of the value
            Attribute attr11 = new Attribute("implicit");  // field of CtElement, to avoid complicated this.
            for (Map.Entry<CtWrapper, CodeGraph> entry : pn.getInstance().entrySet()) {
                CtWrapper n = entry.getKey();
                CodeGraph g = entry.getValue();

                attr1.addValue(Attribute.computeLocationInParent(n), g);
                attr2.addValue(Attribute.computeNodeType(n), g);
                attr3.addValue(Attribute.computeNodeType2(n), g);
                attr4.addValue(Attribute.computeNodeType3(n), g);
                attr5.addValue(Attribute.computeValue(n), g);
                attr6.addValue(Attribute.computeValue2(n), g);
                attr7.addValue(Attribute.computePosition(n), g);
                attr8.addValue(Attribute.computeListSize(n), g);
                attr9.addValue(Attribute.computeListIndex(n), g);
                attr10.addValue(Attribute.computeValueType(n), g);
                attr11.addValue(Attribute.computeImplicit(n), g);
            }
            pn.setComparedAttribute(attr1);
            pn.setComparedAttribute(attr2);
            pn.setComparedAttribute(attr3);
            pn.setComparedAttribute(attr4);
            pn.setComparedAttribute(attr5);
            pn.setComparedAttribute(attr6);
            if (attr7.getValueSet().size() > 0)
                pn.setComparedAttribute(attr7);
            pn.setComparedAttribute(attr8);
            pn.setComparedAttribute(attr9);
            pn.setComparedAttribute(attr10);
            pn.setComparedAttribute(attr11);
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
                List<Map.Entry<Object, Integer>> sorted = new ArrayList<>(a.sort().entrySet());
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
