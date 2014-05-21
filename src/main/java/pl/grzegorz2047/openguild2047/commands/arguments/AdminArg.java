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

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.api.Guild;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.managers.TagManager;

/**
 *
 * @author Grzegorz
 */
public class AdminArg {
        public static boolean execute(CommandSender sender, String[] args) {
        if(args.length >=2) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(MsgManager.cmdonlyforplayer);
                return true;
            }
            Player player =  (Player) sender;
            if(!sender.hasPermission("openguild.command.admin")) {
                player.sendMessage(MsgManager.get("permission"));
                return true;
                
            }
            if(args.length > 2) {
                if(args[1].equalsIgnoreCase("banguild")) {
                    String tag = args[2];
                    if(Data.getInstance().ClansTag.contains(tag.toLowerCase())) {
                        //SimpleGuild sg = Data.getInstance().guilds.get(tag);
                        //saveDb(sg);
                        
                        return true;
                    }
                
                }
                if(args[1].equalsIgnoreCase("closeguild")) {
                    String tag = args[2];
                    if(Data.getInstance().ClansTag.contains(tag.toLowerCase())) {
                    SimpleGuild sg = Data.getInstance().guilds.get(tag);
                    saveDb(sg);
                    sg.getHome().getBlock().setType(Material.AIR);
                    Location loc = sg.getHome();
                    Block b = sg.getHome().getWorld().getBlockAt(loc.getBlockX(),loc.getBlockY() - 1, loc.getBlockZ());
                    b.setType(Material.AIR);
                    for(UUID member : sg.getMembers()) {
                        TagManager.removeTag(member);
                        Data.getInstance().guildsplayers.remove(member);
                    }
                
                    Data.getInstance().ClansTag.remove(tag);
                    Data.getInstance().guilds.remove(tag);
                    Data.getInstance().cuboids.remove(tag);
                    }
                }
            }
            if(args[1].equalsIgnoreCase("kick")) {
                
            }

            
            
        } else {
            sender.sendMessage(MsgManager.wrongcmdargument);
            return true;
        }
        
        return true;
    }

    private static void saveDb(Guild guild) {
        SQLHandler.delete(guild);
        for(UUID p : guild.getMembers()) {//Usuwa totalnie gildie
            SQLHandler.update(p, SQLHandler.PType.GUILD, "");
        }
    }
        
}
