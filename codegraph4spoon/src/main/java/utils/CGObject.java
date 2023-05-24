package utils;

import codegraph.Edge;
import model.CtWrapper;

import java.util.*;

public class CGObject {
    List<Integer> vertexes = new ArrayList<>();
    Map<Integer, CtWrapper> vertexMap = new LinkedHashMap<>();
    Map<Integer, Map<String, Object>> attributes = new LinkedHashMap<>();
    List<List<String>> edges = new ArrayList<>();
    List<Integer> vertexLabel = new ArrayList<>();
    Map<Integer, Map<String, Integer>> attributeLabel = new LinkedHashMap<>();
    List<List<Integer>> edgeLabel = new ArrayList<>();
    Map<Edge, Boolean> edgeMap = new LinkedHashMap<>();  // whether kept

    public CGObject() {}
}
