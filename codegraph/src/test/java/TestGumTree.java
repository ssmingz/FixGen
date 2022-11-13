import builder.GraphBuilder;
import builder.Matcher;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.DiffImpl;
import gumtree.spoon.diff.operations.*;
import model.CodeGraph;
import model.GraphConfiguration;
import model.graph.node.Node;
import org.junit.Test;
import pattern.Pair;
import spoon.reflect.declaration.CtElement;
import utils.FileIO;
import utils.JavaASTUtil;

import java.util.*;

public class TestGumTree {
    @Test
    public void testGumTreeDiff() throws Exception {
        String srcFile = "src/test/resources/0b20e4026c_d87861eb35/buggy_version/DashboardCommand.java";
        String dstFile = "src/test/resources/0b20e4026c_d87861eb35/fixed_version/DashboardCommand.java";
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
    public void testActionGraph() {
        String srcFile = "src/test/resources/0b20e4026c_d87861eb35/buggy_version/DashboardCommand.java";
        String dstFile = "src/test/resources/0b20e4026c_d87861eb35/fixed_version/DashboardCommand.java";
        String diffFile = "src/test/resources/0b20e4026c_d87861eb35/0b20e4026c_d87861eb35.diff";
        List<Integer> lineList = null;
        Map<String, List<Integer>> map = JavaASTUtil.getDiffLinesInBuggyFile(diffFile);
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            String fPath = entry.getKey();
            String fName = fPath.split("/")[fPath.split("/").length-1];
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

        for (Pair<CodeGraph, CodeGraph> pair : changedCG) {
            CodeGraph srcGraph = pair.getFirst();
            CodeGraph dstGraph = pair.getSecond();
            int method_start = srcGraph.getStartLine();
            int method_end = srcGraph.getEndLine();
            AstComparator diff = new AstComparator();
            Diff editScript = diff.compare(FileIO.readStringFromFile(srcFile), FileIO.readStringFromFile(dstFile));
            Map<CtElement, CtElement> mappings = new LinkedHashMap<>();
            for (Mapping mapping : ((DiffImpl) editScript)._mappingsComp.asSet()) {
                Tree srcTree = (Tree) mapping.getFirst();
                Tree dstTree = (Tree) mapping.getSecond();
                CtElement srcElement = (CtElement) srcTree.getMetadata("spoon_object");
                CtElement dstElement = (CtElement) dstTree.getMetadata("spoon_object");
                if (srcElement == null || srcElement.getPosition() == null) {
                    continue;
                }
                int spoon_start = srcElement.getPosition().getLine();
                if (spoon_start >= method_start && spoon_start <= method_end) {
                    mappings.put(srcElement, dstElement);
                }
            }
            Map<CtElement, Node> src_matcher = Matcher.mapSpoonToCodeGraph(srcGraph.getNodes(), new ArrayList<>(mappings.keySet()));
            Map<CtElement, Node> dst_matcher = Matcher.mapSpoonToCodeGraph(dstGraph.getNodes(), new ArrayList<>(mappings.values()));
            Map<Node, Node> src_to_dst = Matcher.mapSrcToDst(src_matcher, dst_matcher, mappings);

            // add modifications
            List<Operation> operations = editScript.getRootOperations();
            List<Node> changedNodes = new ArrayList<>();
            for (Operation operation : operations) {
                if (operation instanceof InsertOperation) {
                    changedNodes.addAll(Matcher.mapOperationToCodeGraph((InsertOperation) operation, srcGraph, src_matcher));
                } else if (operation instanceof MoveOperation) {
                    changedNodes.addAll(Matcher.mapOperationToCodeGraph((MoveOperation) operation, srcGraph, src_matcher));
                } else if (operation instanceof DeleteOperation) {
                    changedNodes.addAll(Matcher.mapOperationToCodeGraph((DeleteOperation) operation, srcGraph, src_matcher));
                } else if (operation instanceof UpdateOperation) {
                    changedNodes.addAll(Matcher.mapOperationToCodeGraph((UpdateOperation) operation, srcGraph, src_matcher));
                }
            }

            System.out.println("debug");
        }


    }

}
