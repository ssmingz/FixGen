import builder.GraphBuilder;
import builder.GraphConfiguration;
import builder.PatternAbstractor;
import builder.PatternExtractor;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.CodeGraph;
import model.pattern.Pattern;
import org.javatuples.Pair;
import utils.ASTUtil;
import utils.DotGraph;
import utils.ObjectUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

// 根据项目的历史修改，提取pattern
public class ExtractPatternOnStars {
    public static void main(String[] args) throws IOException {
        Path diffPath = Path.of(System.getProperty("user.dir"),"StarBench", "diff");
        Path diffFilePath = Path.of(System.getProperty("user.dir"),"StarBench", "diffFile");

        Path resultPath = Path.of(System.getProperty("user.dir"), "out", "StarBench", "Results");
        Path logPath = Path.of(System.getProperty("user.dir"), "out", "StarBench", "log");
        Files.createDirectories(resultPath);
        Files.createDirectories(logPath);

        List<String> totalBuggyVersionFiles = new ArrayList<>();
        List<String> ErrorInActionGraph = new ArrayList<>();
        List<String> ErrorInPatternExtract = new ArrayList<>();
        List<String> ErrorInCalculateAttribute = new ArrayList<>();

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
                    List<String> totalBuggyMethods = new ArrayList<>();
                    Path buggyVersion = diffFilePath.resolve(Path.of(projectName, diff, "buggy_version", file2lines.getKey()));
                    Path fixedVersion = diffFilePath.resolve(Path.of(projectName, diff, "fixed_version", file2lines.getKey()));

                    System.out.println(buggyVersion);

//                    if(! "E:\\dataset\\FixBench\\fixBenchTest\\difffile\\beam\\0ca5a1bc5a_2e0e6a838e\\buggy_version\\TransformTranslator.java".equals(buggyVersion.toString())) {
//                        continue;
//                    }

                    // beam/2ca30a4ff1_99e081d77d
                    if(!buggyVersion.toFile().canRead() || !fixedVersion.toFile().canRead()) {
                        continue;
                    }

                    totalBuggyVersionFiles.add(buggyVersion.toString());
                    List<CodeGraph> SameMethodActionGraphsInOneFile = new ArrayList<>();
                    try {
                        SameMethodActionGraphsInOneFile = GraphBuilder.buildActionGraphInMethods(buggyVersion.toAbsolutePath().toString(), fixedVersion.toAbsolutePath().toString(), file2lines.getValue());
                    } catch (Exception e) {
                        ErrorInActionGraph.add(buggyVersion.toString());
                    }

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

                        // linux max file length is 255
                        if(methodSignature.length() > 150) {
                            methodSignature = methodSignature.substring(0, 150);
                        }

                        System.out.println(methodSignature);

//                        if(! "TransformTranslator.java__groupByKey#void".equals(methodSignature)) {
//                            continue;
//                        }

                        DotGraph dg = new DotGraph(actionGraph, new GraphConfiguration(), 0);
                        dg.toDotFile(diffLogDir.resolve(String.format("%s__action.dot", methodSignature)).toFile());


                        List<Pattern> Patterns = null;
                        try {
                            Patterns = PatternExtractor.combineGraphs(ags);
                        } catch (Exception e) {
                            ErrorInPatternExtract.add(buggyVersion.toString() + " : " + methodSignature);
                        }

                        for (Pattern pattern : Patterns) {
                            try{
                                PatternAbstractor.buildWithoutAbstract(pattern);
                            } catch (Exception e) {
                                ErrorInCalculateAttribute.add(buggyVersion.toString() + " : " + methodSignature + " : " + Patterns.indexOf(pattern));
                            }
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

            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(logPath.resolve("totalBuggyFiles.txt").toString()));
        for (String totalBuggyVersionFile : totalBuggyVersionFiles) {
            writer.write(totalBuggyVersionFile);
            writer.newLine();
        }
        writer.close();

        writer = new BufferedWriter(new FileWriter(logPath.resolve("ErrorInActionGraph.txt").toString()));
        for (String err : ErrorInActionGraph) {
            writer.write(err);
            writer.newLine();
        }
        writer.close();

        writer = new BufferedWriter(new FileWriter(logPath.resolve("ErrorInPatternExtract.txt").toString()));
        for (String err : ErrorInPatternExtract) {
            writer.write(err);
            writer.newLine();
        }
        writer.close();

        writer = new BufferedWriter(new FileWriter(logPath.resolve("ErrorInCalculateAttribute.txt").toString()));
        for (String err : ErrorInCalculateAttribute) {
            writer.write(err);
            writer.newLine();
        }
        writer.close();
    }
}
