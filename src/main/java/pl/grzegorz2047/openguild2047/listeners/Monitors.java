package pl.grzegorz2047.openguild2047.listeners;


import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.cuboidmanagement.CuboidStuff;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.managers.TagManager;

public class Monitors implements Listener {
    
    private OpenGuild plugin;
    
    public Monitors(OpenGuild plugin) {
        this.plugin = plugin;
    }

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
            plugin.getTagManager().setTag(e.getPlayer().getUniqueId());
        }
        
        if(plugin.getGuildHelper().hasGuild(e.getPlayer().getUniqueId())) {
            SimpleGuild guild = plugin.getGuildHelper().getPlayerGuild(e.getPlayer().getUniqueId());
            for(UUID uuid : guild.getMembers()) {
                Player online = Bukkit.getPlayer(uuid);
                if(online != null) {
                    online.sendMessage(MsgManager.get("guildmemberjoined").replace("{PLAYER}", e.getPlayer().getName()));
                }
            }
        }
        
        // Updater
        if(e.getPlayer().isOp() && com.github.grzegorz2047.openguild.OpenGuild.getUpdater().isEnabled() && com.github.grzegorz2047.openguild.OpenGuild.getUpdater().isAvailable()) {
            e.getPlayer().sendMessage(ChatColor.RED + " =============== OpenGuild UPDATER =============== ");
            if(GenConf.lang.equalsIgnoreCase("PL")) {
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Znaleziono aktualizacje! Prosze zaktualizowac Twój plugin do najnowszej wersji!");
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Pobierz go z https://github.com/grzegorz2047/OpenGuild2047/releases");
            }
            else if(GenConf.lang.equalsIgnoreCase("SV")) {
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Uppdatering hittas! Uppdatera ditt plugin till den senaste version!");
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Ladda ner det från https://github.com/grzegorz2047/OpenGuild2047/releases");
            } else {
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Update found! Please update your plugin to the newest version!");
                e.getPlayer().sendMessage(ChatColor.YELLOW + "Download it from https://github.com/grzegorz2047/OpenGuild2047/releases");
            }
            e.getPlayer().sendMessage(ChatColor.RED + " =============== OpenGuild UPDATER =============== ");
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(plugin.getGuildHelper().hasGuild(e.getPlayer().getUniqueId())) {
            SimpleGuild guild = plugin.getGuildHelper().getPlayerGuild(e.getPlayer().getUniqueId());
            for(UUID uuid : guild.getMembers()) {
                Player online = Bukkit.getPlayer(uuid);
                if(online != null) {
                    online.sendMessage(MsgManager.get("guildmemberleft").replace("{PLAYER}", e.getPlayer().getName()));
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerQuitEvent e) {
        CuboidStuff.playersenteredcuboid.remove(e.getPlayer().getName());
    }
//Przy wychodzeniu to trzeba to CuboidStuff.playersenteredcuboid.put(player.getName(), tag);
}
