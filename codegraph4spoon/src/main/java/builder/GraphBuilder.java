package builder;

import codegraph.Scope;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.*;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.Delete;
import model.actions.Insert;
import model.actions.Move;
import model.actions.Update;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.CtElementImpl;
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
        g.setFileName(ctMethod.getPosition().getFile().getAbsolutePath());
        g.buildNode(ctMethod, null, new Scope(null));
        g.setEntryNode(ctMethod);
        return g;
    }

    public static CodeGraph buildActionGraph(String srcPath, String tarPath) {
        // code graph
        CodeGraph cg1 = GraphBuilder.buildGraph(srcPath, new String[] {}, 8, new int[] {});
        CodeGraph cg2 = GraphBuilder.buildGraph(tarPath, new String[] {}, 8, new int[] {});
        // gumtree diff
        AstComparator diff = new AstComparator();
        Diff editScript = diff.compare(cg1.getEntryNode(), cg2.getEntryNode());
        // attach CtElement mapping
        cg1.setMappingStore(editScript.getMappingsComp());
        // add actions to src graph
        // TODO: add relationships in dst graph
        // TODO: add dst children to allNodes
        for (Operation op : editScript.getRootOperations()) {
            if (op instanceof DeleteOperation) {
                CtElementImpl src = (CtElementImpl) op.getSrcNode();
                Delete del = new Delete(src, op);
                cg1.updateCGId(del);
            } else if (op instanceof UpdateOperation) {
                CtElementImpl src = (CtElementImpl) op.getSrcNode();
                CtElementImpl dst = (CtElementImpl) op.getDstNode();
                Update upd = new Update(src, dst, op);
                cg1.updateCGId(upd);
                cg1.updateCGId(dst);
            } else if (op instanceof InsertOperation) {
                CtElementImpl src = (CtElementImpl) op.getSrcNode();
                CtElementImpl parent = (CtElementImpl) ((InsertOperation) op).getParent();
                int pos = ((InsertOperation) op).getPosition();
                Insert ins = new Insert(src, parent, pos, op);
                cg1.updateCGId(ins);
                cg1.updateCGId(src);
            } else if (op instanceof MoveOperation) {
                CtElementImpl src = (CtElementImpl) op.getSrcNode();
                CtElementImpl parent = (CtElementImpl) ((MoveOperation) op).getParent();
                int pos = ((MoveOperation) op).getPosition();
                Move mov = new Move(src, parent, pos, op);
                cg1.updateCGId(mov);
            }
        }
        return cg1;
    }
}
