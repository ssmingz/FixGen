package builder;

import model.CodeGraph;
import model.CtWrapper;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.javatuples.Pair;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.visitor.ForceImportProcessor;
import spoon.support.reflect.cu.position.SourcePositionImpl;
import spoon.support.reflect.declaration.CtElementImpl;
import utils.ObjectUtil;

import java.util.HashMap;
import java.util.Iterator;
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
                    SourcePosition pos = entry.getValue().getCtElementImpl().getPosition();
                    if (pos.isValidPosition()) {
                        String buggyFile = entry.getValue().getCtElementImpl().getPosition().getFile().getAbsolutePath();
                        int buggyLine = entry.getValue().getCtElementImpl().getPosition().getLine();
                        System.out.println("[buggy line]" + buggyFile + "#" + buggyLine);
                        return buggyFile + "#" + buggyLine;
                    } else {
                        System.out.println("[warn]SourcePosition is not valid:" + entry.getValue().getCtElementImpl().toString());
                    }
                }
            }
        }
        return "FAILED";
    }

    public void applyPattern(Pattern pat, CodeGraph target, String filePath) {
        // delete action nodes and related edges in pattern
        pat.deleteActionRelated();

        Map<PatternNode, PatternNode> nodeAttachAction = new HashMap<>();
        for (PatternNode action : pat.getActionSet()) {
            for (PatternEdge ie : action.inEdges()) {
                if (ie.type == PatternEdge.EdgeType.ACTION) {
                    nodeAttachAction.put(ie.getSource(), ie.getTarget());
                }
            }
        }

        // compare with the target
        Pair<Map<PatternNode, CtWrapper>, Double> mappingScore = pat.compareCG(target);
        if(mappingScore.getValue1()>SIMILARITY_THRESHOLD){
            Map<PatternNode, CtWrapper> mapping = mappingScore.getValue0();
            // pattern.start is the action point, that is also the buggy point
            for(Map.Entry<PatternNode, CtWrapper> entry : mapping.entrySet()) {
                if (nodeAttachAction.containsKey(entry.getKey())) {
                    // record the original root node
                    CtElementImpl root = target.getEntryNode();
                    // apply action
                    PatternNode action = nodeAttachAction.get(entry.getKey());
                    CtElementImpl oriNode = entry.getValue().getCtElementImpl();
                    if (action.getAttribute("nodeType").getTag().equals("Delete")) {
                        applyDelete(action, oriNode, target);
                    } else if (action.getAttribute("nodeType").getTag().equals("Update")) {
                        applyUpdate(action, oriNode, target);
                    } else if (action.getAttribute("nodeType").getTag().equals("Insert")) {
                        applyInsert(action, oriNode, target, mapping);
                    } else if (action.getAttribute("nodeType").getTag().equals("Move")) {
                        applyMove(action, oriNode, target, mapping);
                    } else {
                        System.out.println("[warn]Invalid action nodeType");
                    }
                    ObjectUtil.writeStringToFile(root.prettyprint(), filePath);
                }
            }
        }
    }

    private void applyDelete(PatternNode action, CtElementImpl oriNode, CodeGraph target) {
        // TODO: remove if in list, or else replace by null
        Iterator<CtWrapper> itr = target.getNodes().iterator();
        while (itr.hasNext()) {
            CtWrapper e = itr.next();
            if (e.getCtElementImpl() == oriNode) {
                itr.remove();
                break;
            }
        }
        oriNode.delete();
    }

    private void applyMove(PatternNode action, CtElementImpl oriNode, CodeGraph target, Map<PatternNode, CtWrapper> mapping) {
        // find move.target in pattern
        CtElementImpl parentInTarget = null;
        for (PatternEdge oe : action.outEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                PatternNode parentInPattern = oe.getTarget();
                if (mapping.containsKey(parentInPattern)) {
                    // find move.target in target code graph
                    parentInTarget = mapping.get(parentInPattern).getCtElementImpl();
                } else {
                    System.out.println("[error]Cannot find mapped MOVE.target in target code graph: " + target.getFileName());
                    return;
                }
            }
        }
        // TODO: move to where, the concrete position of move.target
        // TODO: delete oriNode in the old tree
    }

    private void applyUpdate(PatternNode action, CtElementImpl oriNode, CodeGraph target) {
        PatternNode newInPattern = null;
        for (PatternEdge oe : action.outEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                newInPattern = oe.getTarget();
                break;
            }
        }
        if (newInPattern != null) {
            // TODO: follow the subtree of update.target to create a new CtElementImpl
            // TODO: replace oriNode with the new node in the old tree
            //oriNode.replace();
        } else {
            System.out.println("[error]Cannot find UPDATE.target in pattern: " + target.getFileName());
        }
    }

    private void applyInsert(PatternNode action, CtElementImpl oriNode, CodeGraph target, Map<PatternNode, CtWrapper> mapping) {
        // insert.target is the new node to be created
        PatternNode newInPattern = null;
        for (PatternEdge oe : action.outEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                newInPattern = oe.getTarget();
                break;
            }
        }
        if (newInPattern != null) {
            // TODO: follow the subtree of insert.target to create a new CtElementImpl
            // insert.source is the parent, which is oriNode
            // TODO: insert to where, the concrete position of insert.source
        } else {
            System.out.println("[error]Cannot find INSERT.target in pattern: " + target.getFileName());
        }
    }

    public CtElementImpl getRoot(CtElementImpl node) {
        CtElementImpl pointer = node;
        while (pointer.getParent()!=null) {
            pointer = (CtElementImpl) pointer.getParent();
        }
        return pointer;
    }
}
