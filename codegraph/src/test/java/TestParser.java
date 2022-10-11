import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.junit.Test;
import static org.junit.Assert.*;

import utils.FileIO;
import utils.JavaASTUtil;


public class TestParser {
    private static ASTParser parser;
    private static HashMap<String, ASTNode> asts = new HashMap<>();
    private static int mismatches = 0;

    @Test
    public void testParse() {
        ArrayList<File> files = getPaths(new String[] {"D:\\expdata\\code\\FixGen\\codegraph\\src"});
        parse(files);
        parse(files);
    }

    @Test
    public void testParseSource() {
        String srcpath = "D:\\expdata\\code\\FixGen\\codegraph\\src\\main\\java\\utils\\FileIO.java";
        ASTNode cu = JavaASTUtil.parseSource(FileIO.readStringFromFile(srcpath), srcpath, "FileIO.java", null);
        assertTrue(cu instanceof CompilationUnit);
    }

    private static ArrayList<File> getPaths(String[] roots) {
        ArrayList<File> files = new ArrayList<>();
        for (String root : roots) {
            files.addAll(getPaths(new File(root)));
        }
        return files;
    }

    private static ArrayList<File> getPaths(File dir) {
        ArrayList<File> files = new ArrayList<>();
        if (dir.isDirectory()) {
            for (File sub : dir.listFiles()) {
                files.addAll(getPaths(sub));
            }
        }
        else if (dir.getName().endsWith(".java")) {
            files.add(dir);
        }
        return files;
    }

    public static void parse(ArrayList<File> files) {
        long start = System.currentTimeMillis();
        mismatches = 0;
        for (File file : files) {
            String name = file.getName();
            String source = FileIO.readStringFromFile(file.getAbsolutePath());
            init();
            parser.setSource(source.toCharArray());
            parser.setUnitName(name);
            ASTNode node = parser.createAST(null);
            ASTNode ast = asts.get(file.getAbsolutePath());
            if (ast == null) {
                asts.put(file.getAbsolutePath(), node);
            } else {
                if (!ast.subtreeMatch(new ASTMatcher() {
                    @Override
                    public boolean match(MethodInvocation node, Object other) {
                        if (super.match(node, other)) {
                            if (node.resolveMethodBinding() == null)
                                return false;
                            MethodInvocation mi = (MethodInvocation) other;
                            if (node.resolveMethodBinding().isEqualTo(mi.resolveMethodBinding()))
                                return true;
                            return false;
                        }
                        return false;
                    }
                }, node)) {
                    mismatches++;
                    System.err.println("Not matched!!! " + mismatches);
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Online: " + (end - start) + "ms");
    }

    public static void init() {
        @SuppressWarnings("rawtypes")
        Map<String, String> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        parser = ASTParser.newParser(AST.JLS8);
        parser.setCompilerOptions(options);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);

        String property = System.getProperty("java.class.path", ".");
        parser.setEnvironment(
                property.split(File.pathSeparator),
                new String[]{},
                new String[]{},
                true);
    }

}
