import builder.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.CodeGraph;
import model.pattern.Pattern;
import org.junit.Test;
import utils.DotGraph;
import utils.ObjectUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestInteractWithPattern {
    @Test
    public void testModelResult() {
        String testPro = "ant";
        int testId = 13;
        int targetNo = 0;
        String base = String.format("src/test/resources/c3/%s/%d/%d", testPro, testId, targetNo);

        // build for the target
        String srcPath = String.format("%s/before.java", base);
        String tarPath = String.format("%s/after.java", base);
        CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
        drawCodeGraph(ag, System.getProperty("user.dir") + "/out/cg_temp.dot");

        // init the pattern
        List<Pattern> patterns = PatternExtractor.combineGraphs(new ArrayList<>(){
            {
                add(ag);
            }
        });
        drawPattern(patterns, System.getProperty("user.dir") + "/out/", false);

        // load element label
        String key = "/Users/yumeng/JavaProjects/FixGen/codegraph4spoon/src/test/resources/c3/ant/13/0/before.java";
        JSONArray labels = (JSONArray) ((JSONObject) ObjectUtil.readJsonFromFile("/Users/yumeng/JavaProjects/FixGen/codegraph4spoon/out/c3_ant_13_0.json")).get(key);
        // modify the pattern according to the label
        for (int i=0; i<patterns.size(); i++) {
            Pattern pattern = patterns.get(i);
            PatternAbstractor.buildWithoutAbstract(pattern);
            JSONObject label = labels.getJSONObject(i);
            InteractPattern.abstractByJSONObject(pattern, label, key);
            // save the pattern
            String patternPath = String.format("%s/out/pattern_temp_%d.dat", System.getProperty("user.dir"), i);
            ObjectUtil.writeObjectToFile(pattern, patternPath);
        }
        // load the pattern
        String patternPath = String.format("%s/out/pattern_temp_%d.dat", System.getProperty("user.dir"), 0);
        Pattern pat_reload = (Pattern) ObjectUtil.readObjectFromFile(patternPath);
        // interact with the pattern
        InteractPattern.abstractVertex(pat_reload, 122, 1, key);
        InteractPattern.abstractEdge(pat_reload, 122, 22, 0, key);
        InteractPattern.abstractAttribute(pat_reload, 122, "locationInParent", 1, key);
        drawPattern(pat_reload, System.getProperty("user.dir") + "/out/modified_pattern_temp.dot", true);
    }

    private void drawPattern(Pattern pattern, String path, boolean isAbstract) {
        DotGraph dot = new DotGraph(pattern, 0, isAbstract, false);
        dot.toDotFile(new File(path));
    }

    private void drawPattern(List<Pattern> patternList, String base, boolean isAbstract) {
        for (int i=0; i<patternList.size(); i++) {
            String path = String.format("%s/pattern_temp_%d.dot", base, i);
            drawPattern(patternList.get(i), path, isAbstract);
        }
    }

    private void drawCodeGraph(CodeGraph cg, String path) {
        DotGraph dot = new DotGraph(cg, new GraphConfiguration(), 0);
        dot.toDotFile(new File(path));
    }
}
