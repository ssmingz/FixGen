import builder.BugLocator;
import builder.GraphBuilder;
import builder.GraphConfiguration;
import config.Options;
import config.pojo.Option;
import model.CodeGraph;
import model.pattern.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DotGraph;
import utils.ObjectUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DefectFaults {
    private final static Logger logger = LoggerFactory.getLogger(DefectFaults.class);

    public static void main(String[] args) {
        Option option = processArgs(args);
        logger.info("option: {}", option);
        run(option);
    }

    private static void run(Option option){
        Path projectPath = Paths.get(option.defectFaults.projectPath);
        Path patternPath = Paths.get(option.defectFaults.patternPath);
        Path resultsPath = Paths.get(option.defectFaults.resultsPath);

        if(! resultsPath.toFile().exists()) {
            resultsPath.toFile().mkdirs();
        }

        List<Pattern> patterns = new ArrayList<>();
        Arrays.stream(Objects.requireNonNull(patternPath.toFile().listFiles())).forEach(file -> {
            Pattern pattern = (Pattern)ObjectUtil.readObjectFromFile(file.getAbsolutePath());
            patterns.add(pattern);
        });

        try(Stream<Path> stream = Files.walk(projectPath)) {
            List<Path> javaFiles = stream.filter(path -> path.toString().endsWith(".java")).collect(Collectors.toList());
            javaFiles.forEach(path -> {
                try{
                    String relativePath = path.toString().replace(projectPath.toString(), "").replace(".java", "").replaceAll("\\\\", "/");
                    List<CodeGraph> subjectActionGraphs = GraphBuilder.buildMethodGraphs(path.toString(), new String[]{}, 8, new int[]{});
                    for (int i = 0; i < patterns.size(); i++) {
                        Pattern pattern = patterns.get(i);
                        BugLocator detector = new BugLocator(1.0);
                        for (CodeGraph subjectActionGraph : subjectActionGraphs) {
                            String patchPath = String.format("%s%sPattern%d_patch_%d.java", resultsPath, relativePath, i, subjectActionGraphs.indexOf(subjectActionGraph));
                            detector.applyPattern(pattern, subjectActionGraph, patchPath, "new");
                        }
                    }
                } catch (Exception e) {
                    logger.error("error in buildGraph", e);
                }
            });

        } catch (IOException e) {
            logger.error("error in walk projectPath", e);
        }

    }

    private static Option processArgs(String[] args) {
        return Options.parse();
    }

}
