package utils;

import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

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
                classpaths == null ? new String[]{} : classpaths,
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
    public static ASTNode parseSource(String source) {
        Map options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setCompilerOptions(options);
        parser.setSource(source.toCharArray());
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

}

