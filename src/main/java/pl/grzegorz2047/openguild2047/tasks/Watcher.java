package pl.grzegorz2047.openguild2047.tasks;

import pl.grzegorz2047.openguild2047.configuration.GenConf;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.relations.Relations;
import pl.grzegorz2047.openguild2047.teleporters.Teleporter;
import pl.grzegorz2047.openguild2047.teleporters.TpaRequester;
import pl.grzegorz2047.openguild2047.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild2047.tntguildblocker.TntGuildBlocker;

/**
 * Created by grzeg on 13.08.2017.
 */
public class Watcher implements Runnable {
    private final AntiLogoutManager logout;
    private final Teleporter teleporter;
    private final TpaRequester tpaRequester;
    private final Guilds guilds;
    private final Relations relations;
    private final TntGuildBlocker tntGuildBlocker;

    private int seconds;

    public Watcher(AntiLogoutManager logout, Teleporter teleporter, TpaRequester tpaRequester, Guilds guilds, Relations relations, TntGuildBlocker tntGuildBlocker) {
        this.logout = logout;
        this.teleporter = teleporter;
        this.tpaRequester = tpaRequester;
        this.guilds = guilds;
        this.relations = relations;
        this.tntGuildBlocker = tntGuildBlocker;
    }

    @Override
    public void run() {
        seconds++;
        if (seconds % 60 == 0) {
            seconds = 0;
        }

        if (GenConf.ANTI_LOGOUT) {
            logout.updatePlayerActionBar();
            logout.checkExpiredFights();
        }

        if(GenConf.TELEPORT_COOLDOWN > 0) {
            teleporter.checkHomeRequests();
        }

        if(GenConf.TPA_ENABLED) {
            tpaRequester.checkExpiredTpaRequests();
        }
        this.guilds.checkPlayerInvitations();
        relations.checkGuildPendingRelations();
        if(GenConf.enableTNTExplodeListener) {
            tntGuildBlocker.checkTimesForBlockedGuilds();
        }
    }

}
