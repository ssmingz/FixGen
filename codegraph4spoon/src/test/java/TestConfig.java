import java.util.LinkedHashMap;
import java.util.Map;

public class TestConfig {
    static String WIN_BASE = "D:/expdata/c3/";
    static String MAC_BASE = "/Users/yumeng/PycharmProjects/c3/";
    static String FIXBENCH_MAC_BASE = "/Users/yumeng/PycharmProjects/FixBench/WithinSingleMethod/";
    static int MAX_GROUP = 1;
    static boolean SKIP_EXIST_OUTPUT = true;

    static String LOG_PATH = "./out/log/log.txt";

    static Map<String, int[]> D4J_DEPRECATED = new LinkedHashMap<>(){{
        put("chart", new int[]{6});
        put("closure", new int[]{63, 93});
        put("collections", new int[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24});
        put("lang", new int[]{2});
        put("time", new int[]{21});
    }};
    static Map<String, Integer> D4J_PROJECTS = new LinkedHashMap<>(){{
        put("chart", 26);
        put("cli", 40);
        put("closure", 176);
        put("codec", 18);
        put("collections", 28);
        put("compress", 47);
        put("csv", 16);
        put("gson", 18);
        put("jacksoncore", 26);
        put("jacksondatabind", 112);
        put("jacksonxml", 6);
        put("jsoup", 93);
        put("jxpath", 22);
        put("lang", 65);
        put("math", 106);
        put("mockito", 38);
        put("time",27);
    }};
}
