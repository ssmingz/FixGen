package model;

import spoon.support.reflect.declaration.CtElementImpl;

public class CtWrapper{
    private CtElementImpl ctElementImpl;

    public CtWrapper(CtElementImpl cte){
        ctElementImpl = cte;
    }

    public CtElementImpl getCtElementImpl() {
        return ctElementImpl;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CtWrapper)) {
            return false;
        }
        return this.ctElementImpl == ((CtWrapper) o).ctElementImpl;
    }
}
