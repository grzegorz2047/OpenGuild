package pl.grzegorz2047.openguild2047.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.grzegorz2047.openguild2047.OpenGuild;

import java.io.*;

/**
 * Created by grzeg on 23.08.2017.
 */
public class FileValidator {

    public void validateFile(InputStream inputStream, String name) {
        OpenGuild.getOGLogger().info("Validating file '" + name + ".yml ...");

        YamlConfiguration c = new YamlConfiguration();
        try {
            File file = new File("plugins/OpenGuild2047/" + name + ".yml");
            if (!file.exists()) {
                OpenGuild.getOGLogger().info("File plugins/OpenGuild2047/" + name + ".yml does not exists - creating ...");
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
                if (!c.contains(k)) {
                    c.set(k, configInside.get(k));
                }
            }

            c.save(file);
            targetReader.close();
        } catch (IOException | InvalidConfigurationException e) {
            OpenGuild. getOGLogger().exceptionThrown(e);
        }
    }
}
