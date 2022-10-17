import org.eclipse.jdt.core.dom.ASTNode;

import java.util.ArrayList;

public abstract class Node {
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
}
