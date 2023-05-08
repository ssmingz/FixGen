package builder;

import model.CodeGraph;
import model.graph.Scope;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.CtMethodImpl;
import utils.ASTUtil;

public class GraphBuilder {
    /**
     * build a code graph by given an input file path,
     * includeLines contains line numbers of lines in diff file (set empty when using C3 dataset)
     */
    public static CodeGraph buildGraph(String inputPath, String[] classPaths, int compileLevel, int[] includeLines) {
        Launcher launcher = new Launcher();

        launcher.addInputResource(inputPath);
        if (classPaths.length > 0)
            launcher.getEnvironment().setSourceClasspath(classPaths);
        launcher.getEnvironment().setComplianceLevel(compileLevel);

        launcher.buildModel();
        CtModel model = launcher.getModel();

        for (CtElement method : model.getElements(s -> s instanceof CtMethodImpl)) {
            if (checkByLine((CtMethodImpl) method, includeLines)) {
                CodeGraph graph = buildGraph((CtMethodImpl) method);
                return graph;
            }
        }
        return null;
    }

    private static boolean checkByLine(CtMethodImpl method, int[] includeLines) {
        int start = method.getPosition().getLine();
        int end = method.getPosition().getEndLine();
        for (int line : includeLines) {
            if (line < start || line > end) {
                return false;
            }
        }
        return true;
    }

    public static CodeGraph buildGraph(CtMethodImpl ctMethod) {
        String sig = ASTUtil.buildSignature(ctMethod);
        CodeGraph g = new CodeGraph();
        g.setCtMethod(ctMethod);
        g.setName(sig);
        g.setEntryNode(g.buildNode(ctMethod, null, new Scope(null)));
        return g;
    }
}
