package builder;

import codegraph.*;
import com.github.gumtreediff.tree.Tree;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.*;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.Delete;
import model.actions.Insert;
import model.actions.Move;
import model.actions.Update;
import org.apache.bcel.classfile.Code;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Triplet;
import org.javatuples.Tuple;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.*;
import utils.ASTUtil;
import utils.CtChildScanner;
import utils.ObjectUtil;

import java.util.*;
import java.util.stream.Collectors;

public class GraphBuilder {
    /**
     * build a code graph by given an input file path,
     * includeLines contains line numbers of lines in diff file (set empty when using C3 dataset)
     */
    public static CodeGraph buildGraph(String inputPath, String[] classPaths, int compileLevel, int[] includeLines) {
        Launcher launcher = new Launcher();

        launcher.addInputResource(inputPath);
        if (classPaths.length > 0)
            launcher.getEnvironment().setSourceClasspath(classPaths);
        launcher.getEnvironment().setComplianceLevel(compileLevel);

        launcher.buildModel();
        CtModel model = launcher.getModel();

        for (CtElement method : model.getElements(s -> s instanceof CtMethodImpl
                || s instanceof CtConstructorImpl || s instanceof CtAnonymousExecutableImpl)) {
            if (checkByLine((CtElementImpl) method, includeLines)) {
                CodeGraph graph = buildGraph((CtExecutableImpl) method);
                return graph;
            }
        }
        return null;
    }

    /**
     * build a method code graph list by given an input file path,
     * includeLines contains line numbers of lines in diff file (set empty when using C3 dataset)
     * we should take into account the method that contains any line in includeLines
     */
    public static List<CodeGraph> buildMethodGraphs(String inputPath, String[] classPaths, int compileLevel, int[] includeLines) {
        Launcher launcher = new Launcher();

        launcher.addInputResource(inputPath);
        if (classPaths.length > 0)
            launcher.getEnvironment().setSourceClasspath(classPaths);
        launcher.getEnvironment().setComplianceLevel(compileLevel);

        launcher.buildModel();
        CtModel model = launcher.getModel();

        List<CodeGraph> methodGraphs = new ArrayList<>();

        for (CtElement method : model.getElements(s -> (s instanceof CtMethodImpl
                || s instanceof CtConstructorImpl || s instanceof CtAnonymousExecutableImpl) && s.getParent().getParent() instanceof CtPackageImpl)) {
            if (checkLineAppearedInMethod((CtElementImpl) method, includeLines)) {
                CodeGraph graph = buildGraph((CtExecutableImpl) method);
                methodGraphs.add(graph);
            }
        }
        return methodGraphs;
    }
    private static boolean checkLineAppearedInMethod(CtElementImpl method, int[] includeLines) {
        if (!method.getPosition().isValidPosition())
            return false;
        if (includeLines.length == 0) {
            return true;
        }
        int start = method.getPosition().getLine();
        int end = method.getPosition().getEndLine();
        for (int line : includeLines) {
            if (start <= line && line <= end) {
                return true;
            }
        }
        return false;
    }

    private static boolean checkByLine(CtElementImpl method, int[] includeLines) {
        if (!method.getPosition().isValidPosition())
            return false;
        int start = method.getPosition().getLine();
        int end = method.getPosition().getEndLine();
        for (int line : includeLines) {
            // the method include these lines ?
            // any line in lines is included in the method return true ?
            if (line < start || line > end) {
                return false;
            }
        }
        return true;
    }

    public static CodeGraph buildGraph(CtExecutableImpl ctMethod) {
        String sig = ASTUtil.buildSignature(ctMethod);
        CodeGraph g = new CodeGraph();
        g.setCtMethod(ctMethod);
        g.setName(sig);
        g.setFileName(ctMethod.getPosition().getFile().getAbsolutePath());
        g.buildNode(ctMethod, null, new Scope(null));
        g.setEntryNode(ctMethod);
        // update edge id
        for (CtWrapper w : g._allNodes) {
            for (Edge oe : w.getCtElementImpl()._outEdges) {
                g.updateCGId(oe);
            }
        }
        return g;
    }

    public static CodeGraph generateActionGraphByCodeGraph(CodeGraph cg1, CodeGraph cg2) {
        // gumtree diff
        AstComparator diff = new AstComparator();
        Diff editScript = diff.compare(cg1.getEntryNode(), cg2.getEntryNode());
        // attach CtElement mapping
        cg1.setMapping(editScript.getMappingsComp());
        // add actions to src graph
        for (Operation op : editScript.getRootOperations()) {
            if (op instanceof DeleteOperation) {
                CtElementImpl src = (CtElementImpl) op.getSrcNode();
                if (op.getSrcNode() instanceof gumtree.spoon.builder.CtWrapper) {
                    gumtree.spoon.builder.CtWrapper ori = (gumtree.spoon.builder.CtWrapper) op.getSrcNode();
                    for (CtWrapper ctw : cg1._allNodes) {
                        if (ctw.getCtElementImpl().getParent() == op.getSrcNode().getParent()
                                && ctw.toLabelString().equals(ori.getValue().toString())
                                && ctw.getCtElementImpl() instanceof CtVirtualElement
                                && ((CtVirtualElement) ctw.getCtElementImpl()).getLocationInParent().toLowerCase(Locale.ROOT).equals(ori.getRoleInParent().name().toLowerCase(Locale.ROOT))) {
                            src = ctw.getCtElementImpl();
                            break;
                        }
                    }
                }
                Delete del = new Delete(src, op);
                cg1.updateCGId(del);

            } else if (op instanceof UpdateOperation) {
                CtElementImpl src = (CtElementImpl) op.getSrcNode();
                CtElementImpl dst = (CtElementImpl) op.getDstNode();
                Update upd = new Update(src, dst, op);
                cg1.updateCGId(upd);
                // recursively add dst.children (including dst)
                CtChildScanner scanner = new CtChildScanner();
                scanner.scan(dst);
                for (CtWrapper child : scanner.childList) {
                    child.getCtElementImpl().setActionRelated(true);
                    cg1.updateCGId(child.getCtElementImpl());
                    // add new ASTEdge for CtVirtual
                    if (!child.getCtElementImpl().hasInEdge((CtElementImpl) child.getCtElementImpl().getParent(), Edge.EdgeType.AST)) {
                        new ASTEdge((CtElementImpl) child.getCtElementImpl().getParent(), child.getCtElementImpl());
//                        System.out.printf("[debug]add AST Edge with its parent:%s\n", child.getCtElementImpl().prettyprint());
                    }
                }
                for (CtWrapper child : scanner.childList) {
                    List<Triplet<CtElementImpl, CtElementImpl, Edge.EdgeType>> newEdges = new ArrayList<>();
                    for (Edge ie : child.getCtElementImpl()._inEdges) {
                        CtElementImpl ieSourceInDstGraph = ie.getSource();
                        if (ie.type == Edge.EdgeType.DEF_USE || ie.type == Edge.EdgeType.CONTROL_DEP) {
                            CtElementImpl ieSourceInSrcGraph = ObjectUtil.findMappedNodeInSrcGraph(ieSourceInDstGraph, cg1);
                            if (ieSourceInSrcGraph != null) {
                                newEdges.add(Triplet.with(ieSourceInSrcGraph, child.getCtElementImpl(), ie.type));
                            } else {
//                                System.out.printf("[warn]Unable to find %s Edge.source in srcGraph for:%s\n", ie.getLabel(), child.getCtElementImpl().prettyprint());
                            }
                        }
                    }
                    for (Edge oe : child.getCtElementImpl()._outEdges) {
                        CtElementImpl oeTargetInDstGraph = oe.getTarget();
                        if (oe.type == Edge.EdgeType.CONTROL_DEP || oe.type == Edge.EdgeType.DATA_DEP || oe.type == Edge.EdgeType.DEF_USE) {
                            CtElementImpl oeTargetInSrcGraph = ObjectUtil.findMappedNodeInSrcGraph(oeTargetInDstGraph, cg1);
                            if (oeTargetInSrcGraph != null) {
                                newEdges.add(Triplet.with(child.getCtElementImpl(), oeTargetInSrcGraph, oe.type));
                            } else {
//                                System.out.printf("[warn]Unable to find %s Edge.target in srcGraph for:%s\n", oe.getLabel(), child.getCtElementImpl().prettyprint());
                            }
                        }
                    }
                    for (Triplet<CtElementImpl, CtElementImpl, Edge.EdgeType> tri : newEdges) {
                        ObjectUtil.newEdge(tri.getValue0(), tri.getValue1(), tri.getValue2());
                    }
                }

            } else if (op instanceof InsertOperation) {
                CtElementImpl insTar = (CtElementImpl) op.getSrcNode();
                // step1. use the mapping if finds: parent-in-dstgraph <--> parent-in-srcgraph
                CtElementImpl insSrc = (CtElementImpl) ((InsertOperation) op).getParent();
                boolean flag = false;
                for (Map.Entry<CtWrapper, CtWrapper> entry : cg1.getMapping().entrySet()) {
                    if (entry.getValue().equals(new CtWrapper((CtElementImpl) insTar.getParent()))) {
                        insSrc = entry.getKey().getCtElementImpl();
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
//                    System.out.println("[warn]Not find the CtElement.parent mapping in src/dst graph: " + insSrc.prettyprint());
                }
                // step2. create insert node and connect the three nodes
                Insert ins = new Insert(insTar, insSrc, insTar.getRoleInParent(), op);
                cg1.updateCGId(ins);
                // step3. recursively add insTar.children (including insTar)
                CtChildScanner scanner = new CtChildScanner();
                scanner.scan(insTar);
                for (CtWrapper child : scanner.childList) {
                    child.getCtElementImpl().setActionRelated(true);
                    cg1.updateCGId(child.getCtElementImpl());
                    // add new ASTEdge for CtVirtual
                    if (!child.getCtElementImpl().hasInEdge((CtElementImpl) child.getCtElementImpl().getParent(), Edge.EdgeType.AST)) {
                        new ASTEdge((CtElementImpl) child.getCtElementImpl().getParent(), child.getCtElementImpl());
//                        System.out.printf("[debug]add AST Edge with its parent:%s\n", child.getCtElementImpl().prettyprint());
                    }
                }
                for (CtWrapper child : scanner.childList) {
                    List<Triplet<CtElementImpl, CtElementImpl, Edge.EdgeType>> newEdges = new ArrayList<>();
                    for (Edge ie : child.getCtElementImpl()._inEdges) {
                        CtElementImpl ieSourceInDstGraph = ie.getSource();
                        if (ie.type == Edge.EdgeType.DEF_USE || ie.type == Edge.EdgeType.CONTROL_DEP) {
                            CtElementImpl ieSourceInSrcGraph = ObjectUtil.findMappedNodeInSrcGraph(ieSourceInDstGraph, cg1);
                            if (ieSourceInSrcGraph != null) {
                                newEdges.add(Triplet.with(ieSourceInSrcGraph, child.getCtElementImpl(), ie.type));
                            } else {
//                                System.out.printf("[warn]Unable to find %s Edge.source in srcGraph for:%s\n", ie.getLabel(), child.getCtElementImpl().prettyprint());
                            }
                        }
                    }
                    for (Edge oe : child.getCtElementImpl()._outEdges) {
                        CtElementImpl oeTargetInDstGraph = oe.getTarget();
                        if (oe.type == Edge.EdgeType.CONTROL_DEP || oe.type == Edge.EdgeType.DATA_DEP || oe.type == Edge.EdgeType.DEF_USE) {
                            CtElementImpl oeTargetInSrcGraph = ObjectUtil.findMappedNodeInSrcGraph(oeTargetInDstGraph, cg1);
                            if (oeTargetInSrcGraph != null) {
                                newEdges.add(Triplet.with(child.getCtElementImpl(), oeTargetInSrcGraph, oe.type));
                            } else {
//                                System.out.printf("[warn]Unable to find %s Edge.target in srcGraph for:%s\n", oe.getLabel(), child.getCtElementImpl().prettyprint());
                            }
                        }
                    }
                    for (Triplet<CtElementImpl, CtElementImpl, Edge.EdgeType> tri : newEdges) {
                        ObjectUtil.newEdge(tri.getValue0(), tri.getValue1(), tri.getValue2());
                    }
                }
            } else if (op instanceof MoveOperation) {
                CtElementImpl movedInSrc = (CtElementImpl) op.getSrcNode();
                CtElementImpl movedInDst = (CtElementImpl) editScript.getMappingsComp().getDstForSrc((Tree) movedInSrc.getMetadata("gtnode")).getMetadata("spoon_object");
                CtElementImpl parent = (CtElementImpl) ((MoveOperation) op).getParent();
                Move mov = new Move(movedInSrc, parent, movedInDst, op);
                cg1.updateCGId(mov);
            }
        }
        // update edge id
        for (CtWrapper w : cg1._allNodes) {
            for (Edge oe : w.getCtElementImpl()._outEdges) {
                cg1.updateCGId(oe);
            }
        }
        return cg1;
    }

    public static List<CodeGraph> buildActionGraphInMethods(String srcPath, String tarPath, int[] includeLines) {
        List<CodeGraph> CodeGraphs = new ArrayList<>();

        // code graph
        List<CodeGraph> cg1s = GraphBuilder.buildMethodGraphs(srcPath, new String[] {}, 8, includeLines);
        List<CodeGraph> cg2s = GraphBuilder.buildMethodGraphs(tarPath, new String[] {}, 8, includeLines);

        List<String> sameMethod = cg1s.stream()
                .filter(codeGraph1 -> cg2s.stream().anyMatch(cg2 -> cg2.getGraphName().equals(codeGraph1.getGraphName())))
                .map(codeGraph -> codeGraph.getGraphName())
                .collect(Collectors.toList());

        if(! sameMethod.isEmpty()) {
            for (String methodSignature : sameMethod) {
                CodeGraph cg1 = cg1s.stream()
                        .filter(codeGraph -> codeGraph.getGraphName().equals(methodSignature))
                        .findFirst()
                        .orElseThrow();

                CodeGraph cg2 = cg2s.stream()
                        .filter(codeGraph -> codeGraph.getGraphName().equals(methodSignature))
                        .findFirst()
                        .orElseThrow();

                CodeGraph actionGraph = generateActionGraphByCodeGraph(cg1, cg2);
                CodeGraphs.add(actionGraph);
            }
        }
        return CodeGraphs;
    }

    public static CodeGraph buildActionGraph(String srcPath, String tarPath, int[] includeLines) {
        // code graph
        CodeGraph cg1 = GraphBuilder.buildGraph(srcPath, new String[] {}, 8, includeLines);
        CodeGraph cg2 = GraphBuilder.buildGraph(tarPath, new String[] {}, 8, includeLines);
        // gumtree diff
        AstComparator diff = new AstComparator();
        Diff editScript = diff.compare(cg1.getEntryNode(), cg2.getEntryNode());
        // attach CtElement mapping
        cg1.setMapping(editScript.getMappingsComp());
        // add actions to src graph
        for (Operation op : editScript.getRootOperations()) {
            if (op instanceof DeleteOperation) {
                CtElementImpl src = (CtElementImpl) op.getSrcNode();
                if (op.getSrcNode() instanceof gumtree.spoon.builder.CtWrapper) {
                    gumtree.spoon.builder.CtWrapper ori = (gumtree.spoon.builder.CtWrapper) op.getSrcNode();
                    for (CtWrapper ctw : cg1._allNodes) {
                        if (ctw.getCtElementImpl().getParent() == op.getSrcNode().getParent()
                                && ctw.toLabelString().equals(ori.getValue().toString())
                                && ctw.getCtElementImpl() instanceof CtVirtualElement
                                && ((CtVirtualElement) ctw.getCtElementImpl()).getLocationInParent().toLowerCase(Locale.ROOT).equals(ori.getRoleInParent().name().toLowerCase(Locale.ROOT))) {
                            src = ctw.getCtElementImpl();
                            break;
                        }
                    }
                }
                Delete del = new Delete(src, op);
                cg1.updateCGId(del);

            } else if (op instanceof UpdateOperation) {
                CtElementImpl src = (CtElementImpl) op.getSrcNode();
                CtElementImpl dst = (CtElementImpl) op.getDstNode();
                Update upd = new Update(src, dst, op);
                cg1.updateCGId(upd);
                // recursively add dst.children (including dst)
                CtChildScanner scanner = new CtChildScanner();
                scanner.scan(dst);
                for (CtWrapper child : scanner.childList) {
                    child.getCtElementImpl().setActionRelated(true);
                    cg1.updateCGId(child.getCtElementImpl());
                    // add new ASTEdge for CtVirtual
                    if (!child.getCtElementImpl().hasInEdge((CtElementImpl) child.getCtElementImpl().getParent(), Edge.EdgeType.AST)) {
                        new ASTEdge((CtElementImpl) child.getCtElementImpl().getParent(), child.getCtElementImpl());
//                        System.out.printf("[debug]add AST Edge with its parent:%s\n", child.getCtElementImpl().prettyprint());
                    }
                }
                for (CtWrapper child : scanner.childList) {
                    List<Triplet<CtElementImpl, CtElementImpl, Edge.EdgeType>> newEdges = new ArrayList<>();
                    for (Edge ie : child.getCtElementImpl()._inEdges) {
                        CtElementImpl ieSourceInDstGraph = ie.getSource();
                        if (ie.type == Edge.EdgeType.DEF_USE || ie.type == Edge.EdgeType.CONTROL_DEP) {
                            CtElementImpl ieSourceInSrcGraph = ObjectUtil.findMappedNodeInSrcGraph(ieSourceInDstGraph, cg1);
                            if (ieSourceInSrcGraph != null) {
                                newEdges.add(Triplet.with(ieSourceInSrcGraph, child.getCtElementImpl(), ie.type));
                            } else {
//                                System.out.printf("[warn]Unable to find %s Edge.source in srcGraph for:%s\n", ie.getLabel(), child.getCtElementImpl().prettyprint());
                            }
                        }
                    }
                    for (Edge oe : child.getCtElementImpl()._outEdges) {
                        CtElementImpl oeTargetInDstGraph = oe.getTarget();
                        if (oe.type == Edge.EdgeType.CONTROL_DEP || oe.type == Edge.EdgeType.DATA_DEP || oe.type == Edge.EdgeType.DEF_USE) {
                            CtElementImpl oeTargetInSrcGraph = ObjectUtil.findMappedNodeInSrcGraph(oeTargetInDstGraph, cg1);
                            if (oeTargetInSrcGraph != null) {
                                newEdges.add(Triplet.with(child.getCtElementImpl(), oeTargetInSrcGraph, oe.type));
                            } else {
//                                System.out.printf("[warn]Unable to find %s Edge.target in srcGraph for:%s\n", oe.getLabel(), child.getCtElementImpl().prettyprint());
                            }
                        }
                    }
                    for (Triplet<CtElementImpl, CtElementImpl, Edge.EdgeType> tri : newEdges) {
                        ObjectUtil.newEdge(tri.getValue0(), tri.getValue1(), tri.getValue2());
                    }
                }

            } else if (op instanceof InsertOperation) {
                CtElementImpl insTar = (CtElementImpl) op.getSrcNode();
                // step1. use the mapping if finds: parent-in-dstgraph <--> parent-in-srcgraph
                CtElementImpl insSrc = (CtElementImpl) ((InsertOperation) op).getParent();
                boolean flag = false;
                for (Map.Entry<CtWrapper, CtWrapper> entry : cg1.getMapping().entrySet()) {
                    if (entry.getValue().equals(new CtWrapper((CtElementImpl) insTar.getParent()))) {
                        insSrc = entry.getKey().getCtElementImpl();
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
//                    System.out.println("[warn]Not find the CtElement.parent mapping in src/dst graph: " + insSrc.prettyprint());
                }
                // step2. create insert node and connect the three nodes
                Insert ins = new Insert(insTar, insSrc, insTar.getRoleInParent(), op);
                cg1.updateCGId(ins);
                // step3. recursively add insTar.children (including insTar)
                CtChildScanner scanner = new CtChildScanner();
                scanner.scan(insTar);
                for (CtWrapper child : scanner.childList) {
                    child.getCtElementImpl().setActionRelated(true);
                    cg1.updateCGId(child.getCtElementImpl());
                    // add new ASTEdge for CtVirtual
                    if (!child.getCtElementImpl().hasInEdge((CtElementImpl) child.getCtElementImpl().getParent(), Edge.EdgeType.AST)) {
                        new ASTEdge((CtElementImpl) child.getCtElementImpl().getParent(), child.getCtElementImpl());
//                        System.out.printf("[debug]add AST Edge with its parent:%s\n", child.getCtElementImpl().prettyprint());
                    }
                }
                for (CtWrapper child : scanner.childList) {
                    List<Triplet<CtElementImpl, CtElementImpl, Edge.EdgeType>> newEdges = new ArrayList<>();
                    for (Edge ie : child.getCtElementImpl()._inEdges) {
                        CtElementImpl ieSourceInDstGraph = ie.getSource();
                        if (ie.type == Edge.EdgeType.DEF_USE || ie.type == Edge.EdgeType.CONTROL_DEP) {
                            CtElementImpl ieSourceInSrcGraph = ObjectUtil.findMappedNodeInSrcGraph(ieSourceInDstGraph, cg1);
                            if (ieSourceInSrcGraph != null) {
                                newEdges.add(Triplet.with(ieSourceInSrcGraph, child.getCtElementImpl(), ie.type));
                            } else {
//                                System.out.printf("[warn]Unable to find %s Edge.source in srcGraph for:%s\n", ie.getLabel(), child.getCtElementImpl().prettyprint());
                            }
                        }
                    }
                    for (Edge oe : child.getCtElementImpl()._outEdges) {
                        CtElementImpl oeTargetInDstGraph = oe.getTarget();
                        if (oe.type == Edge.EdgeType.CONTROL_DEP || oe.type == Edge.EdgeType.DATA_DEP || oe.type == Edge.EdgeType.DEF_USE) {
                            CtElementImpl oeTargetInSrcGraph = ObjectUtil.findMappedNodeInSrcGraph(oeTargetInDstGraph, cg1);
                            if (oeTargetInSrcGraph != null) {
                                newEdges.add(Triplet.with(child.getCtElementImpl(), oeTargetInSrcGraph, oe.type));
                            } else {
//                                System.out.printf("[warn]Unable to find %s Edge.target in srcGraph for:%s\n", oe.getLabel(), child.getCtElementImpl().prettyprint());
                            }
                        }
                    }
                    for (Triplet<CtElementImpl, CtElementImpl, Edge.EdgeType> tri : newEdges) {
                        ObjectUtil.newEdge(tri.getValue0(), tri.getValue1(), tri.getValue2());
                    }
                }
            } else if (op instanceof MoveOperation) {
                CtElementImpl movedInSrc = (CtElementImpl) op.getSrcNode();
                CtElementImpl movedInDst = (CtElementImpl) editScript.getMappingsComp().getDstForSrc((Tree) movedInSrc.getMetadata("gtnode")).getMetadata("spoon_object");
                CtElementImpl parent = (CtElementImpl) ((MoveOperation) op).getParent();
                Move mov = new Move(movedInSrc, parent, movedInDst, op);
                cg1.updateCGId(mov);
            }
        }
        // update edge id
        for (CtWrapper w : cg1._allNodes) {
            for (Edge oe : w.getCtElementImpl()._outEdges) {
                cg1.updateCGId(oe);
            }
        }
        return cg1;
    }
}
