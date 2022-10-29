package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import model.CodeGraph;
import model.graph.node.Node;
import model.graph.node.expr.AssignExpr;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

public class JavaASTUtil {
    @SuppressWarnings("rawtypes")
    public static ASTNode parseSource(String source, String path, String name, String[] classpaths) {
        Map options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        String srcDir = getSrcDir(source, path, name);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setCompilerOptions(options);
        parser.setEnvironment(
                classpaths == null ? System.getProperty("java.class.path", ".").split(File.pathSeparator) : classpaths,
                new String[]{srcDir},
                new String[]{"UTF-8"},
                true);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setSource(source.toCharArray());
        parser.setUnitName(name);
        return parser.createAST(null);
    }

    @SuppressWarnings("rawtypes")
    public static ASTNode parseSource(String source, String[] classpaths) {
        Map options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setCompilerOptions(options);
        parser.setSource(source.toCharArray());
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setEnvironment(
                classpaths == null ? new String[0] : classpaths,
                new String[]{},
                new String[]{},
                true);
        ASTNode ast = parser.createAST(null);
        return ast;
    }

    @SuppressWarnings("rawtypes")
    private static String getSrcDir(String source, String path, String name) {
        Map options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setCompilerOptions(options);
        parser.setSource(source.toCharArray());
        ASTNode ast = parser.createAST(null);
        CompilationUnit cu =  (CompilationUnit) ast;
        String srcDir = path;
        if (cu.getPackage() != null) {
            String p = cu.getPackage().getName().getFullyQualifiedName();
            int end = path.length() - p.length() - 1 - name.length();
            if (end > 0)
                srcDir = path.substring(0, end);
        } else {
            int end = path.length() - name.length();
            if (end > 0)
                srcDir = path.substring(0, end);
        }
        return srcDir;
    }

    public static String buildSignature(MethodDeclaration method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName().getIdentifier() + "#");
        for (int i = 0; i < method.parameters().size(); i++) {
            SingleVariableDeclaration svd = (SingleVariableDeclaration) method.parameters().get(i);
            sb.append(JavaASTUtil.getSimpleType(svd.getType()) + "#");
        }
        return sb.toString();
    }

    public static String buildSignature(AbstractTypeDeclaration type) {
        if (type.getRoot() instanceof CompilationUnit) {
            CompilationUnit cu = (CompilationUnit) type.getRoot();
            String prefix = cu.getPackage() == null ? "" : cu.getPackage().getName().getFullyQualifiedName() + ".";
            String classname = prefix + type.getName().getIdentifier();
            return classname;
        }
        return null;
    }

    public static String getSimpleType(Type type) {
        if (type.isArrayType()) {
            ArrayType t = (ArrayType) type;
            String pt = getSimpleType(t.getElementType());
            for (int i = 0; i < t.getDimensions(); i++)
                pt += "[]";
            return pt;
        } else if (type.isParameterizedType()) {
            ParameterizedType t = (ParameterizedType) type;
            return getSimpleType(t.getType());
        } else if (type.isPrimitiveType()) {
            String pt = type.toString();
            return pt;
        } else if (type.isQualifiedType()) {
            QualifiedType t = (QualifiedType) type;
            return t.getName().getIdentifier();
        } else if (type.isSimpleType()) {
            SimpleType st = (SimpleType) type;
            String pt = st.getName().getFullyQualifiedName();
            if (st.getName() instanceof QualifiedName)
                pt = getSimpleName(st.getName());
            if (pt.isEmpty())
                pt = st.getName().getFullyQualifiedName();
            return pt;
        } else if (type.isIntersectionType()) {
            IntersectionType it = (IntersectionType) type;
            @SuppressWarnings("unchecked")
            ArrayList<Type> types = new ArrayList<>(it.types());
            String s = getSimpleType(types.get(0));
            for (int i = 1; i < types.size(); i++)
                s += "&" + getSimpleType(types.get(i));
            return s;
        }  else if (type.isUnionType()) {
            UnionType ut = (UnionType) type;
            String s = getSimpleType((Type) ut.types().get(0));
            for (int i = 1; i < ut.types().size(); i++)
                s += "|" + getSimpleType((Type) ut.types().get(i));
            return s;
        } else if (type.isWildcardType()) {
            WildcardType t = (WildcardType) type;
            return getSimpleType(t.getBound());
        } else if (type.isNameQualifiedType()) {
            NameQualifiedType nqt = (NameQualifiedType) type;
            return nqt.getName().getIdentifier();
        } else if (type.isAnnotatable()) {
            return type.toString();
        }
        System.err.println("ERROR: Declare a variable with unknown type!!!");
        System.exit(1);
        return null;
    }

    public static String getSimpleName(Name name) {
        if (name.isSimpleName()) {
            SimpleName sn = (SimpleName) name;
            if (Character.isUpperCase(sn.getIdentifier().charAt(0)))
                return sn.getIdentifier();
            return "";
        }
        QualifiedName qn = (QualifiedName) name;
        if (Character.isUpperCase(qn.getFullyQualifiedName().charAt(0)))
            return qn.getFullyQualifiedName();
        String sqn = getSimpleName(qn.getQualifier());
        if (sqn.isEmpty())
            return getSimpleName(qn.getName());
        return sqn + "." + qn.getName().getIdentifier();
    }

    public static boolean isConstant(IVariableBinding vb) {
        if (vb.isEnumConstant())
            return true;
        int modifiers = vb.getModifiers();
        return Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers);
    }

    public static String buildGraphSignature(CodeGraph cg) {
        String filename = cg.getFilePath();
        String methodname = cg.getGraphName();
        return filename + " " + methodname;
    }
}

