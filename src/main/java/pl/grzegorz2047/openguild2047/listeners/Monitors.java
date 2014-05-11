package pl.grzegorz2047.openguild2047.listeners;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.cuboidmanagement.CuboidStuff;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.TagManager;

public class Monitors implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent e) {
        //MySQLHandler.update(e.getEntity().getName(), PType.DEADS, (Integer) SQLHandler.select(e.getEntity().getName()).get(5) + 1);
        if(e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();
            SQLHandler.update(killer.getUniqueId(), SQLHandler.PType.KILLS, 1);
        }
        SQLHandler.update(e.getEntity().getUniqueId(), SQLHandler.PType.DEADS, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(!SQLHandler.existsPlayer(e.getPlayer().getUniqueId())) {
            SQLHandler.insert(null, "false", 0, 0, e.getPlayer().getUniqueId());
        } else {
           /* if(Data.getInstance().isPlayerInGuild(e.getPlayer().getUniqueId())) {
                NametagAPI.setPrefix(e.getPlayer().getName(), GenConf.colortagu + Data.getInstance().getPlayersGuild(e.getPlayer().getUniqueId()).getTag() + "§r ");

            } else {
                if(NametagAPI.hasCustomNametag(e.getPlayer().getName())) {
                    NametagAPI.resetNametag(e.getPlayer().getName());
                }
            }*/
            TagManager.setTag(e.getPlayer().getUniqueId());
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent e) {
        CuboidStuff.playersenteredcuboid.remove(e.getPlayer().getName());
    }
//Przy wychodzeniu to trzeba to CuboidStuff.playersenteredcuboid.put(player.getName(), tag);
}
