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

import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.commands.CommandHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * This command shows informations about specified or players' guild.
 * 
 * Usage: /guild info [optional: tag (if you're member of a guild)]
 */
public class GuildInfoCommand extends CommandHandler {

    public GuildInfoCommand(OpenGuild plugin) {
        super(plugin);
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        GuildHelper guildHelper = getPlugin().getGuildHelper();
        
        if(args.length == 2) {
            String guildToCheck = args[1];
            
            if(!guildHelper.doesGuildExists(guildToCheck)) {
                sender.sendMessage(MsgManager.guilddoesntexists);
                return;
            }
            
            SimpleGuild guild = guildHelper.getGuilds().get(guildToCheck);
            sender.sendMessage(ChatColor.DARK_GRAY + " ----------------- " + ChatColor.GOLD + MsgManager.getIgnorePref("ginfotit").replace("{GUILD}", guild.getTag().toUpperCase()) + ChatColor.DARK_GRAY + " ----------------- ");
            sender.sendMessage(MsgManager.getIgnorePref("ginfodesc").replace("{DESCRIPTION}", guild.getDescription()));
            sender.sendMessage(MsgManager.getIgnorePref("ginfoleader").replace("{LEADER}", Bukkit.getOfflinePlayer(guild.getLeader()).getName()));
            sender.sendMessage(MsgManager.getIgnorePref("ginfomemlist").replace("{SIZE}", String.valueOf(guild.getMembers().size())).replace("{MEMBERS}", getMembers(guild.getMembers())));
        } else {
            if(!(sender instanceof Player)) {
                sender.sendMessage(MsgManager.cmdonlyforplayer);
                return;
            }
            
            Player player = (Player) sender;
            if(!guildHelper.hasGuild(player)) {
                player.sendMessage(MsgManager.get("notinguild"));
                return;
            }
            
            SimpleGuild guild = guildHelper.getPlayerGuild(player.getUniqueId());
            
            sender.sendMessage(ChatColor.DARK_GRAY + " ----------------- " + ChatColor.GOLD + MsgManager.getIgnorePref("ginfotit").replace("{GUILD}", guild.getTag().toUpperCase()) + ChatColor.DARK_GRAY + " ----------------- ");
            sender.sendMessage(MsgManager.getIgnorePref("ginfodesc").replace("{DESCRIPTION}", guild.getDescription()));
            sender.sendMessage(MsgManager.getIgnorePref("ginfoleader").replace("{LEADER}", Bukkit.getOfflinePlayer(guild.getLeader()).getName()));
            sender.sendMessage(MsgManager.getIgnorePref("ginfomemlist").replace("{SIZE}", String.valueOf(guild.getMembers().size())).replace("{MEMBERS}", getMembers(guild.getMembers())));
        }
    }
    
    private String getMembers(List<UUID> uuids) {
        StringBuilder builder = new StringBuilder();
        for(UUID uuid : uuids) {
            builder.append(Bukkit.getOfflinePlayer(uuid).getName());
            builder.append(", ");
        }
        return builder.toString();
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

}