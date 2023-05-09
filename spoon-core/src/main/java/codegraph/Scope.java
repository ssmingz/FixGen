package codegraph;

import spoon.reflect.declaration.CtElement;

import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {
    private Scope parent;
    private Map<String, CtElement> defVars = new LinkedHashMap<>();
    private Map<String, CtElement> usedVars = new LinkedHashMap<>();

    public Scope(Scope p) {
        parent = p;
    }
}
