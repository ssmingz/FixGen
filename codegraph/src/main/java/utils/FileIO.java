package utils;

import model.CodeGraph;
import model.graph.edge.Edge;
import model.graph.node.Node;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

public class FileIO {
    static Map<String, Integer> nodeLabelMapping = new LinkedHashMap<>();
    static int nodeLabelCounter = 0;
    static Map<String, Integer> edgeLabelMapping = new LinkedHashMap<>();
    static int edgeLabelCounter = 0;

    static int nodeCounter = 0;

    public static String readStringFromFile(String inputFile) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
            byte[] bytes = new byte[(int) new File(inputFile).length()];
            in.read(bytes);
            in.close();
            return new String(bytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeStringToFile(String string, String outputFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(string);
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static String getSimpleClassName(String className) {
        String name = className.substring(className.lastIndexOf('.') + 1);
        return name;
    }

    public static ArrayList<File> getPaths(File dir) {
        ArrayList<File> files = new ArrayList<>();
        if (dir.isDirectory())
            for (File sub : dir.listFiles())
                files.addAll(getPaths(sub));
        else if (dir.getName().endsWith(".java"))
            files.add(dir);
        return files;
    }


    public static void printCodeGraphToSPMFtxt(List<CodeGraph> graphs, String outPath) {
        StringBuilder content = new StringBuilder();
        for (int i=0; i<graphs.size(); i++) {
            System.out.println("Start printing CodeGraph " + i);
            List<String> gContent = printCodeGraphToSPMFtxt(graphs.get(i), i);
            content.append(String.join("\n", gContent));
            content.append("\n\n");
        }
        writeStringToFile(content.toString(), outPath);
    }

    public static List<String> printCodeGraphToSPMFtxt(CodeGraph graph, int graphId) {
        String graphLine = "t # " + graphId;
        List<String> nodeLines = new ArrayList<>();
        List<String> edgeLines = new ArrayList<>();
        Map<Node, Integer> nodeIdMapping = new LinkedHashMap<>();
        Set<Node> targets = new LinkedHashSet<>();
        for (Node aNode : graph.getNodes()) {
            String nodeLabel = aNode.getClass().getName().substring(aNode.getClass().getName().lastIndexOf(".")+1);
            if (!nodeLabelMapping.containsKey(nodeLabel)) {
                nodeLabelMapping.put(nodeLabel, nodeLabelCounter++);
            }
            nodeLabel = nodeLabelMapping.get(nodeLabel).toString();

//            if (aNode.outEdges.stream().anyMatch(p->p.type== Edge.EdgeType.ACTION)
//                    || aNode.inEdges.stream().anyMatch(p->p.type== Edge.EdgeType.ACTION)) {
                String aNodeLine = "v " + nodeCounter + " " + nodeLabel;
                nodeLines.add(aNodeLine);
                targets.add(aNode);
//            }
            nodeIdMapping.put(aNode, nodeCounter++);
        }
        for (Node aNode : targets) {
            // only traverse out edges
            for (Edge oe : aNode.outEdges) {
                Integer inId = nodeIdMapping.get(oe.getSource());
                Integer outId = nodeIdMapping.get(oe.getTarget());
                if (inId == null || outId == null)
                    continue;
                String edgeLabel = oe.getLabel();
                if (!edgeLabelMapping.containsKey(edgeLabel)) {
                    edgeLabelMapping.put(edgeLabel, edgeLabelCounter++);
                }
                edgeLabel = edgeLabelMapping.get(edgeLabel).toString();
                edgeLabel = mapEdgeLabelToInt(oe.getLabel());
                String aEdgeLine = "e " + inId + " " + outId + " " + edgeLabel;
                edgeLines.add(aEdgeLine);
            }
        }
        List<String> content = new ArrayList<>();
        content.add(graphLine);
        content.addAll(nodeLines);
        content.addAll(edgeLines);
        return content;
    }

    public static String mapEdgeLabelToInt(String label) {
        switch (label) {
            case "Action":
                return "0";
            case "AST":
                return "1";
            case "Control Dep":
                return "2";
            case "Data Dep":
                return "3";
            case "Define-Use":
                return "4";
            default:
                return "5";
        }
    }
}

