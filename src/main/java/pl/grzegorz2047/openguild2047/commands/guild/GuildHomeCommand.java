/*
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.grzegorz2047.openguild2047.commands.guild;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.Guilds;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.event.guild.GuildHomeTeleportEvent;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used by players to teleport to their guild home.
 * 
 * Usage: /guild home
 */
public class GuildHomeCommand extends Command {
    public GuildHomeCommand() {
        setPermission("openguild.command.home");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        
        Guilds guilds = this.getPlugin().getGuilds();
        
        final Player player = (Player) sender;
        if(!guilds.hasGuild(player)) {
            player.sendMessage(MsgManager.notinguild);
            return;
        }
        
        if(GenConf.TELEPORT_COOLDOWN < 1) {
            GenConf.TELEPORT_COOLDOWN = 5;
            getPlugin().getOGLogger().warning("Teleport cooldown is smaller than 1 second! Change it in your config.yml");
        }
        
        Guild guild = guilds.getPlayerGuild(player.getUniqueId());
        final Location home = guild.getHome();
        final Location startLocation = player.getLocation();
        
        GuildHomeTeleportEvent event = new GuildHomeTeleportEvent(guild, player);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return;
        }
        
        player.sendMessage(ChatColor.GRAY + MsgManager.timetotpnotify.replace("{GUILD}", guild.getTag().toUpperCase()).replace("{HOMETPSECONDS}", String.valueOf(GenConf.TELEPORT_COOLDOWN)));
        
        new BukkitRunnable() {
            private int timeElapsed = 0;
            
            @Override
            public void run() {
                timeElapsed++;
                
                if(!player.isOnline()) {
                    cancel();
                }
                
                Location currentLocation = player.getLocation();
                if(currentLocation.distance(startLocation) > 1) {
                    player.sendMessage(MsgManager.get("tpcan"));
                    cancel();
                } else {
                    if(timeElapsed == GenConf.TELEPORT_COOLDOWN) {
                        // This is fixed in latest Bukkit, but it is not released yet.
                        if(player.isInsideVehicle()) {
                            player.getVehicle().setPassenger(null);
                        }

                        player.teleport(home);
                        cancel();
                    }
                }
            }
        }.runTaskTimer(getPlugin(), 0L, 20L * 1);
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
