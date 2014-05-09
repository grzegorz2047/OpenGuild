/*
 * The MIT License
 *
 * Copyright 2014 Aleksander.
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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Aleksander
 */
public class UnbanArg  {
    
    public static boolean execute(CommandSender sender, String[] args) {
        if(!GenConf.hcBans) {
            sender.sendMessage(MsgManager.get("hcnotenabled"));
            return true;
        }
        else if(!sender.hasPermission("openguild.hardcore.unban")) {
            sender.sendMessage(MsgManager.get("permission"));
            return true;
        }
        else if(args.length != 2) {
            sender.sendMessage(MsgManager.get("wrongcmdargument"));
            sender.sendMessage(ChatColor.RED + "/g unban <player>");
            return true;
        }
        OfflinePlayer player = Bukkit.getPlayer(args[1]);
        if(player == null || !player.hasPlayedBefore()) {
            sender.sendMessage(MsgManager.get("notplayedbefore"));
            return true;
        }
        
        long banned = MySQLHandler.getBan(player.getUniqueId());
        if(banned != 0) {
            MySQLHandler.update(player.getUniqueId(), MySQLHandler.PType.BAN_TIME, 0);
            Guilds.getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") was unbanned by " + sender.getName());
            sender.sendMessage(MsgManager.get("hcub"));
        } else {
            sender.sendMessage(MsgManager.get("notbanned"));
        }
        return true;
    }
    
}
