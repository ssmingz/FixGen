package utils;

import model.CtWrapper;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.Set;

public class ObjectUtil {
    public static boolean equals(CtElementImpl a, CtElementImpl b) {
        if (a.equals(b)) {
            if (a.getPosition().isValidPosition() && b.getPosition().isValidPosition()) {
                return a.getPosition().equals(b.getPosition()) && a.getClass().equals(b.getClass());
            }
        }
        return false;
    }

    public static CtWrapper findCtKeyInSet(Set<CtWrapper> ctSet, CtWrapper target) {
        for (CtWrapper e : ctSet) {
            if (e.equals(target))
                return e;
        }
        return null;
    }
}
