import builder.PatternExtractor;
import org.junit.Test;
import pattern.Pattern;

import java.util.Set;

public class TestPatternExtractor {
    @Test
    public void testModificationExtract() {
        String bugCommit = System.getProperty("user.dir") + "/codegraph/src/test/res/0b20e4026c_d87861eb35/buggy_version/DashboardCommand.java";
        String fixCommit = System.getProperty("user.dir") + "/codegraph/src/test/res/0b20e4026c_d87861eb35/fixed_version/DashboardCommand.java";

        PatternExtractor extractor = new PatternExtractor();
        Set<Pattern> patterns = extractor.extractPattern(bugCommit, fixCommit);
        System.out.println("Extract pattern: " + patterns.size());
    }
}
