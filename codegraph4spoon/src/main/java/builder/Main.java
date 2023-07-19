package builder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.CodeGraph;
import model.pattern.Pattern;
import org.javatuples.Pair;
import utils.DotGraph;
import utils.ObjectUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String testPro = "ant";
        int testId = 13;
        int targetNo = 0;
        String base = String.format("codegraph4spoon/src/test/resources/c3/%s/%d", testPro, testId);
        String srcPath = String.format("%s/%d/before.java", base, targetNo);
        String tarPath = String.format("%s/%d/after.java", base, targetNo);
        String outDir = String.format("%s/out/c3_%s_%d_%d", System.getProperty("user.dir"), testPro, testId, targetNo);
        if (!new File(outDir).exists()) {
            new File(outDir).mkdirs();
        }
        // 2. build action graph
        CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});

        // 3. json file as model input
        // init the pattern
        List<Pattern> patterns = PatternExtractor.combineGraphs(new ArrayList<>(){
            {
                add(ag);
            }
        });
        DotGraph.drawPattern(patterns, outDir, false);

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
        String jsonPath = String.format("%s/c3_%s_%d_%d.json", outDir, testPro, testId, targetNo);
        ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);

        // 4. build pattern from model output
        String key = "/Users/yumeng/JavaProjects/FixGen/codegraph4spoon/src/test/resources/c3/ant/13/0/before.java";
        String modelResult = String.format("%s/c3_%s_%d_%d_predict.json", outDir, testPro, testId, targetNo);
        JSONArray labels = (JSONArray) ((JSONObject) ObjectUtil.readJsonFromFile(modelResult)).get(key);
        // modify the pattern according to the label
        for (int i=0; i<patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            PatternAbstractor.buildWithoutAbstract(pattern);
            JSONObject label = labels.getJSONObject(i);
            InteractPattern.abstractByJSONObject(pattern, label, key);
            // save the pattern
            String patternPath = String.format("%s/pattern_c3_%s_%d_%d_%d_predict.dat", outDir, testPro, testId, targetNo, i);
            ObjectUtil.writeObjectToFile(pattern, patternPath);
        }
        DotGraph.drawPattern(patterns, outDir, true);

        // 5. apply pattern to source file
        // build for the target
        CodeGraph target_ag = GraphBuilder.buildGraph(srcPath, new String[] {}, 8, new int[] {});
        for (int i=0; i<patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            // locate the buggy line
            BugLocator detector = new BugLocator(0.6);
            String patchPath = String.format("%s/patch_%s_%d_%d_%d.java", outDir, testPro, testId, targetNo, i);
            detector.applyPattern(pattern, target_ag, patchPath);
        }
    }
}
