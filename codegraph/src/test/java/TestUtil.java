import builder.GraphBuilder;
import model.CodeGraph;
import model.GraphConfiguration;
import utils.JavaASTUtil;
import utils.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class TestUtil {
    public static Map<String, String> loadBuggyFileName(String filePath) {
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

    public static List<Pair<CodeGraph, CodeGraph>> getCodeGraphPair(String srcFile, String dstFile, String diffFile, String srcName) {
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
        for (CodeGraph cg2 : cgs2) {
            if (lineList != null) {
                for (Integer cline : lineList) {
                    if (cg2.getStartLine() <= cline && cg2.getEndLine() >= cline) {
                        for (CodeGraph cg1 : cgs1) {
                            if (cg1.getGraphName().equals(cg2.getGraphName())) {
                                if (changedCG.stream().noneMatch(p -> p.getFirst().getGraphName().equals(cg2.getGraphName())))
                                    changedCG.add(new Pair<>(cg1, cg2));
                            }
                        }
                    }
                }
            }
        }
        return changedCG;
    }
}
