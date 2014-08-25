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

package pl.grzegorz2047.openguild2047.commands;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.SimplePlayerGuild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Aleksander
 */
public class TeamCommand implements CommandExecutor {
    
    private OpenGuild plugin;
    
    public TeamCommand(OpenGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("team")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
                return true;
            }
            if(args.length <= 0) {
                sender.sendMessage(MsgManager.get("cmdsyntaxerr"));
                sender.sendMessage(ChatColor.RED + "/team <message...>");
                return true;
            } else {
                String message;
                StringBuilder builder = new StringBuilder();
                for(String arg : args) {
                    builder.append(arg);
                    builder.append(" ");
                }
                message = builder.toString();
                sendMessage((Player) sender, message);
                return true;
            }
        }
        return false;
    }
    
    private void sendMessage(Player author, String message) {
        GuildHelper helper = this.plugin.getGuildHelper();
        if(!helper.hasGuild(author)) {
            author.sendMessage(MsgManager.get("notinguild"));
        } else {
            SimpleGuild guild = this.plugin.getGuildHelper().getPlayerGuild(author.getUniqueId());
            String name = author.getName();
            if(author.getDisplayName() != null)
                name = author.getDisplayName();

            for(UUID member : guild.getMembers()) {
                Player pMember = Bukkit.getPlayer(member);
                if(pMember != null)
                    pMember.sendMessage(ChatColor.GRAY + "[Guild] " + ChatColor.BLUE + name + ChatColor.GRAY + ": " + ChatColor.WHITE + message);
            }
            /* NEW EVENT CODE
            GuildsChatMessageEvent event = new GuildsChatMessageEvent(OpenGuild.getUser(author), OpenGuild.getGuild(author), message);
            Bukkit.getPluginManager().callEvent(event);
            if(!event.isCancelled()) {
                for(UUID member : event.getGuild().getMembers()) {
                    Player pMember = Bukkit.getPlayer(member);
                    if(pMember != null) {
                        pMember.sendMessage(event.getFormat());
                    }
                }
            }*/
        }
    }
    
}
