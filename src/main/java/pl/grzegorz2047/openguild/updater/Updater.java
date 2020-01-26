package pl.grzegorz2047.openguild.updater;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.utils.NewVersionChecker;

import java.util.List;

/**
 * Created by grzeg on 23.08.2017.
 */
public final class Updater {

    NewVersionChecker newVersionChecker = new NewVersionChecker();
    private boolean updaterEnabled;

    public Updater(FileConfiguration config) {
        updaterEnabled = config.getBoolean("updater", false);
    }

    public void checkForUpdates() {

        if (!updaterEnabled) {
            OpenGuild.getOGLogger().warning("Updater is disabled.");
        } else {

            if (isAvailable()) {
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

    private boolean isAvailable() {
        try {
            String version = newVersionChecker.getVersions().get(0);
            return Float.parseFloat(version) < Float.parseFloat(Bukkit.getPluginManager().getPlugin("OpenGuild").getDescription().getVersion());
        } catch (Exception ex) {
            OpenGuild.getOGLogger().info("Couldnt check updates! :<");
            return false;
        }
    }

    public void notifyOpAboutUpdate(Player player) {
        if (player.isOp() && updaterEnabled && isAvailable()) {
            player.sendMessage(ChatColor.RED + " =============== OpenGuild UPDATER =============== ");
            if (MsgManager.LANG.equalsIgnoreCase("PL")) {
                player.sendMessage(ChatColor.YELLOW + "Znaleziono aktualizacje! Prosze zaktualizowac Twój plugin do najnowszej wersji!");
                player.sendMessage(ChatColor.YELLOW + "Pobierz go z https://github.com/grzegorz2047/OpenGuild/releases");
            } else if (MsgManager.LANG.equalsIgnoreCase("SV")) {
                player.sendMessage(ChatColor.YELLOW + "Uppdatering hittas! Uppdatera ditt plugin till den senaste version!");
                player.sendMessage(ChatColor.YELLOW + "Ladda ner det från https://github.com/grzegorz2047/OpenGuild/releases");
            } else {
                player.sendMessage(ChatColor.YELLOW + "Update found! Please update your plugin to the newest version!");
                player.sendMessage(ChatColor.YELLOW + "Download it from https://github.com/grzegorz2047/OpenGuild/releases");
            }
            player.sendMessage(ChatColor.RED + " =============== OpenGuild UPDATER =============== ");
        }
    }
}
