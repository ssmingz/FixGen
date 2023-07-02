package model.pattern;

import codegraph.CtVirtualElement;
import codegraph.visitor.ReplaceNameVisitor;
import codegraph.visitor.TokenVisitor;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.ActionNode;
import model.actions.Insert;
import model.actions.Move;
import org.javatuples.Pair;
import spoon.Launcher;
import spoon.experimental.CtUnresolvedImport;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.code.CtCodeElementImpl;
import spoon.support.reflect.declaration.*;
import spoon.support.reflect.reference.CtReferenceImpl;
import spoon.support.reflect.reference.CtTypeMemberWildcardImportReferenceImpl;

import java.io.Serializable;
import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Attribute implements Serializable {
    private String _name;
    private Map<Object, Integer> _numByValues = new LinkedHashMap<>();
    private Map<CodeGraph, Object> _valueByCG = new LinkedHashMap<>();
    private Object _tag = ""; // choose which value to use
    private boolean isAbstract = false;

    private static int MAX_TOKEN_LENGTH = 15;
    private static final Set<Class> rootTypes = new HashSet<>() {{
        add(CtCodeElementImpl.class);
        add(CtCompilationUnitImpl.class);
        add(CtImportImpl.class);
        add(CtModuleRequirementImpl.class);
        add(CtNamedElementImpl.class);
        add(CtPackageDeclarationImpl.class);
        add(CtPackageExportImpl.class);
        add(CtProvidedServiceImpl.class);
        add(CtReferenceImpl.class);
        add(CtTypeMemberWildcardImportReferenceImpl.class);
        add(CtUnresolvedImport.class);
        add(CtUsedServiceImpl.class);
    }};

    public Attribute(String name) {
        _name = name;
    }

    public void setTag(Object v) {
        _tag = v;
    }

    public String getName() {
        return _name;
    }

    public Set<Object> getValueSet() {
        return _numByValues.keySet();
    }

    public boolean isAbstract() { return isAbstract; }

    public void setAbstract(boolean abs) { isAbstract = abs; }

    public void addValue(Object v, CodeGraph g) {
        if (v == null)
            return;
        _valueByCG.put(g, v);
        if (_numByValues.containsKey(v)) {
            int s = _numByValues.get(v) + 1;
            _numByValues.put(v, s);
        } else {
            _numByValues.put(v, 1);
        }
    }

    public Object getValueByCG(CodeGraph g) {
        return _valueByCG.getOrDefault(g, "MISSING");
    }

    public Object getTag() {
        return _tag;
    }

    public int getSupport(Object aValue) {
        return _numByValues.getOrDefault(aValue, 0);
    }

    public Map<Object, Integer> sort() {
        return  _numByValues
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
    }

    /*********************************************************/
    /******* calculate attribute *****************************/
    /*********************************************************/
    public static String computeLocationInParent(CtWrapper n) {
        String role = null;
        if (n.getCtElementImpl() instanceof ActionNode) {
            role = "ACTION";
        } else if (n.isVirtual()) {
            role = ((CtVirtualElement) n.getCtElementImpl()).getLocationInParent();
        } else {
            role = n.getCtElementImpl().getRoleInParent().name();
        }
        return role;
    }

    /**
     * type, e.g. CtIfImpl in CtCodeElementImpl.CtStatementImpl.CtIfImpl
     */
    public static String computeNodeType(CtWrapper n) {
        String type = n.getCtElementImpl().getClass().getName();
        return type;
    }

    /**
     * Do not record if exceed MAX_LENGTH.
     */
    public static String computeValue(CtWrapper n) {
        TokenVisitor visitor = new TokenVisitor();
        visitor.scan(n.getCtElementImpl());

        if (visitor.tokens.size() > MAX_TOKEN_LENGTH)
            return String.format("exceed MAX_TOKEN_LENGTH:%d tokens", visitor.tokens.size());
        else
            return n.toLabelString();
    }

    /**
     * Do not record if exceed MAX_LENGTH.
     * Replace name by type.
     */
    public static String computeValue2(CtWrapper n) {
        TokenVisitor visitor = new TokenVisitor();
        visitor.scan(n.getCtElementImpl());

        String value = n.toLabelString();
        if (visitor.tokens.size() < MAX_TOKEN_LENGTH) {
            // replace name by type
            Launcher launcher = new Launcher();
            ReplaceNameVisitor replaceVisitor = new ReplaceNameVisitor(launcher.getEnvironment());
            replaceVisitor.scan(n.getCtElementImpl());
            value = replaceVisitor.toString();
        } else {
            value = String.format("exceed MAX_TOKEN_LENGTH:%d tokens", visitor.tokens.size());
        }
        return value;
    }

    /**
     * super type, e.g. CtStatementImpl in CtCodeElementImpl.CtStatementImpl.CtIfImpl
     */
    public static String computeNodeType2(CtWrapper n) {
        Class clazz = n.getCtElementImpl().getClass().getSuperclass();
        if (rootTypes.contains(clazz)) {
            clazz = n.getCtElementImpl().getClass();
        }
        return clazz.getName();
    }

    /**
     * super type of the super type, e.g. CtStatementImpl in CtCodeElementImpl.CtStatementImpl.CtLoopImpl.CtWhileImpl
     */
    public static String computeNodeType3(CtWrapper n) {
        Class clazz = n.getCtElementImpl().getClass().getSuperclass();
        if (rootTypes.contains(clazz)) {
            clazz = n.getCtElementImpl().getClass();
        } else {
            clazz = clazz.getSuperclass();
            if (rootTypes.contains(clazz)) {
                clazz = n.getCtElementImpl().getClass().getSuperclass();
            }
        }
        return clazz.getName();
    }

    public static List<Pair<CtRole, Class>> computePosition(CtWrapper n) {
        if (n.getCtElementImpl() instanceof ActionNode) {
            ActionNode node = (ActionNode) n.getCtElementImpl();
            if (node instanceof Insert) {
                return ((Insert) node)._roleList;
            } else if (node instanceof Move) {
                return ((Move) node)._roleList;
            }
        }
        return null;
    }
}
