package utils;

import builder.Matcher;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.reference.CtExecutableReferenceImpl;

import java.util.Objects;

/**
 * equals() in CtElement may cause conflict, so create a more precise one
 */
public class CtObject {
    public CtElement ctElement;
    public String locationInParent;

    public CtObject(CtElement cte) {
        ctElement = cte;
        locationInParent = null;
    }

    public CtObject(CtElement cte, String loc) {
        ctElement = cte;
        locationInParent = loc;
    }

    @Override
    public boolean equals(Object cte2) {
        if (cte2 instanceof CtObject) {
            if (this.ctElement instanceof CtExecutableReferenceImpl && ((CtObject) cte2).ctElement instanceof CtExecutableReferenceImpl)
                return this.ctElement.getPosition().getLine()==((CtObject) cte2).ctElement.getPosition().getLine() && this.ctElement.prettyprint().equals(((CtObject) cte2).ctElement.prettyprint());
            else
                return Matcher.equalsInSameSrc(this.ctElement, ((CtObject) cte2).ctElement) && Objects.equals(this.locationInParent, ((CtObject) cte2).locationInParent);
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        return ctElement.hashCode();
    }
}
