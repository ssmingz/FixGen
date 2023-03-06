import builder.GraphBuilder;
import model.CodeGraph;
import model.GraphConfiguration;
import model.graph.node.Node;
import org.junit.Test;
import utils.DotGraph;
import utils.JavaASTUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TestGraphBuilder {
    @Test
    public void testBuildForMethod() {
        CodeGraph cg = buildCGForMethod("void m(int i) { int i = 0; } }");
        System.out.println(cg);
    }

    @Test
    public void testBuildForFile() {
        String srcPath = System.getProperty("user.dir") + "/src/test/resources/input4test.java";
        GraphConfiguration config = new GraphConfiguration();
        GraphBuilder builder = new GraphBuilder(config);
        Collection<CodeGraph> cgs = builder.build(srcPath, null);
        DotGraph dg = new DotGraph((CodeGraph) cgs.toArray()[0], config, 0);
        File dir = new File(System.getProperty("user.dir") + "/src/test/resources/new.dot");
        dg.toDotFile(dir);
    }

    public static CodeGraph buildCGForMethod(String code) {
        return buildCGForMethod(code, new GraphConfiguration());
    }

    public static CodeGraph buildCGForMethod(String code, GraphConfiguration configuration) {
        String classCode = "class C { " + code + "}";
        Collection<CodeGraph> groums = buildCGsForClass(classCode, configuration);
        assertThat(groums.size(), is(1));
        return groums.iterator().next();
    }

    private static Collection<CodeGraph> buildCGsForClass(String classCode, GraphConfiguration configuration) {
        GraphBuilder builder = new GraphBuilder(configuration);
        String projectName = "test";
        String basePath = getTestFilePath("/") + projectName;
        return builder.build(classCode, basePath, projectName, null);
    }

    private static String getTestFilePath(String relativePath) {
        if (!relativePath.startsWith("/")) {
            relativePath = "/" + relativePath;
        }
        return TestGraphBuilder.class.getResource(relativePath).getFile();
    }

}
