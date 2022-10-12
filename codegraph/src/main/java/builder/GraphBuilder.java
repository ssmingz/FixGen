package builder;

import com.sun.org.apache.bcel.internal.classfile.*;
import com.sun.org.apache.bcel.internal.generic.Type;
import model.CodeGraph;
import model.GraphBuildingContext;
import model.GraphConfiguration;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import utils.FileIO;
import utils.JavaASTUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static com.sun.org.apache.bcel.internal.Const.T_UNKNOWN;

public class GraphBuilder {
    private final GraphConfiguration configuration;

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
        buildStandardJars();
        buildHierarchy(new File(path));
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
            for (int i = 0 ; i < cu.types().size(); i++)
                if (cu.types().get(i) instanceof TypeDeclaration)
                    graphs.addAll(buildGraphs((TypeDeclaration) cu.types().get(i), path, ""));
        }
        for (CodeGraph g : graphs) {
            g.setProjectName(dir.getAbsolutePath());
        }
        return graphs;
    }


    private void buildStandardJars() {
        String jrePath = System.getProperty("java.home") + "/lib";
        buildJar(jrePath + "/rt.jar");
    }

    private void buildJar(String jarFilePath) {
        try {
            JarFile jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entries = jarFile.entries();
            while(entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if(entry.getName().endsWith(".class") && entry.getName().startsWith("java")) {
                    try {
                        ClassParser parser = new ClassParser(jarFilePath, entry.getName());
                        JavaClass jc = parser.parse();
                        String className = jc.getClassName();
                        className = className.replace('$', '.');
                        HashMap<String, String> fieldTypes = GraphBuildingContext.typeFieldType.get(className);
                        if (fieldTypes == null)
                            fieldTypes = new HashMap<>();
                        for (Field field : jc.getFields())
                            buildJar(field, fieldTypes);
                        if (!fieldTypes.isEmpty())
                            GraphBuildingContext.typeFieldType.put(className, fieldTypes);
                        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
                        HashMap<String, HashSet<String>> methodExceptions = GraphBuildingContext.typeMethodExceptions.get(simpleClassName);
                        if (methodExceptions == null)
                            methodExceptions = new HashMap<>();
                        for (Method method : jc.getMethods())
                            buildJar(method, methodExceptions);
                        if (!methodExceptions.isEmpty())
                            GraphBuildingContext.typeMethodExceptions.put(simpleClassName, methodExceptions);
                        if (jc.getSuperclassName() != null) {
                            String stype = FileIO.getSimpleClassName(jc.getSuperclassName());
                            HashSet<String> subs =  GraphBuildingContext.exceptionHierarchy.get(stype);
                            if (subs == null) {
                                subs = new HashSet<>();
                                GraphBuildingContext.exceptionHierarchy.put(stype, subs);
                            }
                            subs.add(simpleClassName);
                        }
                    } catch (IOException | ClassFormatException e) {
                        System.err.println("Error in parsing class file: " + entry.getName());
                        System.err.println(e.getMessage());
                    }
                }
            }
            jarFile.close();
        } catch (IOException e) {
            System.err.println("Error in parsing jar file: " + jarFilePath);
            System.err.println(e.getMessage());
        }
    }

    private void buildJar(Field field, HashMap<String, String> fieldTypes) {
        String name = field.getName();
        if (name.startsWith("this$"))
            return;
        String type = getSimpleType(field.getType());
        fieldTypes.put(name, type);
    }

    private void buildJar(Method method, HashMap<String, HashSet<String>> methodExceptions) {
        String name = method.getName();
        name += "(" + method.getArgumentTypes().length + ")";
        HashSet<String> exceptions = methodExceptions.get(name);
        if (exceptions == null)
            exceptions = new HashSet<>();
        if (method.getExceptionTable() != null)
            for (String exception: method.getExceptionTable().getExceptionNames())
                exceptions.add(exception.substring(exception.lastIndexOf('.') + 1));
        if (!exceptions.isEmpty())
            methodExceptions.put(name, exceptions);
    }

    private void buildHierarchy(File file) {
        if (file.isDirectory()) {
            for (File sub : file.listFiles())
                buildHierarchy(sub);
        } else if (file.isFile()) {
            if (file.getName().endsWith(".jar"))
                buildJar(file.getAbsolutePath());
            else if (file.getName().endsWith(".java")) {
                try {
                    CompilationUnit cu = (CompilationUnit) JavaASTUtil.parseSource(FileIO.readStringFromFile(file.getAbsolutePath()), null);
                    for (int i = 0 ; i < cu.types().size(); i++)
                        buildHierarchy((AbstractTypeDeclaration) cu.types().get(i), cu.getPackage() == null ? "" : cu.getPackage().getName().getFullyQualifiedName() + ".");
                } catch (Exception e) {
                    System.err.println("Failed to parse file " + file.getAbsolutePath() + ": " + e.getClass().getName());
                    // TODO Suppress runtime problems with unknown reason
                }
            }
        }
        GraphBuildingContext.buildExceptionHierarchy();
    }

    private void buildHierarchy(AbstractTypeDeclaration type, String prefix) {
        if (type instanceof TypeDeclaration)
            buildHierarchy((TypeDeclaration) type, prefix);
        else if (type instanceof EnumDeclaration)
            buildHierarchy((EnumDeclaration) type, prefix);
        else if (type instanceof AnnotationTypeDeclaration)
            buildHierarchy((AnnotationTypeDeclaration) type, prefix);
    }

    private void buildHierarchy(TypeDeclaration type, String prefix) {
        String className = prefix + type.getName().getIdentifier();
        if (type.getSuperclassType() != null) {
            String stype = JavaASTUtil.getSimpleType(type.getSuperclassType());
            HashSet<String> subs =  GraphBuildingContext.exceptionHierarchy.get(stype);
            if (subs == null) {
                subs = new HashSet<>();
                GraphBuildingContext.exceptionHierarchy.put(stype, subs);
            }
            subs.add(className);
        }
        HashMap<String, String> fieldTypes = GraphBuildingContext.typeFieldType.get(className);
        if (fieldTypes == null)
            fieldTypes = new HashMap<>();
        for (FieldDeclaration field : type.getFields())
            buildHierarchy(field, fieldTypes);
        if (!fieldTypes.isEmpty())
            GraphBuildingContext.typeFieldType.put(className, fieldTypes);
        HashMap<String, HashSet<String>> methodExceptions = GraphBuildingContext.typeMethodExceptions.get(className);
        if (methodExceptions == null)
            methodExceptions = new HashMap<>();
        for (MethodDeclaration method : type.getMethods())
            buildHierarchy(method, methodExceptions);
        if (!methodExceptions.isEmpty())
            GraphBuildingContext.typeMethodExceptions.put(className, methodExceptions);
        for (TypeDeclaration inner : type.getTypes())
            buildHierarchy(inner, className + ".");
    }

    private void buildHierarchy(EnumDeclaration ed, String prefix) {
        String className = prefix + ed.getName().getIdentifier();
        HashMap<String, String> fieldTypes = GraphBuildingContext.typeFieldType.get(className);
        if (fieldTypes == null)
            fieldTypes = new HashMap<>();
        HashMap<String, HashSet<String>> methodExceptions = GraphBuildingContext.typeMethodExceptions.get(className);
        if (methodExceptions == null)
            methodExceptions = new HashMap<>();
        for (int i = 0; i < ed.bodyDeclarations().size(); i++) {
            BodyDeclaration bd = (BodyDeclaration) ed.bodyDeclarations().get(i);
            if (bd instanceof FieldDeclaration)
                buildHierarchy((FieldDeclaration) bd, fieldTypes);
            else if (bd instanceof MethodDeclaration)
                buildHierarchy((MethodDeclaration) bd, methodExceptions);
        }
        if (!fieldTypes.isEmpty())
            GraphBuildingContext.typeFieldType.put(className, fieldTypes);
        if (!methodExceptions.isEmpty())
            GraphBuildingContext.typeMethodExceptions.put(className, methodExceptions);
    }

    private void buildHierarchy(AnnotationTypeDeclaration type, String prefix) {
        // TODO
    }

    private void buildHierarchy(FieldDeclaration f, HashMap<String, String> fieldTypes) {
        String type = JavaASTUtil.getSimpleType(f.getType());
        for (int j = 0; j < f.fragments().size(); j++) {
            VariableDeclarationFragment vdf = (VariableDeclarationFragment) f.fragments().get(j);
            String dimensions = "";
            for (int i = 0; i < vdf.getExtraDimensions(); i++)
                dimensions += "[]";
            fieldTypes.put(vdf.getName().getIdentifier(), type + dimensions);
        }
    }

    private void buildHierarchy(MethodDeclaration method, HashMap<String, HashSet<String>> methodExceptions) {
        String name = method.getName().getIdentifier();
        if (method.isConstructor())
            name = "<init>";
        name += "(" + method.parameters().size() + ")";
        HashSet<String> exceptions = methodExceptions.get(name);
        if (exceptions == null)
            exceptions = new HashSet<>();
        for (int i = 0; i < method.thrownExceptionTypes().size(); i++)
            exceptions.add(JavaASTUtil.getSimpleType((org.eclipse.jdt.core.dom.Type)method.thrownExceptionTypes().get(i)));
        if (!exceptions.isEmpty())
            methodExceptions.put(name, exceptions);
    }


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

    public ArrayList<CodeGraph> buildGraphs(String srcCode, String path, String name, String[] classpaths) {
        ArrayList<CodeGraph> graphs = new ArrayList<>();
        CompilationUnit cu = (CompilationUnit) JavaASTUtil.parseSource(srcCode, path, name, classpaths);
        for (int i = 0; i < cu.types().size(); i++) {
            if (cu.types().get(i) instanceof TypeDeclaration) {
                graphs.addAll(buildGraphs((TypeDeclaration) cu.types().get(i), path, ""));
            }
        }
        return graphs;
    }

    public ArrayList<CodeGraph> buildGraphs(TypeDeclaration type, String path, String prefix) {
        ArrayList<CodeGraph> graphs = new ArrayList<>();
        for (MethodDeclaration method : type.getMethods()) {
            CodeGraph g = buildGraph(method, path, prefix + type.getName().getIdentifier() + ".");
            graphs.add(g);
        }
        for (TypeDeclaration inner : type.getTypes()) {
            graphs.addAll(buildGraphs(inner, path, prefix + type.getName().getIdentifier() + "."));
        }
        return graphs;
    }

    public CodeGraph buildGraph(MethodDeclaration method, String filepath, String name) {
        String sig = JavaASTUtil.buildSignature(method);
        System.out.println(filepath + " " + name + sig);
        CodeGraph g = new CodeGraph(method, new GraphBuildingContext(), configuration);
        g.setFilePath(filepath);
        g.setName(name + sig);
        return g;
    }

    private String getSimpleType(Type type) {
        return ((type.equals(Type.NULL) || (type.getType() >= T_UNKNOWN)))? type.getSignature() : signatureToString(type.getSignature());
    }

    /*
     * Modify com.sun.org.apache.bcel.internal.classfile.Utility.signatureToString(signature, false)
     */
    private String signatureToString(String signature) {
        try {
            switch(signature.charAt(0)) {
                case 'B' : return "number"; //return "byte";
                case 'C' : return "char";
                case 'D' : return "number"; //return "double";
                case 'F' : return "number"; //return "float";
                case 'I' : return "number"; //return "int";
                case 'J' : return "number"; //return "long";

                case 'L' : { // Full class name
                    int index = signature.indexOf(';'); // Look for closing `;'

                    if(index < 0)
                        throw new ClassFormatException("Invalid signature: " + signature);

                    return compactClassName(signature.substring(1, index));
                }

                case 'S' : return "number"; //return "short";
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
        className = className.replace('/', '.'); // Is `/' on all systems, even DOS
        className = className.substring(className.lastIndexOf('.') + 1);
        return className;
    }
}
