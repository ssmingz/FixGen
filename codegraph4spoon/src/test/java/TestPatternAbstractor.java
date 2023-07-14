import builder.GraphBuilder;
import builder.GraphConfiguration;
import builder.PatternAbstractor;
import builder.PatternExtractor;
import model.CodeGraph;
import model.pattern.Pattern;
import org.junit.Test;
import utils.ASTUtil;
import utils.DotGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class TestPatternAbstractor {
    @Test
    public void testPatternAbstractFromMultiplePairs() {
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
            // abstract pattern
            PatternAbstractor abs = new PatternAbstractor((int) Math.floor(size*0.8));
            pat = abs.abstractPattern(pat);
            DotGraph dot = new DotGraph(pat, 0, true, false);
            File dir = new File(String.format("%s/out/c3_%s_%d_pattern_abstract_%d.dot", System.getProperty("user.dir"), testPro, testId, combinedGraphs.indexOf(pat)));
            dot.toDotFile(dir);
        }
    }

    @Test
    public void testPatternAbstractorOnC3() {
        String[] projects = {"junit", "cobertura", "checkstyle"};
        String base = TestConfig.MAC_BASE;
        int patSizeCounter = 0, patCounter = 0;
        for (int i=0; i<projects.length; i++) {
            File dir = new File(String.format(base + "dataset/" + projects[i]));
            for (File group : dir.listFiles()) {
                if (group.isDirectory()) {
                    List<CodeGraph> ags = new ArrayList<>();
                    File basedir = new File(String.format("%s/codegraphs/%s/%s", base, projects[i], group.getName()));
                    if (!basedir.exists() || !basedir.isDirectory()) {
                        basedir.mkdirs();
                    }
                    if (basedir.listFiles().length>0)
                        continue;
                    System.out.println("start " + group.getAbsolutePath());
                    for (File pair : group.listFiles()) {
                        if (pair.isDirectory()) {
                            String srcPath = pair.getAbsolutePath()+"/before.java";
                            String tarPath = pair.getAbsolutePath()+"/after.java";
                            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
                            ags.add(ag);

                            GraphConfiguration config = new GraphConfiguration();
                            int nodeIndexCounter = 0;
                            DotGraph dg = new DotGraph(ag, config, nodeIndexCounter);
                            File dogf0 = new File(String.format("%s/codegraph_%s.dot", basedir, pair.getName()));
                            dg.toDotFile(dogf0);
                        }
                    }
                    // extract pattern from more-than-one graphs
                    try {
                        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
                        patCounter++;
                        if (combinedGraphs.size()>1) {
                            System.out.println("[warn]extracted pattern not single, size:" + combinedGraphs.size());
                            patSizeCounter++;
                        }
                        for (Pattern pat : combinedGraphs) {
                            DotGraph dot = new DotGraph(pat, 0, false, false);
                            File dotf = new File(String.format("%s/pattern_%d.dot", basedir, combinedGraphs.indexOf(pat)));
                            dot.toDotFile(dotf);

                            // abstract pattern
                            int size = group.listFiles(File::isDirectory).length;
                            PatternAbstractor abs = new PatternAbstractor(size*0.8);
                            pat = abs.abstractPattern(pat);

                            DotGraph dot2 = new DotGraph(pat, 0, true, false);
                            File dotf2 = new File(String.format("%s/pattern_abstract_%d.dot", basedir, combinedGraphs.indexOf(pat)));
                            dot2.toDotFile(dotf2);

                            assertNotNull(pat);
                            System.out.println(group.getAbsolutePath() + ": abstract pattern ok");
                        }
                    } catch (Exception e) {
                        System.out.println(group.getAbsolutePath() + " " + e.getMessage());
                    }
                }
            }
        }
        System.out.printf("extracted pattern not single: %d/%d%n", patSizeCounter, patCounter);
    }

    @Test
    public void testPatternAbstractorOnC3_fordebug() {
        String pro = "junit";
        int group = 19;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("%s/dataset/%s/%d", TestConfig.MAC_BASE, pro, group);
        int size = new File(base).listFiles(File::isDirectory).length;
        for (int i=0; i<size; i++) {
            String srcPath = String.format("%s/%d/before.java", base, i);
            String tarPath = String.format("%s/%d/after.java", base, i);
            // build action graph
            CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, new int[] {});
            ags.add(ag);
            // draw dot graph
            GraphConfiguration config = new GraphConfiguration();
            int nodeIndexCounter = 0;
            DotGraph dg = new DotGraph(ag, config, nodeIndexCounter);
            File dir = new File(System.getProperty("user.dir") + "/out/" + String.format("c3_%s_%d_%d_action.dot", pro, group, i));
            dg.toDotFile(dir);
        }
        // extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot = new DotGraph(pat, 0, false, false);
            File dir = new File(System.getProperty("user.dir") + String.format("/out/c3_%s_%d_pattern_%d.dot", pro, group, combinedGraphs.indexOf(pat)));
            dot.toDotFile(dir);
            PatternAbstractor abs = new PatternAbstractor(size);
            pat = abs.abstractPattern(pat);
            DotGraph dot2 = new DotGraph(pat, 0, true, false);
            File dir2 = new File(System.getProperty("user.dir") + String.format("/out/c3_%s_%d_pattern_abstract_%d.dot", pro, group, combinedGraphs.indexOf(pat)));
            dot2.toDotFile(dir2);
        }
    }

    @Test
    public void testPatternAbstractorOnFixBench_fordebug() {
        String pro = "FindBugs-DM_DEFAULT_ENCODING";
        int group = 2;
        List<CodeGraph> ags = new ArrayList<>();
        String base = String.format("%s/%s/%d", TestConfig.FIXBENCH_MAC_BASE, pro, group);
        File[] ins = new File(base).listFiles(File::isDirectory);
        int size = ins.length;
        for (File i : ins) {
            String diffFile = String.format("%s/diff.diff", i.getAbsolutePath());
            Map<String, int[]> map = ASTUtil.getDiffLinesInBuggyFile(diffFile);
            for (Map.Entry<String, int[]> entry : map.entrySet()) {
                String srcPath = String.format("%s/old/%s", i.getAbsolutePath(), entry.getKey());
                String tarPath = String.format("%s/new/%s", i.getAbsolutePath(), entry.getKey());
                // build action graph
                CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, entry.getValue());
                System.out.println("[ok]finish parsing source file:" + i.getAbsolutePath());
                ags.add(ag);
                // draw dot graph
                GraphConfiguration config = new GraphConfiguration();
                int nodeIndexCounter = 0;
                DotGraph dg = new DotGraph(ag, config, nodeIndexCounter);
                File dir = new File(System.getProperty("user.dir") + "/out/" + String.format("fixbench_%s_%s_%s_action.dot", pro, group, i.getName()));
                dg.toDotFile(dir);
            }
        }// extract pattern from more-than-one graphs
        List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
        for (Pattern pat : combinedGraphs) {
            DotGraph dot = new DotGraph(pat, 0, false, false);
            File dir = new File(System.getProperty("user.dir") + String.format("/out/fixbench_%s_%s_pattern_%d.dot", pro, group, combinedGraphs.indexOf(pat)));
            dot.toDotFile(dir);
            PatternAbstractor abs = new PatternAbstractor(size);
            pat = abs.abstractPattern(pat);
            DotGraph dot2 = new DotGraph(pat, 0, true, false);
            File dir2 = new File(System.getProperty("user.dir") + String.format("/out/fixbench_%s_%s_pattern_abstract_%d.dot", pro, group, combinedGraphs.indexOf(pat)));
            dot2.toDotFile(dir2);
        }
    }

    @Test
    public void testPatternAbstractorOnFixBench() {
        String[] projects = {"FindBugs-DM_CONVERT_CASE", "FindBugs-DM_DEFAULT_ENCODING"};
        String base = TestConfig.FIXBENCH_MAC_BASE;
        int patSizeCounter = 0, patCounter = 0;
        for (int i=0; i<projects.length; i++) {
            File dir = new File(base + projects[i]);
            for (File group : dir.listFiles(File::isDirectory)) {
                List<CodeGraph> ags = new ArrayList<>();
                File basedir = new File(String.format("%s/codegraphs/%s/%s", base, projects[i], group.getName()));
                if (!basedir.exists() || !basedir.isDirectory()) {
                    basedir.mkdirs();
                }
                System.out.println("start " + group.getAbsolutePath());
                for (File f : group.listFiles(File::isDirectory)) {
                    String diffFile = String.format("%s/diff.diff", f.getAbsolutePath());
                    Map<String, int[]> map = ASTUtil.getDiffLinesInBuggyFile(diffFile);
                    for (Map.Entry<String, int[]> entry : map.entrySet()) {
                        String srcPath = String.format("%s/old/%s", f.getAbsolutePath(), entry.getKey());
                        String tarPath = String.format("%s/new/%s", f.getAbsolutePath(), entry.getKey());
                        // build action graph
                        CodeGraph ag = GraphBuilder.buildActionGraph(srcPath, tarPath, entry.getValue());
                        System.out.println("[ok]finish parsing source file:" + f.getAbsolutePath());
                        ags.add(ag);
                        // draw dot graph
                        GraphConfiguration config = new GraphConfiguration();
                        int nodeIndexCounter = 0;
                        DotGraph dg = new DotGraph(ag, config, nodeIndexCounter);
                        File dogf0 = new File(String.format("%s/codegraph_%s.dot", basedir, f.getName()));
                        dg.toDotFile(dogf0);
                    }
                }
                // extract pattern from more-than-one graphs
                try {
                    List<Pattern> combinedGraphs = PatternExtractor.combineGraphs(ags);
                    patCounter++;
                    if (combinedGraphs.size()>1) {
                        System.out.println("[warn]extracted pattern not single, size:" + combinedGraphs.size());
                        patSizeCounter++;
                    }
                    for (Pattern pat : combinedGraphs) {
                        DotGraph dot = new DotGraph(pat, 0, false, false);
                        File dotf = new File(String.format("%s/pattern_%d.dot", basedir, combinedGraphs.indexOf(pat)));
                        dot.toDotFile(dotf);

                        // abstract pattern
                        int size = group.listFiles(p -> p.isDirectory()).length;
                        PatternAbstractor abs = new PatternAbstractor(size*0.8);
                        pat = abs.abstractPattern(pat);

                        DotGraph dot2 = new DotGraph(pat, 0, true, false);
                        File dotf2 = new File(String.format("%s/pattern_abstract_%d.dot", basedir, combinedGraphs.indexOf(pat)));
                        dot2.toDotFile(dotf2);

                        assertNotNull(pat);
                        System.out.println(group.getAbsolutePath() + ": abstract pattern ok");
                    }
                } catch (Exception e) {
                    System.out.println(group.getAbsolutePath() + " " + e.getMessage());
                }
            }
        }
        System.out.printf("extracted pattern not single: %d/%d%n", patSizeCounter, patCounter);
    }
}
