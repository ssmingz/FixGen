package builder;

import model.CodeGraph;
import model.GraphConfiguration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import pattern.Pair;
import pattern.Pattern;
import utils.Constant;
import utils.FileIO;
import utils.JavaASTUtil;

import java.util.*;

public class PatternExtractor {
    public Set<Pattern> extractPattern(String bugCommit, String fixCommit) {
        return extractPattern(bugCommit, fixCommit, Constant.MAX_CHANGE_LINE);
    }

    public Set<Pattern> extractPattern(String bugCommit, String fixCommit, int maxChangeLine) {
        CompilationUnit bugCU = (CompilationUnit) JavaASTUtil.parseSource(FileIO.readStringFromFile(bugCommit), null);
        CompilationUnit fixCU = (CompilationUnit) JavaASTUtil.parseSource(FileIO.readStringFromFile(fixCommit), null);
        if (bugCommit == null || fixCommit == null) {
            return Collections.emptySet();
        }

        List<Pair<MethodDeclaration, MethodDeclaration>> matchMap = Matcher.match(bugCU, fixCU);
        return extractPattern(matchMap, bugCommit, bugCU, fixCommit, fixCU, maxChangeLine);
    }

    private Set<Pattern> extractPattern(List<Pair<MethodDeclaration, MethodDeclaration>> matchMap,
                                        String bugCommit, CompilationUnit bugCU,
                                        String fixCommit, CompilationUnit fixCU,
                                        int maxChangeLine) {
        Set<Pattern> patterns = new HashSet<>();

        GraphBuilder cgBuilder = new GraphBuilder(new GraphConfiguration());
        for (Pair<MethodDeclaration, MethodDeclaration> pair : matchMap) {
            cgBuilder.setCurrentCU(bugCU);
            cgBuilder.setCurrentType(JavaASTUtil.getTypeDecl(pair.getFirst()));
            CodeGraph bugGraph = cgBuilder.buildGraph(pair.getFirst(), bugCommit, null);
            cgBuilder.setCurrentCU(fixCU);
            cgBuilder.setCurrentType(JavaASTUtil.getTypeDecl(pair.getSecond()));
            CodeGraph fixGraph = cgBuilder.buildGraph(pair.getSecond(), fixCommit, null);

            if (bugGraph.toSrcString().equals(fixGraph.toSrcString())) {
                continue;
            }

            // TODO: extract text diff and compare it with maxChangeLine

            if (Matcher.match(bugGraph, fixGraph)) {

            }

        }

        return patterns;
    }
}
