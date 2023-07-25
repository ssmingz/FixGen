import builder.GraphBuilder;
import builder.GraphConfiguration;
import builder.PatternAbstractor;
import builder.PatternExtractor;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.CodeGraph;
import model.pattern.Pattern;
import org.javatuples.Pair;
import org.junit.Test;
import utils.ASTUtil;
import utils.DotGraph;
import utils.FileFilterImpl;
import utils.ObjectUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

public class TestDefects4J {
    @Test
    public void testAll() {
        boolean OUTPUT_TO_FILE = true;

        if (OUTPUT_TO_FILE) {
            FileOutputStream puts = null;
            try {
                puts = new FileOutputStream(TestConfig.LOG_PATH,true);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            PrintStream out = new PrintStream(puts);
            System.setOut(out);
        }

        String base = "/Users/yumeng/JavaProjects/FixGen/data/d4j-info-2.0";
        for (Map.Entry<String, Integer> entry : TestConfig.D4J_PROJECTS.entrySet()) {
            for (int id=1; id<=entry.getValue(); id++) {
                String proj = entry.getKey();
                int finalId = id;
                if (TestConfig.D4J_DEPRECATED.containsKey(proj) && Arrays.stream(TestConfig.D4J_DEPRECATED.get(proj)).anyMatch(i -> i == finalId))
                    continue;
                System.out.printf("[start]%s %d\n", proj, id);
                testExtractJsonFromSingleChange(base, proj, id);
                System.out.printf("[finish]%s %d\n", proj, id);
            }
        }
    }

    public void testExtractJsonFromSingleChange(String base, String proj, int id) {
        FileFilterImpl fileFilter1 = new FileFilterImpl();
        fileFilter1.accept(new File(String.format("%s/buggy_fix/buggy/%s/%s_%d_buggy", base, proj, proj, id)));
        List<String> srcList = fileFilter1.target;
        FileFilterImpl fileFilter2 = new FileFilterImpl();
        fileFilter2.accept(new File(String.format("%s/buggy_fix/fixed/%s/%s_%d_fixed", base, proj, proj, id)));
        List<String> tarList = fileFilter2.target;
        String diffFile = String.format("%s/patches/%s/%d.src.patch", base, proj, id);
        Map<String, int[]> map = ASTUtil.getDiffLinesInBuggyFile(diffFile);
        int diffIndex = 0;
        for (Map.Entry<String, int[]> entry : map.entrySet()) {
            diffIndex++;
            String[] srcPath = srcList.stream().filter(f -> f.endsWith(entry.getKey())).toArray(String[]::new);
            String[] tarPath = tarList.stream().filter(f -> f.endsWith(entry.getKey())).toArray(String[]::new);
            if (srcPath.length == 1 && tarPath.length == 1) {
                try {
                    // build action graph
                    CodeGraph ag = GraphBuilder.buildActionGraph(srcPath[0], tarPath[0], entry.getValue());
                    // init the pattern
                    List<Pattern> patterns = PatternExtractor.combineGraphs(new ArrayList<>(){
                        {
                            add(ag);
                        }
                    });
                    Map<String, JSONArray> patternsByID = new LinkedHashMap<>();
                    for (Pattern pat : patterns) {
                        // abstract pattern
                        PatternAbstractor abs = new PatternAbstractor(1);
                        pat = abs.abstractPattern(pat);
                        // get feature json object
                        List<Pair<String, JSONObject>> patternByID = ObjectUtil.getFeatureJsonObj(pat, pat.getIdPattern());
                        for (Pair<String, JSONObject> pair : patternByID) {
                            if (!patternsByID.containsKey(pair.getValue0())) {
                                patternsByID.put(pair.getValue0(), new JSONArray());
                            }
                            patternsByID.get(pair.getValue0()).add(pair.getValue1());
                        }
                    }
                    // write json object to file
                    String jsonPath = System.getProperty("user.dir") + String.format("/out/defects4j/%s/%d_%d.json", proj, id, diffIndex);
                    ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);
                } catch (NullPointerException npe) {
                    System.out.println("[error]Fix changes in more than one method in a file");
                }
            } else {
                System.out.println("[error]Do not have the only target file");
            }
        }
        // extract json

    }

    @Test
    public void testLoadModelResult() {
        // TODO: load model result
        // TODO: save pattern
    }
}
