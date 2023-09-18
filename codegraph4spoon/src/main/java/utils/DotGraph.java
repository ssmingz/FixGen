package utils;

import codegraph.*;
import model.CodeGraph;
import model.CtWrapper;
import builder.GraphConfiguration;
import model.actions.ActionEdge;
import model.actions.ActionNode;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DotGraph {
    public static final String SHAPE_ELLIPSE = "ellipse";
    private GraphConfiguration configuration;
    private StringBuilder graph = new StringBuilder();
    private CodeGraph codeGraph;
    private int nodeLabel = 0;

    /**
     * draw a dot graph for a code graph
     */
    public DotGraph(CodeGraph cg, GraphConfiguration config, int nodeIndexStart) {
        configuration = config;
        codeGraph = cg;
        // start
        graph.append(addStart(cg.getGraphName()));

        List<CtWrapper> nodes = cg.getNodes();
        HashMap<CtWrapper, Integer> idByNode = new HashMap<>();
        // add nodes
        int id = nodeIndexStart;
        for (CtWrapper node : nodes) {
            CtElementImpl ctElement = node.getCtElementImpl();
            int pos = -1;
            if (ctElement.getPosition().isValidPosition()) {
                pos = ctElement.getPosition().getLine();
            }
            idByNode.put(node, id);
            String label = String.format("%d#L%d:%s@%s", cg.getElementId(node), pos, ctElement.getClass().getSimpleName(), ObjectUtil.printNode(ctElement));
            graph.append(addNode(id, label, SHAPE_ELLIPSE, null, null, null));
            id++;
        }
        // add edges
        for (CtWrapper node : nodes) {
            if (!idByNode.containsKey(node)) continue;
            int sId = idByNode.get(node);
            CtElementImpl ctElement = node.getCtElementImpl();
            for (Edge e : ctElement._outEdges) {
                for (CtWrapper node2 : nodes) {
                    CtElementImpl ctElement2 = node2.getCtElementImpl();
                    if (e.getTarget() == ctElement2) {
                        if (!idByNode.containsKey(node2))
                            continue;
                        int tId = idByNode.get(node2);
                        String label = String.format("%d#%s", cg.getElementId(e), addEdgeLabel(e));
                        graph.append(addEdge(sId, tId, null, null, label));
                    }
                }
            }
        }
        // end
        graph.append(addEnd());
    }

    /**
     * draw a dot graph for a pattern
     */
    public DotGraph(Pattern pat, int nodeIndexStart, boolean isAbstract, boolean showAbstract) {
        // start
        graph.append(addStart(pat.getPatternName()));

        List<PatternNode> nodes = pat.getNodes();
        HashMap<PatternNode, Integer> idByNode = new HashMap<>();
        // add nodes
        int id = nodeIndexStart;
        for (PatternNode node : nodes) {
            if (!showAbstract && node.isAbstract()) continue;
            idByNode.put(node, id);
            String label = isAbstract ? node.toLabelAfterAbstract() : node.toLabel();
            String style = node.isAbstract() ? "dashed" : null;
            Map<Object, Integer> PatternId = pat.getIdPattern().entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            graph.append(addNode(id, PatternId.get(node)+"#\n"+label, SHAPE_ELLIPSE, style, null, null));
            id++;
        }
        // add edges
        for (PatternNode node : nodes) {
            if (!showAbstract && node.isAbstract()) continue;
            if (!idByNode.containsKey(node)) continue;
            int sId = idByNode.get(node);
            for (PatternEdge e : node.outEdges()) {
                if (!showAbstract && e.isAbstract()) continue;
                if (!idByNode.containsKey(e.getTarget())) continue;
                int tId = idByNode.get(e.getTarget());
                String label = String.format("%s:%d\n%s", e.getLabel(), e.getInstanceNumber(), e.toLabel());
                String style = e.isAbstract() ? "dashed" : null;
                graph.append(addEdge(sId, tId, style, null, label));
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
            label = label.replace("\b", "\\\b")
                    .replace("\f", "\\\f")
                    .replace("\b", "\\\b")
                    //.replace("\n", "\\\n")
                    .replace("\r", "\\\r")
                    .replace("\t", "\\\t")
                    .replace("\"", "\\\"")
                    .replace("{", "\\{")
                    .replace("}", "\\}");
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

    public static void drawPattern(Pattern pattern, String path, boolean isAbstract) {
        DotGraph dot = new DotGraph(pattern, 0, isAbstract, false);
        dot.toDotFile(new File(path));
    }

    public static void drawPattern(List<Pattern> patternList, String base, boolean isAbstract) {
        for (int i=0; i<patternList.size(); i++) {
            String abs = isAbstract ? "_abstract" : "";
            String path = String.format("%s/pattern%s_%d.dot", base, abs, i);
            drawPattern(patternList.get(i), path, isAbstract);
        }
    }

    public static void drawCodeGraph(CodeGraph cg, String path) {
        DotGraph dot = new DotGraph(cg, new GraphConfiguration(), 0);
        dot.toDotFile(new File(path));
    }
}
