package utils;

import spoon.support.reflect.declaration.CtElementImpl;

public class ObjectUtil {
    public static boolean equals(CtElementImpl a, CtElementImpl b) {
        if (a.equals(b)) {
            if (a.getPosition().isValidPosition() && b.getPosition().isValidPosition()) {
                return a.getPosition().equals(b.getPosition()) && a.getClass().equals(b.getClass());
            }
        }
        return false;
    }
}
