import builder.GraphBuilder;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import model.CodeGraph;
import model.GraphConfiguration;
import model.graph.node.actions.ActionNode;
import org.junit.Test;
import utils.Pair;
import utils.FileIO;
import utils.JavaASTUtil;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestGumTree {
    @Test
    public void testGumTreeDiff() throws Exception {
        // String srcFile =
        // "src/test/resources/0b20e4026c_d87861eb35/buggy_version/DashboardCommand.java";
        // String dstFile =
        // "src/test/resources/0b20e4026c_d87861eb35/fixed_version/DashboardCommand.java";
        String srcFile = "/Users/yumeng/Workspace/BUGs/FixBench/WithinSingleMethod/Genesis-NP/Genesis#178/old/FixedPointLongCodec.java";
        String dstFile = "/Users/yumeng/Workspace/BUGs/FixBench/WithinSingleMethod/Genesis-NP/Genesis#178/new/FixedPointLongCodec.java";
        AstComparator diff = new AstComparator();
        Diff editScript = diff.compare(FileIO.readStringFromFile(srcFile), FileIO.readStringFromFile(dstFile));
        System.out.println("debug");
    }

    @Test
    public void testGetDiffLinesInBuggyFile() {
        String srcFile = "src/test/resources/0b20e4026c_d87861eb35/0b20e4026c_d87861eb35.diff";
        Map<String, List<Integer>> lines = JavaASTUtil.getDiffLinesInBuggyFile(srcFile);
        System.out.println(lines);
    }

    @Test
    public void testAddActionToCodeGraph() {
        String srcFile = "src/test/resources/0b20e4026c_d87861eb35/buggy_version/DashboardCommand.java";
        String dstFile = "src/test/resources/0b20e4026c_d87861eb35/fixed_version/DashboardCommand.java";
        String diffFile = "src/test/resources/0b20e4026c_d87861eb35/0b20e4026c_d87861eb35.diff";
        List<Integer> lineList = null;
        Map<String, List<Integer>> map = JavaASTUtil.getDiffLinesInBuggyFile(diffFile);
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            String fPath = entry.getKey();
            String fName = fPath.split("/")[fPath.split("/").length - 1];
            if (srcFile.endsWith(fName)) {
                lineList = entry.getValue();
            }
        }

        GraphConfiguration config = new GraphConfiguration();
        GraphBuilder builder = new GraphBuilder(config);
        Collection<CodeGraph> cgs1 = builder.build(srcFile, null);
        Collection<CodeGraph> cgs2 = builder.build(dstFile, null);

        List<Pair<CodeGraph, CodeGraph>> changedCG = new ArrayList<>();
        for (CodeGraph cg : cgs1) {
            if (lineList != null) {
                for (Integer cline : lineList) {
                    if (cg.getStartLine() <= cline && cg.getEndLine() >= cline) {
                        for (CodeGraph cg2 : cgs2) {
                            if (cg.getGraphName().equals(cg2.getGraphName())) {
                                changedCG.add(new Pair<>(cg, cg2));
                            }
                        }
                    }
                }
            }
        }

        List<CodeGraph> actionGraphs = new ArrayList<>();
        for (Pair<CodeGraph, CodeGraph> pair : changedCG) {
            CodeGraph srcGraph = pair.getFirst();
            CodeGraph dstGraph = pair.getSecond();
            AstComparator diff = new AstComparator();
            Diff editScript = diff.compare(FileIO.readStringFromFile(srcFile), FileIO.readStringFromFile(dstFile));
            srcGraph.addActionByFilePair(editScript);
            assertEquals(2, srcGraph.getNodes().stream().filter(p->p instanceof ActionNode).collect(Collectors.toList()).size());
            actionGraphs.add(srcGraph);
        }
    }

}
