package model;

import org.eclipse.jdt.core.dom.*;
import utils.JavaASTUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class GraphBuildingContext {
    public static HashMap<String, HashMap<String, String>> typeFieldType = new HashMap<>();
    public static HashMap<String, HashMap<String, HashSet<String>>> typeMethodExceptions = new HashMap<>();
    public static HashMap<String, HashSet<String>> exceptionHierarchy = new HashMap<>();

    private MethodDeclaration method;
    private String type = "this", superType = "Object";
    private Stack<HashMap<String, String>> localVariables = new Stack<>(), localVariableTypes = new Stack<>();
    private HashMap<String, String> fieldTypes = new HashMap<>();

    public void setMethod(MethodDeclaration method) {
        this.method = method;
        ASTNode p = this.method.getParent();
        if (p != null) {
            if (p instanceof AbstractTypeDeclaration) {
                AbstractTypeDeclaration atd = (AbstractTypeDeclaration) p;
                this.type = atd.getName().getIdentifier();
            }
            if (p instanceof TypeDeclaration) {
                buildFieldType((TypeDeclaration) p);
                TypeDeclaration td = (TypeDeclaration) p;
                if (td.getSuperclassType() != null)
                    this.superType = JavaASTUtil.getSimpleType(td.getSuperclassType());
            }
            else if (p instanceof EnumDeclaration)
                buildFieldType((EnumDeclaration) p);
        }
    }

    private void buildFieldType(TypeDeclaration td) {
        for (FieldDeclaration f : td.getFields()) {
            String type = JavaASTUtil.getSimpleType(f.getType());
            for (int i = 0; i < f.fragments().size(); i++) {
                VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(i);
                String dimensions = "";
                for (int j = 0; j < vdf.getExtraDimensions(); j++)
                    dimensions += "[]";
                buildFieldType(vdf.getName().getIdentifier(), type + dimensions);
            }
        }
        ASTNode p = td.getParent();
        if (p != null) {
            if (p instanceof TypeDeclaration)
                buildFieldType((TypeDeclaration) p);
            else if (p instanceof EnumDeclaration)
                buildFieldType((EnumDeclaration) p);
        }
    }

    private void buildFieldType(EnumDeclaration ed) {
        for (int i = 0; i < ed.bodyDeclarations().size(); i++) {
            if (ed.bodyDeclarations().get(i) instanceof FieldDeclaration) {
                FieldDeclaration f = (FieldDeclaration) ed.bodyDeclarations().get(i);
                String type = JavaASTUtil.getSimpleType(f.getType());
                for (int j = 0; j < f.fragments().size(); j++) {
                    VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(j);
                    String dimensions = "";
                    for (int k = 0; k < vdf.getExtraDimensions(); k++)
                        dimensions += "[]";
                    buildFieldType(vdf.getName().getIdentifier(), type + dimensions);
                }
            }
        }
        ASTNode p = ed.getParent();
        if (p != null) {
            if (p instanceof TypeDeclaration)
                buildFieldType((TypeDeclaration) p);
            else if (p instanceof EnumDeclaration)
                buildFieldType((EnumDeclaration) p);
        }
    }

    private void buildFieldType(String name, String type) {
        if (!this.fieldTypes.containsKey(name))
            this.fieldTypes.put(name, type);
    }

    public void addLocalVariable(String identifier, String key, String type) {
        this.localVariables.peek().put(identifier, key);
        this.localVariableTypes.peek().put(identifier, type);
    }

    public void addScope() {
        this.localVariables.push(new HashMap<String, String>());
        this.localVariableTypes.push(new HashMap<String, String>());
    }

    public void removeScope() {
        this.localVariables.pop();
        this.localVariableTypes.pop();
    }

    public String[] getLocalVariableInfo(String identifier) {
        for (int i = localVariables.size() - 1; i >= 0; i--) {
            HashMap<String, String> variables = this.localVariables.get(i);
            if (variables.containsKey(identifier))
                return new String[]{variables.get(identifier), this.localVariableTypes.get(i).get(identifier)};
        }
        return null;
    }

    public String getFieldType(SimpleName node) {
        if (node.resolveTypeBinding() != null) {
            return node.resolveTypeBinding().getTypeDeclaration().getName();
        }
        String name = node.getIdentifier();
        String type = this.fieldTypes.get(name);
        if (type == null) {
            buildSuperFieldTypes();
            type = this.fieldTypes.get(name);
        }
        return type;
    }

    public void buildSuperFieldTypes() {
        ASTNode p = this.method.getParent();
        if (p instanceof TypeDeclaration)
            buildSuperFieldTypes((TypeDeclaration) p);
    }

    private void buildSuperFieldTypes(TypeDeclaration td) {
        if (td.getSuperclassType() != null) {
            String stype = JavaASTUtil.getSimpleType(td.getSuperclassType());
            buildSuperFieldTypes(stype);
        }
        ASTNode p = td.getParent();
        if (p instanceof TypeDeclaration)
            buildSuperFieldTypes((TypeDeclaration) p);
    }

    private void buildSuperFieldTypes(String stype) {
        ArrayList<String> qns = getQualifiedTypes(stype);
        for (String qn : qns) {
            HashMap<String, String> superFieldType = typeFieldType.get(qn);
            if (superFieldType != null) {
                for (String name : superFieldType.keySet())
                    buildFieldType(name, superFieldType.get(name));
                break;
            }
        }
    }

    private ArrayList<String> getQualifiedTypes(String stype) {
        ArrayList<String> qts = new ArrayList<>();
        int index = stype.indexOf('.');
        String qual = null;
        if (index > -1) {
            if (Character.isLowerCase(stype.charAt(0))) {
                qts.add(stype);
                return qts;
            }
            qual = stype.substring(0, index);
        }
        CompilationUnit cu = (CompilationUnit) this.method.getRoot();
        for (int i = 0; i < cu.imports().size(); i++) {
            ImportDeclaration id = (ImportDeclaration) cu.imports().get(i);
            if (id.isOnDemand()) {
                qts.add(id.getName().getFullyQualifiedName() + "." + stype);
            } else if (!id.isStatic()) {
                String qn = id.getName().getFullyQualifiedName();
                if (qn.endsWith("." + stype)) {
                    qts.add(qn);
                    return qts;
                }
                if (qual != null && qn.endsWith("." + qual)) {
                    qts.add(qn + stype.substring(qual.length()));
                    return qts;
                }
            }
        }
        String pkg = "";
        if (cu.getPackage() != null)
            pkg = cu.getPackage().getName().getFullyQualifiedName();
        qts.add(0, pkg + "." + stype);
        return qts;
    }

    public String getType() {
        return type;
    }

    public static void buildExceptionHierarchy() {
        HashSet<String> exceptionTypes = new HashSet<>();
        buildExceptionHierarchy("Throwable", exceptionTypes);
        for (String type : new HashSet<String>(exceptionHierarchy.keySet()))
            if (!exceptionTypes.contains(type))
                exceptionHierarchy.remove(type);
    }

    private static void buildExceptionHierarchy(String type, HashSet<String> exceptionTypes) {
        if (exceptionTypes.contains(type))
            return;
        exceptionTypes.add(type);
        HashSet<String> subs = exceptionHierarchy.get(type);
        if (subs != null)
            for (String sub : subs)
                buildExceptionHierarchy(sub, exceptionTypes);
    }
}
