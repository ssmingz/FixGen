import builder.GraphBuilder;
import builder.GraphConfiguration;
import model.CodeGraph;
import org.junit.Test;
import utils.DotGraph;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class TestSpoonActionGraph {
    @Test
    public void testGraphBuilder() {
        for (int i=0; i<4; i++) {
            String srcPath = String.format("src/test/resources/c3/ant/13/%d/before.java", i);
            String tarPath = String.format("src/test/resources/c3/ant/13/%d/after.java", i);
            // build action graph
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath);
            // draw dot graph
            GraphConfiguration config = new GraphConfiguration();
            int nodeIndexCounter = 0;
            DotGraph dg1 = new DotGraph(ag, config, nodeIndexCounter);
            File dir1 = new File(System.getProperty("user.dir") + "/out/" + String.format("c3_ant_13_%d_action.dot", i));
            dg1.toDotFile(dir1);
        }
    }

    @Test
    public void testGraphBuilderOnC3() {
        String base = TestConfig.WIN_BASE;
        String[] projects = {"ant", "junit", "checkstyle", "cobertura"};
        for (int i=0; i<projects.length; i++) {
            File dir = new File(String.format(base + "dataset/" + projects[i]));
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    for (File pair : group.listFiles()) {
                        if (pair.isDirectory()) {
                            String srcPath = pair.getAbsolutePath()+"/before.java";
                            String tarPath = pair.getAbsolutePath()+"/after.java";
                            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath);
                            assertNotNull("CodeGraph shouldn't be null", ag);
                            System.out.println(pair.getAbsolutePath() + ": generate code graph ok");
                        }
                    }
                }
            }
        }
    }

    @Test
    public void testGraphBuilderOnC3_fordebug() {
        String pro = "ant";
        int group = 135;
        int pair = 0;
        String srcPath = String.format("%s/dataset/%s/%d/%d/before.java", TestConfig.WIN_BASE, pro, group, pair);
        String tarPath = String.format("%s/dataset/%s/%d/%d/after.java", TestConfig.WIN_BASE, pro, group, pair);
        // build action graph
        CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath);
        // draw dot graph
        GraphConfiguration config = new GraphConfiguration();
        int nodeIndexCounter = 0;
        DotGraph dg1 = new DotGraph(ag, config, nodeIndexCounter);
        File dir1 = new File(System.getProperty("user.dir") + "/out/" + String.format("c3_%s_%d_%d_action.dot", pro, group, pair));
        dg1.toDotFile(dir1);
    }
}
