import builder.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.CodeGraph;
import model.pattern.Pattern;
import org.javatuples.Pair;
import org.junit.Test;
import utils.ASTUtil;
import utils.DotGraph;
import utils.ObjectUtil;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class TestOpenSourceProjects {

    private static Path diffPath = Path.of(TestConfig.BENCHMARK_WIN_BASE, "diff");
    private static Path diffFilePath = Path.of(TestConfig.BENCHMARK_WIN_BASE, "difffile");
    private static Path projectRepos = Path.of(TestConfig.BENCHMARK_WIN_BASE, "project-repos");

    private static Path resultPath = Path.of(System.getProperty("user.dir"), "out", "FixBench", "Results");
    private static Path logPath = Path.of(System.getProperty("user.dir"), "out", "FixBench", "log");


    private static Path afterAbstractPatternsPath = Path.of(System.getProperty("user.dir"), "out", "FixBench", "afterAbstract");
    private static Path beforeAbstractPatternsPath = Path.of(System.getProperty("user.dir"), "out", "FixBench", "result-copy", "Results");

    // ================ for debug ====================

    private static Path debugBasePath = Path.of(System.getProperty("user.dir"), "out", "FixBench", "debug");
    private static Path debugLogPath = debugBasePath.resolve("log");
    private static Path debugResultPath = debugBasePath.resolve("Results");


    @Test
    public void testPattern2Json4OneJavaFile() throws IOException {
        Files.createDirectories(debugLogPath);
        Files.createDirectories(debugResultPath);

        String projectName = "beam";
        String diff = "d9ee909962_92439c4d53";
        String javaFileName = "CoderRegistry.java";

        Path diffLogDir = debugLogPath.resolve(Path.of(projectName, diff));
        Path diffResultDir = debugResultPath.resolve(Path.of(projectName, diff));

        Path diffFile = diffPath.resolve(Path.of(projectName, diff + ".diff"));

        Map<String, int[]> map = ASTUtil.getDiffLinesInBuggyFile(diffFile.toString()).entrySet()
                .stream()
                .filter(entry -> entry.getKey().endsWith(".java"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Path buggyVersion = diffFilePath.resolve(Path.of(projectName, diff, "buggy_version", javaFileName));
        Path fixedVersion = diffFilePath.resolve(Path.of(projectName, diff, "fixed_version", javaFileName));

        List<CodeGraph> SameMethodActionGraphsInOneFile = GraphBuilder.buildActionGraphInMethods(
                buggyVersion.toAbsolutePath().toString(),
                fixedVersion.toAbsolutePath().toString(),
                map.get(javaFileName)
        );

        for (CodeGraph actionGraph : SameMethodActionGraphsInOneFile) {
            List<CodeGraph> ags = new ArrayList<>(Arrays.asList(actionGraph));

            String methodSignature = String.format("%s__%s", javaFileName, actionGraph.getGraphName())
                    .replace("?", "void")
                    .replace(",", "-")
                    .replace("<", "")
                    .replace(">", "");
            System.out.println(methodSignature);

            DotGraph dg = new DotGraph(actionGraph, new GraphConfiguration(), 0);
            dg.toDotFile(diffLogDir.resolve(String.format("%s__action.dot", methodSignature)).toFile());

            List<Pattern> Patterns = PatternExtractor.combineGraphs(ags);

            for (Pattern pattern : Patterns) {
                PatternAbstractor.buildWithoutAbstract(pattern);
                DotGraph dot = new DotGraph(pattern, 0, false, false);
                dot.toDotFile(diffLogDir.resolve(String.format("%s__pattern__%d.dot", methodSignature, Patterns.indexOf(pattern))).toFile());

                Map<String, JSONArray> OnePatternMap = new LinkedHashMap<>();
                List<Pair<String, JSONObject>> patternByID = ObjectUtil.getFeatureJsonObj(pattern, pattern.getIdPattern());

                for (Pair<String, JSONObject> pair : patternByID) {
                    if (!OnePatternMap.containsKey(pair.getValue0())) {
                        OnePatternMap.put(pair.getValue0(), new JSONArray());
                    }
                    OnePatternMap.get(pair.getValue0()).add(pair.getValue1());
                }

                ObjectUtil.writeFeatureJsonObjToFile(
                        OnePatternMap,
                        diffResultDir.resolve(String.format("%s__pattern__%d.json", methodSignature, Patterns.indexOf(pattern))).toString()
                );

                ObjectUtil.writeObjectToFile(
                        pattern,
                        diffResultDir.resolve(String.format("%s__pattern__%d.dat", methodSignature, Patterns.indexOf(pattern))).toString()
                );  // save

            }

        }
    }

    @Test
    public void testPattern2Json() throws IOException {
        Files.createDirectories(resultPath);
        Files.createDirectories(logPath);

        List<String> totalBuggyVersionFiles = new ArrayList<>();
        List<String> projectNames = Arrays.stream(diffPath.toFile().listFiles())
                .filter(File::isDirectory)
                .map(File::getName)
                .collect(Collectors.toList());

        for (String projectName : projectNames) {
            Path projectDiffs = diffPath.resolve(projectName);
            List<String> diffs = Arrays.stream(Objects.requireNonNull(projectDiffs.toFile().listFiles()))
                    .filter(file -> file.getName().endsWith(".diff"))
                    .map(file -> file.getName().replaceAll(".diff", ""))
                    .collect(Collectors.toList());

            for (String diff : diffs) {
                Path diffFile = projectDiffs.resolve(diff + ".diff");
                Path diffLogDir = logPath.resolve(Path.of(projectName, diff));
                Path diffResultDir = resultPath.resolve(Path.of(projectName, diff));
                Map<String, int[]> map = ASTUtil.getDiffLinesInBuggyFile(diffFile.toString()).entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().endsWith(".java"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

                // each java file
                for (Map.Entry<String, int[]> file2lines : map.entrySet()) {
                    Path buggyVersion = diffFilePath.resolve(Path.of(projectName, diff, "buggy_version", file2lines.getKey()));
                    Path fixedVersion = diffFilePath.resolve(Path.of(projectName, diff, "fixed_version", file2lines.getKey()));

                    System.out.println(buggyVersion);

                    // for debug
//                    if(! "E:\\dataset\\FixBench\\fixBenchTest\\diffFile\\beam\\0ca5a1bc5a_2e0e6a838e\\buggy_version\\TransformTranslator.java".equals(buggyVersion.toString())) {
//                        continue;
//                    }

                    // beam/2ca30a4ff1_99e081d77d
                    if(!buggyVersion.toFile().canRead() || !fixedVersion.toFile().canRead()) {
                        continue;
                    }

                    totalBuggyVersionFiles.add(buggyVersion.toString());
                    List<CodeGraph> SameMethodActionGraphsInOneFile = GraphBuilder.buildActionGraphInMethods(buggyVersion.toAbsolutePath().toString(), fixedVersion.toAbsolutePath().toString(), file2lines.getValue());

                    if(! SameMethodActionGraphsInOneFile.isEmpty()) {
                        diffLogDir.toFile().mkdirs();
                        diffResultDir.toFile().mkdirs();
                    }

                    // each method code graph
                    for (CodeGraph actionGraph : SameMethodActionGraphsInOneFile) {
                        List<CodeGraph> ags = new ArrayList<>(Arrays.asList(actionGraph));

                        String methodSignature = String.format("%s__%s", file2lines.getKey(), actionGraph.getGraphName())
                                .replace("?", "void")
                                .replace(",", "-")
                                .replace("<", "")
                                .replace(">", "");
                        System.out.println(methodSignature);

//                        if(! "TransformTranslator.java__groupByKey#void".equals(methodSignature)) {
//                            continue;
//                        }

                        DotGraph dg = new DotGraph(actionGraph, new GraphConfiguration(), 0);
                        dg.toDotFile(diffLogDir.resolve(String.format("%s__action.dot", methodSignature)).toFile());

                        List<Pattern> Patterns = PatternExtractor.combineGraphs(ags);

                        for (Pattern pattern : Patterns) {
                            PatternAbstractor.buildWithoutAbstract(pattern);
                            DotGraph dot = new DotGraph(pattern, 0, false, false);
                            dot.toDotFile(diffLogDir.resolve(String.format("%s__pattern__%d.dot", methodSignature, Patterns.indexOf(pattern))).toFile());

                            Map<String, JSONArray> OnePatternMap = new LinkedHashMap<>();
                            List<Pair<String, JSONObject>> patternByID = ObjectUtil.getFeatureJsonObj(pattern, pattern.getIdPattern());

                            for (Pair<String, JSONObject> pair : patternByID) {
                                if (!OnePatternMap.containsKey(pair.getValue0())) {
                                    OnePatternMap.put(pair.getValue0(), new JSONArray());
                                }
                                OnePatternMap.get(pair.getValue0()).add(pair.getValue1());
                            }

                            ObjectUtil.writeFeatureJsonObjToFile(
                                    OnePatternMap,
                                    diffResultDir.resolve(String.format("%s__pattern__%d.json", methodSignature, Patterns.indexOf(pattern))).toString()
                            );

                            ObjectUtil.writeObjectToFile(
                                    pattern,
                                    diffResultDir.resolve(String.format("%s__pattern__%d.dat", methodSignature, Patterns.indexOf(pattern))).toString()
                            );  // save

                            Pattern patternLoad = (Pattern) ObjectUtil.readObjectFromFile(
                                    diffResultDir.resolve(String.format("%s__pattern__%d.dat", methodSignature, Patterns.indexOf(pattern))).toString()
                            );

                        }

                    }

                }

            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(logPath.resolve("totalBuggyFiles.txt").toString()));
        for (String totalBuggyVersionFile : totalBuggyVersionFiles) {
            writer.write(totalBuggyVersionFile);
            writer.newLine();
        }
        writer.close();
    }

    @Test
    public void testBugLocation() throws IOException {
        List<String> projectNames = Arrays.stream(beforeAbstractPatternsPath.toFile().listFiles())
                .filter(File::isDirectory)
                .map(File::getName)
                .collect(Collectors.toList());

        for (String projectName : projectNames) {
            List<Pattern> patternLib = new ArrayList<>();
            Path projectPattern = beforeAbstractPatternsPath.resolve(projectName);
            Path projectRepo = projectRepos.resolve(projectName);

            List<String> diffs = Arrays.stream(projectPattern.toFile().listFiles())
                    .map(File::getName)
                    .collect(Collectors.toList());
            for (String diff : diffs) {
                Path diffPatterns = projectPattern.resolve(diff);
                List<String> patternNames = Arrays.stream(diffPatterns.toFile().listFiles())
                        .filter(file -> file.getName().endsWith(".dat"))
                        .map(file -> file.getName().replaceAll(".dat", ""))
                        .collect(Collectors.toList());

                Path afterAbstractPattern = afterAbstractPatternsPath.resolve(diff);

                for (String patternName : patternNames) {
                    Pattern pattern = (Pattern) ObjectUtil.readObjectFromFile(diffPatterns.resolve(patternName + ".dat").toString());
                    pattern.setPatternName(diffPatterns.resolve(patternName + ".dat").toString());
                    if(pattern == null) {
                        continue;
                    }

                    String key = diffFilePath.resolve(Path.of(projectName, diff, "buggy_version", patternName.substring(0, patternName.indexOf(".java") + 5) + "$$0")).toString();

                    try{
                        JSONObject id = (JSONObject) ((JSONObject) ObjectUtil.readJsonFromFile(diffPatterns.resolve(patternName + ".json").toString()))
                                .getJSONArray(key.replace("$$0", ""))
                                .getJSONObject(0);
                        JSONObject label = (JSONObject) ((JSONObject) ObjectUtil.readJsonFromFile(afterAbstractPattern.resolve(patternName + ".json").toString()))
                                .get(key);
                        InteractPattern.abstractByJSONObject(pattern, id, label, key.replace("$$0", ""));

                        Path diffLogDir = logPath.resolve(Path.of(projectName, diff));
                        DotGraph dot = new DotGraph(pattern, 0, true, false);
                        dot.toDotFile(diffLogDir.resolve(pattern.getPatternName().replace(".dat", "__abstract.dot")).toFile());

                        patternLib.add(pattern);
                    } catch (Exception e) {
//                        System.err.println("[error] cannot read jsonObj");
                        e.printStackTrace();
                    }
                }
            }

            // apply fix only in corresponding project (no cross projects)
            System.out.println("begin location");
            locateFaultOnPatternLib(projectRepo, patternLib);

        }
    }

    @Test
    public void testBuildPatternLib4OneProject() throws IOException {
        String projectName = "beam";
        List<Pattern> patternLib = new ArrayList<>();

        Path projectPattern = beforeAbstractPatternsPath.resolve(projectName);
        List<String> diffs = Arrays.stream(projectPattern.toFile().listFiles())
                .map(File::getName)
                .collect(Collectors.toList());

        for (String diff : diffs) {
            Path diffPatterns = projectPattern.resolve(diff);
            List<String> patternNames = Arrays.stream(diffPatterns.toFile().listFiles())
                    .filter(file -> file.getName().endsWith(".dat"))
                    .map(file -> file.getName().replaceAll(".dat", ""))
                    .collect(Collectors.toList());

            Path afterAbstractPattern = afterAbstractPatternsPath.resolve(Path.of(diff));
            for (String patternName : patternNames) {
                System.out.println(diffPatterns + File.separator + patternName);
                Pattern pattern = (Pattern) ObjectUtil.readObjectFromFile(diffPatterns.resolve(patternName + ".dat").toString());
                if(pattern == null) {
                    continue;
                }

                String key = diffFilePath.resolve(Path.of(projectName, diff, "buggy_version", patternName.substring(0, patternName.indexOf(".java") + 5) + "$$0")).toString();
                try{
                    JSONObject id = (JSONObject) ((JSONObject) ObjectUtil.readJsonFromFile(diffPatterns.resolve(patternName + ".json").toString()))
                            .getJSONArray(key.replace("$$0", ""))
                            .getJSONObject(0);
                    JSONObject label = (JSONObject) ((JSONObject) ObjectUtil.readJsonFromFile(afterAbstractPattern.resolve(patternName + ".json").toString()))
                            .get(key);
                    InteractPattern.abstractByJSONObject(pattern, id, label, key.replace("$$0", ""));
                    patternLib.add(pattern);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void locateFaultOnPatternLib(Path projectPath, List<Pattern> patternLib) throws IOException {
        List<Path> alljavaFiles = Files.walk(projectPath)
                .filter(file -> file.toString().endsWith(".java"))
//                .filter(file -> !file.toString().contains(File.separator + "test" + File.separator))
                .filter(file -> !file.toString().contains(File.separator + ".")) // filtrate the stuff in the hidden files
                .collect(Collectors.toList());

        alljavaFiles.forEach(System.out::println);

        int canMatchMethodCount = 0;
        Map<String, Integer> matchedPatternCount = new HashMap<>();
        int totalMethodCount = 0;
        for (Path javaFile : alljavaFiles) {
            List<CodeGraph> methodsCodeGraph = GraphBuilder.buildMethodGraphs(javaFile.toString(), new String[] {}, 8, new int[] {});
            totalMethodCount += methodsCodeGraph.size();
            for (CodeGraph methodCodeGraph : methodsCodeGraph) {
                List<Pattern> matchedPatterns = new ArrayList<>();
                BugLocator detector = new BugLocator(0.8);
                for (Pattern pattern : patternLib) {
                    String result = detector.locateFaultByPattern(pattern, methodCodeGraph);
                    if(!"FAILED".equals(result)) {
                        matchedPatterns.add(pattern);
                    }
                }

                if(! matchedPatterns.isEmpty()) {
                    canMatchMethodCount++;
                }
                System.out.println("cg: " + methodCodeGraph.getGraphName());

                matchedPatterns.forEach(pattern -> {
                    matchedPatternCount.compute(pattern.getPatternName(), (k, v) -> v == null ? 1 : v + 1);
                });

            }

        }

        List<Map.Entry<String, Integer>> sortedMatchedPattern = new ArrayList<>(matchedPatternCount.entrySet());
        Collections.sort(sortedMatchedPattern, (v1, v2) -> v2.getValue() - v1.getValue());

        for (Map.Entry<String, Integer> matchedPattern : sortedMatchedPattern) {
            System.out.println(matchedPattern.getKey() + ":" + matchedPattern.getValue());
        }

        System.out.println(String.format("bug location count: %d/%d", canMatchMethodCount, totalMethodCount));
    }




    @Test
    public void testAbstractPattern4OneJavaFile() throws IOException {
        String projectName = "beam";
        String diff = "d08675cd59_80be89e4fa";
        String javaFileName = "TransformTranslator.java";

        Path diffLogDir = debugLogPath.resolve(Path.of(projectName, diff));
        Path diffResultDir = debugResultPath.resolve(Path.of(projectName, diff));

        Path diffFile = diffPath.resolve(Path.of(projectName, diff + ".diff"));

        Map<String, int[]> map = ASTUtil.getDiffLinesInBuggyFile(diffFile.toString()).entrySet()
                .stream()
                .filter(entry -> entry.getKey().endsWith(".java"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Path buggyVersion = diffFilePath.resolve(Path.of(projectName, diff, "buggy_version", javaFileName));
        Path fixedVersion = diffFilePath.resolve(Path.of(projectName, diff, "fixed_version", javaFileName));

        List<CodeGraph> SameMethodActionGraphsInOneFile = GraphBuilder.buildActionGraphInMethods(
                buggyVersion.toAbsolutePath().toString(),
                fixedVersion.toAbsolutePath().toString(),
                map.get(javaFileName)
        );

        for (CodeGraph actionGraph : SameMethodActionGraphsInOneFile) {
            List<CodeGraph> ags = new ArrayList<>(Arrays.asList(actionGraph));

            String methodSignature = String.format("%s__%s", javaFileName, actionGraph.getGraphName())
                    .replace("?", "void")
                    .replace(",", "-")
                    .replace("<", "")
                    .replace(">", "");
            System.out.println(methodSignature);

            List<Pattern> Patterns = PatternExtractor.combineGraphs(ags);

            for (Pattern pattern : Patterns) {

                PatternAbstractor.buildWithoutAbstract(pattern);
                DotGraph dot = new DotGraph(pattern, 0, false, false);
                String patternName = String.format("%s__pattern__%d", methodSignature, Patterns.indexOf(pattern));

                dot.toDotFile(diffLogDir.resolve(patternName + ".dat").toFile());

                Map<String, JSONArray> OnePatternMap = new LinkedHashMap<>();
                List<Pair<String, JSONObject>> patternByID = ObjectUtil.getFeatureJsonObj(pattern, pattern.getIdPattern());

                for (Pair<String, JSONObject> pair : patternByID) {
                    if (!OnePatternMap.containsKey(pair.getValue0())) {
                        OnePatternMap.put(pair.getValue0(), new JSONArray());
                    }
                    OnePatternMap.get(pair.getValue0()).add(pair.getValue1());
                }

                ObjectUtil.writeFeatureJsonObjToFile(
                        OnePatternMap,
                        diffResultDir.resolve(String.format("%s__pattern__%d.json", methodSignature, Patterns.indexOf(pattern))).toString()
                );

                ObjectUtil.writeObjectToFile(
                        pattern,
                        diffResultDir.resolve(String.format("%s__pattern__%d.dat", methodSignature, Patterns.indexOf(pattern))).toString()
                );  // save

                Pattern patternLoad = (Pattern) ObjectUtil.readObjectFromFile(
                        diffResultDir.resolve(String.format("%s__pattern__%d.dat", methodSignature, Patterns.indexOf(pattern))).toString()
                );

                Path afterAbstractPattern = afterAbstractPatternsPath.resolve(diff);
                String key = diffFilePath.resolve(Path.of(projectName, diff, "buggy_version", patternName.substring(0, patternName.indexOf(".java") + 5) + "$$0")).toString();

                try{
                    JSONObject id = (JSONObject) ((JSONObject) ObjectUtil.readJsonFromFile(diffResultDir.resolve(patternName + ".json").toString()))
                            .getJSONArray(key.replace("$$0", ""))
                            .getJSONObject(0);
                    JSONObject label = (JSONObject) ((JSONObject) ObjectUtil.readJsonFromFile(afterAbstractPattern.resolve(patternName + ".json").toString()))
                            .get(key);
                    InteractPattern.abstractByJSONObject(pattern, id, label, key.replace("$$0", ""));
                    System.out.println("method 1");
                    InteractPattern.abstractByJSONObject(patternLoad, id, label, key.replace("$$0", ""));
                    System.out.println("method 2");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }


}
