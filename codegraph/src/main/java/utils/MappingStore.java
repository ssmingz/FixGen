package utils;

import builder.Matcher;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import gumtree.spoon.builder.SpoonGumTreeBuilder;
import gumtree.spoon.diff.Diff;
import model.CodeGraph;
import model.graph.node.Node;
import spoon.reflect.declaration.CtElement;

import java.util.*;
import java.util.stream.Collectors;

public class MappingStore {
    public CodeGraph srcCodeGraph;
    public CodeGraph dstCodeGraph;
    /**
     * src to dst edit by gumtree-spoon-diff
     */
    public Diff edit;
    /**
     * src-dst Spoon node map
     */
    public BiMap<CtObject, CtObject> spoonMap = HashBiMap.create();
    /**
     * CodeGraph-Spoon node map
     */
    public BiMap<Node, CtObject> srcCGSpoonMap = HashBiMap.create();
    public BiMap<Node, CtObject> dstCGSpoonMap = HashBiMap.create();
    /**
     * Spoon-CodeGraph node map
     */
    public BiMap<CtObject, Node> srcSpoonCGMap = HashBiMap.create();
    public BiMap<CtObject, Node> dstSpoonCGMap = HashBiMap.create();
    /**
     * src-dst CodeGraph node map
     */
    public BiMap<Node, Node> cgMap = HashBiMap.create();
    /**
     * not mapped CodeGraph node
     */
    public List<Node> srcNotMapped = new ArrayList<>();
    public List<Node> dstNotMapped = new ArrayList<>();

    public MappingStore(CodeGraph src, CodeGraph dst, Diff gtEdit) {
        srcCodeGraph = src;
        dstCodeGraph = dst;
        edit = gtEdit;
    }

    public static Set<CtObject> findByCt(BiMap<Node, CtObject> cgSpoonMap, CtObject ct) {
        Set<CtObject> result = new LinkedHashSet<>();
        for (CtObject value : cgSpoonMap.values()) {
            if (Matcher.equalsInSameSrc(value.ctElement, ct.ctElement)) {
                result.add(value);
            }
        }
        return result;
    }

    public void init() {
        initSpoonMap();
        srcCGSpoonMap = Matcher.mapCodeGraphAndSpoon(srcCodeGraph, edit.getMappingsComp().src);
        dstCGSpoonMap = Matcher.mapCodeGraphAndSpoon(dstCodeGraph, edit.getMappingsComp().dst);
        srcSpoonCGMap = srcCGSpoonMap.inverse();
        dstSpoonCGMap = dstCGSpoonMap.inverse();
        for (Map.Entry<CtObject, CtObject> entry : spoonMap.entrySet()) {
            CtObject src = entry.getKey();
            CtObject dst = entry.getValue();
            for (CtObject aSrc : findByCt(srcCGSpoonMap, src)) {
                for (CtObject aDst : findByCt(dstCGSpoonMap, dst)) {
                    if (Matcher.equals(aSrc.ctElement, aDst.ctElement) && Objects.equals(aSrc.locationInParent, aDst.locationInParent))
                        cgMap.put(srcSpoonCGMap.get(aSrc), dstSpoonCGMap.get(aDst));
                    else if (Objects.equals(aSrc.locationInParent, aDst.locationInParent)
                            && !aSrc.ctElement.prettyprint().equals(aDst.ctElement.prettyprint()) && edit.getMappingsComp().isSrcMapped((Tree) src.ctElement.getMetadata(SpoonGumTreeBuilder.GUMTREE_NODE))
                            && edit.getMappingsComp().isDstMapped((Tree) dst.ctElement.getMetadata(SpoonGumTreeBuilder.GUMTREE_NODE))) {
                        cgMap.put(srcSpoonCGMap.get(aSrc), dstSpoonCGMap.get(aDst));
                    }
                }
            }
        }
        // handle not mapped
        srcNotMapped = srcCodeGraph.getNodes().stream().filter(n -> !srcCGSpoonMap.containsKey(n)).collect(Collectors.toList());
        dstNotMapped = dstCodeGraph.getNodes().stream().filter(n -> !dstCGSpoonMap.containsKey(n)).collect(Collectors.toList());
        Iterator<Node> itr1 = srcNotMapped.iterator();
        while (itr1.hasNext()) {
            Node src = itr1.next();
            Iterator<Node> itr2 = dstNotMapped.iterator();
            while (itr2.hasNext()) {
                Node dst = itr2.next();
                if (src.getClass().getSimpleName().equals(dst.getClass().getSimpleName())
                        && sameContent(src, dst)) {
                    itr1.remove();
                    itr2.remove();
                    cgMap.put(src, dst);
                    break;
                }
            }
        }
    }

    private boolean sameContent(Node src, Node dst) {
        if (src.getASTNode()!=null && dst.getASTNode()!=null && src.getASTNode().toString().equals(dst.getASTNode().toString()))
            return true;
        else
            return src.getASTNode() == null && dst.getASTNode() == null && src.toLabelString().equals(dst.toLabelString());
    }

    private void initSpoonMap() {
        for (Mapping mapping : edit.getMappingsComp().asSet()) {
            Tree srcTree = mapping.first;
            Tree dstTree = mapping.second;
            CtElement srcCtElement = (CtElement) srcTree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
            CtElement dstCtElement = (CtElement) dstTree.getMetadata(SpoonGumTreeBuilder.SPOON_OBJECT);
            if (srcCtElement != null && srcCtElement.getPosition().isValidPosition()) {
                int spoon_start = srcCtElement.getPosition().getLine();
                if (spoon_start >= srcCodeGraph.getStartLine() && spoon_start <= srcCodeGraph.getEndLine()) {
                    spoonMap.put(new CtObject(srcCtElement), new CtObject(dstCtElement));
                }
            }
        }
    }
}
