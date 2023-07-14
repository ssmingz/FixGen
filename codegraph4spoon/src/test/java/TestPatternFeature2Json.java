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
import utils.DotGraph;
import utils.ObjectUtil;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertNotNull;

public class TestPatternFeature2Json {
    @Test
    public void testWriteFeatCsvFromMultiplePairs() {
        String testPro = "ant";
        int testId = 13;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("src/test/resources/c3/%s/%d", testPro, testId);
        int size = new File(base).listFiles().length;
        for (int i=0; i<size; i++) {
            String srcPath = String.format("%s/%d/before.java", base, i);
            String tarPath = String.format("%s/%d/after.java", base, i);
            // build action graph
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
            ags.add(ag);
        }
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        for (Pattern pat : combinedGraphs) {
            // elements before abstraction
            HashMap<Integer, Object> idPattern = pat.getIdPattern();
            // abstract pattern
            PatternAbstractor abs = new PatternAbstractor(size);
            pat = abs.abstractPattern(pat);
            // write feature csv
            String jsonPath = System.getProperty("user.dir") + String.format("/out/c3_%s_%d", testPro, testId);
            ObjectUtil.writeFeatureJsonToFile(pat, pat.getIdPattern(), jsonPath);
        }
    }

    @Test
    public void testWriteFeatJsonFromMultiplePairsToSingleFile() {
        String testPro = "ant";
        int testId = 13;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("src/test/resources/c3/%s/%d", testPro, testId);
        int size = new File(base).listFiles(File::isDirectory).length;
        for (int i=0; i<size; i++) {
            String srcPath = String.format("%s/%d/before.java", base, i);
            String tarPath = String.format("%s/%d/after.java", base, i);
            // build action graph
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
            ags.add(ag);
        }
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        Map<String, JSONArray> patternsByID = new LinkedHashMap<>();
        for (Pattern pat : combinedGraphs) {
            // abstract pattern
            PatternAbstractor abs = new PatternAbstractor(size);
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
        String jsonPath = System.getProperty("user.dir") + String.format("/out/c3_%s_%d.json", testPro, testId);;
        ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);
    }

    @Test
    public void testWriteFeatJsonFromMultiplePairsToSingleFile_C3() {
        String[] projects = {"junit", "checkstyle", "cobertura", "drjava", "ant", "swt"};
        //String[] projects = {"junit"};
        String base = TestConfig.MAC_BASE;
        for (int i=0; i<projects.length; i++) {
            Map<String, JSONArray> patternsByID = new LinkedHashMap<>();
            int graphCounter = 0, groupCounter = 0, groupBuffer = 0;
            File dir = new File(String.format(base + "dataset/" + projects[i]));
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    try {
                        List<CodeGraph> ags = new ArrayList<>();
                        for (File pair : group.listFiles()) {
                            if (pair.isDirectory()) {
                                String srcPath = pair.getAbsolutePath()+"/before.java";
                                String tarPath = pair.getAbsolutePath()+"/after.java";
                                CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
                                ags.add(ag);
                                graphCounter++;
                            }
                        }
                        // extract pattern from more-than-one graphs
                        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
                        for (Pattern pat : combinedGraphs) {
                            // abstract pattern
                            PatternAbstractor abs = new PatternAbstractor(group.listFiles(File::isDirectory).length);
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
                        System.out.println(group.getAbsolutePath() + ": " + combinedGraphs.size() + " patterns");
                        groupCounter++;
                    } catch (Exception e) {
                        System.out.println(group.getAbsolutePath() + ": " + " 0 patterns");
                    }
                    if (groupCounter>TestConfig.MAX_GROUP) {
                        groupBuffer++;
                        // write json object to file
                        String jsonPath = System.getProperty("user.dir") + String.format("/out/c3_%s_%d.json", projects[i], groupBuffer);
                        ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);
                        groupCounter = 0;
                        patternsByID.clear();
                    }
                }
            }
            if (groupCounter > 0) {
                groupBuffer++;
                // write json object to file
                String jsonPath = System.getProperty("user.dir") + String.format("/out/c3_%s_%d.json", projects[i], groupBuffer);
                ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);
            }
            System.out.printf("Total codegraph instances: %d\n", graphCounter);
            System.out.printf("Total group instances: %d\n", (groupBuffer-1)*TestConfig.MAX_GROUP+groupCounter);
        }
    }
}
