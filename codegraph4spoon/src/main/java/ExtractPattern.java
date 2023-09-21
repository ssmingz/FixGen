import builder.GraphBuilder;
import builder.InteractPattern;
import builder.PatternAbstractor;
import builder.PatternExtractor;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import config.Options;
import config.pojo.Option;
import model.CodeGraph;
import model.pattern.Pattern;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ObjectUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ExtractPattern {
    private final static Logger logger = LoggerFactory.getLogger(ExtractPattern.class);
    public static void main(String[] args) {
        Option option = processArgs(args);
        logger.info("option: {}", option);
        run(option);
    }

    private static void run(Option option) {
        Path dataPath = Paths.get(option.extractPattern.dataPath);
        Path patternPath = Paths.get(option.extractPattern.patternPath);

        if(! patternPath.toFile().exists()) {
            patternPath.toFile().mkdirs();
        }

        File[] groups = Arrays.stream(Objects.requireNonNull(dataPath.toFile().listFiles())).filter(File::isDirectory).toArray(File[]::new);

        for (File group : groups) {
            String groupID = group.getName();
            Path dataGroupRoot = dataPath.resolve(groupID);
            List<String> cases = Arrays.stream(Objects.requireNonNull(dataGroupRoot.toFile().listFiles()))
                    .filter(File::isDirectory)
                    .map(File::getName)
                    .collect(Collectors.toList());

            for (String patternCaseNum : cases) {
                try{
                    Path patternBeforePath = dataGroupRoot.resolve(patternCaseNum).resolve("before.java");
                    Path patternAfterPath = dataGroupRoot.resolve(patternCaseNum).resolve("after.java");
                    CodeGraph ag = GraphBuilder.buildActionGraph(patternBeforePath.toString(), patternAfterPath.toString(), new int[]{});
                    List<Pattern> patterns = PatternExtractor.combineGraphs(List.of(ag), "new");

                    Map<String, JSONArray> patternsByID = new LinkedHashMap<>();
                    for (Pattern pat : patterns) {
                        // collect attributes
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
                    String jsonFileName = String.format("%s_%s_%s.json", option.extractPattern.datasetName, groupID, patternCaseNum);
                    String jsonPath = option.jsonBefore + "/" + jsonFileName;
                    ObjectUtil.writeFeatureJsonObjToFile(patternsByID, jsonPath);

                    String[] cmds = {option.pythonCmd, option.modelPath, "--path", jsonFileName};
                    logger.info("cmd: {}", Arrays.toString(cmds));

                    Process process = (new ProcessBuilder(cmds).directory(new File(option.modelWorkPath)))
                            .redirectErrorStream(true).start();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    process.getInputStream(),
                                    StandardCharsets.UTF_8
                            )
                    );

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    System.out.println(process.waitFor());

                    String modelJsonPath = option.jsonAfter + "/" +  jsonFileName;
                    Map<String, JSONObject> modelPrediction = (Map<String, JSONObject>) ObjectUtil.readJsonFromFile(modelJsonPath.toString());

                    for (int i = 0; i < patterns.size(); i++) {
                        Pattern pattern = patterns.get(i);

                        String key = dataGroupRoot.resolve(patternCaseNum).resolve("before.java").toString() + "$$" + i;
                        JSONObject labelJson = modelPrediction.get(key);
                        JSONObject oriJson = ((JSONObject) ObjectUtil.readJsonFromFile(jsonPath)).getJSONArray(ag.getFileName()).getJSONObject(i);
                        InteractPattern.abstractByJSONObject(pattern, oriJson, labelJson, ag.getFileName());

                        ObjectUtil.writeObjectToFile(
                                pattern,
                                patternPath.resolve(String.format("%s_%s__pattern_%s_%d.dat", option.extractPattern.datasetName, groupID, patternCaseNum, i)).toString());

                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }


        }






    }

    private static Option processArgs(String[] args) {
        return Options.parse();
    }

}
