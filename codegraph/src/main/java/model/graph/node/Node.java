package model.graph.node;

import model.NodeComparator;
import model.graph.Scope;
import model.graph.edge.ASTEdge;
import model.graph.edge.ControlEdge;
import model.graph.edge.DataEdge;
import model.graph.edge.Edge;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.NameExpr;
import model.graph.node.stmt.StmtNode;
import org.eclipse.jdt.core.dom.ASTNode;

import java.util.*;

public abstract class Node implements NodeComparator {
    protected String _fileName;
    protected int _startLine;
    protected int _endLine;
    /**
     * original AST node in the JDT abstract tree model
     * NOTE: AST node does not support serialization
     */
    protected transient ASTNode _astNode;
    /**
     * parent node in the abstract syntax tree
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
    
    /**
     * @param oriNode   : original abstract syntax tree node in the JDT model
     * @param fileName  : source file name
     * @param startLine : start line number of the node in the original source file
     * @param endLine   : end line number of the node in the original source file
     */
    public Node(ASTNode oriNode, String fileName, int startLine, int endLine) {
        this(oriNode, fileName, startLine, endLine, null);
    }

    /**
     * @param oriNode   : original abstract syntax tree node in the JDT model
     * @param fileName  : source file name (with absolute path)
     * @param startLine : start line number of the node in the original source file
     * @param endLine   : end line number of the node in the original source file
     * @param parent    : parent node in the abstract syntax tree
     */
    public Node(ASTNode oriNode, String fileName, int startLine, int endLine, Node parent) {
        _fileName = fileName;
        _startLine = startLine;
        _endLine = endLine;
        _astNode = oriNode;
        _parent = parent;
    }

    public void addOutEdge(Edge edge) {
        outEdges.add(edge);
    }

    public void addInEdge(Edge edge) {
        inEdges.add(edge);
    }

    public void setParent(Node node) {
        _parent = node;
    }

    public void setControlDependency(Node controller) {
        _controlDependency = controller;
        if (controller != null) {
            new ControlEdge(controller, this);
        }
    }

    public void setDataDependency(Node controller) {
        if (controller != null) {
            if (controller.hasASTChildren()) {
                for (Edge out : controller.outEdges) {
                    if (out instanceof ASTEdge && out.getTarget() instanceof NameExpr) {
                        // only for direct children
                        new DataEdge(out.getTarget(), this);
                    }
                }
            } else {
                new DataEdge(controller, this);
            }
        }
    }

    public void addDataDepNode(Node controller) {
        if (controller != null) {
            if (controller.hasASTChildren()) {
                for (Edge out : controller.outEdges) {
                    if (out instanceof ASTEdge && out.getTarget() instanceof NameExpr) {
                        // only for direct children
                        _dataDependency.add(controller);
                    }
                }
            } else {
                _dataDependency.add(controller);
            }
        }
    }

    public void setScope(Scope scope) { _scope = scope; }

    public boolean hasASTChildren() {
        for (Edge out : outEdges) {
            if (out instanceof ASTEdge)
                return true;
        }
        return false;
    }

    public Node getDirectControlNode() {
        return _controlDependency;
    }

    public LinkedHashSet<Node> getRecursiveControlNodes() {
        LinkedHashSet<Node> all = new LinkedHashSet<>();
        if (_controlDependency != null) {
            all.add(_controlDependency);
            all.addAll(_controlDependency.getRecursiveControlNodes());
        }
        return all;
    }

    public Scope getScope() {
        return _scope;
    }

    public Set<Node> getDirectDataDependentNodes() {
        Set<Node> all = new HashSet<>();
        all.addAll(_dataDependency);
        for (Node ch : getDirectASTChildren()) {
            all.addAll(ch.getDirectDataDependentNodes());
        }
        return all;
    }

    public LinkedHashSet<Node> getRecursiveDataDependentNodes() {
        LinkedHashSet<Node> all = new LinkedHashSet<>();
        for (Node ch : getDirectDataDependentNodes()) {
            all.add(ch);
            all.addAll(ch.getRecursiveDataDependentNodes());
        }
        return all;
    }


    public List<Node> getDirectASTChildren() {
        List<Node> ch = new ArrayList<>();
        for (Edge e : outEdges) {
            if (e instanceof ASTEdge)
                ch.add(e.getTarget());
        }
        return ch;
    }

    public ASTNode getASTNode() {
        return _astNode;
    }

    public int getStartSourceLine() {
        return _startLine;
    }
    public int getEndSourceLine() {
        return _endLine;
    }

    public abstract String toLabelString();

    public Node getParent() {
        return _parent;
    }

    public void setBindingNode(Node binding) {
        _bindingNode = binding;
        if (_bindingNode != null) {
            binding._bindingNode = this;
        }
    }

    public Node getBindingNode() {
        return _bindingNode;
    }
}

