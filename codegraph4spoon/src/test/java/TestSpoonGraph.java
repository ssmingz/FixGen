import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import utils.FileIO;

public class TestSpoonGraph {
    @Test
    public void testSpoon() {
        String src = FileIO.readStringFromFile("src/test/resources/c3/ant/13/0/before.java");
        CtClass l = Launcher.parseClass(src);
        l.isAbstract();
    }
}
