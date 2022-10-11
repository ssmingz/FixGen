package model;

public abstract class BaseEdge {
    protected int id;
    protected BaseNode source;
    protected BaseNode target;
    protected String label;

    public BaseEdge(BaseNode source, BaseNode target) {
        this.source = source;
        this.target = target;
    }

    public int getId() {
        return id;
    }

    public abstract String getLabel();

    public BaseNode getSource() {
        return source;
    }

    public BaseNode getTarget() {
        return target;
    }

    public void delete() {
        this.source.outEdges.remove(this);
        this.target.inEdges.remove(this);
        this.source = null;
        this.target = null;
    }

    @Override
    public String toString() {
        return source + "-" + getLabel() +"->" + target;
    }
}
