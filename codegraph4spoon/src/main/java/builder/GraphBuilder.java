package builder;

import codegraph.ASTEdge;
import codegraph.DefUseEdge;
import codegraph.Edge;
import codegraph.Scope;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.*;
import model.CodeGraph;
import model.CtWrapper;
import model.actions.Delete;
import model.actions.Insert;
import model.actions.Move;
import model.actions.Update;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Triplet;
import org.javatuples.Tuple;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.CtElementImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import utils.ASTUtil;
import utils.CtChildScanner;
import utils.ObjectUtil;

import java.util.*;

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

        for (CtElement method : model.getElements(s -> s instanceof CtMethodImpl)) {
            if (checkByLine((CtMethodImpl) method, includeLines)) {
                CodeGraph graph = buildGraph((CtMethodImpl) method);
                return graph;
            }
        }
        return null;
    }

    private static boolean checkByLine(CtMethodImpl method, int[] includeLines) {
        int start = method.getPosition().getLine();
        int end = method.getPosition().getEndLine();
        for (int line : includeLines) {
            if (line < start || line > end) {
                return false;
            }
        }
        return true;
    }

    public static CodeGraph buildGraph(CtMethodImpl ctMethod) {
        String sig = ASTUtil.buildSignature(ctMethod);
        CodeGraph g = new CodeGraph();
        g.setCtMethod(ctMethod);
        g.setName(sig);
        g.setFileName(ctMethod.getPosition().getFile().getAbsolutePath());
        g.buildNode(ctMethod, null, new Scope(null));
        g.setEntryNode(ctMethod);
        return g;
    }

    public static CodeGraph buildActionGraph(String srcPath, String tarPath) {
        // code graph
        CodeGraph cg1 = GraphBuilder.buildGraph(srcPath, new String[] {}, 8, new int[] {});
        CodeGraph cg2 = GraphBuilder.buildGraph(tarPath, new String[] {}, 8, new int[] {});
        // gumtree diff
        AstComparator diff = new AstComparator();
        Diff editScript = diff.compare(cg1.getEntryNode(), cg2.getEntryNode());
        // attach CtElement mapping
        cg1.setMappingStore(editScript.getMappingsComp());
        // add actions to src graph
        // TODO: add relationships in dst graph
        // TODO: add dst children to allNodes
        for (Operation op : editScript.getRootOperations()) {
            if (op instanceof DeleteOperation) {
                CtElementImpl src = (CtElementImpl) op.getSrcNode();
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
                int pos = ((InsertOperation) op).getPosition();
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
                Insert ins = new Insert(insTar, insSrc, pos, op);
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
                CtElementImpl src = (CtElementImpl) op.getSrcNode();
                CtElementImpl parent = (CtElementImpl) ((MoveOperation) op).getParent();
                int pos = ((MoveOperation) op).getPosition();
                Move mov = new Move(src, parent, pos, op);
                cg1.updateCGId(mov);
            }
        }
        return cg1;
    }
}
