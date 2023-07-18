package builder;

import codegraph.Edge;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.Delete;
import model.actions.Insert;
import model.actions.Move;
import model.actions.Update;
import model.pattern.Pattern;
import model.pattern.PatternEdge;
import model.pattern.PatternNode;
import org.checkerframework.checker.units.qual.C;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.Child;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtThisAccessImpl;
import spoon.support.reflect.declaration.CtElementImpl;
import utils.ObjectUtil;
import utils.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
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
        // record the original root node
        CtElementImpl root = target.getEntryNode();
        if(mappingScore.getValue1()>SIMILARITY_THRESHOLD){
            Map<PatternNode, CtWrapper> mapping = mappingScore.getValue0();
            // pattern.start is the action point, that is also the buggy point
            List<Pair<PatternNode, CtElementImpl>> temp = new ArrayList<>();
            for(Map.Entry<PatternNode, CtWrapper> entry : mapping.entrySet()) {
                for (Pair<PatternNode, PatternNode> pair : nodeAttachAction) {
                    if (pair.getValue0() == entry.getKey()) {
                        // apply action
                        PatternNode action = pair.getValue1();
                        CtElementImpl oriNode = entry.getValue().getCtElementImpl();
                        temp.add(Pair.with(action, oriNode));
                    }
                }
            }
            for(Pair<PatternNode, CtElementImpl> tri : temp) {
                PatternNode action = tri.getValue0();
                CtElementImpl oriNode = tri.getValue1();
                if (action.getAttribute("nodeType").getTag().equals(Delete.class)) {
                    applyDelete(action, oriNode, target);
                } else if (action.getAttribute("nodeType").getTag().equals(Update.class)) {
                    applyUpdate(action, oriNode, target, mapping, target.getMapping());
                } else if (action.getAttribute("nodeType").getTag().equals(Insert.class)) {
                    applyInsert(action, oriNode, target, mapping, target.getMapping());
                } else if (action.getAttribute("nodeType").getTag().equals(Move.class)) {
                    applyMove(action, oriNode, target, mapping);
                } else {
                    System.out.println("[warn]Invalid action nodeType");
                }
            }
            ObjectUtil.writeStringToFile(root.prettyprint(), filePath);
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
        CtElementImpl moveDstTarget = null;
        for (PatternEdge oe : action.outEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                PatternNode moveDstPattern = oe.getTarget();
                if (mapping.containsKey(moveDstPattern)) {
                    // find move.target in target code graph
                    moveDstTarget = mapping.get(moveDstPattern).getCtElementImpl();
                } else {
                    System.out.println("[error]Cannot find mapped MOVE.target in target code graph: " + target.getFileName());
                    return;
                }
            }
        }
        // delete oriNode in the old tree
        oriNode.delete();
        // TODO: move to where, the concrete position of move.target
        List<Pair<CtRole, Class>> roles;
        if (!action.position.isAbstract()) {
            roles = (List<Pair<CtRole, Class>>) action.position.getTag();
        } else {
            roles = new ArrayList<>();
            roles.add(Pair.with(moveDstTarget.getRoleInParent(), moveDstTarget.getClass()));
        }
        modifyValueByRole((CtElementImpl) moveDstTarget.getParent(), roles, oriNode, moveDstTarget);
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
            mapping4pattern.put(newInPattern, new CtWrapper(update));
            mapping4pattern.putAll(addMapping4Child(newInPattern, update));
            // update codegraph node set
            target.deleteCGId(oriNode);
            target.nodeSetAdd(update);
            // TODO: replace oriNode with the new node in the old tree
//            oriNode.replace(update);
            List<Pair<CtRole, Class>> roles;
            if (!action.position.isAbstract()) {
                roles = (List<Pair<CtRole, Class>>) action.position.getTag();
            } else {
                roles = new ArrayList<>();
                roles.add(Pair.with(oriNode.getRoleInParent(), oriNode.getClass()));
            }
            modifyValueByRole((CtElementImpl) oriNode.getParent(), roles, update, oriNode);

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
            // add new mapping
            mapping4pattern.put(newInPattern, new CtWrapper(insert));
            mapping4pattern.putAll(addMapping4Child(newInPattern, insert));
            // insert.source is the parent, which is oriNode
            // TODO: insert to where, the concrete position of insert.source
            modifyValueByRole(oriNode, (List<Pair<CtRole, Class>>) action.position.getTag(), insert, null);
            // update codegraph node set
            target.nodeSetAdd(insert);
        } else {
            System.out.println("[error]Cannot find INSERT.target in pattern: " + target.getFileName());
        }
    }

    private Map<PatternNode, CtWrapper> addMapping4Child(PatternNode pn, CtElementImpl cte) {
        Map<PatternNode, CtWrapper> childMap = new LinkedHashMap<>();
        if (pn == null || cte == null)
            return childMap;
        for (PatternEdge oe : pn.outEdges()) {
            if (oe.type == PatternEdge.EdgeType.AST) {
                String location = !oe.getTarget().getAttribute("locationInParent").isAbstract() ? (String) oe.getTarget().getAttribute("locationInParent").getTag() : null;
                if (location != null && CtRole.fromName(location) != null) {
                    Object child = cte.getValueByRole(CtRole.fromName(location));
                    if (child instanceof CtElementImpl) {
                        childMap.put(oe.getTarget(), new CtWrapper((CtElementImpl) child));
                        childMap.putAll(addMapping4Child(oe.getTarget(), (CtElementImpl) child));
                    } else if (child instanceof List) {
                        int listIndex = !oe.getTarget().
                                listIndex.isAbstract() ? (int) oe.getTarget().listIndex.getTag() : -1;
                        if (listIndex != -1 && ((List<?>) child).size()>listIndex) {
                            childMap.put(oe.getTarget(), new CtWrapper(((List<CtElementImpl>) child).get(listIndex)));
                            childMap.putAll(addMapping4Child(oe.getTarget(), ((List<CtElementImpl>) child).get(listIndex)));
                        }
                    }
                }
            }
        }
        return childMap;
    }

    private void modifyValueByRole(CtElementImpl parent, List<Pair<CtRole, Class>> roles, CtElementImpl child, CtElementImpl replacement) {
        CtRole pre = null;
        for (Pair<CtRole, Class> pair : roles) {
            RoleHandler rh = RoleHandlerHelper.getOptionalRoleHandler(parent.getClass(), pair.getValue0());
            if (rh != null) {
                Object ori = parent.getValueByRole(pair.getValue0());
                if (ori instanceof List) {
                    if (((List<?>) ori).isEmpty()) {
                        ArrayList<CtElementImpl> init = new ArrayList<>();
                        init.add(child);
                        parent.setValueByRole(pair.getValue0(), init);
                    } else if (ori.getClass().getSimpleName().equals("UnmodifiableRandomAccessList")){
                        ArrayList<CtElementImpl> init = new ArrayList<>();
                        init.addAll((List<? extends CtElementImpl>) ori);
                        if (init.contains(replacement))
                            Collections.replaceAll(init, replacement, child);
                        else
                            init.add(child);
                        parent.setValueByRole(pair.getValue0(), Collections.unmodifiableList(init));
                    } else {
                        if (((List<?>) ori).contains(replacement))
                            Collections.replaceAll((List<CtElementImpl>) ori, replacement, child);
                        else
                            ((List<CtElementImpl>) ori).add(child);
                    }
                } else {
                    if (pre != null) {
                        if (((CtElementImpl) ori).getValueByRole(pre) instanceof List) {
                            if (((List<?>) ((CtElementImpl) ori).getValueByRole(pre)).isEmpty()) {
                                ArrayList<CtElementImpl> init = new ArrayList<>();
                                init.add(child);
                                parent.setValueByRole(pre, init);
                            } else if ((((CtElementImpl) ori).getValueByRole(pre)).getClass().getSimpleName().equals("UnmodifiableRandomAccessList")) {
                                ArrayList<CtElementImpl> init = new ArrayList<>();
                                init.addAll((((CtElementImpl) ori).getValueByRole(pre)));
                                if (init.contains(replacement))
                                    Collections.replaceAll(init, replacement, child);
                                else
                                    init.add(child);
                                parent.setValueByRole(pre, init);
                            } else {
                                if (((List<CtElementImpl>) ((CtElementImpl) ori).getValueByRole(pre)).contains(replacement))
                                    Collections.replaceAll(((CtElementImpl) ori).getValueByRole(pre), replacement, child);
                                else
                                    ((List<CtElementImpl>) ((CtElementImpl) ori).getValueByRole(pre)).add(child);
                            }
                        } else {
                            ((CtElementImpl) ori).setValueByRole(pre, child);
                        }
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

    @SuppressWarnings("unchecked")
    public <T> CtElementImpl createSpoonNodeRecursively(PatternNode pnRoot, Map<PatternNode, CtWrapper> mapping4pattern, Map<CtWrapper, CtWrapper> mapping4diff) {
        // nodeType
        Class clazz = !pnRoot.getAttribute("nodeType").isAbstract() ? (Class) pnRoot.getAttribute("nodeType").getTag():null;
        if (clazz == null) {
            throw new IllegalArgumentException("Create new spoon node failed: pattern node does not have node type attribute");
        }
        CtElementImpl newly = null;
        try {
            newly = (CtElementImpl) ReflectUtil.createInstance(clazz);
        } catch (InstantiationException e) {
            System.out.println("[warn]failed to directly use class name to create instance");
            // if is update.target
            for (PatternEdge ie : pnRoot.inEdges()) {
                if (!ie.isAbstract() && ie.type == PatternEdge.EdgeType.ACTION && !ie.getSource().getAttribute("nodeType").isAbstract()
                        && ie.getSource().getAttribute("nodeType").getTag().equals(Update.class)) {
                    for (PatternEdge ie2 : ie.getSource().inEdges()) {
                        if (!ie2.isAbstract() && ie2.type == PatternEdge.EdgeType.ACTION && mapping4pattern.containsKey(ie2.getSource())) {
                            try {
                                clazz = mapping4pattern.get(ie2.getSource()).getCtElementImpl().getClass();
                                newly = (CtElementImpl) ReflectUtil.createInstance(clazz);
                            } catch (InstantiationException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        if (newly == null) {
            throw new IllegalStateException("Create new spoon node failed: createInstance() return null");
        }
        if (!pnRoot.implicit.isAbstract())
            newly.setImplicit((Boolean) pnRoot.implicit.getTag());
        // check value for name
        if (RoleHandlerHelper.getOptionalRoleHandler(newly.getClass(), CtRole.NAME) != null) {
            // check define-use for name
            boolean hasDef = false, hasUse = false;
            for (PatternEdge ie : pnRoot.inEdges()) {
                if (!ie.isAbstract() && ie.type == PatternEdge.EdgeType.DEF_USE) {
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
                    if (!oe.isAbstract() && oe.type == PatternEdge.EdgeType.DEF_USE) {
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
                newly.setValueByRole(CtRole.NAME, !pnRoot.getAttribute("value").isAbstract() ?
                        pnRoot.getAttribute("value").getTag() : pnRoot.getAttribute("value2").getTag());
            }
        }
        String valueType = pnRoot.getAttribute("valueType")!=null && !pnRoot.getAttribute("valueType").isAbstract() ? String.valueOf(pnRoot.getAttribute("valueType").getTag()) : null;
        T value = pnRoot.getAttribute("value")!=null && !pnRoot.getAttribute("value").isAbstract() ? (T) pnRoot.getAttribute("value").getTag() : null;
        if (clazz.equals(CtLiteralImpl.class) && valueType != null && pnRoot.getAttribute("value")!=null && !pnRoot.getAttribute("value").isAbstract()) {
            ((CtLiteralImpl) newly).setValue(value);
        }
        // recursively for children
        for (PatternEdge oe : pnRoot.outEdges()) {
            if (!oe.isAbstract() && oe.type == PatternEdge.EdgeType.AST && !oe.getTarget().isVirtual()) {
                // node type
                CtElementImpl child = createSpoonNodeRecursively(oe.getTarget(), mapping4pattern, mapping4diff);
//                if (child.prettyprint().equals("PlaceHold"))
//                    continue;
                // role in parent
                CtRole role = CtRole.fromName((String) oe.getTarget().getAttribute("locationInParent").getTag());
                Object listSize = oe.getTarget().listSize.getTag();
                Object listIndex = oe.getTarget().listIndex.getTag();
                if (newly.getValueByRole(role) instanceof List) {
                    int size = (Integer) listSize;
                    int index = (Integer) listIndex;
                    if (((List<?>) newly.getValueByRole(role)).isEmpty()) {
                        CtElementImpl[] init = new CtElementImpl[size];
                        init[index] = child;
                        newly.setValueByRole(role, Arrays.asList(init));
                    } else if (newly.getValueByRole(role).getClass().getSimpleName().equals("UnmodifiableRandomAccessList")){
                        CtElementImpl[] init = ((List<?>) newly.getValueByRole(role)).toArray(new CtElementImpl[size]);
                        init[index] = child;
                        newly.setValueByRole(role, Collections.unmodifiableList(Arrays.asList(init)));
                    } else {
                        ((List<CtElementImpl>) newly.getValueByRole(role)).add(index, child);
                    }
                } else {
                    newly.setValueByRole(role, child);
                }
            }
            // TODO: seems no need to extend control dep in patch part in pattern, control dep can be extended from its parent?
        }
        return newly;
    }
}
