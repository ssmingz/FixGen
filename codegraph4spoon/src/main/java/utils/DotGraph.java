package utils;

import codegraph.*;
import model.CodeGraph;
import model.GraphConfiguration;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DotGraph {
    public static final String SHAPE_ELLIPSE = "ellipse";
    private GraphConfiguration configuration;
    private StringBuilder graph = new StringBuilder();
    private CodeGraph codeGraph;
    private int nodeLabel = 0;

    public DotGraph(CodeGraph cg, GraphConfiguration config, int nodeIndexStart) {
        configuration = config;
        codeGraph = cg;
        // start
        graph.append(addStart(cg.getGraphName()));

        List<CtElementImpl> nodes = cg.getNodes();
        HashMap<CtElementImpl, Integer> idByNode = new HashMap<>();
        // add nodes
        int id = nodeIndexStart;
        for (CtElementImpl node : nodes) {
            if (!node.getPosition().isValidPosition()) {
                continue;
            }
            idByNode.put(node, id);
            String label = "" + node.getPosition().getLine() + ":" + node.getClass().getSimpleName() + "@" + node.prettyprint();
            graph.append(addNode(id, label, SHAPE_ELLIPSE, null, null, null));
            id++;
        }
        // add edges
        for (CtElementImpl node : nodes) {
            if (!idByNode.containsKey(node)) continue;
            int sId = idByNode.get(node);
            for (Edge e : node._outEdges) {
                if (!idByNode.containsKey(e.getTarget())) continue;
                int tId = idByNode.get(e.getTarget());
                String label = addEdgeLabel(e);
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

    private String escapeControlChars(String label) {
        if (label != null) {
            label = label.replace("\b", "\\\\b")
                    .replace("\f", "\\\\f")
                    .replace("\b", "\\\\b")
                    .replace("\n", "\\\\n")
                    .replace("\r", "\\\\r")
                    .replace("\t", "\\\\t")
                    .replace("\"", "\\\\\"");
        }
        return label;
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
