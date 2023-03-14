import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import model.CodeGraph;
import model.GraphConfiguration;
import org.junit.Test;
import utils.DotGraph;
import utils.FileIO;
import utils.Pair;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class TestActionGraph {
    @Test
    public void testBuildActionGraph() {
        File rootDir = new File("./src/test/resources/APImisuse");
        Map<String, String> fileNameById = TestUtil.loadBuggyFileName(rootDir + "/bugs.csv");
        // load CodeGraph with actions
        List<CodeGraph> instances = new ArrayList<>();
        for (File bugDir : rootDir.listFiles(File::isDirectory)) {
            String id = bugDir.getAbsolutePath().substring(bugDir.getAbsolutePath().lastIndexOf("/")+1);
            if (Integer.valueOf(id) > 78 || Integer.valueOf(id) < 73)
                continue;

            String srcFile = bugDir.getAbsolutePath() + "/src.java";
            String dstFile = bugDir.getAbsolutePath() + "/tar.java";
            String diffFile = bugDir.getAbsolutePath() + "/diff.diff";
            String srcName = fileNameById.get(id);
            List<Pair<CodeGraph, CodeGraph>> changedCG = TestUtil.getCodeGraphPair(srcFile, dstFile, diffFile, srcName);

            for (Pair<CodeGraph, CodeGraph> pair : changedCG) {
                CodeGraph srcGraph = pair.getFirst();
                CodeGraph dstGraph = pair.getSecond();
                AstComparator diff = new AstComparator();
                Diff editScript = diff.compare(FileIO.readStringFromFile(srcFile), FileIO.readStringFromFile(dstFile));
                srcGraph.addActionByFilePair(editScript);
                instances.add(srcGraph);
            }
        }

        GraphConfiguration config = new GraphConfiguration();
        int nodeIndexCounter = 0;
        for (CodeGraph cg : instances) {
            DotGraph dg = new DotGraph(cg, config, nodeIndexCounter);
            int index = instances.indexOf(cg)+73;
            File dir1 = new File(System.getProperty("user.dir") + "/out/" + index + ".dot");
            dg.toDotFile(dir1);
            File dir2 = new File(System.getProperty("user.dir") + "/out/" + index + ".xml");
            dg.toXmlFile(dir2);
        }
        //FileIO.printCodeGraphToSPMFtxt(instances, "src/test/resources/spmf.txt");

        assertEquals(instances.size(), 5);
    }
}
