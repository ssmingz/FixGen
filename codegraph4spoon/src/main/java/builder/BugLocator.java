package builder;

import model.CodeGraph;
import model.CtWrapper;
import model.actions.Delete;
import model.actions.Insert;
import model.actions.Move;
import model.actions.Update;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.javatuples.Pair;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.support.reflect.declaration.CtElementImpl;
import utils.ObjectUtil;
import utils.ReflectUtil;

import java.util.*;

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

        List<Pair<PatternNode, PatternNode>> nodeAttachAction = new ArrayList<>();
        for (PatternNode action : pat.getActionSet()) {
            for (PatternEdge ie : action.inEdges()) {
                if (ie.type == PatternEdge.EdgeType.ACTION) {
                    nodeAttachAction.add(new Pair<>(ie.getSource(), ie.getTarget()));
                }
            }
        }

        // compare with the target
        Pair<Map<PatternNode, CtWrapper>, Double> mappingScore = pat.compareCG(target);
        if(mappingScore.getValue1()>SIMILARITY_THRESHOLD){
            Map<PatternNode, CtWrapper> mapping = mappingScore.getValue0();
            // pattern.start is the action point, that is also the buggy point
            for(Map.Entry<PatternNode, CtWrapper> entry : mapping.entrySet()) {
                for (Pair<PatternNode, PatternNode> pair : nodeAttachAction) {
                    if (pair.getValue0() == entry.getKey()) {
                        // record the original root node
                        CtElementImpl root = target.getEntryNode();
                        // apply action
                        PatternNode action = pair.getValue1();
                        CtElementImpl oriNode = entry.getValue().getCtElementImpl();
                        if (action.getAttribute("nodeType").getTag().equals(Delete.class.getName())) {
                            applyDelete(action, oriNode, target);
                        } else if (action.getAttribute("nodeType").getTag().equals(Update.class.getName())) {
                            applyUpdate(action, oriNode, target, mapping, target.getMapping());
                        } else if (action.getAttribute("nodeType").getTag().equals(Insert.class.getName())) {
                            applyInsert(action, oriNode, target, mapping, target.getMapping());
                        } else if (action.getAttribute("nodeType").getTag().equals(Move.class.getName())) {
                            applyMove(action, oriNode, target, mapping);
                        } else {
                            System.out.println("[warn]Invalid action nodeType");
                        }
                        ObjectUtil.writeStringToFile(root.prettyprint(), filePath);
                    }
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
        target.deleteCGId(oriNode);
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
        // delete oriNode in the old tree
        oriNode.delete();
        // TODO: move to where, the concrete position of move.target
        modifyValueByRole(parentInTarget, (List<Pair<CtRole, Class>>) action.getAttribute("position").getTag(), oriNode);
    }

    private void applyUpdate(PatternNode action, CtElementImpl oriNode, CodeGraph target, Map<PatternNode, CtWrapper> mapping4pattern, Map<CtWrapper, CtWrapper> mapping4diff) {
        PatternNode newInPattern = null;
        for (PatternEdge oe : action.outEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                newInPattern = oe.getTarget();
                break;
            }
        }
        if (newInPattern != null) {
            // TODO: follow the subtree of update.target to create a new CtElementImpl
            CtElementImpl update = createSpoonNodeRecursively(newInPattern, mapping4pattern, mapping4diff);
            // update codegraph node set
            target.deleteCGId(oriNode);
            target.nodeSetAdd(update);
            // TODO: replace oriNode with the new node in the old tree
            oriNode.replace(update);
        } else {
            System.out.println("[error]Cannot find UPDATE.target in pattern: " + target.getFileName());
        }
    }

    private void applyInsert(PatternNode action, CtElementImpl oriNode, CodeGraph target, Map<PatternNode, CtWrapper> mapping4pattern, Map<CtWrapper, CtWrapper> mapping4diff) {
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
            CtElementImpl insert = createSpoonNodeRecursively(newInPattern, mapping4pattern, mapping4diff);
            // insert.source is the parent, which is oriNode
            // TODO: insert to where, the concrete position of insert.source
            modifyValueByRole(oriNode, (List<Pair<CtRole, Class>>) action.getAttribute("position").getTag(), insert);
            // update codegraph node set
            target.nodeSetAdd(insert);
        } else {
            System.out.println("[error]Cannot find INSERT.target in pattern: " + target.getFileName());
        }
    }

    private void modifyValueByRole(CtElementImpl parent, List<Pair<CtRole, Class>> roles, CtElementImpl child) {
        CtRole pre = null;
        for (Pair<CtRole, Class> pair : roles) {
            RoleHandler rh = RoleHandlerHelper.getOptionalRoleHandler(parent.getClass(), pair.getValue0());
            if (rh != null) {
                CtElementImpl ori = parent.getValueByRole(pair.getValue0());
                if (ori instanceof List) {
                    ((List<CtElementImpl>) ori).add(0, child);
                } else {
                    if (pre != null) {
                        if (ori.getValueByRole(pre) instanceof List)
                            ((List<CtElementImpl>) ori.getValueByRole(pre)).add(child);
                        else
                            ori.setValueByRole(pre, child);
                    } else {
                        parent.setValueByRole(pair.getValue0(), child);
                    }
                }
                break;
            } else {
                pre = pair.getValue0();
            }
        }
    }

    public CtElementImpl getRoot(CtElementImpl node) {
        CtElementImpl pointer = node;
        while (pointer.getParent()!=null) {
            pointer = (CtElementImpl) pointer.getParent();
        }
        return pointer;
    }

    public CtElementImpl createSpoonNodeRecursively(PatternNode pnRoot, Map<PatternNode, CtWrapper> mapping4pattern, Map<CtWrapper, CtWrapper> mapping4diff) {
        // nodeType->nodeType2->nodeType3
        String clazz = pnRoot.getAttribute("nodeType") != null ? pnRoot.getAttribute("nodeType").getTag().toString() :
                        (pnRoot.getAttribute("nodeType2") != null ? pnRoot.getAttribute("nodeType2").getTag().toString() :
                            (pnRoot.getAttribute("nodeType3") != null ? pnRoot.getAttribute("nodeType3").getTag().toString() : null));
        if (clazz == null) {
            throw new IllegalArgumentException("Create new spoon node failed: pattern node does not have node type attribute");
        }
        CtElementImpl newly = (CtElementImpl) ReflectUtil.createInstance(clazz);
        if (newly == null) {
            throw new IllegalStateException("Create new spoon node failed: createInstance() return null");
        }
        // check value for name
        if (RoleHandlerHelper.getOptionalRoleHandler(newly.getClass(), CtRole.NAME) != null) {
            // check define-use for name
            boolean hasDef = false, hasUse = false;
            for (PatternEdge ie : pnRoot.inEdges()) {
                if (ie.type == PatternEdge.EdgeType.DEF_USE) {
                    hasDef = true;
                    if (mapping4pattern.containsKey(ie.getSource()))
                        newly.setValueByRole(CtRole.NAME, mapping4pattern.get(ie.getSource()));
                    else
                        System.out.println("[warn]Cannot find matched define-position in source code according to pattern");
                    break;
                }
            }
            if (!hasDef) {
                for (PatternEdge oe : pnRoot.outEdges()) {
                    if (oe.type == PatternEdge.EdgeType.DEF_USE) {
                        hasUse = true;
                        if (mapping4pattern.containsKey(oe.getTarget()))
                            newly.setValueByRole(CtRole.NAME, mapping4pattern.get(oe.getTarget()));
                        else
                            System.out.println("[warn]Cannot find matched use-position in source code according to pattern");
                        break;
                    }
                }
            }
            if (!hasDef && !hasUse) {
                newly.setValueByRole(CtRole.NAME, pnRoot.getAttribute("value") != null ?
                        pnRoot.getAttribute("value").getTag() : pnRoot.getAttribute("value2").getTag());
            }
        }
        // recursively for children
        for (PatternEdge oe : pnRoot.outEdges()) {
            if (oe.type == PatternEdge.EdgeType.AST) {
                // node type
                CtElementImpl child = createSpoonNodeRecursively(oe.getTarget(), mapping4pattern, mapping4diff);
                // role in parent
                CtRole role = CtRole.fromName((String) oe.getTarget().getAttribute("locationInParent").getTag());
                if (newly.getValueByRole(role) instanceof List) {
                    ((List<CtElementImpl>) newly.getValueByRole(role)).add(child);
                } else {
                    newly.setValueByRole(role, child);
                }
            }
            // TODO: seems no need to extend control dep in patch part in pattern, control dep can be extended from its parent?
        }
        return newly;
    }
}
