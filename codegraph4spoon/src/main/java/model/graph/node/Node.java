package model.graph.node;

import model.graph.Scope;
import model.graph.edge.ASTEdge;
import model.graph.edge.ControlEdge;
import model.graph.edge.Edge;
import spoon.reflect.declaration.CtElement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Node {
    private static volatile Node node;
    /**
     * original spoon node
     */
    protected transient CtElement _ctElement;
    /**
     * parent node in the abstract syntax tree in codegraph
     */
    protected Node _parent;
    /**
     * control dependency
     */
    protected Node _controlDependency = null;
    /**
     * data dependency
     */
    protected Set<Node> _dataDependency = new HashSet<>();

    public ArrayList<Edge> inEdges = new ArrayList<>();
    public ArrayList<Edge> outEdges = new ArrayList<>();

    private Scope _scope;

    /*********************************************************/
    /******* record matched information for change ***********/
    /*********************************************************/
    /**
     * bind the target node in the fixed version
     */
    private Node _bindingNode;

    /* Singleton Pattern */
    public static Node getNode(CtElement ctNode){
        if (node == null){
            synchronized (Node.class){
                if (node == null)
                    node = new Node(ctNode);
            }
        }
        return node;
    }

    private Node(CtElement oriNode) {
        _ctElement = oriNode;
    }

    public void setParent(Node node) {
        _parent = node;
    }

    public void addOutEdge(Edge edge) {
        outEdges.add(edge);
    }

    public void addInEdge(Edge edge) {
        inEdges.add(edge);
    }

    public void setControlDependency(Node controller) {
        _controlDependency = controller;
        if (controller != null) {
            new ControlEdge(controller, this);
        }
    }

    public void addDataDepNode(Node controller) {
        if (controller != null) {
            if (controller.hasASTChildren()) {
                for (Edge out : controller.outEdges) {
                    if (out instanceof ASTEdge) {
                        // only for direct children
                        _dataDependency.add(controller);
                    }
                }
            } else {
                _dataDependency.add(controller);
            }
        }
    }

    public boolean hasASTChildren() {
        for (Edge out : outEdges) {
            if (out instanceof ASTEdge)
                return true;
        }
        return false;
    }
}
