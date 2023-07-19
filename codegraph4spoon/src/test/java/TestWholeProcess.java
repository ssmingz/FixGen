import builder.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.CodeGraph;
import model.pattern.Pattern;
import org.javatuples.Pair;
import org.junit.Test;
import utils.ObjectUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestWholeProcess {
    @Test
    public void testMethod() {
        String testPro = "ant";
        int testId = 13;
        int targetNo = 0;
        String base = String.format("src/test/resources/c3/%s/%d", testPro, testId);
        String srcPath = String.format("%s/%d/before.java", base, targetNo);
        String tarPath = String.format("%s/%d/after.java", base, targetNo);
        // 2. build action graph
        CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});

        // 3. json file as model input
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
        String jsonPath = System.getProperty("user.dir") + String.format("/out/c3_%s_%d_%d.json", testPro, testId, targetNo);
        ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);

        // 4. build pattern from model output
        String key = "/Users/yumeng/JavaProjects/FixGen/codegraph4spoon/src/test/resources/c3/ant/13/0/before.java";
        String modelResult = System.getProperty("user.dir") + String.format("/out/c3_%s_%d_%d_predict.json", testPro, testId, targetNo);
        JSONArray labels = (JSONArray) ((JSONObject) ObjectUtil.readJsonFromFile(modelResult)).get(key);
        // modify the pattern according to the label
        for (int i=0; i<patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            PatternAbstractor.buildWithoutAbstract(pattern);
            JSONObject label = labels.getJSONObject(i);
            InteractPattern.abstractByJSONObject(pattern, label, key);
            // save the pattern
            String patternPath = String.format("%s/out/pattern_c3_%s_%d_%d_predict.dat", System.getProperty("user.dir"), testPro, testId, targetNo, i);
            ObjectUtil.writeObjectToFile(pattern, patternPath);
        }
        // 5. apply pattern to source file
        // build for the target
        CodeGraph target_ag = GraphBuilder.buildGraph(
                String.format("%s/%d/before.java", base, targetNo), new String[] {}, 8, new int[] {});
        for (int i=0; i<patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            // locate the buggy line
            BugLocator detector = new BugLocator(0.6);
            String patchPath = String.format("%s/out/patch_%s_%d_%d_%d.java", System.getProperty("user.dir"), testPro, testId, targetNo, i);
            detector.applyPattern(pattern, target_ag, patchPath);
        }
    }
}
