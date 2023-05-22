package utils;

import codegraph.Edge;
import model.CodeGraph;
import model.CtWrapper;
import model.pattern.Attribute;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ObjectUtil {
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

    public static void writeFeatureCsv(Pattern pat, HashMap<Integer, Object> idPatternBeforeAbs, String csvPath) {
        // header
        String header = "id,location,element_label,feature_list,feature_label";
        // add data
        int indexCounter = 0;
        List<String> dataList = new ArrayList<>();
        Map<CodeGraph, Integer> cgID = new LinkedHashMap<>();
        for (Map.Entry<Integer, Object> entry : idPatternBeforeAbs.entrySet()) {
            Object pObj = entry.getValue();
            // traverse all codegraphs
            if (pObj instanceof PatternNode) {
                PatternNode pn = (PatternNode) pObj;
                for (Map.Entry<CtWrapper, CodeGraph> entry2 : pn.getInstance().entrySet()) {
                    CodeGraph cg = entry2.getValue();
                    if (!cgID.containsKey(cg)) {
                        cgID.put(cg, cgID.size()+1);
                    }
                    StringBuilder data = new StringBuilder();
                    // id
                    data.append(++indexCounter);
                    // location : dataset_project_group_codegraphId_elementId
                    int elementId = cg.getElementId(entry2.getKey());
                    data.append(String.format(",%s##%d", cg.getFileName(), elementId));
                    // element label
                    boolean isRemoved = pat.getNodeSet().contains(pObj) || pat.getEdgeSet().contains(pObj);
                    data.append(String.format(",%d",isRemoved?0:1));
                    // feature list : ["locationInParent", "nodeType", "value"]
                    StringBuilder featStr = new StringBuilder();
                    // feature label list
                    StringBuilder featLabelStr = new StringBuilder();
                    for (Attribute att : pn.getComparedAttributes()) {
                        if (featStr.length()>0) {
                            featStr.append(",").append(StringEscapeUtils.escapeJava(att.getValueByCG(cg)));
                            featLabelStr.append(",").append(!isRemoved&&!att.isAbstract()?1:0);
                        } else {
                            featStr.append(StringEscapeUtils.escapeJava(att.getValueByCG(cg)));
                            featLabelStr.append(!isRemoved&&!att.isAbstract()?1:0);
                        }
                    }
                    data.append(String.format(",[%s]", featStr));
                    data.append(String.format(",[%s]", featLabelStr));
                    dataList.add(String.valueOf(data));
                }
            } else if (pObj instanceof PatternEdge) {
                // TODO: record features for PatternEdge
                PatternEdge pe = (PatternEdge) pObj;
            }
        }
        ObjectUtil.writeToCsv(header, dataList, csvPath, false);
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
}
