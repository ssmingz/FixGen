import builder.GraphBuilder;
import builder.PatternExtractor;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import model.CodeGraph;
import model.GraphConfiguration;
import model.Pattern;
import model.graph.node.Node;
import model.graph.node.actions.*;
import org.junit.Test;
import utils.DotGraph;
import utils.FileIO;
import utils.JavaASTUtil;
import utils.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class TestPatternExtract {
    @Test
    public void testPatternExtract() {
        File rootDir = new File("./src/test/resources/APImisuse_all");
        Map<String, String> fileNameById = loadBuggyFileName(rootDir + "/bugs.csv");
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
            List<Pair<CodeGraph, CodeGraph>> changedCG = getCodeGraphPair(srcFile, dstFile, diffFile, srcName);

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
            File dir1 = new File(System.getProperty("user.dir") + "/src/test/resources/" + index + ".dot");
            dg.toDotFile(dir1);
            File dir2 = new File(System.getProperty("user.dir") + "/src/test/resources/" + index + ".xml");
            dg.toXmlFile(dir2);
        }
        //FileIO.printCodeGraphToSPMFtxt(instances, "src/test/resources/spmf.txt");

        // extract pattern
        //PatternExtractor extractor = new PatternExtractor();
        //Set<Pattern> patterns = extractor.extractPattern(instances);

        System.out.println("debug");
    }

    private Map<String, String> loadBuggyFileName(String filePath) {
        Map<String, String> csv = new LinkedHashMap<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String DELIMITER = ",";
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(DELIMITER);
                csv.put(columns[0], columns[2]);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return csv;
    }

    private List<Pair<CodeGraph, CodeGraph>> getCodeGraphPair(String srcFile, String dstFile, String diffFile, String srcName) {
        List<Integer> lineList = null;
        Map<String, List<Integer>> map = JavaASTUtil.getDiffLinesInBuggyFile(diffFile);
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            String fPath = entry.getKey();
            String fName = fPath.split("/")[fPath.split("/").length - 1];
            if (srcName.endsWith(fName)) {
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
        return changedCG;
    }
}
