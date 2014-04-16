package pl.grzegorz2047.openguild2047.listeners;

import ca.wacos.nametagedit.NametagAPI;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler;

public class Monitors implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent e) {
        //MySQLHandler.update(e.getEntity().getName(), PType.DEADS, (Integer) MySQLHandler.select(e.getEntity().getName()).get(5) + 1);
        if(e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();
            MySQLHandler.update(killer.getName(), MySQLHandler.PType.KILLS, 1);
        }
        MySQLHandler.update(e.getEntity().getName(), MySQLHandler.PType.DEADS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(!MySQLHandler.existsPlayer(e.getPlayer().getName())) {
            MySQLHandler.
                    insert(e.getPlayer().getName(),
                            null, "false",
                            0, 0);
        } else {
            if(Data.getInstance().isPlayerInGuild(e.getPlayer().getName())) {
                NametagAPI.setPrefix(e.getPlayer().getName(), GenConf.colortagu + Data.getInstance().getPlayersGuild(e.getPlayer().getName()).getTag() + "§r ");

            } else {
                if(NametagAPI.hasCustomNametag(e.getPlayer().getName())) {
                    NametagAPI.resetNametag(e.getPlayer().getName());
                }
            }
        }
    }

}
