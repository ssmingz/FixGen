package utils;

import model.CodeGraph;
import model.GraphConfiguration;
import model.graph.edge.*;
import model.graph.node.Node;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.*;

public class DotGraph {
    public static final String SHAPE_ELLIPSE = "ellipse";

    private StringBuilder graph = new StringBuilder();
    private GraphConfiguration configuration;
    private CodeGraph codeGraph;
    private Pattern pattern;
    private int nodeLabel = 0;

    public DotGraph(CodeGraph cg, GraphConfiguration config, int nodeIndexStart) {
        configuration = config;
        codeGraph = cg;
        // start
        graph.append(addStart(cg.getGraphName()));

        List<Node> nodes = cg.getNodes();
        HashMap<Node, Integer> idByNode = new HashMap<>();
        // add nodes
        int id = nodeIndexStart;
        for (Node node : nodes) {
            idByNode.put(node, id);
            String label = "" + node.getStartSourceLine() + ":" + node.toLabelString();
            graph.append(addNode(id, label, SHAPE_ELLIPSE, null, null, null));
            id++;
        }
        // add edges
        for (Node node : nodes) {
            if (!idByNode.containsKey(node)) continue;
            int sId = idByNode.get(node);
            for (Edge e : node.outEdges) {
                if (!idByNode.containsKey(e.getTarget())) continue;
                int tId = idByNode.get(e.getTarget());
                String label = addEdgeLabel(e);
                graph.append(addEdge(sId, tId, null, null, label));
            }
        }
        // end
        graph.append(addEnd());
    }

    public DotGraph(Pattern pat, int nodeIndexStart) {
        pattern = pat;
        // start
        graph.append(addStart(pat.getPatternName()));

        List<PatternNode> nodes = pat.getNodes();
        HashMap<PatternNode, Integer> idByNode = new HashMap<>();
        // add nodes
        int id = nodeIndexStart;
        for (PatternNode node : nodes) {
            idByNode.put(node, id);
            String label = node.toLabel();
            graph.append(addNode(id, label, SHAPE_ELLIPSE, null, null, null));
            id++;
        }
        // add edges
        for (PatternNode node : nodes) {
            if (!idByNode.containsKey(node)) continue;
            int sId = idByNode.get(node);
            for (PatternEdge e : node.outEdges()) {
                if (!idByNode.containsKey(e.getTarget())) continue;
                int tId = idByNode.get(e.getTarget());
                String label = e.getLabel() + ":" + e.getInstanceNumber();
                graph.append(addEdge(sId, tId, null, null, label));
            }
        }
        // end
        graph.append(addEnd());
    }

    private String addEdgeLabel(Edge e) {
        String label = "";
        if (e instanceof ASTEdge && configuration.showASTEdge) {
            label = e.getLabel();
        } else if (e instanceof ControlEdge && configuration.showControlEdge) {
            label = e.getLabel();
        } else if (e instanceof DataEdge && configuration.showDataEdge) {
            label = e.getLabel();
        } else if (e instanceof DefUseEdge && configuration.showDefUseEdge) {
            label = e.getLabel();
        } else if (e instanceof ActionEdge && configuration.showActionEdge) {
            label = e.getLabel();
        }
        return label;
    }

    public String addStart(String name) {
        return "digraph \"" + name + "\" {\n";
    }

    public String addEnd() {
        return "}";
    }

    public String addNode(int id, String label, String shape, String style, String borderColor, String fontColor) {
        StringBuffer buf = new StringBuffer();
        buf.append(id + " [label=\"" + escapeControlChars(label) + "\"");
        if(shape != null && !shape.isEmpty())
            buf.append(" shape=" + shape);
        if(style != null && !style.isEmpty())
            buf.append(" style=" + style);
        if(borderColor != null && !borderColor.isEmpty())
            buf.append(" color=" + borderColor);
        if(fontColor != null && !fontColor.isEmpty())
            buf.append(" fontcolor=" + fontColor);
        buf.append("]\n");

        return buf.toString();
    }

    public String addEdge(int sId, int eId, String style, String color, String label) {
        StringBuffer buf = new StringBuffer();
        if(label == null)
            label = "";
        buf.append(sId + " -> " + eId + " [label=\"" + escapeControlChars(label) + "\"");
        if(style != null && !style.isEmpty())
            buf.append(" style=" + style);
        if(color != null && !color.isEmpty())
            buf.append(" color=" + color + " fontcolor=" + color);
        buf.append("];\n");

        return buf.toString();
    }

    private String escapeControlChars(String label) {
        if (label == null)
            return label;
        else
            return label.replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\b", "\\b")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t")
                    .replace("\"", "\\\"");
    }

    private void ensureDirectory(File path) {
        if (!path.exists()) path.mkdirs();
    }

    public void toDotFile(File file) {
        ensureDirectory(file.getParentFile());
        try {
            BufferedWriter fout = new BufferedWriter(new FileWriter(file));
            fout.append(this.graph.toString());
            fout.flush();
            fout.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toXmlFile(File file) {
        ensureDirectory(file.getParentFile());

        //创建document对象
        Document document = DocumentHelper.createDocument();
        //定义根节点
        Element rootGen = document.addElement("PDG");
        //定义根节点ROOT的子节点们
        Element clazz = rootGen.addElement("CLASS");
        Element method = clazz.addElement("Method");
        Element nodesList = method.addElement("nodes");
        Element controlDep = method.addElement("control_dependence");
        Element dataDep = method.addElement("data_dependence");
        Element actionRelation = method.addElement("action_relation");
        Element astRelation = method.addElement("ast_relation");

        clazz.addAttribute("Name", codeGraph.getGraphName());
        method.addAttribute("Name", codeGraph.getGraphName());

        List<Node> nodes = codeGraph.getNodes();
        HashMap<Node, Integer> idByNode = new HashMap<>();
        // add nodes
        for (Node node : nodes) {
            nodeLabel++;
            idByNode.put(node, nodeLabel);
            String label = "" + node.getStartSourceLine() + ":" + node.toLabelString();
            Element e = nodesList.addElement("Statement");
            e.addAttribute("no", "s"+nodeLabel);
            e.addAttribute("nodelabel", ""+nodeLabel);
            e.addAttribute("statement", label);
        }

        // add edges
        Map<String, List<String>> cd = new LinkedHashMap<>();
        Map<String, List<String>> dd = new LinkedHashMap<>();
        Map<String, List<String>> actr = new LinkedHashMap<>();
        Map<String, List<String>> astr = new LinkedHashMap<>();
        for (Node node : nodes) {
            if (!idByNode.containsKey(node)) continue;
            int sId = idByNode.get(node);
            for (Edge e : node.outEdges) {
                if (!idByNode.containsKey(e.getTarget())) continue;
                int tId = idByNode.get(e.getTarget());
                String label = addEdgeLabel(e);
                if (label.equals("Action")) {
                    if (actr.containsKey("s"+sId)) {
                        List<String> ns = new ArrayList<>(actr.get("s"+sId));
                        ns.add("s"+tId);
                        actr.put("s"+sId, ns);
                    } else {
                        List<String> ns = new ArrayList<>();
                        ns.add("s"+tId);
                        actr.put("s"+sId, ns);
                    }
                } else if (label.equals("Define-Use")) {
                    if (dd.containsKey("s"+sId)) {
                        List<String> ns = new ArrayList<>(dd.get("s"+sId));
                        ns.add("s"+tId);
                        dd.put("s"+sId, ns);
                    } else {
                        List<String> ns = new ArrayList<>();
                        ns.add("s"+tId);
                        dd.put("s"+sId, ns);
                    }
                } else if (label.equals("Control Dep")) {
                    if (cd.containsKey("s"+sId)) {
                        List<String> ns = new ArrayList<>(cd.get("s"+sId));
                        ns.add("s"+tId);
                        cd.put("s"+sId, ns);
                    } else {
                        List<String> ns = new ArrayList<>();
                        ns.add("s"+tId);
                        cd.put("s"+sId, ns);
                    }
                } else if (label.equals("Data Dep")) {
                    if (dd.containsKey("s"+sId)) {
                        List<String> ns = new ArrayList<>(dd.get("s"+sId));
                        ns.add("s"+tId);
                        dd.put("s"+sId, ns);
                    } else {
                        List<String> ns = new ArrayList<>();
                        ns.add("s"+tId);
                        dd.put("s"+sId, ns);
                    }
                } else if (label.equals("")) {
                    if (astr.containsKey("s"+sId)) {
                        List<String> ns = new ArrayList<>(astr.get("s"+sId));
                        ns.add("s"+tId);
                        astr.put("s"+sId, ns);
                    } else {
                        List<String> ns = new ArrayList<>();
                        ns.add("s"+tId);
                        astr.put("s"+sId, ns);
                    }
                } else {
                    continue;
                }

            }
        }
        for (Map.Entry<String, List<String>> entry : cd.entrySet()) {
            Element ek = controlDep.addElement("dependee");
            ek.addAttribute("no", entry.getKey());
            for (String v : entry.getValue()) {
                Element ev = ek.addElement("depender");
                ev.addAttribute("no", v);
            }
        }
        for (Map.Entry<String, List<String>> entry : dd.entrySet()) {
            Element ek = dataDep.addElement("dependee");
            ek.addAttribute("no", entry.getKey());
            for (String v : entry.getValue()) {
                Element ev = ek.addElement("depender");
                ev.addAttribute("no", v);
            }
        }
        for (Map.Entry<String, List<String>> entry : actr.entrySet()) {
            Element ek = actionRelation.addElement("dependee");
            ek.addAttribute("no", entry.getKey());
            for (String v : entry.getValue()) {
                Element ev = ek.addElement("depender");
                ev.addAttribute("no", v);
            }
        }
        for (Map.Entry<String, List<String>> entry : astr.entrySet()) {
            Element ek = astRelation.addElement("dependee");
            ek.addAttribute("no", entry.getKey());
            for (String v : entry.getValue()) {
                Element ev = ek.addElement("depender");
                ev.addAttribute("no", v);
            }
        }

        //将定义好的内容写入xml文件中
        OutputFormat format = null;
        XMLWriter xmlwriter = null;
        try {
            //进行格式化
            format = OutputFormat.createPrettyPrint();
            //设定编码
            format.setEncoding("UTF-8");
            xmlwriter = new XMLWriter(new FileOutputStream(file.getAbsolutePath()), format);
            xmlwriter.write(document); xmlwriter.flush(); xmlwriter.close();
            System.out.println("----------- Xml file successfully created -------------");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("----------- Exception occurred during of create Xml file -------");
        }

    }
}
