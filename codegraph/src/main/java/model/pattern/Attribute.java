package model.pattern;

import model.graph.node.Node;
import model.graph.node.PatchNode;
import model.graph.node.actions.ActionNode;
import model.graph.node.expr.ExprNode;
import org.eclipse.jdt.core.dom.*;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.code.CtExpressionImpl;
import spoon.support.reflect.code.CtStatementImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.declaration.CtTypeImpl;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

public class Attribute {
    private final String _name;
    private Map<String, Integer> _numByValues = new LinkedHashMap<>();
    private String _tag = ""; // choose which value to use

    public Attribute(String name) {
        _name = name;
    }

    public void setTag(String v) {
        _tag = v;
    }

    public String getTag() {
        return _tag;
    }

    public int getSupport(String aValue) {
        return _numByValues.getOrDefault(aValue, 0);
    }

    public Map<String, Integer> sort() {
        return  _numByValues
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
    }

    public void addValue(String v) {
        if (_numByValues.containsKey(v)) {
            int s = _numByValues.get(v) + 1;
            _numByValues.put(v, s);
        } else {
            _numByValues.put(v, 1);
        }
    }

    public String getName() {
        return _name;
    }

    public static void computeValueName(Pattern pat) {
        // only for expression node
        for (PatternNode pn : pat.getNodes()) {
            Attribute attr = new Attribute("valueName");
            for (Node n : pn.getInstance().keySet()) {
                if (n instanceof ExprNode) {
                    String name = n.toLabelString();
                    // replace variable name with type name
                    for (Map.Entry<String, String> entry : pn.getInstance().get(n).getTypeByNameMap().entrySet()) {
                        name = name.replaceAll(entry.getKey(), entry.getValue());
                    }
                    attr.addValue(name);
                } else if (n instanceof PatchNode) {
                    CtElement cte = ((PatchNode)n).getSpoonNode();
                    if (cte instanceof CtExpressionImpl) {
                        String name = cte.toString();
                        // replace variable name with type name
                        for (Map.Entry<String, String> entry : pn.getInstance().get(n).getTypeByNameMap().entrySet()) {
                            name = name.replaceAll(entry.getKey(), entry.getValue());
                        }
                        attr.addValue(name);
                    }
                } else {
                    continue;
                }
                pn.setComparedAttribute(attr);
            }
        }
    }

    public static void computeValueType(Pattern pat) {
        // only for expression node
        for (PatternNode pn : pat.getNodes()) {
            Attribute attr = new Attribute("valueType");
            for (Node n : pn.getInstance().keySet()) {
                if (n instanceof ExprNode) {
                    String type = pn.getInstance().get(n).getTypeByName(n.toLabelString());
                    attr.addValue(type);
                } else if (n instanceof PatchNode) {
                    CtElement cte = ((PatchNode)n).getSpoonNode();
                    if (cte instanceof CtExpressionImpl) {
                        String type = ((CtExpressionImpl<?>) cte).getType().getSimpleName();
                        attr.addValue(type);
                    }
                } else {
                    continue;
                }
                pn.setComparedAttribute(attr);
            }
        }
    }

    public static void computeLocationInParent(Pattern pat) {
        // only for node
        for (PatternNode pn : pat.getNodes()) {
            Attribute attr = new Attribute("locationInParent");
            for (Node n : pn.getInstance().keySet()) {
                if (n.getASTNode() != null) {
                    String loc = n.getASTNode().getLocationInParent().getId().toLowerCase(Locale.ROOT);
                    attr.addValue(loc);
                } else if (n instanceof PatchNode) {
                    String loc = ((PatchNode)n).getSpoonNode().getRoleInParent().getCamelCaseName();
                    attr.addValue(loc);
                } else if (n instanceof ActionNode) {
                    String loc = "action";
                    attr.addValue(loc);
                } else {
                    String loc = getRoleInParent(n);
                    if (!loc.equals("")) {
                        attr.addValue(loc);
                    } else {
                        System.out.println("not attached AST node or spoon node: " + n.toLabelString());
                    }
                }
                pn.setComparedAttribute(attr);
            }
        }
    }

    private static String getRoleInParent(Node n) {
        Field[] fields = n.getParent().getClass().getDeclaredFields();
        for(Field field: fields) {
            //设置是否允许访问，不是修改原来的访问权限修饰词
            field.setAccessible(true);
            //获取字段名，和字段的值
            try {
                if (field.get(n.getParent()).equals(n)) {
                    return field.getName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[ERROR]couldn't get role in parent for node : " + n.toLabelString());
        return "";
    }

    private static String getType(Node n) {
        Field[] fields = n.getParent().getClass().getDeclaredFields();
        for(Field field: fields) {
            field.setAccessible(true);
            try {
                if (field.get(n.getParent()).equals(n)) {
                    return field.getType().getSimpleName();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        System.out.println("[ERROR]couldn't get type in parent for node : " + n.toLabelString());
        return "";
    }

    public static void computeType(Pattern pat) {
        // only for node
        for (PatternNode pn : pat.getNodes()) {
            Attribute attr = new Attribute("nodeType");
            Attribute attr2 = new Attribute("nodeTypeHighLevel");
            for (Node n : pn.getInstance().keySet()) {
                if (n.getASTNode() != null) {
                    String type = n.getASTNode().getClass().getSimpleName();
                    attr.addValue(type);
                    if (n.getASTNode() instanceof Expression) {
                        attr2.addValue("expression");
                    } else if (n.getASTNode() instanceof Statement) {
                        attr2.addValue("statement");
                    } else if (n.getASTNode() instanceof MethodDeclaration) {
                        attr2.addValue("methodDeclaration");
                    } else if (n.getASTNode() instanceof TypeDeclaration) {
                        attr2.addValue("typeDeclaration");
                    } else {
                        continue;
                    }
                    pn.setComparedAttribute(attr2);
                } else if (n instanceof PatchNode) {
                    String type = ((PatchNode)n).getSpoonNode().getClass().getSimpleName();
                    attr.addValue(type);
                    if (((PatchNode)n).getSpoonNode() instanceof CtExpressionImpl) {
                        attr2.addValue("expression");
                    } else if (((PatchNode)n).getSpoonNode() instanceof CtStatementImpl) {
                        attr2.addValue("statement");
                    } else if (((PatchNode)n).getSpoonNode() instanceof CtMethodImpl) {
                        attr2.addValue("methodDeclaration");
                    } else if (((PatchNode)n).getSpoonNode() instanceof CtTypeImpl) {
                        attr2.addValue("typeDeclaration");
                    } else {
                        continue;
                    }
                    pn.setComparedAttribute(attr2);
                } else if (n instanceof ActionNode) {
                    String type = ((ActionNode) n).getType().name();
                    attr.addValue(type);
                } else {
                    String type = getType(n);
                    if (!type.equals("")) {
                        attr.addValue(type);
                    } else {
                        System.out.println("not attached AST node or spoon node: " + n.toLabelString());
                    }
                }
            }
            pn.setComparedAttribute(attr);
        }
    }
}
