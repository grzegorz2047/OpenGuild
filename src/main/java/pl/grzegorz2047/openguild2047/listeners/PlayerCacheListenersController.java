package pl.grzegorz2047.openguild2047.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.database.SQLRecord;
import pl.grzegorz2047.openguild2047.database.TempPlayerData;
import pl.grzegorz2047.openguild2047.guilds.Guilds;

/**
 * Created by grzeg on 25.08.2017.
 */
public class PlayerCacheListenersController implements Listener {

    private final TempPlayerData tempPlayerData;
    private final SQLHandler sqlHandler;
    private final Guilds guilds;

    public PlayerCacheListenersController(TempPlayerData tempPlayerData, SQLHandler sqlHandler, Guilds guilds) {
        this.tempPlayerData = tempPlayerData;
        this.sqlHandler = sqlHandler;
        this.guilds = guilds;
    }

    @EventHandler
    private void onLogin(AsyncPlayerPreLoginEvent e) {
        if (!e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            return;
        }
        this.sqlHandler.getPlayerData(e.getUniqueId(), tempPlayerData);
    }

    @EventHandler
    private void onLogin(PlayerLoginEvent e) {
        if (!e.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) {
            this.tempPlayerData.removePlayer(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    private void onLogin(PlayerJoinEvent e) {

    }

    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        if (e.getPlayer() != null) {
            this.tempPlayerData.removePlayer(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    private void onQuit(PlayerKickEvent e) {
        if (e.getPlayer() != null) {
            this.tempPlayerData.removePlayer(e.getPlayer().getUniqueId());
        }
    }
}
