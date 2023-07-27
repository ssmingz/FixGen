package utils;

import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.support.reflect.declaration.CtExecutableImpl;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.support.reflect.declaration.CtParameterImpl;

import java.io.*;
import java.util.*;

public class ASTUtil {
    public static String buildSignature(CtExecutableImpl method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getSimpleName() + "#?");
        for (int i = 0; i < method.getParameters().size(); i++) {
            CtParameterImpl svd = (CtParameterImpl) method.getParameters().get(i);
            sb.append(",").append(svd.getType().getSimpleName());
        }
        return sb.toString();
    }

    /**
     * Parse a git diff file
     * @param diffpath : .diff file path
     * @return : line number list by source file path
     */
    public static Map<String, int[]> getDiffLinesInBuggyFile(String diffpath) {
        Map<String, int[]> result = new LinkedHashMap<>();
        File diff = new File(diffpath);
        if (diff.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(diff));
                String line = null;
                int lineNoDel = 0, lineNoIns = 0;
                String file = "";
                Set<Integer> linelist = null;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("--- ")) {
                        if (linelist != null && !linelist.isEmpty()) {
                            int[] lineArray = new int[linelist.size()];
                            int counter = 0;
                            for (Integer i : linelist) {
                                lineArray[counter++] = i;
                            }
                            result.put(file.split("/")[file.split("/").length - 1], lineArray);
                            linelist.clear();
                        }
                        file = line.split(" ")[1].replaceFirst("a", "");
                        linelist = new LinkedHashSet<>();
                        continue;
                    }
                    if (line.startsWith("@@ ")) {
                        lineNoDel = Integer.parseInt(line.split(" ")[1].split(",")[0].replace("-",""));
                        lineNoIns = Integer.parseInt(line.split(" ")[2].split(",")[0].replace("+",""));
                        continue;
                    }
                    if (line.startsWith("-") && !line.startsWith("---") && !line.equals("--------")) {
                        linelist.add(lineNoDel);
                    } else if (line.startsWith("+") && !line.startsWith("+++")) {
                        linelist.add(lineNoIns);
                    }
                    if (!line.startsWith("+"))
                        lineNoDel++;
                    if (!line.startsWith("-"))
                        lineNoIns++;
                }
                if (linelist != null && !linelist.isEmpty()) {
                    int[] lineArray = new int[linelist.size()];
                    int counter = 0;
                    for (Integer i : linelist) {
                        lineArray[counter++] = i;
                    }
                    result.put(file.split("/")[file.split("/").length - 1], lineArray);
                    linelist.clear();
                }
                reader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
