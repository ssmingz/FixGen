package model;

import codegraph.CtVirtualElement;
import spoon.support.reflect.declaration.CtElementImpl;

public class CtWrapper{
    /*
    * represent the corresponding CtElement or CtVirtualElement (e.g. can be an attribute of a CtElement)
    */
    private CtElementImpl ctElementImpl;
    private boolean isVirtual = false;

    public CtWrapper(CtElementImpl cte){
        ctElementImpl = cte;
        isVirtual = cte instanceof CtVirtualElement;
    }

    public CtElementImpl getCtElementImpl() {
        return ctElementImpl;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CtWrapper)) {
            return false;
        }
        return this.ctElementImpl == ((CtWrapper) o).ctElementImpl;
    }
}
