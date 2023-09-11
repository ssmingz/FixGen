package builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.pattern.Attribute;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;

public class InteractPattern {

    public static void abstractByJSONObject(Pattern pattern, JSONObject oris, JSONObject labels, String cgName) {
        // ids
        JSONArray vertexes = oris.getJSONArray("vertexes");
        JSONArray edges = oris.getJSONArray("edges");
        JSONArray attributes = oris.getJSONArray("attributes");
        // labels
        JSONArray vertex_label = labels.getJSONArray("node");
        JSONArray edge_label = labels.getJSONArray("edge");

        for (int vi=0; vi<vertexes.size(); vi++) {
            int id = (int) vertexes.get(vi);
            int lab = (int) vertex_label.get(vi);

            // 不处理与action相关的节点
            if (isActionRelatedNode(pattern, cgName, id))
                continue;

            // 不处理与def-use相关的节点
            if(isDefinUseEdge(pattern, cgName, id, id))
                continue;

            InteractPattern.abstractVertex(pattern, id, lab, cgName);

            JSONObject attrs = attributes.getJSONObject(vi).getJSONObject(String.valueOf(id));
            int finalVi = vi;
            JSONObject attr_labels = new JSONObject(){{
                put("locationInParent", labels.getJSONArray("location").getInteger(finalVi));
                put("nodeType", labels.getJSONArray("node_type").getInteger(finalVi));
                put("value", labels.getJSONArray("value").getInteger(finalVi));
                // TODO: to be added
                put("value2", labels.containsKey("value2") && labels.getJSONArray("value2").size() > finalVi ? labels.getJSONArray("value2").getInteger(finalVi) : 1);
                put("valueType", labels.containsKey("value_type") && labels.getJSONArray("value_type").size() > finalVi ? labels.getJSONArray("value_type").getInteger(finalVi) : 1);
            }};
            InteractPattern.abstractAttribute(pattern, id, attrs, attr_labels, cgName);

            JSONArray es = edges.getJSONArray(vi);
            JSONArray e_labs = edge_label.getJSONArray(vi);
            for (int ei=0; ei<es.size(); ei++) {
                int tid = (int) vertexes.get(ei);

                if (isActionRelatedEdge(pattern, cgName, id, tid))
                    continue;

                if(isDefinUseEdge(pattern, cgName, id, tid))
                    continue;

                if (!es.getString(ei).equals(""))
                    InteractPattern.abstractEdge(pattern, id, tid, e_labs.getInteger(ei), cgName);
            }
        }
    }

    private static boolean isActionRelatedNode(Pattern pattern, String graphName, int id) {
        PatternNode pn = pattern.getPatternNodeByCGElementId(graphName, id);
        if (pn != null) {
            return pn.isActionRelated() ||
                    pn.inEdges().stream().anyMatch(e -> e.type == PatternEdge.EdgeType.ACTION) ||
                    pn.outEdges().stream().anyMatch(e -> e.type == PatternEdge.EdgeType.ACTION);
        }
        return false;
    }

    private static boolean isActionRelatedEdge(Pattern pattern, String graphName, int srcId, int tarId) {
        PatternEdge pe = pattern.getPatternEdgeByCGElementId(graphName, srcId, tarId);
        if (pe != null)
            return pe.getSource().isActionRelated() || pe.getTarget().isActionRelated();
        return false;
    }

    private static boolean isDefinUseNode(Pattern pattern, String graphName, int id) {
        PatternNode pn = pattern.getPatternNodeByCGElementId(graphName, id);
        if (pn != null) {
            return pn.outEdges().stream().anyMatch(edge -> edge.type == PatternEdge.EdgeType.DEF_USE) ||
                    pn.inEdges().stream().anyMatch(edge -> edge.type == PatternEdge.EdgeType.DEF_USE);
        }
        return false;
    }

    private static boolean isDefinUseEdge(Pattern pattern, String graphName, int srcId, int tarId) {
        PatternEdge pe = pattern.getPatternEdgeByCGElementId(graphName, srcId, tarId);
        if (pe != null)
            return pe.type == PatternEdge.EdgeType.DEF_USE;
        return false;
    }

    public static PatternNode abstractVertex(Pattern pattern, int id, int label, String graphName) {
        PatternNode pn = pattern.getPatternNodeByCGElementId(graphName, id);
        if (pn != null)
            setVertexVisibility(pn, label == 0);
        return pn;
    }

    public static void abstractAttribute(Pattern pattern, int id, JSONObject attrs, JSONObject attr_labs, String graphName) {
        PatternNode pn = pattern.getPatternNodeByCGElementId(graphName, id);
        if (pn != null) {
            for (String attrName : attrs.keySet()) {
                if (pn.getAttribute(attrName) != null)
                    setAttributeVisibility(pn.getAttribute(attrName), attr_labs.getInteger(attrName) == 0);
            }
        }
    }

    public static void abstractAttribute(Pattern pattern, int id, String attrName, int label, String graphName) {
        PatternNode pn = pattern.getPatternNodeByCGElementId(graphName, id);
        if (pn != null && pn.getAttribute(attrName) != null) {
            setAttributeVisibility(pn.getAttribute(attrName), label == 0);
        }
    }

    public static void abstractEdge(Pattern pattern, int srcId, int tarId, int label, String graphName) {
        PatternEdge pe = pattern.getPatternEdgeByCGElementId(graphName, srcId, tarId);
        if (pe != null) {
            setEdgeVisibility(pe, label == 0);
        }
    }

    public static void setVertexVisibility(PatternNode pn, boolean v) {
        pn.setAbstract(v);
    }

    public static void setEdgeVisibility(PatternEdge e, boolean v) {
        e.setAbstract(v);
    }

    public static void setAttributeVisibility(Attribute a, boolean v) {
        a.setAbstract(v);
    }
}
