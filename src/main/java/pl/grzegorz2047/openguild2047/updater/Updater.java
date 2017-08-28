package pl.grzegorz2047.openguild2047.updater;

import pl.grzegorz2047.openguild2047.BagOfEverything;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.configuration.GenConf;

/**
 * Created by grzeg on 23.08.2017.
 */
public class Updater {

    public void checkForUpdates() {
        if (!GenConf.updater) {
            OpenGuild.getOGLogger().warning("Updater is disabled.");
        } else {
            if (BagOfEverything.getUpdater().isAvailable()) {
                OpenGuild.getOGLogger().info(" ");
                OpenGuild.getOGLogger().info(" ==================== UPDATER ==================== ");
                OpenGuild.getOGLogger().info("Update found! Please update your plugin to the newest version!");
                OpenGuild.getOGLogger().info("Download it from https://github.com/grzegorz2047/OpenGuild2047/releases");
                OpenGuild.getOGLogger().info(" ==================== UPDATER ==================== ");
                OpenGuild.getOGLogger().info(" ");
            } else {
                OpenGuild.getOGLogger().info("No updates found! Good job! :D");
            }
        }
    }
}
