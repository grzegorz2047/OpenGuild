package pl.grzegorz2047.openguild.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.database.TempPlayerData;
import pl.grzegorz2047.openguild.guilds.Guilds;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * File created by grzegorz2047 on 25.08.2017.
 */
public class PlayerCacheListenersController implements Listener {

    private final TempPlayerData tempPlayerData;
    private final SQLHandler sqlHandler;
    private List<UUID> preFire = new ArrayList<>();

    public PlayerCacheListenersController(TempPlayerData tempPlayerData, SQLHandler sqlHandler) {
        this.tempPlayerData = tempPlayerData;
        this.sqlHandler = sqlHandler;
    }

    @EventHandler
    private void onLogin(AsyncPlayerPreLoginEvent e) {
        if (!e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            return;
        }
        preFire.add(e.getUniqueId());
        this.sqlHandler.getPlayerData(e.getUniqueId(), tempPlayerData);
    }

    @EventHandler
    private void onLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        UUID playerUniqueId = player.getUniqueId();
        if (!e.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) {
            this.tempPlayerData.removePlayer(playerUniqueId);
        }
        if (!preFire.contains(playerUniqueId)) {
            this.sqlHandler.getPlayerData(playerUniqueId, tempPlayerData);
        } else {
            preFire.remove(playerUniqueId);
        }
    }


    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (player != null) {
            this.tempPlayerData.removePlayer(player.getUniqueId());
        }
    }

}
