package pl.grzegorz2047.openguild.files;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.grzegorz2047.openguild.OpenGuild;

import java.io.*;
import java.util.Scanner;

/**
 * File created by grzegorz2047 on 23.08.2017.
 */
public class FileDataUpdater {

    private boolean validated = false;
    private YamlConfiguration yamlConfiguration = null;

    public boolean updateFile(InputStream inputStreamData, File file) {
        OpenGuild.getOGLogger().info("Validating file " + file.getName() + " ...");
        if (isNotValidInputData(inputStreamData)) {
            OpenGuild.getOGLogger().info("File " + file.getName() + " is not available - skipping ...");
            return false;
        }
        YamlConfiguration currentYamlConnfigurationInFile = new YamlConfiguration();
        try {
            currentYamlConnfigurationInFile.load(file);
            YamlConfiguration configInsideJarPlugin = new YamlConfiguration();
            updateCurrentYamlWIthNewValues(inputStreamData, currentYamlConnfigurationInFile, file, configInsideJarPlugin);
            yamlConfiguration = currentYamlConnfigurationInFile;
        } catch (IOException | InvalidConfigurationException e) {
            OpenGuild.getOGLogger().setDebugMode(true);
            OpenGuild.getOGLogger().exceptionThrown(e);
        }
        return true;
    }

    public FileConfiguration getUpdatedConfig() {
        return yamlConfiguration;
    }

    public boolean isValidated() {
        return validated;
    }

    private boolean isNotValidInputData(InputStream inputStream) {
        return inputStream == null;
    }

    private void updateCurrentYamlWIthNewValues(InputStream inputStream, YamlConfiguration currentYamlConnfigurationInFile, File file, YamlConfiguration configFromPlugin) throws IOException, InvalidConfigurationException {
        Reader targetReader = new InputStreamReader(inputStream);

        configFromPlugin.load(targetReader);

        for (String k : configFromPlugin.getKeys(true)) {
            //System.out.println("Klucze z configu z jara! " + k);
            if (!currentYamlConnfigurationInFile.contains(k)) {
                //System.out.println("Nie zawiera " + k + " ustawiam na " + configFromPlugin.get(k));
                currentYamlConnfigurationInFile.set(k, configFromPlugin.get(k));
            }
        }

        currentYamlConnfigurationInFile.save(file);
        targetReader.close();
    }


}
