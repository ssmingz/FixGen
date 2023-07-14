package builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.pattern.Attribute;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;

public class InteractPattern {

    public static void abstractByJSONObject(Pattern pattern, JSONObject label, String cgName) {
        JSONArray vertexes = (JSONArray) label.get("vertexes");
        JSONArray vertex_label = (JSONArray) label.get("vertex_label");
        JSONArray attributes = (JSONArray) label.get("attributes");
        JSONArray attribute_labels = (JSONArray) label.get("attribute_label");
        JSONArray edges = (JSONArray) label.get("edges");
        JSONArray edge_label = (JSONArray) label.get("edge_label");
        for (int vi=0; vi<vertexes.size(); vi++) {
            int id = (int) vertexes.get(vi);
            int lab = (int) vertex_label.get(vi);
            InteractPattern.abstractVertex(pattern, id, lab, cgName);

            JSONObject attrs = attributes.getJSONObject(vi).getJSONObject(String.valueOf(id));
            JSONObject attr_labs = attribute_labels.getJSONObject(vi).getJSONObject(String.valueOf(id));
            InteractPattern.abstractAttribute(pattern, id, attrs, attr_labs, cgName);

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
