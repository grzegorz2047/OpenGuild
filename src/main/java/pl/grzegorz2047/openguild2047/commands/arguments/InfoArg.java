/*
 * The MIT License
 *
 * Copyright 2014 student_U190.
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class InfoArg {
    
    public static boolean execute(CommandSender sender, String[] args) {
        if(args.length == 1) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(MsgManager.cmdonlyforplayer);
                return true;
            }
            if(!Data.getInstance().isPlayerInGuild(((Player) sender).getUniqueId())) {
                sender.sendMessage(MsgManager.get("notinguild"));
                return true;
            }
            about(sender, Data.getInstance().guildsplayers.get(((Player) sender).getUniqueId()).getClanTag());
        } else {
            if(!Data.getInstance().guildExists(args[1])) {
                sender.sendMessage(MsgManager.get("guildexists"));
                return true;
            }
            about(sender, args[1]);
        }
        return true;
    }
    
    private static void about(CommandSender sender, String guild) {
        SimpleGuild sg = Data.getInstance().guilds.get(guild);
        sender.sendMessage("Liderem gildii jest "+Bukkit.getOfflinePlayer(sg.getLeader()).getName());
        sender.sendMessage("Liczba czlonkow: "+ sg.getMembers());
        sender.sendMessage("Opis gildii to: "+sg.getDescription());
    }
    
}
