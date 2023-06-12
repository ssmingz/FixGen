package builder;

import model.CodeGraph;
import model.CtWrapper;
import model.pattern.Pattern;
import model.pattern.PatternNode;
import org.javatuples.Pair;

import java.util.Map;

public class BugLocator {
    public double SIMILARITY_THRESHOLD = 1.0;

    public BugLocator(double thres) {
        SIMILARITY_THRESHOLD = thres;
    }

    public String locateFaultByPattern(Pattern pat, CodeGraph target) {
        // delete action nodes and related edges in pattern
        pat.deleteActionRelated();

        // compare with the target
        Pair<Map<PatternNode, CtWrapper>, Double> mappingScore = pat.compareCG(target);
        if(mappingScore.getValue1()>SIMILARITY_THRESHOLD){
            // pattern.start is the action point, that is also the buggy point
            for(Map.Entry<PatternNode, CtWrapper> entry : mappingScore.getValue0().entrySet()) {
                if(entry.getKey().equals(pat.getStart())) {
                    // get bug info of the target node
                    String buggyFile = entry.getValue().getCtElementImpl().getPosition().getFile().getAbsolutePath();
                    int buggyLine = entry.getValue().getCtElementImpl().getPosition().getLine();
                    System.out.println("[buggy line]" + buggyFile + "#" + buggyLine);
                    return buggyFile + "#" + buggyLine;
                }
            }
        }
        return "FAILED";
    }
}
