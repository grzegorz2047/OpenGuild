package pl.grzegorz2047.openguild.files;

import pl.grzegorz2047.openguild.OpenGuild;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class YamlFileCreator {

    private String rootPath = "plugins/";
    private String openGuildPluginFolderName = "OpenGuild";


    public File prepareFileToLoadYamlConfiguration(InputStream inputStream, String name) throws IOException {
        File file = new File(rootPath + openGuildPluginFolderName + "/" + name + ".yml");
        if (!file.exists()) {
            OpenGuild.getOGLogger().info("File plugins/" + openGuildPluginFolderName + "/" + name + ".yml does not exists - creating ...");
            file.createNewFile();
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            writeDefaultDataToFile(file, r);
        }
        return file;
    }


    public void writeDefaultDataToFile(File file, BufferedReader scanner) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        String str = null;
        while ((str = scanner.readLine()) != null) {
            System.out.println("czytam " + str);
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
