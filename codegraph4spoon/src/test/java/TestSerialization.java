import builder.GraphBuilder;
import model.CodeGraph;
import org.junit.Test;
import utils.ObjectUtil;

public class TestSerialization {
    @Test
    public void testSaveCodeGraph() {
        String srcPath = String.format("src/test/resources/c3/ant/13/%d/before.java", 0);
        String tarPath = String.format("src/test/resources/c3/ant/13/%d/after.java", 0);
        // build action graph
        CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath);
        ObjectUtil.writeObjectToFile(ag, String.format("%s/out/c3_ant_13_%d_ActionGraph.dat", System.getProperty("user.dir"), 0));
    }
}
