package model;

public class GraphConfiguration {
    /**
     * Threshold for the minimum number of statements in a method to build AUG
     */
    public int minStatements = 0;
    /**
     * Whether to display edge label in dot graph
     */
    public boolean showASTEdge = false;
    public boolean showControlEdge = true;
    public boolean showDataEdge = true;
    public boolean showDefUseEdge = true;
    public boolean showActionEdge = true;
    /**
     * parsing config
     */
    public boolean parseFieldNode = false;
}
