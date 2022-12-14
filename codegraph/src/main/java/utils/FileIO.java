package utils;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;

public class FileIO {
    public static String readStringFromFile(String inputFile) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
            byte[] bytes = new byte[(int) new File(inputFile).length()];
            in.read(bytes);
            in.close();
            return new String(bytes);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void writeStringToFile(String string, String outputFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(string);
            writer.flush();
            writer.close();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static String getSimpleClassName(String className) {
        String name = className.substring(className.lastIndexOf('.') + 1);
        return name;
    }

    public static ArrayList<File> getPaths(File dir) {
        ArrayList<File> files = new ArrayList<>();
        if (dir.isDirectory())
            for (File sub : dir.listFiles())
                files.addAll(getPaths(sub));
        else if (dir.getName().endsWith(".java"))
            files.add(dir);
        return files;
    }
}

