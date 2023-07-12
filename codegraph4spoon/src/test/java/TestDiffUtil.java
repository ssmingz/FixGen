import org.junit.Test;
import utils.ASTUtil;
import utils.DiffUtil;

import java.io.File;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class TestDiffUtil {
    @Test
    public void testDiffOnExample() {
        String testPro = "ant";
        int testId = 13;
        String base = String.format("src/test/resources/c3/%s/%d", testPro, testId);
        int size = new File(base).listFiles().length;
        for (int i=0; i<size; i++) {
            String srcPath = String.format("%s/%d/before.java", base, i);
            String tarPath = String.format("%s/%d/after.java", base, i);
            String diffPath = String.format("%s/%d/diff.diff", base, i);
            // write diff file
            DiffUtil.writeDiffFile(srcPath, tarPath, diffPath);
        }
    }

    @Test
    public void testDiffOnC3() {
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
                            String diffPath = pair.getAbsolutePath()+"/diff.diff";
                            // write diff to file
                            DiffUtil.writeDiffFile(srcPath, tarPath, diffPath);
                            assertTrue((new File(diffPath)).exists());
                            System.out.println(diffPath + ": generate diff ok");
                        }
                    }
                }
            }
        }
    }
}
