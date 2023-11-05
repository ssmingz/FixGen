import config.Options;
import config.pojo.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DiffUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CorrectCal {

    private final static Logger logger = LoggerFactory.getLogger(CorrectCal.class);
    public static void main(String[] args) {
        Option option = processArgs(args);
        logger.info("option: {}", option);
        run(option);
    }

    private static boolean isPatchCorrect(List<String> groundtruth, List<String> patch) {
        if (groundtruth.size() != patch.size()) return false;
        for (int i = 0; i < groundtruth.size(); i++) {
            if (groundtruth.get(i).startsWith("--- ") != patch.get(i).startsWith("--- ")) return false;
            else if (groundtruth.get(i).startsWith("+++ ") != patch.get(i).startsWith("+++ ")) return false;
            if (!groundtruth.get(i).startsWith("--- ") && !groundtruth.get(i).startsWith("+++ ")) {
                if (!groundtruth.get(i).equals(patch.get(i))) return false;
            }
        }
        return true;
    }

    private static void run(Option option){
        if(option.testOnDataset.datasetName.equals("c3")) {
            List<String> projects = List.of("ant", "checkstyle", "cobertura", "drjava", "junit", "swt");
            for (String project : projects) {
                System.out.println("project: " + project);
                Path projectRootPath = Paths.get(option.testOnDataset.datasetPath).resolve(project);
                Path patchRootPath = Paths.get(option.testOnDataset.patchPath).resolve(project);
                File[] patchGroups = patchRootPath.toFile().listFiles();
                int totalCases = 0;
                int correctCases = 0;
                for (File group : patchGroups) {
                    String groupID = group.getName();
                    Path patchGroupRoot = patchRootPath.resolve(groupID);
                    File[] patchCases = Arrays.stream(Objects.requireNonNull(patchGroupRoot.toFile().listFiles())).filter(File::isDirectory).toArray(File[]::new);
                    for (File patchCase : patchCases) {
                        String patchCaseID = patchCase.getName();
                        totalCases ++;
                        Path beforePath = projectRootPath.resolve(groupID).resolve(patchCaseID).resolve("before.java");
                        Path afterPath = projectRootPath.resolve(groupID).resolve(patchCaseID).resolve("after.java");

                        List<String> beforeAfter = DiffUtil.getDiff(beforePath.toString(), afterPath.toString());
                        Path patchCaseRoot = patchGroupRoot.resolve(patchCaseID);
                        File[] patches = patchCaseRoot.toFile().listFiles();
                        for (File patch : patches) {
                            String patchID = patch.getName();
                            Path patchPath = patchCaseRoot.resolve(patchID);
                            List<String> beforePatch = DiffUtil.getDiff(beforePath.toString(), patchPath.toString());

                            if(isPatchCorrect(beforeAfter, beforePatch)) {
                                System.out.println("[true]correct patch: " + patchPath);
                                correctCases++;
                                break;
                            } else {
                                System.out.println("[false]incorrect patch: " + patchPath);
                            }
                        }
                    }
                }
                System.out.println("total cases: " + totalCases);
                System.out.println("correct cases: " + correctCases);
            }

        } else {
            Path projectRootPath = Paths.get(option.testOnDataset.datasetPath);
            Path patchRootPath = Paths.get(option.testOnDataset.patchPath).resolve(option.testOnDataset.datasetName);

            File[] patchGroups = patchRootPath.toFile().listFiles();
            int totalCases = 0;
            int correctCases = 0;
            for (File group : patchGroups) {
                String groupID = group.getName();
                Path patchGroupRoot = patchRootPath.resolve(groupID);
                File[] patchCases = Arrays.stream(Objects.requireNonNull(patchGroupRoot.toFile().listFiles())).filter(File::isDirectory).toArray(File[]::new);
                for (File patchCase : patchCases) {
                    String patchCaseID = patchCase.getName();
                    totalCases ++;
                    Path beforePath = projectRootPath.resolve(groupID).resolve(patchCaseID).resolve("before.java");
                    Path afterPath = projectRootPath.resolve(groupID).resolve(patchCaseID).resolve("after.java");

                    if(groupID.equals("36") && patchCaseID.equals("0")) {
                        System.out.println("here");
                    }

                    List<String> beforeAfter = DiffUtil.getDiff(beforePath.toString(), afterPath.toString());
                    Path patchCaseRoot = patchGroupRoot.resolve(patchCaseID);
                    File[] patches = patchCaseRoot.toFile().listFiles();
                    for (File patch : patches) {
                        String patchID = patch.getName();
                        Path patchPath = patchCaseRoot.resolve(patchID);
                        List<String> beforePatch = DiffUtil.getDiff(beforePath.toString(), patchPath.toString());

                        if(isPatchCorrect(beforeAfter, beforePatch)) {
                            System.out.println("[true]correct patch: " + patchPath);
                            correctCases++;
                            break;
                        } else {
                            System.out.println("[false]incorrect patch: " + patchPath);
                        }
                    }
                }
            }
            System.out.println("total cases: " + totalCases);
            System.out.println("correct cases: " + correctCases);
        }

    }

    private static Option processArgs(String[] args) {
        return Options.parse();
    }
}
