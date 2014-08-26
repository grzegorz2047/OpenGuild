/**
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package pl.grzegorz2047.openguild2047.commands;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.commands.guild.GuildCreateCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildDescriptionCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildDisbandCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildHelpCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildHomeCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildInfoCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildInvitationAcceptCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildInviteCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildItemsCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildKickCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildLeaveCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildListCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildReloadCommand;
import pl.grzegorz2047.openguild2047.commands.guild.GuildVersionCommand;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * OpenGuild's main command.
 * 
 * Usage: /guild [arguments]
 */
public class GuildCommand implements CommandExecutor {
    
    private OpenGuild plugin;
    
    /**
     * This map stores all sub-commands (and their aliases) and their handlers.
     */
    private Map<String[], CommandHandler> commands = new HashMap<String[], CommandHandler>();
    
    public GuildCommand(OpenGuild plugin) {
        this.plugin = plugin;
        
        // Register 'guild' command sub-commands.
        this.commands.put(new String[]{ "create", "zaloz" }, new GuildCreateCommand(plugin));
        this.commands.put(new String[]{ "accept", "akceptuj" }, new GuildInvitationAcceptCommand(plugin));
        this.commands.put(new String[]{ "help", "pomoc" }, new GuildHelpCommand(plugin));
        this.commands.put(new String[]{ "info" }, new GuildInfoCommand(plugin));
        this.commands.put(new String[]{ "invite", "zapros" }, new GuildInviteCommand(plugin));
        this.commands.put(new String[]{ "kick", "wyrzuc" }, new GuildKickCommand(plugin));
        this.commands.put(new String[]{ "reload" }, new GuildReloadCommand(plugin));
        this.commands.put(new String[]{ "items", "itemy" }, new GuildItemsCommand(plugin));
        this.commands.put(new String[]{ "version", "wersja" }, new GuildVersionCommand(plugin));
        this.commands.put(new String[]{ "leave", "opusc" }, new GuildLeaveCommand(plugin));
        this.commands.put(new String[]{ "disband", "rozwiaz", "zamknij" }, new GuildDisbandCommand(plugin));
        this.commands.put(new String[]{ "dom", "home" }, new GuildHomeCommand(plugin));
        this.commands.put(new String[]{ "list", "lista" }, new GuildListCommand(plugin));
        this.commands.put(new String[]{ "description", "desc", "opis" }, new GuildDescriptionCommand(plugin));
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length == 0) {
            GuildHelpCommand helpCommand = new GuildHelpCommand(plugin);
            helpCommand.executeCommand(sender, args);
        } else {
            String subCommand = args[0];

            boolean subCommandFound = false;
            for(String[] alises : this.commands.keySet()) {
                for(String alias : alises) {
                    if(subCommand.equalsIgnoreCase(alias)) {
                        CommandHandler handler = this.commands.get(alises);
                        if(args.length >= handler.getMinimumArguments()) {
                            handler.executeCommand(sender, args);
                            subCommandFound = true;
                        }
                    }
                }
            }
            
            if(!subCommandFound) {
                sender.sendMessage(MsgManager.get("cmdnotfound").replace("{COMMAND}", "/" + label + " " + subCommand));
            }
        }
        
        return true;
    }
}
