package model;

import codegraph.CtVirtualElement;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.Serializable;

public class CtWrapper implements Serializable {
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

    @Override
    public int hashCode() {
        int hash = 0;
        if(ctElementImpl != null) {
            hash += ctElementImpl.hashCode();
        }
        hash += isVirtual ? 1:0;
        return hash;
    }

    public String toLabelString() {
        return ctElementImpl.prettyprint();
    }
}
