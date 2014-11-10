/*
 * The MIT License
 *
 * Copyright 2014 Adam.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.grzegorz2047.openguild2047.listeners;

import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class PlayerJoinListener implements Listener {
    
    private OpenGuild plugin;
    
    public PlayerJoinListener(OpenGuild plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void handleEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(!plugin.getGuildHelper().getPlayers().containsKey(uuid)) {
            plugin.getSQLHandler().addPlayer(uuid);
            plugin.getGuildHelper().getPlayers().put(uuid, null);
        }
        plugin.getTagManager().setTag(uuid);
        if(plugin.getGuildHelper().hasGuild(player)) {
            Guild guild = plugin.getGuildHelper().getPlayerGuild(uuid);
            player.sendMessage("Aktualnie posiadasz "+guild.getAlliances().size()+" relacji!");
            for(UUID mem : guild.getMembers()) {
                OfflinePlayer om = plugin.getServer().getOfflinePlayer(mem);
                if(om.isOnline()) {
                    if(!player.getUniqueId().equals(om.getUniqueId())){
                        om.getPlayer().sendMessage(MsgManager.get("guildmemberjoined").replace("{PLAYER}", player.getDisplayName()));
                    }
                }
            }
        }
        
        if(player.isOp() && com.github.grzegorz2047.openguild.OpenGuild.getUpdater().isEnabled() && com.github.grzegorz2047.openguild.OpenGuild.getUpdater().isAvailable()) {
            player.sendMessage(ChatColor.RED + " =============== OpenGuild UPDATER =============== ");
            if(GenConf.lang.equalsIgnoreCase("PL")) {
                player.sendMessage(ChatColor.YELLOW + "Znaleziono aktualizacje! Prosze zaktualizowac Twój plugin do najnowszej wersji!");
                player.sendMessage(ChatColor.YELLOW + "Pobierz go z https://github.com/grzegorz2047/OpenGuild2047/releases");
            }
            else if(GenConf.lang.equalsIgnoreCase("SV")) {
                player.sendMessage(ChatColor.YELLOW + "Uppdatering hittas! Uppdatera ditt plugin till den senaste version!");
                player.sendMessage(ChatColor.YELLOW + "Ladda ner det från https://github.com/grzegorz2047/OpenGuild2047/releases");
            } else {
                player.sendMessage(ChatColor.YELLOW + "Update found! Please update your plugin to the newest version!");
                player.sendMessage(ChatColor.YELLOW + "Download it from https://github.com/grzegorz2047/OpenGuild2047/releases");
            }
            player.sendMessage(ChatColor.RED + " =============== OpenGuild UPDATER =============== ");
        }
    }
}
