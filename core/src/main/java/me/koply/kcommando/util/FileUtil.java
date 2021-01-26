package me.koply.kcommando.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtil {

    public static String readFile(File file) {
        try {
            final StringBuilder strBuilder = new StringBuilder();
            final FileInputStream fileInput = new FileInputStream(file);
            final InputStreamReader inputReader = new InputStreamReader(fileInput, StandardCharsets.UTF_8);
            final BufferedReader reader = new BufferedReader(inputReader);

            String line;
            while ((line = reader.readLine()) != null) {
                strBuilder.append(line).append("\n");
            }

            fileInput.close();
            inputReader.close();
            reader.close();

            return strBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void writeFile(File file, String str) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.write(str);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}