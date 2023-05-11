import builder.GraphBuilder;
import builder.GraphConfiguration;
import com.martiansoftware.jsap.Switch;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.*;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.Delete;
import model.actions.Insert;
import model.actions.Move;
import model.actions.Update;
import org.junit.Test;
import spoon.support.reflect.declaration.CtElementImpl;
import utils.DotGraph;
import utils.FileIO;

import java.io.File;

public class TestSpoonActionGraph {
    @Test
    public void testGraphBuilder() {
        for (int i=0; i<4; i++) {
            String srcPath = String.format("src/test/resources/c3/ant/13/%d/before.java", i);
            String tarPath = String.format("src/test/resources/c3/ant/13/%d/after.java", i);
            // code graph
            CodeGraph cg1 = GraphBuilder.buildGraph(srcPath, new String[] {}, 8, new int[] {});
            CodeGraph cg2 = GraphBuilder.buildGraph(tarPath, new String[] {}, 8, new int[] {});
            // gumtree diff
            AstComparator diff = new AstComparator();
            Diff editScript = diff.compare(cg1.getEntryNode(), cg2.getEntryNode());
            // add actions to src graph
            // TODO: add relationships in dst graph
            // TODO: add dst children to allNodes
            for (Operation op : editScript.getRootOperations()) {
                if (op instanceof DeleteOperation) {
                    CtElementImpl src = (CtElementImpl) op.getSrcNode();
                    Delete del = new Delete(src, op);
                    cg1.getNodes().add(new CtWrapper(del));
                } else if (op instanceof UpdateOperation) {
                    CtElementImpl src = (CtElementImpl) op.getSrcNode();
                    CtElementImpl dst = (CtElementImpl) op.getDstNode();
                    Update upd = new Update(src, dst, op);
                    cg1.getNodes().add(new CtWrapper(upd));
                    cg1.getNodes().add(new CtWrapper(dst));
                } else if (op instanceof InsertOperation) {
                    CtElementImpl src = (CtElementImpl) op.getSrcNode();
                    CtElementImpl parent = (CtElementImpl) ((InsertOperation) op).getParent();
                    int pos = ((InsertOperation) op).getPosition();
                    Insert ins = new Insert(src, parent, pos, op);
                    cg1.getNodes().add(new CtWrapper(ins));
                } else if (op instanceof MoveOperation) {
                    CtElementImpl src = (CtElementImpl) op.getSrcNode();
                    CtElementImpl parent = (CtElementImpl) ((MoveOperation) op).getParent();
                    int pos = ((MoveOperation) op).getPosition();
                    Move mov = new Move(src, parent, pos, op);
                    cg1.getNodes().add(new CtWrapper(mov));
                }
            }
            // draw dot graph
            GraphConfiguration config = new GraphConfiguration();
            int nodeIndexCounter = 0;
            DotGraph dg1 = new DotGraph(cg1, config, nodeIndexCounter);
            File dir1 = new File(System.getProperty("user.dir") + "/out/" + String.format("c3_ant_13_%d_action.dot", i));
            dg1.toDotFile(dir1);
        }
    }
}
