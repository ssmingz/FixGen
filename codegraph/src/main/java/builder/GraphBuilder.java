package builder;

import model.CodeGraph;
import model.GraphBuildingContext;
import model.GraphConfiguration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import utils.FileIO;
import utils.JavaASTUtil;

import java.io.File;
import java.util.ArrayList;

public class GraphBuilder {
    private final GraphConfiguration configuration;
    public GraphBuilder(GraphConfiguration configuration) {
        this.configuration = configuration;
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
}
