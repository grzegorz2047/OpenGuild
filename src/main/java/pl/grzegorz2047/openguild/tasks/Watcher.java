package pl.grzegorz2047.openguild.tasks;

import org.bukkit.configuration.file.FileConfiguration;
import pl.grzegorz2047.openguild.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.listeners.EntityDamageByEntityListener;
import pl.grzegorz2047.openguild.listeners.TNTExplodeListener;
import pl.grzegorz2047.openguild.relations.Relations;
import pl.grzegorz2047.openguild.teleporters.Teleporter;
import pl.grzegorz2047.openguild.teleporters.TpaRequester;
import pl.grzegorz2047.openguild.tntguildblocker.TntGuildBlocker;

/**
 * File created by grzegorz2047 on 13.08.2017.
 */

public class Watcher implements Runnable {
    private final AntiLogoutManager logout;
    private final Teleporter teleporter;
    private final TpaRequester tpaRequester;
    private final Guilds guilds;
    private final Relations relations;
    private final TntGuildBlocker tntGuildBlocker;
    private final int TELEPORT_COOLDOWN;

    private int seconds;
    private boolean tpaEnabled;

    public Watcher(AntiLogoutManager logout, Teleporter teleporter, TpaRequester tpaRequester, Guilds guilds, Relations relations, TntGuildBlocker tntGuildBlocker, FileConfiguration config) {
        this.logout = logout;
        this.teleporter = teleporter;
        this.tpaRequester = tpaRequester;
        this.guilds = guilds;
        this.relations = relations;
        this.tntGuildBlocker = tntGuildBlocker;
        tpaEnabled = config.getBoolean("tpa-command", false);
        TELEPORT_COOLDOWN = config.getInt("teleport-cooldown", 10);
    }

    @Override
    public void run() {
        seconds++;
        if (seconds % 60 == 0) {
            seconds = 0;
        }

        if (EntityDamageByEntityListener.ANTI_LOGOUT) {
            logout.updatePlayerActionBar();
            logout.checkExpiredFights();
        }

        if (TELEPORT_COOLDOWN > 0) {
            teleporter.checkHomeRequests();
        }

        if (tpaEnabled) {
            tpaRequester.checkExpiredTpaRequests();
        }
        this.guilds.checkPlayerInvitations();
        relations.checkGuildPendingRelations();
        if (TNTExplodeListener.TNT_BLOCK_ENABLED) {
            tntGuildBlocker.checkTimesForBlockedGuilds();
        }
    }

}
