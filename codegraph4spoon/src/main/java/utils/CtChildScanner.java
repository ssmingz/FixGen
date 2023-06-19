package utils;

import codegraph.CtVirtualElement;
import codegraph.Edge;
import model.CtWrapper;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtScanner;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class CtChildScanner extends CtScanner {
    public HashSet<CtWrapper> childList = new LinkedHashSet<>();

    public CtChildScanner() {
    }

    @Override
    protected void exit(CtElement e) {
        if (ObjectUtil.findCtKeyInSet(childList,new CtWrapper((CtElementImpl) e))==null)
            childList.add(new CtWrapper((CtElementImpl) e));
        for (Edge oe : ((CtElementImpl) e)._outEdges) {
            if(oe.type == Edge.EdgeType.AST && ObjectUtil.findCtKeyInSet(childList,new CtWrapper(oe.getTarget()))==null)
                childList.add(new CtWrapper(oe.getTarget()));
        }
    }

}
