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
            InteractPattern.abstractVertex(pattern, id, lab, cgName);

            JSONObject attrs = attributes.getJSONObject(vi).getJSONObject(String.valueOf(id));
            int finalVi = vi;
            JSONObject attr_labels = new JSONObject(){{
                put("locationInParent", labels.getJSONArray("location").getInteger(finalVi));
                put("nodeType", labels.getJSONArray("type").getInteger(finalVi));
                put("value", labels.getJSONArray("attrvalue").getInteger(finalVi));
                // TODO: to be added
                put("value2", labels.containsKey("attrvalue2") && labels.getJSONArray("attrvalue2").size() > finalVi ? labels.getJSONArray("attrvalue2").getInteger(finalVi) : 1);
                put("valueType", labels.containsKey("valuetype") && labels.getJSONArray("valuetype").size() > finalVi ? labels.getJSONArray("valuetype").getInteger(finalVi) : 1);
            }};
            InteractPattern.abstractAttribute(pattern, id, attrs, attr_labels, cgName);

            JSONArray es = edges.getJSONArray(vi);
            JSONArray e_labs = edge_label.getJSONArray(vi);
            for (int ei=0; ei<es.size(); ei++) {
                int tid = (int) vertexes.get(ei);
                if (!es.getString(ei).equals(""))
                    InteractPattern.abstractEdge(pattern, id, tid, e_labs.getInteger(ei), cgName);
            }
        }
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
