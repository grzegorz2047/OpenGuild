package pl.grzegorz2047.openguild2047.updater;

import pl.grzegorz2047.openguild2047.BagOfEverything;
import pl.grzegorz2047.openguild2047.configuration.GenConf;

/**
 * Created by grzeg on 23.08.2017.
 */
public class Updater {

    public void checkForUpdates() {
        if (!GenConf.updater) {
            pl.grzegorz2047.openguild2047.api.Guilds.getLogger().warning("Updater is disabled.");
        } else {
            if (BagOfEverything.getUpdater().isAvailable()) {
                pl.grzegorz2047.openguild2047.api.Guilds.getLogger().info(" ");
                pl.grzegorz2047.openguild2047.api.Guilds.getLogger().info(" ==================== UPDATER ==================== ");
                pl.grzegorz2047.openguild2047.api.Guilds.getLogger().info("Update found! Please update your plugin to the newest version!");
                pl.grzegorz2047.openguild2047.api.Guilds.getLogger().info("Download it from https://github.com/grzegorz2047/OpenGuild2047/releases");
                pl.grzegorz2047.openguild2047.api.Guilds.getLogger().info(" ==================== UPDATER ==================== ");
                pl.grzegorz2047.openguild2047.api.Guilds.getLogger().info(" ");
            } else {
                pl.grzegorz2047.openguild2047.api.Guilds.getLogger().info("No updates found! Good job! :D");
            }
        }
    }
}
