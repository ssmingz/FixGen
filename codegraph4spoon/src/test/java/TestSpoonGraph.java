import builder.GraphBuilder;
import model.CodeGraph;
import builder.GraphConfiguration;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.support.reflect.declaration.CtMethodImpl;
import utils.DotGraph;

import java.io.File;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestSpoonGraph {
    @Test
    public void testSpoon() {
        Launcher launcher = new Launcher();
        // path can be a folder or a file
        // addInputResource can be called several times
        launcher.addInputResource("src/test/resources/c3/ant/13/0/before.java");
        // the compliance level should be set to the java version targeted by the input resources, e.g. Java 17
        launcher.getEnvironment().setComplianceLevel(8);
        launcher.buildModel();
        CtModel model = launcher.getModel();
        Collection<CtMethodImpl> methods = model.getElements(s -> s instanceof CtMethodImpl);
        assertEquals(methods.size(), 1);
    }

    @Test
    public void testGraphBuilder() {
        for (int i=0; i<4; i++) {
            CodeGraph cg1 = GraphBuilder.buildGraph(String.format("src/test/resources/c3/ant/13/%d/before.java", i), new String[] {}, 8, new int[] {});
            assertNotNull("CodeGraph shouldn't be null", cg1);
            // draw dot graph
            GraphConfiguration config = new GraphConfiguration();
            int nodeIndexCounter = 0;
            DotGraph dg1 = new DotGraph(cg1, config, nodeIndexCounter);
            File dir1 = new File(System.getProperty("user.dir") + "/out/" + String.format("c3_ant_13_%d_before.dot", i));
            dg1.toDotFile(dir1);

            CodeGraph cg2 = GraphBuilder.buildGraph(String.format("src/test/resources/c3/ant/13/%d/after.java", i), new String[] {}, 8, new int[] {});
            assertNotNull("CodeGraph shouldn't be null", cg2);
            nodeIndexCounter = 0;
            DotGraph dg2 = new DotGraph(cg2, config, nodeIndexCounter);
            File dir2 = new File(System.getProperty("user.dir") + "/out/" + String.format("c3_ant_13_%d_after.dot", i));
            dg2.toDotFile(dir2);
        }
    }

    @Test
    public void testGraphBuilderOnC3() {
        String base = "D:/expdata/c3/dataset/";
        String[] projects = {"ant", "junit", "checkstyle", "cobertura"};
        for (int i=0; i<projects.length; i++) {
            File dir = new File(String.format(base + projects[i]));
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    for (File pair : group.listFiles()) {
                        if (pair.isDirectory()) {
                            CodeGraph cg1 = GraphBuilder.buildGraph(pair.getAbsolutePath()+"/before.java", new String[] {}, 8, new int[] {});
                            assertNotNull("CodeGraph shouldn't be null", cg1);
                            CodeGraph cg2 = GraphBuilder.buildGraph(pair.getAbsolutePath()+"/after.java", new String[] {}, 8, new int[] {});
                            assertNotNull("CodeGraph shouldn't be null", cg2);
                            System.out.println(pair.getAbsolutePath() + ": generate code graph ok");
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testGraphBuilderOnC3_fordebug() {
        String pro = "cobertura";
        int group = 117;
        int pair = 0;
        String kind = "before";
        CodeGraph cg = GraphBuilder.buildGraph(String.format("D:/expdata/c3/dataset/%s/%d/%d/%s.java", pro, group, pair, kind), new String[] {}, 8, new int[] {});
        assertNotNull("CodeGraph shouldn't be null", cg);
        // draw dot graph
        GraphConfiguration config = new GraphConfiguration();
        int nodeIndexCounter = 0;
        DotGraph dg1 = new DotGraph(cg, config, nodeIndexCounter);
        File dir1 = new File(System.getProperty("user.dir") + "/out/" + String.format("c3_%s_%d_%d_%s.dot", pro, group, pair, kind));
        dg1.toDotFile(dir1);
    }
}
