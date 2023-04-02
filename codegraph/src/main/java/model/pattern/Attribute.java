package model.pattern;

import model.CodeGraph;
import model.graph.node.Node;
import model.graph.node.PatchNode;
import model.graph.node.actions.ActionNode;
import model.graph.node.expr.ExprNode;
import model.graph.node.expr.SimpName;
import org.eclipse.jdt.core.dom.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtExpressionImpl;
import spoon.support.reflect.code.CtStatementImpl;
import spoon.support.reflect.code.CtVariableAccessImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.declaration.CtTypeImpl;

import java.lang.reflect.Field;
import java.util.*;

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

    public Set<String> getValueSet() {
        return _numByValues.keySet();
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

    public static String getRoleInParent(Node n) {
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

    public static String getType(Node n) {
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

    public static String getCamelCaseName(String name) {
        String s = name.toLowerCase();
        String[] tokens = s.split("_");
        if (tokens.length == 1) {
            return s;
        } else {
            StringBuilder buffer = new StringBuilder(tokens[0]);
            for (int i = 1; i < tokens.length; i++) {
                String t = tokens[i];
                buffer.append(Character.toUpperCase(t.charAt(0)));
                buffer.append(t.substring(1));
            }
            return buffer.toString();
        }
    }

    public static boolean isIdentifierValid(char c) {
        if (c == 36 || c == 95)
            return true;
        else if (c>=48 && c<=57)
            return true;
        else if (c>=65 && c<=90)
            return true;
        else if (c>=97 && c<=122)
            return true;
        else
            return false;
    }

    public void computeValueName(Node n, CodeGraph cg) {
        // only for expression node
        if (n instanceof ExprNode) {
            String name = n.toLabelString();
            // replace variable name with type name
            if (!(n instanceof SimpName)) {
                for (Map.Entry<String, String> entry : cg.getTypeByNameMap().entrySet()) {
                    // TODO: replace name with tokenization
                    String key = entry.getKey();
                    String value = entry.getValue();
                    StringBuilder nameNew = new StringBuilder();
                    for (int start=0, end=name.indexOf(key); start<name.length()&&end<name.length(); ) {
                        if (end>=start) {
                            nameNew.append(name, start, end);
                            if (end==0 && !isIdentifierValid(name.charAt(end + key.length()))) {
                                nameNew.append(value);
                            } else if (end == name.length()-1 && !isIdentifierValid(name.charAt(end-1))) {
                                nameNew.append(value);
                            } else if (end>0 && end+key.length()<name.length() && !isIdentifierValid(name.charAt(end + key.length())) && !isIdentifierValid(name.charAt(end-1))) {
                                nameNew.append(value);
                            } else {
                                nameNew.append(key);
                            }
                            start = end + key.length();
                            end = name.indexOf(key, start);
                        } else {
                            nameNew.append(name.substring(start));
                            break;
                        }
                    }
                    name = String.valueOf(nameNew);
                }
            }
            addValue(name);
        } else if (n instanceof PatchNode) {
            CtElement cte = ((PatchNode)n).getSpoonNode();
            if (cte instanceof CtExpressionImpl) {
                String name = cte.toString();
                // replace variable name with type name
                if (!(cte instanceof CtVariableAccessImpl)) {
                    for (Map.Entry<String, String> entry : cg.getTypeByNameMap().entrySet())
                        name = name.replaceAll(entry.getKey(), entry.getValue());
                }
                addValue(name);
            }
        }
    }

    public void computeValueType(Node n, CodeGraph cg) {
        // only for expression node
        if (n instanceof ExprNode) {
            String type = cg.getTypeByName(n.toLabelString());
            addValue(type);
        } else if (n instanceof PatchNode) {
            CtElement cte = ((PatchNode)n).getSpoonNode();
            if (cte instanceof CtExpressionImpl) {
                CtTypeReference<?> ctype = ((CtExpressionImpl<?>) cte).getType();
                if (ctype != null) {
                    addValue(ctype.getSimpleName());
                }
            }
        }
    }

    public void computeLocationInParent(Node n) {
        // only for node
        if (n.getASTNode() != null) {
            String loc = n.getASTNode().getLocationInParent().getId();
            addValue(loc);
        } else if (n instanceof PatchNode) {
            String loc = ((PatchNode)n).getSpoonNode().getRoleInParent().getCamelCaseName();
            addValue(loc);
        } else if (n instanceof ActionNode) {
            String loc = "action";
            addValue(loc);
        } else {
            String loc = getRoleInParent(n);
            if (!loc.equals("")) {
                if (loc.startsWith("_"))
                    loc = loc.substring(1);
                addValue(loc);
            } else {
                System.out.println("not attached AST node or spoon node: " + n.toLabelString());
            }
        }
    }

    public void computeNodeType(Node n) {
        // only for node
        if (n.getASTNode() != null) {
            // nodeType
            String type = n.getASTNode().getClass().getSimpleName();
            addValue(type);
        } else if (n instanceof PatchNode) {
            // nodeType
            String type = ((PatchNode)n).getSpoonNode().getClass().getSimpleName();
            addValue(type);
        } else if (n instanceof ActionNode) {
            String type = ((ActionNode) n).getType().name();
            addValue(type);
        } else {
            String type = getType(n);
            if (!type.equals("")) {
                addValue(type);
            } else {
                System.out.println("not attached AST node or spoon node: " + n.toLabelString());
            }
        }
    }

    public void computeNodeTypeHighLevel(Node n) {
        // only for node
        if (n.getASTNode() != null) {
            if (n.getASTNode() instanceof Expression) {
                addValue("expression");
            } else if (n.getASTNode() instanceof Statement) {
                addValue("statement");
            } else if (n.getASTNode() instanceof MethodDeclaration) {
                addValue("methodDeclaration");
            } else if (n.getASTNode() instanceof TypeDeclaration) {
                addValue("typeDeclaration");
            }
        } else if (n instanceof PatchNode) {
            if (((PatchNode)n).getSpoonNode() instanceof CtExpressionImpl) {
                addValue("expression");
            } else if (((PatchNode)n).getSpoonNode() instanceof CtStatementImpl) {
                addValue("statement");
            } else if (((PatchNode)n).getSpoonNode() instanceof CtMethodImpl) {
                addValue("methodDeclaration");
            } else if (((PatchNode)n).getSpoonNode() instanceof CtTypeImpl) {
                addValue("typeDeclaration");
            }
        }
    }

    public void computeCodeContent(Node n) {
        if (n instanceof PatchNode) {
            addValue(((PatchNode) n).getSpoonNode().toString());  // TODO: abstract variable type
        }
    }
}
