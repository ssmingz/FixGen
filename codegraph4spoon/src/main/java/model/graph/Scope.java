package model.graph;

import model.graph.node.Node;

import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {
    private Scope parent;
    private Map<String, Node> defVars = new LinkedHashMap<>();
    private Map<String, Node> usedVars = new LinkedHashMap<>();

    public Scope(Scope p) {
        parent = p;
    }
}
