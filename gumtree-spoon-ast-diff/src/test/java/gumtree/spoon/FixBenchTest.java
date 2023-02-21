package gumtree.spoon;

import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import org.junit.Test;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class FixBenchTest {
    public List<File> traverseAllFiles(File rootFile) {
        List<File> res = new ArrayList<>();
        for (File f : rootFile.listFiles()) {
            if (f.isDirectory())
                res.addAll(traverseAllFiles(f));
            if (f.isFile())
                res.add(f);
        }
        return res;
    }

    public String findJavaFile(String rootPath) {
        File root = new File(rootPath);
        if (root.exists()) {
            List<File> allJavaFile = traverseAllFiles(root).stream().filter(x -> x.getAbsolutePath().endsWith(".java")).collect(Collectors.toList());
            if (allJavaFile.size() == 1) {
                return allJavaFile.get(0).getAbsolutePath();
            }
        }
        return null;
    }

    public <T> List<CtElement> getCtElementsByType(CtElement node, Class<T> targetClass) {
        List<CtElement> children = new ArrayList<>();
        for (CtElement e : node.getDirectChildren()) {
            if (e.getClass().equals(targetClass)) {
                children.add(e);
            }
            children.addAll(getCtElementsByType(e, targetClass));
        }
        return children;
    }

    public CtElement findNullCheckNode(CtBinaryOperatorImpl binExpr) {
        CtElement checked = null;
        boolean isNullCheck = false;
         for (CtElement e : binExpr.getDirectChildren()) {
             if (e.toString().equals("null")) {
                 isNullCheck = true;
             } else {
                 checked = e;
             }
         }
        return isNullCheck ? checked : null;
    }

    @Test
    public void testForGenesisNP() throws Exception {
        File dir = new File("/Users/yumeng/Workspace/BUGs/FixBench/WithinSingleMethod/Genesis-NP/");
        Map<String, List<String>> checkList = new LinkedHashMap<>();
        Map<String, String> typeNullList = new LinkedHashMap<>();
        for (File root : dir.listFiles()) {
            String srcRoot = root.getAbsolutePath() + "/old";
            String dstRoot = root.getAbsolutePath() + "/new";

            String srcFile = findJavaFile(srcRoot);
            String dstFile = findJavaFile(dstRoot);

            if (srcFile == null || dstFile == null) {
                System.out.println("[JavaFileNotFound]" + srcRoot);
                continue;
            }

            File fl = new File(srcFile);
            File fr = new File(dstFile);

            try {
                AstComparator diff = new AstComparator();
                // DiffConfiguration diffConfig = new DiffConfiguration();
                // diffConfig.setMatcher(new CompositeMatchers.ClassicGumtreeTheta());
                Diff editScript = diff.compare(fl, fr);

                for (Operation op : editScript.getRootOperations()) {
                    List<CtElement> targetCtElements = getCtElementsByType(op.getNode(), CtBinaryOperatorImpl.class);
                    for (CtElement e : targetCtElements) {
                        CtElement checkedNode = findNullCheckNode((CtBinaryOperatorImpl) e);
                        if (checkedNode instanceof CtTypedElement) {
                            CtTypeReference checkedType = ((CtTypedElement<?>) checkedNode).getType();
                            if (checkedType != null) {
                                String typeStr = checkedType.toString();
                                if (checkList.containsKey(typeStr)) {
                                    List<String> newlist = checkList.get(typeStr);
                                    newlist.add(root.getAbsolutePath());
                                    //newlist.add(checkedNode.toString());
                                    checkList.put(typeStr, newlist);
                                } else {
                                    List<String> initlist = new ArrayList<>();
                                    initlist.add(root.getAbsolutePath());
                                    //initlist.add(checkedNode.toString());
                                    checkList.put(typeStr, initlist);
                                }
                                System.out.println(typeStr +":"+ checkList.get(typeStr).size());
                            } else {
                                typeNullList.put("[TypeNull]" + checkedNode.toString(), root.getAbsolutePath());
                                //checkList.put("[TypeNull]" + checkedNode.toString(), Collections.singletonList(checkedNode.toString()));
                                System.out.println("[TypeNull]" + checkedNode.toString());
                            }
                        }
                    }
                }
            } catch (Exception ab) {
            }
//            SpoonSupport support = new SpoonSupport();
//            if (op instanceof InsertOperation) {
//                CtElement invokeSrc = ((InsertOperation) op).getParent();
//                CtElement invokeTgt = support.getMappedElement(editScript, invokeSrc, true);
//                System.out.println(invokeSrc.toString());
//                System.out.println(invokeTgt.toString());
//            }
        }
        System.out.println("-------------- Result --------------");
        Map<String, List<String>> orderedByNum = new LinkedHashMap<>();
        checkList.entrySet()
                .stream()
                .sorted((p1, p2) -> Integer.valueOf(p2.getValue().size()).compareTo(Integer.valueOf(p1.getValue().size())))
                .collect(Collectors.toList()).forEach(e -> orderedByNum.put(e.getKey(), e.getValue()));
        for (Map.Entry<String, List<String>> entry : orderedByNum.entrySet()) {
            String typeStr = entry.getKey();
            int num = entry.getValue().size();
            System.out.println(typeStr + ":" + num);
            for (String ins : entry.getValue())
                System.out.println("\t" + ins);
        }
        for (Map.Entry<String, String> entry : typeNullList.entrySet())
            System.out.println(entry.getKey() + ":" + entry.getValue());
    }
}
