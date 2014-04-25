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

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.grzegorz2047.openguild2047.Data;
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
public class LeaderArg {

    public static boolean execute(CommandSender sender, String args[]) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return false;
        }
        Player p = (Player) sender;
        if(Data.getInstance().isPlayerInGuild(p.getUniqueId())) {
            SimpleGuild sg = Data.getInstance().getPlayersGuild(p.getUniqueId());
            if(args.length >= 3) {
                if(args[1].equalsIgnoreCase("zmien")) {
                    if(sg.getLeader().equals(p.getUniqueId())) {
                        if(Bukkit.getPlayer(p.getName()) != null) {
                            Player leader = Bukkit.getPlayer(p.getName());
                            sg.setLeader(leader.getUniqueId());
                            saveDb(Guilds.getGuild(p), leader.getUniqueId());
                            return true;
                        } else {
                            p.sendMessage(MsgManager.playerneverplayed);
                            return false;
                        }
                    } else {
                        p.sendMessage(MsgManager.playernotleader);
                        return false;
                    }
                } else {
                    p.sendMessage(MsgManager.wrongcmdargument);
                    return false;
                }
            } else {
                p.sendMessage("Liderem gildii jest " + sg.getLeader());
                return true;
            }
        } else {
            p.sendMessage(MsgManager.notinguild);
            return false;
        }
    }

    private static void saveDb(Guild guild, UUID leader) {
        MySQLHandler.update(guild, Type.LEADER, leader.toString());
    }

}
