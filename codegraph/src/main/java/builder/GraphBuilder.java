package builder;

import com.sun.org.apache.bcel.internal.classfile.*;
import com.sun.org.apache.bcel.internal.generic.Type;
import model.CodeGraph;
import model.GraphBuildingContext;
import model.GraphConfiguration;
import model.graph.Scope;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import utils.FileIO;
import utils.JavaASTUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.sun.org.apache.bcel.internal.Const.T_UNKNOWN;

public class GraphBuilder {
    private final GraphConfiguration configuration;

    private CompilationUnit currentCU = null;
    private TypeDeclaration currentType = null;

    public GraphBuilder(GraphConfiguration configuration) {
        this.configuration = configuration;
    }

    public Collection<CodeGraph> build(String source, String basePath, String projectName, String[] classpath) {
        return new GraphBuilder(configuration).buildGraphs(source, basePath, projectName, classpath)
                .stream().collect(Collectors.toList());
    }

    public Collection<CodeGraph> build(String sourcePath, String[] classpaths) {
        return new ArrayList<>(new GraphBuilder(configuration).buildBatch(sourcePath, classpaths));
    }
    public Collection<CodeGraph> build(String[] sourcePaths, String[] classpaths) {
        GraphBuilder builder = new GraphBuilder(configuration);
        return Arrays.stream(sourcePaths)
                .flatMap(sourcePath -> builder.buildBatch(sourcePath, classpaths).stream())
                .collect(Collectors.toList());
    }

    public ArrayList<CodeGraph> buildBatch(String path, String[] classpaths) {
        return buildBatchGraphs(new File(path), classpaths);
    }

    private ArrayList<CodeGraph> buildBatchGraphs(File dir, String[] classpaths) {
        ArrayList<File> files = FileIO.getPaths(dir);
        String[] paths = new String[files.size()];
        for (int i = 0; i < files.size(); i++) {
            paths[i] = files.get(i).getAbsolutePath();
        }
        HashMap<String, CompilationUnit> cus = new HashMap<>();
        FileASTRequestor r = new FileASTRequestor() {
            @Override
            public void acceptAST(String sourceFilePath, CompilationUnit cu) {
                cus.put(sourceFilePath, cu);
            }
        };
        @SuppressWarnings("rawtypes")
        Map<String, String> options = JavaCore.getOptions();
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setCompilerOptions(options);
        parser.setEnvironment(
                classpaths == null ? new String[0] : classpaths,
                new String[]{},
                new String[]{},
                true);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.createASTs(paths, null, new String[0], r, null);
        ArrayList<CodeGraph> graphs = new ArrayList<>();
        for (String path : cus.keySet()) {
            CompilationUnit cu = cus.get(path);
            for (int i = 0 ; i < cu.types().size(); i++) {
                currentCU = cu;
                if (cu.types().get(i) instanceof TypeDeclaration) {
                    graphs.addAll(buildGraphs((TypeDeclaration) cu.types().get(i), path, ""));
                }
            }
        }
        // set project name for graph
//        for (CodeGraph g : graphs) {
//            g.setProjectName(dir.getAbsolutePath());
//        }
        return graphs;
    }

    /**
     * Build graph from File (can be singe file or directory)
     */
    public ArrayList<CodeGraph> buildGraphs(File file, String[] classpaths) {
        ArrayList<CodeGraph> graphs = new ArrayList<>();
        if (file.isDirectory()) {
            for (File sub : file.listFiles()) {
                graphs.addAll(buildGraphs(sub, classpaths));
            }
        } else if (file.isFile() && file.getName().endsWith(".java")) {
            String srcCode = FileIO.readStringFromFile(file.getAbsolutePath());
            graphs.addAll(buildGraphs(srcCode, file.getAbsolutePath(), file.getName(), classpaths));
        }
        return graphs;
    }

    /**
     * Build graph from source code string
     */
    public ArrayList<CodeGraph> buildGraphs(String srcCode, String path, String name, String[] classpaths) {
        ArrayList<CodeGraph> graphs = new ArrayList<>();
        CompilationUnit cu = (CompilationUnit) JavaASTUtil.parseSource(srcCode, path, name, classpaths);
        currentCU = cu;
        for (int i = 0; i < cu.types().size(); i++) {
            if (cu.types().get(i) instanceof TypeDeclaration) {
                graphs.addAll(buildGraphs((TypeDeclaration) cu.types().get(i), path, ""));
            }
        }
        return graphs;
    }

    /**
     * Build graph from TypeDeclaration
     */
    public ArrayList<CodeGraph> buildGraphs(TypeDeclaration type, String path, String prefix) {
        ArrayList<CodeGraph> graphs = new ArrayList<>();
        currentType = type;
        for (MethodDeclaration method : type.getMethods()) {
            CodeGraph g = buildGraph(method, path, prefix + type.getName().getIdentifier() + ".");
            graphs.add(g);
        }
        for (TypeDeclaration inner : type.getTypes()) {
            graphs.addAll(buildGraphs(inner, path, prefix + type.getName().getIdentifier() + "."));
        }
        return graphs;
    }

    /**
     * Build graph from MethodDeclaration
     */
    public CodeGraph buildGraph(MethodDeclaration method, String filepath, String name) {
        String sig = JavaASTUtil.buildSignature(method);
        System.out.println(filepath + " " + name + sig);
        CodeGraph g = new CodeGraph(new GraphBuildingContext(), configuration);
        g.setFilePath(filepath);
        g.setName(name + sig);
        g.setCompilationUnit(currentCU);
        g.buildFieldNode(currentType);
        g.setEntryNode(g.buildNode(method, null, new Scope(null)));
        return g;
    }

    /**
     * Transform Type to String
     */
    private String getSimpleType(Type type) {
        return ((type.equals(Type.NULL) || (type.getType() >= T_UNKNOWN)))? type.getSignature() : signatureToString(type.getSignature());
    }

    /*
     * Modify com.sun.org.apache.bcel.internal.classfile.Utility.signatureToString(signature, false)
     */
    private String signatureToString(String signature) {
        try {
            switch(signature.charAt(0)) {
                case 'B' : return "byte";
                case 'C' : return "char";
                case 'D' : return "double";
                case 'F' : return "float";
                case 'I' : return "int";
                case 'J' : return "long";

                case 'L' : { // Full class name
                    int index = signature.indexOf(';'); // Look for closing `;'

                    if(index < 0)
                        throw new ClassFormatException("Invalid signature: " + signature);

                    return compactClassName(signature.substring(1, index));
                }

                case 'S' : return "short";
                case 'Z' : return "boolean";

                case '[' : { // Array declaration
                    int n;
                    StringBuffer brackets;

                    brackets = new StringBuffer(); // Accumulate []'s

                    // Count opening brackets and look for optional size argument
                    for(n=0; signature.charAt(n) == '['; n++)
                        brackets.append("[]");

                    // The rest of the string denotes a `<field_type>'
                    String type = signatureToString(signature.substring(n));

                    return type + brackets.toString();
                }

                case 'V' : return "void";

                default  : throw new ClassFormatException("Invalid signature: `" +
                        signature + "'");
            }
        } catch(StringIndexOutOfBoundsException e) { // Should never occur
            throw new ClassFormatException("Invalid signature: " + e + ":" + signature);
        }
    }

    /*
     * Modify com.sun.org.apache.bcel.internal.classfile.Utility.compactClassName(long class name, false)
     */
    public static final String compactClassName(String className) {
        int index = className.indexOf('<');
        if (index > -1)
            className = className.substring(0, index);
        className = className.replace('/', '.');
        className = className.substring(className.lastIndexOf('.') + 1);
        return className;
    }

    public void setCurrentCU(CompilationUnit cu) {
        currentCU = cu;
    }

    public void setCurrentType(TypeDeclaration type) {
        currentType = type;
    }
}
