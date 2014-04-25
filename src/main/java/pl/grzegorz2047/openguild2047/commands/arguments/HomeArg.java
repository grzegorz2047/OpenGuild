/*
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
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
package pl.grzegorz2047.openguild2047.commands.arguments;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.api.Guild;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler.Type;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class HomeArg {

    private static List<String> blocked = new ArrayList<String>();

    public static boolean execute(CommandSender sender, String args[]) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(GenConf.prefix + MsgManager.cmdonlyforplayer);
            return false;
        }
        Player p = (Player) sender;
        if(!GenConf.homecommand) {
            sender.sendMessage(GenConf.prefix + MsgManager.homenotenabled);
            return false;
        }
        if(Data.getInstance().isPlayerInGuild(p.getUniqueId())) {
            if(args.length >= 2) {
                SimpleGuild sg = Data.getInstance().getPlayersGuild(p.getUniqueId()); // lol przeciez jest Guilds.getGuild(p); xD
                if(sg.getLeader().equals(p.getUniqueId())) {
                    sg.setHome(p.getLocation());
                    saveDb(Guilds.getGuild(p), p.getLocation());
                    return true;
                } else {
                    p.sendMessage(MsgManager.playernotleader);
                    return false;
                }
            } else {
                Location homeloc = Data.getInstance().getPlayersGuild(p.getUniqueId()).getHome();
                int cooldown = GenConf.TELEPORT_COOLDOWN;
                if(cooldown <= 0) {
                    teleport(p, homeloc, 10); // Teleportacja gdy jest bledny config - 10 sekund
                    Guilds.getLogger().severe("Czas oczekiwania teleportacji musi miec minimum 1 sekunde!");
                    return true;
                }
                teleport(p, homeloc, cooldown);
                p.sendMessage(MsgManager.teleportsuccess);
                return true;
            }

        } else {
            p.sendMessage(MsgManager.notinguild);
            return false;
        }
    }

    private static void saveDb(Guild guild, Location location) {
        MySQLHandler.update(guild, Type.HOME_X, location.getBlockX());
        MySQLHandler.update(guild, Type.HOME_Y, location.getBlockY());
        MySQLHandler.update(guild, Type.HOME_Z, location.getBlockZ());
    }

    private static void teleport(final Player player, final Location location, final int seconds) {
        if(!(blocked == null) && blocked.contains(player.getName().toLowerCase())) {
            player.sendMessage(ChatColor.RED + "Juz trwa odliczanie! Prosze sie nie ruszac.");
            return;
        }
        final World world = player.getLocation().getWorld();
        final int x = player.getLocation().getBlockX();
        final int y = player.getLocation().getBlockY();
        final int z = player.getLocation().getBlockZ();
        blocked.add(player.getName().toLowerCase());
        player.sendMessage(ChatColor.GRAY + "Zostaniesz teleportowany/a do gildii " + Guilds.getGuild(player).getTag() + " za " + seconds + " sekund!");
        Bukkit.getScheduler().scheduleSyncDelayedTask(OpenGuild.get(), new Runnable() {

            @Override
            public void run() {
                Location loc = player.getLocation();
                blocked.remove(player.getName().toLowerCase());
                if(world.getName().equals(loc.getWorld().getName())
                        && x == loc.getBlockX()
                        && y == loc.getBlockY()
                        && z == loc.getBlockZ()) {
                    player.teleport(Guilds.getGuild(player).getHome());
                    player.sendMessage(ChatColor.GREEN + "Zostales/as teleportowany/a do gildii " + Guilds.getGuild(player).getTag() + "!");
                } else {
                    player.sendMessage(ChatColor.GRAY + "Teleportacja zostala anulowana poniewaz sie ruszyles/as!");
                }
            }
        }, seconds * 20L);
    }

}
