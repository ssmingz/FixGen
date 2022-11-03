package gumtree;

import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import org.junit.Test;
import utils.FileIO;
import utils.JavaASTUtil;

import static org.junit.Assert.assertTrue;


public class TestGumTree {
    @Test
    public void testGumTreeDiff() throws Exception {
        String srcFile = System.getProperty("user.dir") + "/codegraph/src/test/resources/0b20e4026c_d87861eb35/buggy_version/DashboardCommand.java";
        String dstFile = System.getProperty("user.dir") + "/codegraph/src/test/resources/0b20e4026c_d87861eb35/fixed_version/DashboardCommand.java";
        AstComparator diff = new AstComparator();
        Diff editScript = diff.compare(FileIO.readStringFromFile(srcFile), FileIO.readStringFromFile(dstFile));

        System.out.println(editScript.toString());
        assertTrue(editScript.getRootOperations().size() == 1);
    }
}
