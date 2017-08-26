package pl.grzegorz2047.openguild2047.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.database.SQLRecord;
import pl.grzegorz2047.openguild2047.database.TempPlayerData;
import pl.grzegorz2047.openguild2047.guilds.Guilds;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by grzeg on 25.08.2017.
 */
public class PlayerCacheListenersController implements Listener {

    private final TempPlayerData tempPlayerData;
    private final SQLHandler sqlHandler;
    private final Guilds guilds;
    private List<UUID> preFire = new ArrayList<>();

    public PlayerCacheListenersController(TempPlayerData tempPlayerData, SQLHandler sqlHandler, Guilds guilds) {
        this.tempPlayerData = tempPlayerData;
        this.sqlHandler = sqlHandler;
        this.guilds = guilds;
    }

    @EventHandler
    private void onLogin(AsyncPlayerPreLoginEvent e) {
        if (!e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
            System.out.println(" gracz " + e.getUniqueId() + " nie moze jeszcze wejsc na serwer on prelogin!");
            return;
        }
        preFire.add(e.getUniqueId());
        System.out.println(" gracz " + e.getUniqueId() + " wchodzi na serwer on prelogin!!");
        this.sqlHandler.getPlayerData(e.getUniqueId(), tempPlayerData);
    }

    @EventHandler
    private void onLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        if (!e.getResult().equals(PlayerLoginEvent.Result.ALLOWED)) {
            System.out.println(" gracz " + player.getName() + " nie moze jeszcze wejsc na serwer on login!");
            this.tempPlayerData.removePlayer(player.getUniqueId());
        }
        if (!preFire.contains(player.getUniqueId())) {
            System.out.println("prelogin nie wystartowal dla " + player.getName());
            this.sqlHandler.getPlayerData(player.getUniqueId(), tempPlayerData);
        } else {
            preFire.remove(player.getUniqueId());
        }
    }


    @EventHandler
    private void onQuit(PlayerQuitEvent e) {
        if (e.getPlayer() != null) {
            this.tempPlayerData.removePlayer(e.getPlayer().getUniqueId());
        }
    }

}
