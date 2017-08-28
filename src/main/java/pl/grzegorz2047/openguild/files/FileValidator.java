package pl.grzegorz2047.openguild.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.grzegorz2047.openguild.OpenGuild;

import java.io.*;

/**
 * File created by grzegorz2047 on 23.08.2017.
 */
public class FileValidator {

    public void validateFile(InputStream inputStream, String name) {
        OpenGuild.getOGLogger().info("Validating file '" + name + ".yml ...");

        YamlConfiguration c = new YamlConfiguration();
        try {
            String openGuildPluginFolderName = "OpenGuild";
            File file = new File("plugins/" + openGuildPluginFolderName + "/" + name + ".yml");
            if (!file.exists()) {
                OpenGuild.getOGLogger().info("File plugins/" + openGuildPluginFolderName + "/" + name + ".yml does not exists - creating ...");
                file.createNewFile();
            }

            c.load(file);

            YamlConfiguration configInside = new YamlConfiguration();

            if (inputStream == null) {
                OpenGuild.getOGLogger().info("File " + name + ".yml does not exists - skipping ...");
                file.delete();
                return;
            }
            Reader targetReader = new InputStreamReader(inputStream);

            configInside.load(targetReader);

            for (String k : configInside.getKeys(true)) {
                //System.out.println("Klucze z configu z jara! " + k);
                if (!c.contains(k)) {
                    //System.out.println("Nie zawiera " + k + " ustawiam na " + configInside.get(k));
                    c.set(k, configInside.get(k));
                }
            }

            c.save(file);
            targetReader.close();
        } catch (IOException | InvalidConfigurationException e) {
            OpenGuild.getOGLogger().exceptionThrown(e);
        }
    }
}
