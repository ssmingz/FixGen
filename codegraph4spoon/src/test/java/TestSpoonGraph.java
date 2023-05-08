import builder.GraphBuilder;
import model.CodeGraph;
import org.eclipse.jdt.core.JavaCore;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.support.reflect.declaration.CtMethodImpl;
import utils.FileIO;

import java.util.ArrayList;
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
        CodeGraph cg = GraphBuilder.buildGraph("src/test/resources/c3/ant/13/0/before.java", new String[] {}, 8, new int[] {});
        assertNotNull("CodeGraph shouldn't be null", cg);
    }
}
