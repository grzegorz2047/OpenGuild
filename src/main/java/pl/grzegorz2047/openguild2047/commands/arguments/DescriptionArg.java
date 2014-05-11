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
  //
package pl.grzegorz2047.openguild2047.commands.arguments;

import com.github.grzegorz2047.openguild.event.MessageBroadcastEvent;

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
import pl.grzegorz2047.openguild2047.utils.GenUtil;

/**
 *
 * @author Grzegorz
 */
public class DescriptionArg {

    public static boolean execute(CommandSender sender, String args[]) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return true;
        }
        Player p = (Player) sender;
        if(args.length > 1) {
            if(args[1].equalsIgnoreCase("zmien")) {
                if(args.length > 3) {
                    if(Data.getInstance().isPlayerInGuild(p.getUniqueId())) {
                        SimpleGuild sg = Data.getInstance().getPlayersGuild(p.getUniqueId());
                        if(sg.getLeader().equals(p.getUniqueId())) {
                            String desc = GenUtil.argsToString(args, 3, args.length);
                            sg.setDescription(desc);
                            saveDb(Guilds.getGuild(p), desc);
                            
                            // Event
                            MessageBroadcastEvent event = new MessageBroadcastEvent(MessageBroadcastEvent.Message.DESCRIPTION);
                            Bukkit.getPluginManager().callEvent(event);
                            if(!event.isCancelled()) {
                                Bukkit.broadcastMessage(event.getMessage().replace("{TAG}", sg.getTag()).replace("{PLAYER}", sender.getName()).replace("{DESC}", desc));
                            }
                            return true;
                        } else {
                            p.sendMessage(MsgManager.playernotleader);
                            return true;
                        }
                    } else {
                        p.sendMessage(MsgManager.notinguild);
                        return true;
                    }
                } else {
                    p.sendMessage(MsgManager.wronguseddesccmd);
                    return true;
                }
            } else {
                p.sendMessage(MsgManager.wrongcmdargument);
                return true;
            }
        } else {
            if(Data.getInstance().isPlayerInGuild(p.getUniqueId())) {
                SimpleGuild sg = Data.getInstance().getPlayersGuild(p.getUniqueId());
                p.sendMessage("Opis gildii to: " + sg.getDescription());
                return true;
            } else {
                p.sendMessage(MsgManager.notinguild);
                return true;
            }
        }
    }

    private static void saveDb(Guild guild, String description) {
        MySQLHandler.update(guild, Type.DESCRIPTION, description);
    }

}
