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
import org.apache.commons.lang3.SerializationUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.meta.RoleHandler;
import spoon.reflect.meta.impl.RoleHandlerHelper;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.support.reflect.CtModifierHandler;
import spoon.support.reflect.code.*;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.reference.CtLocalVariableReferenceImpl;
import spoon.support.reflect.reference.CtReferenceImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;
import spoon.support.reflect.reference.CtVariableReferenceImpl;
import utils.ObjectUtil;
import utils.ReflectUtil;
import utils.SyntaxUtil;

import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class BugLocator {
    public double SIMILARITY_THRESHOLD = 1.0;

    public BugLocator(double thres) {
        SIMILARITY_THRESHOLD = thres;
    }

    public String locateFaultByPattern(Pattern pat, CodeGraph target) {
        // delete action nodes and related edges in pattern
        pat.deleteActionRelated();

        // compare with the target
        List<Pair<Map<PatternNode, CtWrapper>, Double>> mappingScoreList = pat.compareCG(target, "origin");
        for (Pair<Map<PatternNode, CtWrapper>, Double> mappingScore : mappingScoreList) {
            if (mappingScore.getValue1() >= SIMILARITY_THRESHOLD) {
                // pattern.start is the action point, that is also the buggy point
                for (Map.Entry<PatternNode, CtWrapper> entry : mappingScore.getValue0().entrySet()) {
                    if (entry.getKey().equals(pat.getStart())) {
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
        }
        return "FAILED";
    }


    public void applyPattern(Pattern pat, CodeGraph target, String filePath, String runType) {
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
        List<Pair<Map<PatternNode, CtWrapper>, Double>> mappingScoreList = pat.compareCG(target, runType);

//        List<Pair<Map<PatternNode, CtWrapper>, Double>> maxList = mappingScoreList.stream().filter(p -> Objects.equals(p.getValue1(), maxScore)).collect(Collectors.toList());
        List<Pair<Map<PatternNode, CtWrapper>, Double>> maxList = mappingScoreList.stream().limit(10).collect(Collectors.toList());
        for (Pair<Map<PatternNode, CtWrapper>, Double> mappingScore : maxList) {
            if (mappingScore.getValue1() < SIMILARITY_THRESHOLD) {
                System.out.printf("[warn]Not satisfy SIMILARITY_THRESHOLD: %s %d\n", target.getFileName(), maxList.indexOf(mappingScore));
                break;
            }
            CodeGraph targetCopy = SerializationUtils.clone(target);
            CtElementImpl root = targetCopy.getEntryNode();

            Map<PatternNode, CtWrapper> mapping = mappingScore.getValue0();
            // pattern.start is the action point, that is also the buggy point
            List<Pair<PatternNode, CtElementImpl>> temp = new ArrayList<>();
            for (Map.Entry<PatternNode, CtWrapper> entry : mapping.entrySet()) {
                for (Pair<PatternNode, PatternNode> pair : nodeAttachAction) {
                    if (pair.getValue0() == entry.getKey()) {
                        // apply action
                        PatternNode action = pair.getValue1();
                        CtElementImpl oriNode = ObjectUtil.findMappedNodeInNewGraph(entry.getValue().getCtElementImpl(), targetCopy);
                        temp.add(Pair.with(action, oriNode));
                    }
                }
            }

            List<CtElementImpl> updateOriNode = new ArrayList<>();
            for (Pair<PatternNode, CtElementImpl> pair : temp) {
                PatternNode action = pair.getValue0();
                CtElementImpl oriNode = pair.getValue1();
                if (action.getAttribute("nodeType").getTag().equals(Update.class)) {
                    updateOriNode.add(oriNode);
                }
            }

            int currentIndex = 0;
            while (!temp.isEmpty()) {
                if (currentIndex >= temp.size()) {
                    currentIndex = 0; // Wrap around to the beginning
                }

                boolean actionSucceeded = true;

                Pair<PatternNode, CtElementImpl> pair = temp.get(currentIndex);
                PatternNode action = pair.getValue0();
                CtElementImpl oriNode = pair.getValue1();

                System.out.println(action.getAttribute("nodeType").getTag());
                try {
                    if (action.getAttribute("nodeType").getTag().equals(Delete.class)) {
                        actionSucceeded = applyDelete(action, oriNode, targetCopy, mapping);
                    } else if (action.getAttribute("nodeType").getTag().equals(Update.class)) {
                        actionSucceeded = applyUpdate(action, oriNode, targetCopy, mapping, targetCopy.getMapping());
                    } else if (action.getAttribute("nodeType").getTag().equals(Insert.class)) {
                        actionSucceeded = applyInsert(action, oriNode, targetCopy, mapping, targetCopy.getMapping());
                    } else if (action.getAttribute("nodeType").getTag().equals(Move.class)) {
                        actionSucceeded = applyMove(action, oriNode, targetCopy, mapping);
                    } else {
                        System.out.println("[warn]Invalid action nodeType");
                    }
                    if (actionSucceeded) {
                        temp.remove(currentIndex);
                    } else if (temp.size() == 1) {
                        throw new IllegalStateException();
                    } else {
                        currentIndex++;
                    }
                } catch (Exception e) {
                    temp.remove(currentIndex);
                    System.out.printf("[error]Generate patch failed due to node building error : %s in action %s\n", targetCopy.getFileName(), action.getAttribute("nodeType").getTag());
                }
            }
            for(CtElementImpl updateNode: updateOriNode) {
                targetCopy.deleteCGId(updateNode);
                updateNode.delete();
            }

            String filePathTopMatch = filePath.split("\\.")[0] + "#" + maxList.indexOf(mappingScore) + ".java";

            ObjectUtil.writeStringToFile(root.prettyprint(), filePathTopMatch);
        }
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
        List<Pair<Map<PatternNode, CtWrapper>, Double>> mappingScoreList = pat.compareCG(target, "new");
        // record the original root node
        CtElementImpl root = target.getEntryNode();
        Pair<Map<PatternNode, CtWrapper>, Double> mappingScore = mappingScoreList.get(0);
//        for (Pair<Map<PatternNode, CtWrapper>, Double> mappingScore : mappingScoreList) {
        if (mappingScore.getValue1() >= SIMILARITY_THRESHOLD) {
            Map<PatternNode, CtWrapper> mapping = mappingScore.getValue0();
            // pattern.start is the action point, that is also the buggy point
            List<Pair<PatternNode, CtElementImpl>> temp = new ArrayList<>();
            for (Map.Entry<PatternNode, CtWrapper> entry : mapping.entrySet()) {
                for (Pair<PatternNode, PatternNode> pair : nodeAttachAction) {
                    if (pair.getValue0() == entry.getKey()) {
                        // apply action
                        PatternNode action = pair.getValue1();
                        CtElementImpl oriNode = entry.getValue().getCtElementImpl();
                        temp.add(Pair.with(action, oriNode));
                    }
                }
            }
            try {
                for (Pair<PatternNode, CtElementImpl> pair : temp) {
                    System.out.println(pair.getValue0().getAttribute("nodeType").getTag());
                    PatternNode action = pair.getValue0();
                    CtElementImpl oriNode = pair.getValue1();
                    if (action.getAttribute("nodeType").getTag().equals(Delete.class)) {
                        applyDelete(action, oriNode, target, mapping);
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
            } catch (IllegalStateException se) {
                System.out.printf("[error]Generate patch failed due to node building error : %s\n", target.getFileName());
            }
        }
//        }

    }

    private boolean applyDelete(PatternNode action, CtElementImpl oriNode, CodeGraph target, Map<PatternNode, CtWrapper> mapping4pattern) {
        PatternNode toBeDeleted = null;
        for (PatternEdge oe : action.inEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                toBeDeleted = oe.getSource();
                break;
            }
        }
        if (!mapping4pattern.containsKey(toBeDeleted)) {

            return false;
        }
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
        return true;
    }

    private boolean applyMove(PatternNode action, CtElementImpl oriNode, CodeGraph target, Map<PatternNode, CtWrapper> mapping) {
        PatternNode moveSource = null;
        PatternNode moveTarget = null;
        for (PatternEdge oe : action.inEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                moveSource = oe.getSource();
                break;
            }
        }
        for (PatternEdge oe : action.outEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                moveTarget = oe.getTarget();
                break;
            }
        }
        if (!mapping.containsKey(moveSource) || !mapping.containsKey(moveTarget)) {
//            System.out.println("[error]Cannot find mapped MOVE.source or MOVE.target in target code graph: " + target.getFileName());
            return false;
        }
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
                    return false;
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
        modifyValueByRole((CtElementImpl) moveDstTarget.getParent(), roles, moveTarget, oriNode, moveDstTarget);
        return true;
    }

    private boolean applyUpdate(PatternNode action, CtElementImpl oriNode, CodeGraph target, Map<PatternNode, CtWrapper> mapping4pattern, Map<CtWrapper, CtWrapper> mapping4diff) {
        PatternNode updateSource = null;
        for (PatternEdge oe : action.inEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                updateSource = oe.getSource();
                break;
            }
        }
        if (!mapping4pattern.containsKey(updateSource)) {
//            System.out.println("[error]Cannot find mapped UPDATE.source in target code graph: " + target.getFileName());
            return false;
        }

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
            if (update == null) {
                System.out.println("[error]Create new spoon node failed return null");
                throw new IllegalStateException();
            }
            mapping4pattern.put(newInPattern, new CtWrapper(update));
            mapping4pattern.putAll(addMapping4Child(newInPattern, update));
            // update codegraph node set
//            target.deleteCGId(oriNode);
            target.nodeSetAdd(update);
            // TODO: replace oriNode with the new node in the old tree
//            oriNode.replace(update);
            List<Pair<CtRole, Class>> roles;
            if (!action.position.isAbstract()) {
//                roles = (List<Pair<CtRole, Class>>) action.position.getTag();
                roles = new ArrayList<>();
                roles.add(Pair.with(oriNode.getRoleInParent(), oriNode.getClass()));
            } else {
                roles = new ArrayList<>();
                roles.add(Pair.with(oriNode.getRoleInParent(), oriNode.getClass()));
            }

            if(oriNode.getParent().getClass().equals(CtThisAccessImpl.class)) {
                oriNode = (CtElementImpl) oriNode.getParent();
            }

            modifyValueByRole((CtElementImpl) oriNode.getParent(), roles, newInPattern, update, oriNode);
//            oriNode.delete();
        } else {
            System.out.println("[error]Cannot find UPDATE.target in pattern: " + target.getFileName());
        }
        return true;
    }

    private boolean applyInsert(PatternNode action, CtElementImpl oriNode, CodeGraph target, Map<PatternNode, CtWrapper> mapping4pattern, Map<CtWrapper, CtWrapper> mapping4diff) {
        PatternNode insertSource = null;
        for (PatternEdge oe : action.inEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                insertSource = oe.getSource();
                break;
            }
        }
        if (!mapping4pattern.containsKey(insertSource)) {
            System.out.println("[error]Cannot find mapped INSERT.source in target code graph: " + target.getFileName());
            return false;
        }

        // insert.target is the new node to be created
        PatternNode newInPattern = null;
        for (PatternEdge oe : action.outEdges()) {
            if (oe.type == PatternEdge.EdgeType.ACTION) {
                newInPattern = oe.getTarget();
                break;
            }
        }
        if (newInPattern != null) {
            if(! newInPattern.getAttribute("locationInParent").isAbstract() &&
                    ! newInPattern.getAttribute("value").isAbstract() &&
                    newInPattern.getAttribute("locationInParent").getTag().equals("MODIFIER")) {

                Map<String, ModifierKind> modifierKindMap = new HashMap<>();
                Arrays.stream(ModifierKind.values()).forEach(modifierKind -> {
                    modifierKindMap.put(modifierKind.name().toLowerCase(Locale.ROOT), modifierKind);
                });

                String modifier = newInPattern.getAttribute("value").getTag().toString();
                if(RoleHandlerHelper.getOptionalRoleHandler(oriNode.getClass(), CtRole.MODIFIER) != null) {
                    CtModifierHandler handler = new CtModifierHandler(oriNode);
                    Set<ModifierKind> copyModifiers = new HashSet<>(handler.getModifiers());
                    copyModifiers.add(modifierKindMap.get(modifier));
                    oriNode.setValueByRole(CtRole.MODIFIER, copyModifiers);

//                    if(oriNode.getValueByRole(CtRole.) instanceof Set<?>) {
//                        ((Set<ModifierKind>) oriNode.getValueByRole(CtRole.MODIFIER)).add(ModifierKind.FINAL);
//                    }
                }

            } else {
                // TODO: follow the subtree of insert.target to create a new CtElementImpl
                CtElementImpl insert = createSpoonNodeRecursively(newInPattern, mapping4pattern, mapping4diff);
                if (insert == null) {
                    System.out.println("[error]Create new spoon node failed return null");
                    throw new IllegalStateException();
                }
                // add new mapping
                mapping4pattern.put(newInPattern, new CtWrapper(insert));
                mapping4pattern.putAll(addMapping4Child(newInPattern, insert));
                // insert.source is the parent, which is oriNode
                // TODO: insert to where, the concrete position of insert.source
                modifyValueByRole(oriNode, (List<Pair<CtRole, Class>>) action.position.getTag(), newInPattern, insert, null);
                // update codegraph node set
                target.nodeSetAdd(insert);
            }
        } else {
            System.out.println("[error]Cannot find INSERT.target in pattern: " + target.getFileName());
        }
        return true;
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
                        if (listIndex != -1 && ((List<?>) child).size() > listIndex) {
                            childMap.put(oe.getTarget(), new CtWrapper(((List<CtElementImpl>) child).get(listIndex)));
                            childMap.putAll(addMapping4Child(oe.getTarget(), ((List<CtElementImpl>) child).get(listIndex)));
                        }
                    }
                }
            }
        }
        return childMap;
    }

    private void modifyValueByRole(CtElementImpl parent, List<Pair<CtRole, Class>> roles, PatternNode pn, CtElementImpl child, CtElementImpl replacement) {
        CtRole pre = null;
        int listIndex = pn.listIndex.isAbstract() ? -1 : (int) pn.listIndex.getTag();
        for (Pair<CtRole, Class> pair : roles) {
            if (pair.getValue0() == null)
                continue;
            RoleHandler rh = RoleHandlerHelper.getOptionalRoleHandler(parent.getClass(), pair.getValue0());
            if (rh != null) {
                Object ori = parent.getValueByRole(pair.getValue0());
                if (ori instanceof List) {
                    if (((List<?>) ori).isEmpty()) {
                        ArrayList<CtElementImpl> init = new ArrayList<>();
                        init.add(child);
                        parent.setValueByRole(pair.getValue0(), init);
                    } else if (ori.getClass().getSimpleName().equals("UnmodifiableRandomAccessList")) {
                        ArrayList<CtElementImpl> init = new ArrayList<>();
                        init.addAll((List<? extends CtElementImpl>) ori);
                        if (init.contains(replacement))
                            Collections.replaceAll(init, replacement, child);
                        else
                            addNewElement(init, child, listIndex);
                        parent.setValueByRole(pair.getValue0(), Collections.unmodifiableList(init));
                    } else {
                        if (((List<?>) ori).contains(replacement))
                            Collections.replaceAll((List<CtElementImpl>) ori, replacement, child);
                        else
                            addNewElement((List<CtElementImpl>) ori, child, listIndex);
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
                                    addNewElement(init, child, listIndex);
                                parent.setValueByRole(pre, init);
                            } else {
                                if (((List<CtElementImpl>) ((CtElementImpl) ori).getValueByRole(pre)).contains(replacement))
                                    Collections.replaceAll(((CtElementImpl) ori).getValueByRole(pre), replacement, child);
                                else
                                    addNewElement(((CtElementImpl) ori).getValueByRole(pre), child, listIndex);
                            }
                        } else {
                            ((CtElementImpl) ori).setValueByRole(pre, child);
                        }
                    } else {
                        parent.setValueByRole(pair.getValue0(), child);
                    }
                }
                child.setParent(parent);
                break;
            } else {
                pre = pair.getValue0();
            }
        }
    }

    private void addNewElement(List<CtElementImpl> list, CtElementImpl child, int pos) {
        if (pos == -1) {
            if (!list.isEmpty() && isEndStmt(list.get(list.size() - 1)))
                list.add(list.size() - 1, child);
            else
                list.add(child);
        } else {
            list.add(pos, child);
        }
    }

    private boolean isEndStmt(CtElementImpl ctElement) {
        return (ctElement instanceof CtReturnImpl || ctElement instanceof CtThrowImpl
                || ctElement instanceof CtBreakImpl || ctElement instanceof CtContinueImpl);
    }

    public CtElementImpl getRoot(CtElementImpl node) {
        CtElementImpl pointer = node;
        while (pointer.getParent() != null) {
            pointer = (CtElementImpl) pointer.getParent();
        }
        return pointer;
    }

    @SuppressWarnings("unchecked")
    public <T> CtElementImpl createSpoonNodeRecursively(PatternNode pnRoot, Map<PatternNode, CtWrapper> mapping4pattern, Map<CtWrapper, CtWrapper> mapping4diff) {
        // nodeType
        Class clazz = !pnRoot.getAttribute("nodeType").isAbstract() ? (Class) pnRoot.getAttribute("nodeType").getTag() : null;

        if (clazz == null) {
            System.out.println("[error]Create new spoon node failed: pattern node does not have node type attribute");
            throw new IllegalStateException();
        }

        CtElementImpl newly = null;
        try {
            newly = (CtElementImpl) ReflectUtil.createInstance(clazz);
        } catch (InstantiationException e) {
            System.out.println("[warn]failed to directly use class name to create instance");
            // if is update.target
            try {
                boolean createFlag = false;
                for (PatternEdge ie : pnRoot.inEdges()) {
                    if (!ie.isAbstract() && ie.type == PatternEdge.EdgeType.ACTION && !ie.getSource().getAttribute("nodeType").isAbstract()
                            && ie.getSource().getAttribute("nodeType").getTag().equals(Update.class)) {
                        for (PatternEdge ie2 : ie.getSource().inEdges()) {
                            if (!ie2.isAbstract() && ie2.type == PatternEdge.EdgeType.ACTION && mapping4pattern.containsKey(ie2.getSource())) {
                                clazz = mapping4pattern.get(ie2.getSource()).getCtElementImpl().getClass();
                                newly = (CtElementImpl) ReflectUtil.createInstance(clazz);
                                createFlag = true;
                            }
                        }
                    }
                }
                if (newly == null) {
                    throw new IllegalStateException();
                }
            } catch (Exception ex) {
                try {
                    if (clazz.equals(CtVariableAccessImpl.class)) {
                        clazz = CtVariableReadImpl.class;
                    } else if (clazz.equals(CtVariableReferenceImpl.class)) {
                        clazz = CtLocalVariableReferenceImpl.class;
                    } else {
                        if (Modifier.isAbstract(clazz.getModifiers())) {
                            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
                            scanner.addIncludeFilter(new AssignableTypeFilter(clazz));

                            List<Class<?>> subclasses = new ArrayList<>();
                            String basePackage = clazz.getPackage().getName();
                            Set<BeanDefinition> components = scanner.findCandidateComponents(basePackage);
                            for (BeanDefinition component : components) {
                                try {
                                    Class<?> subclazz = Class.forName(component.getBeanClassName());
                                    subclasses.add(subclazz);
                                } catch (ClassNotFoundException clse) {
                                    System.out.println("sub class not found");
                                }
                            }
                            for (Class<?> subclazz : subclasses) {
                                if (!Modifier.isAbstract(subclazz.getModifiers()) && subclazz.getName().contains("Read") && subclazz.getName().contains("Variable")) {
                                    clazz = subclazz;
                                    break;
                                }
                            }
                        }
                    }
                    newly = (CtElementImpl) ReflectUtil.createInstance(clazz);
                } catch (InstantiationException e1) {
                    System.out.println("[error]Create new spoon node failed: createInstance() return null");
                    throw new IllegalStateException();
                }
            }
        }
        if (newly == null) {
            System.out.println("[error]Create new spoon node failed: createInstance() return null");
            throw new IllegalStateException();
        }

        if (!pnRoot.implicit.isAbstract())
            newly.setImplicit((Boolean) pnRoot.implicit.getTag());
        // check value for name
        if (RoleHandlerHelper.getOptionalRoleHandler(newly.getClass(), CtRole.NAME) != null ||
                RoleHandlerHelper.getOptionalRoleHandler(newly.getClass(), CtRole.VALUE) != null) {
            // check define-use for name
            PatternNode define = findDefine(pnRoot), use = null;
            if (mapping4pattern.containsKey(define))
                newly.setValueByRole(CtRole.NAME, mapping4pattern.get(define).toLabelString());
            if (define == null) {
                use = findUse(pnRoot);
                if (mapping4pattern.containsKey(use))
                    newly.setValueByRole(CtRole.NAME, mapping4pattern.get(use).toLabelString());
            }
            if (define == null && use == null) {
                if (!pnRoot.getAttribute("value").isAbstract()) {
                    if (clazz.equals(CtLiteralImpl.class)) {
                        T value = (T) pnRoot.getAttribute("value").getTag();
                        ((CtLiteralImpl) newly).setValue(value);
                    } else {
                        String name = (String) pnRoot.getAttribute("value").getTag();
                        if (SyntaxUtil.checkIdentifierForJLSCorrectness(name, newly.getFactory().getEnvironment().getComplianceLevel()))
                            newly.setValueByRole(CtRole.NAME, name);
                        else
                            System.out.println("[warn]Generated variable name is not syntax-valid");
                    }
//                    String name = (String) pnRoot.getAttribute("value").getTag();
//                    if (SyntaxUtil.checkIdentifierForJLSCorrectness(name, newly.getFactory().getEnvironment().getComplianceLevel()))
//                        newly.setValueByRole(CtRole.NAME, name);
//                    else
//                        System.out.println("[warn]Generated variable name is not syntax-valid");
                } else if (clazz.equals(CtLiteralImpl.class)) {
                    T value = !pnRoot.getAttribute("value").isAbstract() ? (T) pnRoot.getAttribute("value").getTag() : null;
                    ((CtLiteralImpl) newly).setValue(value);
                } else {
                    System.out.println("[warn]Cannot find matched variable name in source code according to pattern");
                }
            }
        }
        // recursively for children
        for (PatternEdge oe : pnRoot.outEdges()) {
            if (!oe.isAbstract() && oe.type == PatternEdge.EdgeType.AST) {
                String r = oe.getTarget().getAttribute("locationInParent").isAbstract() ? null : (String) oe.getTarget().getAttribute("locationInParent").getTag();
                if (r == null)
                    System.out.println("[error]Create new spoon node failed: pattern node does not have same CtRole");
                else if ("OPERATOR_KIND".equals(r)) {
                    if (RoleHandlerHelper.getOptionalRoleHandler(newly.getClass(), CtRole.fromName(r)) != null) {
                        String kind = oe.getTarget().getAttribute("value").isAbstract() ? null : (String) oe.getTarget().getAttribute("value").getTag();
                        if (kind != null)
                            newly.setValueByRole(CtRole.fromName(r), BinaryOperatorKind.valueOf(kind));
                        else
                            System.out.println("[error]Create new spoon node failed: pattern node does not have same operator kind");
                    }
                } else if (!oe.getTarget().isVirtual()) {
                    // node type
                    CtElementImpl child = createSpoonNodeRecursively(oe.getTarget(), mapping4pattern, mapping4diff);
                    // role in parent
                    CtRole role = CtRole.fromName(r);
                    Object listSize = oe.getTarget().listSize.getTag();
                    Object listIndex = oe.getTarget().listIndex.getTag();
                    if (newly.getValueByRole(role) instanceof List) {
                        int size = (Integer) listSize;
                        int index = (Integer) listIndex;
                        if (((List<?>) newly.getValueByRole(role)).isEmpty()) {
                            CtElementImpl[] init = new CtElementImpl[size];
                            init[index] = child;
                            newly.setValueByRole(role, Arrays.asList(init));
                        } else if (newly.getValueByRole(role).getClass().getSimpleName().equals("UnmodifiableRandomAccessList")) {
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
            }
            // TODO: seems no need to extend control dep in patch part in pattern, control dep can be extended from its parent?
        }
        return newly;
    }

    private PatternNode findDefine(PatternNode pnRoot) {
        for (PatternEdge ie : pnRoot.inEdges()) {
            if (!ie.isAbstract() && ie.type == PatternEdge.EdgeType.DEF_USE) {
                return ie.getSource();
            }
        }
        for (PatternEdge ie : pnRoot.inEdges()) {
            if (!ie.isAbstract() && !ie.getSource().getAttribute("nodeType").isAbstract()
                    && SyntaxUtil.isVariableType(ie.getSource().getAttribute("nodeType").getTag())) {
                return findDefine(ie.getSource());
            }
        }
        return null;
    }

    private PatternNode findUse(PatternNode pnRoot) {
        for (PatternEdge oe : pnRoot.outEdges()) {
            if (!oe.isAbstract() && oe.type == PatternEdge.EdgeType.DEF_USE) {
                return oe.getSource();
            }
        }
        for (PatternEdge oe : pnRoot.outEdges()) {
            if (!oe.isAbstract() && !oe.getTarget().getAttribute("nodeType").isAbstract()
                    && SyntaxUtil.isVariableType(oe.getTarget().getAttribute("nodeType").getTag())) {
                return findDefine(oe.getTarget());
            }
        }
        return null;
    }
}
