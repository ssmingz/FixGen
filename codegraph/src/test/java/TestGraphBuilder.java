import builder.GraphBuilder;
import model.CodeGraph;
import model.GraphConfiguration;
import org.junit.Test;

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
