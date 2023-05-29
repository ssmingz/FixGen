package utils;

import codegraph.Edge;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.gumtreediff.tree.Tree;
import model.CodeGraph;
import model.CtWrapper;
import model.pattern.Attribute;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.apache.commons.lang3.StringUtils;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.*;
import java.util.*;

public class ObjectUtil {
    private static String SPACE = "   ";

    public static boolean equals(CtElementImpl a, CtElementImpl b) {
        if (a.equals(b)) {
            if (a.getPosition().isValidPosition() && b.getPosition().isValidPosition()) {
                return a.getPosition().equals(b.getPosition()) && a.getClass().equals(b.getClass());
            }
        }
        return false;
    }

    public static CtWrapper findCtKeyInSet(Set<CtWrapper> ctSet, CtWrapper target) {
        for (CtWrapper e : ctSet) {
            if (e.equals(target))
                return e;
        }
        return null;
    }

    /**
     *
     * @param addFlag : whether append
     */
    public static void writeToCsv(String headLabel, List<String> dataList, String filePath, boolean addFlag) {
        BufferedWriter buffWriter = null;
        try {
            File csvFile = new File(filePath);
            FileWriter writer = new FileWriter(csvFile, addFlag);
            buffWriter = new BufferedWriter(writer, 1024);
            if (StringUtils.isNotBlank(headLabel)) {
                buffWriter.write(headLabel);
                buffWriter.newLine();
            }
            for (String rowStr : dataList) {
                if (StringUtils.isNotBlank(rowStr)) {
                    buffWriter.write(rowStr);
                    buffWriter.newLine();
                }
            }
            buffWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (buffWriter != null) {
                    buffWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean hasEdge(CtElementImpl cteA, CtElementImpl cteB) {
        for (Edge ie : cteA._inEdges) {
            if (ie.getSource() == cteB)
                return true;
        }
        for (Edge oe : cteA._outEdges) {
            if (oe.getTarget() == cteB)
                return true;
        }
        return false;
    }

    public static Set<Edge> findAllEdges(CtElementImpl cteA, CtElementImpl cteB) {
        Set<Edge> result = new LinkedHashSet<>();
        for (Edge ie : cteA._inEdges) {
            if (ie.getSource() == cteB)
                result.add(ie);
        }
        for (Edge oe : cteA._outEdges) {
            if (oe.getTarget() == cteB)
                result.add(oe);
        }
        return result;
    }

    public static void writeFeatureJson(Pattern pat, HashMap<Integer, Object> idPatternBeforeAbs, String jsonPath) {
        try {
            Map<CodeGraph, CGObject> cgObjects = new LinkedHashMap<>();
            for (Map.Entry<Integer, Object> entry : idPatternBeforeAbs.entrySet()) {
                Object pObj = entry.getValue();
                // traverse all codegraphs
                if (pObj instanceof PatternNode) {
                    PatternNode pn = (PatternNode) pObj;
                    for (Map.Entry<CtWrapper, CodeGraph> entry2 : pn.getInstance().entrySet()) {
                        CodeGraph cg = entry2.getValue();
                        if (!cgObjects.containsKey(cg)) {
                            cgObjects.put(cg, new CGObject());
                        }
                        CGObject cgObj = cgObjects.get(cg);
                        // vertex id
                        int vID = cg.getElementId(entry2.getKey());
                        cgObj.vertexes.add(vID);
                        cgObj.vertexMap.put(vID, entry2.getKey());
                        // vertex label
                        boolean isRemoved = pat.getNodeSet().contains(pObj);
                        cgObj.vertexLabel.add(isRemoved ? 0 : 1);
                        // feature list
                        for (Attribute att : pn.getComparedAttributes()) {
                            if (!cgObj.attributes.containsKey(vID)) {
                                Map<String, Object> vals = new LinkedHashMap<>();
                                cgObj.attributes.put(vID, vals);
                                Map<String, Integer> labs = new LinkedHashMap<>();
                                cgObj.attributeLabel.put(vID, labs);
                            }
                            cgObj.attributes.get(vID).put(att.getName(), att.getValueByCG(cg));
                            cgObj.attributeLabel.get(vID).put(att.getName(), att.isAbstract() ? 1 : 0);
                        }
                    }
                } else if (pObj instanceof PatternEdge) {
                    PatternEdge pe = (PatternEdge) pObj;
                    boolean isKept = pat.getEdgeSet().contains(pObj);  // contain --> keep --> not remove --> 1
                    String patLabel = pe.getLabel();
                    for (Map.Entry<Edge, CodeGraph> entry2 : pe.getInstance().entrySet()) {
                        Edge e = entry2.getKey();
                        CodeGraph cg = entry2.getValue();
                        if (!cgObjects.containsKey(cg)) {
                            cgObjects.put(cg, new CGObject());
                        }
                        CGObject cgObj = cgObjects.get(cg);
                        cgObj.edgeMap.put(e, Objects.equals(e.getLabel(), patLabel) ? isKept : Boolean.FALSE);
                    }
                }
            }
            for (Map.Entry<CodeGraph, CGObject> entry : cgObjects.entrySet()) {
                CGObject cgObj = entry.getValue();
                // edge
                int vSize = cgObj.vertexes.size();
                String[][] edgeMatrix = new String[vSize][vSize];
                int[][] edgeMatrix2 = new int[vSize][vSize];
                for (int i=0; i<vSize; i++) {
                    List<String> edgeType = new ArrayList<>();
                    List<Integer> edgeLabel = new ArrayList<>();
                    for (int j=0; j<vSize; j++) {
                        CtWrapper src = cgObj.vertexMap.get(cgObj.vertexes.get(i));
                        CtWrapper tar = cgObj.vertexMap.get(cgObj.vertexes.get(j));
                        if (ObjectUtil.hasEdge(src.getCtElementImpl(), tar.getCtElementImpl())) {
                            Set<Edge> edges = ObjectUtil.findAllEdges(src.getCtElementImpl(), tar.getCtElementImpl());
                            for (Edge e : edges) {
                                edgeMatrix[i][j] = e.getLabel();
                                edgeMatrix2[i][j] = cgObj.edgeMap.get(e)?1:0;
                            }
                            if (edges.size() > 1)
                                System.out.println("[WARN]More than one edge between two nodes : " + entry.getKey().getFileName());
                        } else {
                            edgeMatrix[i][j] = "";
                        }
                        edgeType.add(edgeMatrix[i][j]);
                        edgeLabel.add(edgeMatrix2[i][j]);
                    }
                    cgObj.edges.add(edgeType);
                    cgObj.edgeLabel.add(edgeLabel);
                }
                String jsonPath0 = jsonPath + "/" + entry.getKey().getFileName().replaceAll("/","_");
                // create a new file
                File file = new File(jsonPath0);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                // create json file content
                JSONObject root = new JSONObject();
                // vertex id
                JSONArray vertexes = new JSONArray();
                vertexes.addAll(cgObj.vertexes);
                root.put("vertexes", vertexes);
                // attributes
                JSONArray attributes = new JSONArray();
                for (Map.Entry<Integer, Map<String, Object>> na : cgObj.attributes.entrySet()) {
                    JSONObject ja = new JSONObject();
                    ja.putAll(na.getValue());
                    JSONObject va = new JSONObject();
                    va.put(String.valueOf(na.getKey()), ja);
                    attributes.add(va);
                }
                root.put("attributes", attributes);
                // edges
                JSONArray edges = new JSONArray();
                for (List<String> es : cgObj.edges) {
                    JSONArray e = new JSONArray();
                    e.addAll(es);
                    edges.add(e);
                }
                root.put("edges", edges);
                // vertex label
                JSONArray vertexLabel = new JSONArray();
                vertexLabel.addAll(cgObj.vertexLabel);
                root.put("vertex_label", vertexLabel);
                // attribute label
                JSONArray attributeLabel = new JSONArray();
                for (Map.Entry<Integer, Map<String, Integer>> na : cgObj.attributeLabel.entrySet()) {
                    JSONObject ja = new JSONObject();
                    ja.putAll(na.getValue());
                    JSONObject va = new JSONObject();
                    va.put(String.valueOf(na.getKey()), ja);
                    attributeLabel.add(va);
                }
                root.put("attribute_label", attributeLabel);
                // edge label
                JSONArray edgeLabel = new JSONArray();
                for (List<Integer> es : cgObj.edgeLabel) {
                    JSONArray el = new JSONArray();
                    el.addAll(es);
                    edgeLabel.add(el);
                }
                root.put("edge_label", edgeLabel);
                // format json string
                String jsonString = JSON.toJSONString(root, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue);
                // write to file
                Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
                write.write(jsonString);
                write.flush();
                write.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
