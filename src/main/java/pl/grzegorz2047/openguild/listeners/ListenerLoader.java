package pl.grzegorz2047.openguild.listeners;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import pl.grzegorz2047.openguild.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild.configuration.GenConf;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.database.TempPlayerData;
import pl.grzegorz2047.openguild.dropstone.DropFromBlocks;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.TagManager;
import pl.grzegorz2047.openguild.ranking.EloRanking;
import pl.grzegorz2047.openguild.teleporters.Teleporter;
import pl.grzegorz2047.openguild.teleporters.TpaRequester;
import pl.grzegorz2047.openguild.tntguildblocker.TntGuildBlocker;
import pl.grzegorz2047.openguild.updater.Updater;

/**
 * File created by grzegorz2047 on 23.08.2017.
 */
public class ListenerLoader {

    private final Guilds guilds;
    private final TagManager tagManager;
    private final SQLHandler sqlHandler;
    private final Teleporter teleporter;
    private final TpaRequester tpaRequester;
    private final Cuboids cuboids;
    private final AntiLogoutManager logout;
    private final DropFromBlocks drop;
    private final Plugin p;
    private final TntGuildBlocker tntGuildBlocker;
    private final Updater updater;

    //Definitywnie trzeba coś ti zrobić xd

    public ListenerLoader(Plugin p, Guilds guilds, TagManager tagManager, SQLHandler sqlHandler, Teleporter teleporter, TpaRequester tpaRequester, Cuboids cuboids, AntiLogoutManager antiLogoutManager, DropFromBlocks dropFromBlocks, TntGuildBlocker tntGuildBlocker, Updater updater) {
        this.guilds = guilds;
        this.tagManager = tagManager;
        this.sqlHandler = sqlHandler;
        this.teleporter = teleporter;
        this.tpaRequester = tpaRequester;
        this.cuboids = cuboids;
        this.logout = antiLogoutManager;
        this.drop = dropFromBlocks;
        this.tntGuildBlocker = tntGuildBlocker;
        this.p = p;
        this.updater = updater;
    }

    public void loadListeners(PluginManager pm) {
        TempPlayerData tempPlayerData = new TempPlayerData();
        EloRanking eloRanking = new EloRanking(p, sqlHandler);
        pm.registerEvents(new PlayerJoinListener(guilds, tagManager, sqlHandler, tempPlayerData, updater), p);
        pm.registerEvents(new PlayerChatListener(guilds), p);
        pm.registerEvents(new PlayerDeathListener(sqlHandler, logout, eloRanking), p);
        pm.registerEvents(new PlayerKickListener(teleporter, cuboids, tpaRequester, guilds), p);
        pm.registerEvents(new PlayerQuitListener(guilds, cuboids, logout, teleporter, tpaRequester, tempPlayerData), p);
        pm.registerEvents(new PlayerCacheListenersController(tempPlayerData, sqlHandler), p);
        if (GenConf.BLOCK_STRENGTH_2) {
            pm.registerEvents(new EnchantInsertListener(), p);
        }

        if (GenConf.CUBOID_ENABLED) {
            pm.registerEvents(new CuboidAndSpawnManipulationListeners(cuboids, drop, guilds), p);
        }

        pm.registerEvents(new EntityDamageByEntityListener(logout, guilds), p);

        if (GenConf.ENABLED_PLAYER_MOVE_EVENT) {
            pm.registerEvents(new PlayerMoveListener(guilds, cuboids), p);
        }
        if (GenConf.TNT_PLACE_BLOCK_CUBOID_ENABLED) {
            pm.registerEvents(new TNTExplode(guilds, drop, tntGuildBlocker), p);
        }
    }
}
