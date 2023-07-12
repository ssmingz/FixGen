import builder.BugLocator;
import builder.GraphBuilder;
import builder.PatternAbstractor;
import builder.PatternExtractor;
import model.CodeGraph;
import model.pattern.Pattern;
import org.junit.Test;
import utils.DiffUtil;
import utils.ObjectUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestPatchCorrectness {
    @Test
    public void testPatchCorrectness1() {
        String patchPath = "/Users/yumeng/JavaProjects/FixGen/codegraph4spoon/out/patch/junit/2/0/patch_0.java";
        String beforePath = "/Users/yumeng/PycharmProjects/c3/dataset/junit/2/0/before.java";
        String afterPath = "/Users/yumeng/PycharmProjects/c3/dataset/junit/2/0/after.java";
        List<String> beforePatch =  DiffUtil.getDiff(beforePath, patchPath);
        List<String> beforeAfter = DiffUtil.getDiff(beforePath, afterPath);
        System.out.println(isPatchCorrect(beforeAfter, beforePatch));
    }

    @Test
    public void testPatchCorrectness2() {
        String[] projects = {"junit"};
        String base_gt = TestConfig.MAC_BASE;
        String base_patch = "/Users/yumeng/JavaProjects/FixGen/codegraph4spoon/out/patch/";
        int targetCounter = 0, correctCounter = 0;
        for (int i=0; i<projects.length; i++) {
            File dir = new File(base_gt + "dataset/" + projects[i]);
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    int testId = Integer.parseInt(group.getName());
                    String baseDir = String.format("%s/%d", dir, testId);
                    int size = (int) Arrays.stream(new File(baseDir).listFiles()).filter(File::isDirectory).count();
                    // each as target
                    for (int targetNo=0; targetNo<size; targetNo++) {
                        File patchDir = new File(String.format("%s/%s/%d/%d", base_patch, projects[i], testId, targetNo));
                        if (!patchDir.exists())
                            continue;
                        targetCounter++;
                        for (File patch : patchDir.listFiles()) {
                            if (patch.getName().endsWith(".java")) {
                                String patchPath = patch.getAbsolutePath();
                                String beforePath = String.format("%s/dataset/%s/%d/%d/before.java", base_gt, projects[i], testId, targetNo);
                                String afterPath = String.format("%s/dataset/%s/%d/%d/after.java", base_gt, projects[i], testId, targetNo);
                                List<String> beforePatch =  DiffUtil.getDiff(beforePath, patchPath);
                                List<String> beforeAfter = DiffUtil.getDiff(beforePath, afterPath);
                                boolean correctness = isPatchCorrect(beforeAfter, beforePatch);
                                correctCounter += correctness ? 1 : 0;
                                System.out.printf("[%b]%s%n", correctness, patchPath);
                                if (correctness)
                                    break;
                            }
                        }
                    }
                }
            }
        }
        System.out.println("[stat]target bug instance number: "+targetCounter);
        System.out.println("[stat]patch correct number: "+correctCounter);
        System.out.println("[stat]patch correct %: "+(correctCounter*1.0/targetCounter));
    }

    public boolean isPatchCorrect(List<String> groundtruth, List<String> patch) {
        if (groundtruth.size() != patch.size())
            return false;
        for (int i=0; i<groundtruth.size(); i++) {
            if (groundtruth.get(i).startsWith("--- ") != patch.get(i).startsWith("--- "))
                return false;
            else if (groundtruth.get(i).startsWith("+++ ") != patch.get(i).startsWith("+++ "))
                return false;
            if (!groundtruth.get(i).startsWith("--- ") && !groundtruth.get(i).startsWith("+++ ")) {
                if (!groundtruth.get(i).equals(patch.get(i)))
                    return false;
            }
        }
        return true;
    }
}
