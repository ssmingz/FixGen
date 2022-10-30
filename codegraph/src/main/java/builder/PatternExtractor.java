package builder;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import pattern.Pair;
import pattern.Pattern;
import utils.Constant;
import utils.JavaASTUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PatternExtractor {
    public Set<Pattern> extractPattern(String bugCommit, String fixCommit) {
        return extractPattern(bugCommit, fixCommit, Constant.MAX_CHANGE_LINE);
    }

    public Set<Pattern> extractPattern(String bugCommit, String fixCommit, int maxChangeLine) {
        CompilationUnit bugCU = (CompilationUnit) JavaASTUtil.parseSource(bugCommit, null);
        CompilationUnit fixCU = (CompilationUnit) JavaASTUtil.parseSource(fixCommit, null);
        if (bugCommit == null || fixCommit == null) {
            return Collections.emptySet();
        }

        List<Pair<MethodDeclaration, MethodDeclaration>> matchMap = Matcher.match(bugCU, fixCU);
        return extractPattern(matchMap, bugCommit, bugCU, fixCommit, fixCU, maxChangeLine);
    }
}
