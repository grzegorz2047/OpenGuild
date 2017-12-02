package pl.grzegorz2047.openguild.files;

import pl.grzegorz2047.openguild.OpenGuild;

import java.io.*;
import java.util.Scanner;

public class YamlFileCreator {

    public File prepareFileToLoadYamlConfiguration(File file, BufferedReader bufferedReader) throws IOException {
        if (!file.exists()) {
            OpenGuild.getOGLogger().info("File " + file.getAbsolutePath() + ".yml does not exists - creating ...");
            file.createNewFile();
            writeDefaultDataToFile(file, bufferedReader);
        }
        return file;
    }


    private void writeDefaultDataToFile(File file, BufferedReader scanner) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        String str;
        while ((str = scanner.readLine()) != null) {
            //System.out.println("czytam " + str);
            bw.write(str);
            bw.newLine();
        }
        bw.flush();
        bw.close();
    }

    private boolean isFileJustCreated(Scanner scanner) {
        return !scanner.hasNextLine();
    }

}
