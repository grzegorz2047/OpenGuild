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

package pl.grzegorz2047.openguild2047.commands.guild;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import pl.grzegorz2047.openguild2047.commands.CommandHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used by players to teleport to their guild home.
 * 
 * Usage: /guild home
 */
public class GuildHomeCommand extends CommandHandler {

    public GuildHomeCommand(OpenGuild plugin) {
        super(plugin);
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        
        GuildHelper guildHelper = this.getPlugin().getGuildHelper();
        
        final Player player = (Player) sender;
        if(!guildHelper.hasGuild(player)) {
            player.sendMessage(MsgManager.notinguild);
            return;
        }
        
        if(GenConf.TELEPORT_COOLDOWN < 1) {
            GenConf.TELEPORT_COOLDOWN = 5;
            getPlugin().getOGLogger().warning("Teleport cooldown is smaller than 1 second! Change it in your config.yml");
        }
        
        Guild guild = guildHelper.getPlayerGuild(player.getUniqueId());
        final Location home = guild.getHome();
        final Location startLocation = player.getLocation();
        
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
                    player.sendMessage(ChatColor.RED + "Teleportacja zostala przerwana, poniewaz sie poruszyles!");
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
    public int getMinimumArguments() {
        return 1;
    }

}
