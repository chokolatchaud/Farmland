package fr.kevyn.farmland.persistance;

import java.io.*;

public class FileManager {

    public static void createfile(File file) throws IOException {
        if (!file.exists()) {

            file.getParentFile().mkdirs();

            file.createNewFile();
        }
    }

    public static void savefile(File file,String text) {
        final FileWriter writer;

        try {
            createfile(file);
            writer = new FileWriter(file);
            writer.write(text);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String Readfile(File file) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            final StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
            }
            reader.close();
            return text.toString();

        }catch (IOException E) {
            E.printStackTrace();
        }

        return "";
    }


}
