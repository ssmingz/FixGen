package utils;

import model.CodeGraph;
import model.graph.edge.Edge;
import model.graph.node.Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DotGraph {
    public static final String SHAPE_ELLIPSE = "ellipse";

    private StringBuilder graph = new StringBuilder();

    public DotGraph(CodeGraph cg) {
        // start
        graph.append(addStart(cg.getGraphName()));

        List<Node> nodes = cg.getNodes();
        HashMap<Node, Integer> idByNode = new HashMap<>();
        // add nodes
        int id = 0;
        for (Node node : nodes) {
            id++;
            idByNode.put(node, id);
            String label = "" + node.getStartSourceLine();
            graph.append(addNode(id, label, SHAPE_ELLIPSE, null, null, null));
        }
        // add edges
        for (Node node : nodes) {
            if (!idByNode.containsKey(node)) continue;
            int sId = idByNode.get(node);
            for (Edge e : node.outEdges) {
                if (!idByNode.containsKey(e.getTarget())) continue;
                int tId = idByNode.get(e.getTarget());
                String label = e.getLabel();
                graph.append(addEdge(sId, tId, null, null, label));
            }
        }
        // end
        graph.append(addEnd());
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
}
