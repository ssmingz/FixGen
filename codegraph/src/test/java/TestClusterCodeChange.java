import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.gumtreediff.actions.model.*;
import com.alibaba.fastjson.JSONArray;
import gumtree.spoon.AstComparator;
import gumtree.spoon.diff.Diff;
import gumtree.spoon.diff.operations.Operation;
import org.junit.Test;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class TestClusterCodeChange {
    private final String datasource = "/Users/yumeng/Workspace/BUGs/FixBench/WithinSingleMethod/Genesis-NP";

    public List<CCPair> loadPairs(String rootPath) throws Exception {
        List<CCPair> pairs = new ArrayList<>();
        File dir = new File(rootPath);
        for (File f : Objects.requireNonNull(dir.listFiles(File::isDirectory))) {
            File newRoot = new File(f.getAbsolutePath() + System.getProperty("file.separator") + "new");
            File oldRoot = new File(f.getAbsolutePath() + System.getProperty("file.separator") + "old");
            if (newRoot.isDirectory() && Objects.requireNonNull(newRoot.listFiles(pathname -> pathname.getName().endsWith(".java"))).length == 1
                    && oldRoot.isDirectory() && Objects.requireNonNull(oldRoot.listFiles(pathname -> pathname.getName().endsWith(".java"))).length == 1)
            {
                File newF = newRoot.listFiles()[0];
                File oldF = oldRoot.listFiles()[0];

                try {
                    AstComparator diff = new AstComparator();
                    Diff result = diff.compare(oldF, newF);
                    List<Operation> actions = result.getRootOperations();
                    CCPair pair = new CCPair(f.getAbsolutePath(), oldF.getAbsolutePath(), newF.getAbsolutePath(), actions);
                    pairs.add(pair);
//                    System.out.println("[ok]" + f.getAbsolutePath());
                } catch (Exception ab) {
//                    System.out.println("[error]" + f.getAbsolutePath() + ": gumtree failed");
                }
            } else {
//                System.out.println("[error]" + f.getAbsolutePath() + ": illegal file structures");
            }
        }
        return pairs;
    }

    public String printAction(Action act) {
        if (act instanceof Update)
            return printAct((Update) act);
        else if (act instanceof Delete)
            return printAct((Delete) act);
        else if (act instanceof Insert)
            return printAct((Insert) act);
        else if (act instanceof Move)
            return printAct((Move) act);
        else
            return "FAIL PARSING";
    }

    public String printAct(Update upt) {
        return "Update " + upt.getNode().getLabel() + " => " + upt.getValue();
    }

    public String printAct(Delete del) {
        return "Delete " + del.getNode().getLabel();
    }

    public String printAct(Insert ins) {
        if (ins.getNode().getLabel() == "") {
            ins.getNode();
        }
        return "Insert " + ins.getNode().getLabel() + " to " + ins.getParent().getLabel() + " at " + ins.getPosition();
    }

    public String printAct(Move mov) {
        return "Move " + mov.getNode().getLabel() + " to " + mov.getParent().getLabel() + " at " + mov.getPosition();
    }

    private static boolean compareStrList(List<String> list, List<String> list1) {
        list.sort(Comparator.comparing(String::hashCode));
        list1.sort(Comparator.comparing(String::hashCode));
        return list.toString().equals(list1.toString());
    }

    /**
     * Compare the name and arguments to cluster actions that update the use of an API
     */
    public Map<List<String>, List<CCPair>> clusterForAPI(List<CCPair> target) {
        Map<List<String>, List<CCPair>> result = new LinkedHashMap<>();
        for (CCPair pair : target) {
            List<String> actStrs = pair.actions.stream().map(act -> printAction(act.getAction())).collect(Collectors.toList());
            Optional<List<String>> findKey = containsKey(result.keySet(), actStrs);
            if (findKey.isPresent()) {
                List<CCPair> oldClu = result.get(findKey.get());
                List<CCPair> newClu = new ArrayList<>(oldClu);
                newClu.add(pair);
                result.replace(findKey.get(), oldClu, newClu);
            } else {
                List<CCPair> clusters = new ArrayList<>();
                clusters.add(pair);
                result.put(actStrs, clusters);
            }
        }
        return result;
    }

    private Optional<List<String>> containsKey(Set<List<String>> orilist, List<String> newKey) {
        return orilist.stream().filter(e -> compareStrList(e, newKey)).findAny();
    }

    public String printClusters(Map<List<String>, List<CCPair>> clusters) {
        if (clusters == null) return "";
        JSONArray array = new JSONArray();
        for (Map.Entry<List<String>, List<CCPair>> entry : clusters.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("@action", entry.getKey().toString());
            jsonObject.put("@cluster", entry.getValue().toString());
            jsonObject.put("@number", entry.getValue().size());
            array.add(jsonObject);
        }
        List<JSONObject> arrList = array.toJavaList(JSONObject.class);
        Collections.sort(arrList, new Comparator<JSONObject>() {
            private static final String KEY_NAME = "@number";
            @Override
            public int compare(JSONObject a, JSONObject b) {
                String aNum = a.getString(KEY_NAME);
                String bNum = b.getString(KEY_NAME);
                return -aNum.compareTo(bNum);
            }
        });
        JSONArray sortedJsonArray = new JSONArray();
        for (JSONObject j : arrList) {
            sortedJsonArray.add(j);
        }
        return JSON.toJSONString(sortedJsonArray, SerializerFeature.PrettyFormat, SerializerFeature.SortField);
    }

    @Test
    public void test_actionExtractor() throws Exception {
        List<CCPair> initPairs = loadPairs(datasource);
        System.out.println("total pairs : " + initPairs.size());
        Map<List<String>, List<CCPair>> clusters = clusterForAPI(initPairs);
        System.out.println("total clusters : " + clusters.size());
        String json = printClusters(clusters);
        System.out.println(json);
    }

    class CCPair {
        String rootPath;
        String srcPath;  // old file path, buggy one
        String tarPath;  // new file path, fixing one
        List<Operation> actions;

        public CCPair(String root, String srcP, String tarP, List<Operation> acts) {
            srcPath = srcP;
            tarPath = tarP;
            actions = acts;
            rootPath = root;
        }

        @Override
        public String toString() {
            return rootPath.replace(datasource + System.getProperty("file.separator"), "");
        }
    }

}
