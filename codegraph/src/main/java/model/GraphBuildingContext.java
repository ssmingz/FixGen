package model;

import org.eclipse.jdt.core.dom.*;
import utils.JavaASTUtil;

import java.util.HashMap;
import java.util.Stack;

public class GraphBuildingContext {
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

}
