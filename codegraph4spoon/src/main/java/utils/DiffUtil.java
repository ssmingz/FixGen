package utils;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.Patch;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DiffUtil {
    public static void writeDiffFile(String srcPath, String tarPath, String outputPath) {
        try {
            // read the original file
            List<String> original = Files.readAllLines(new File(srcPath).toPath());
            // read the comparing file
            List<String> revised = Files.readAllLines(new File(tarPath).toPath());
            // different part of these two files
            Patch<String> patch = DiffUtils.diff(original, revised);
            // generate unified diff format
            List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(srcPath, tarPath, original, patch, 0);
            // write diff to file
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath));
            for (String s : unifiedDiff) {
                bufferedWriter.write(s);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                System.out.println(s);
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            System.out.println("Finish write diff file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getDiff(String srcPath, String tarPath) {
        List<String> unifiedDiff = new ArrayList<>();
        try {
            // read the original file
            List<String> original = Files.readAllLines(new File(srcPath).toPath());
            // read the comparing file
            List<String> revised = Files.readAllLines(new File(tarPath).toPath());
            // different part of these two files
            Patch<String> patch = DiffUtils.diff(original, revised);
            // generate unified diff format
            unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(srcPath, tarPath, original, patch, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return unifiedDiff;
    }

    public static String formatCode(String input) {
        String formattedSource = input;
        try {
            formattedSource = new Formatter().formatSource(input);
        } catch (FormatterException e) {
            e.printStackTrace();
        }
        return formattedSource;
    }
}
