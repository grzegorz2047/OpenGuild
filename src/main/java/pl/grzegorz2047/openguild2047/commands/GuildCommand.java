/*
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.grzegorz2047.openguild2047.commands;

import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild2047.GuildHomeTeleporter;
import pl.grzegorz2047.openguild2047.Guilds;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.commands.guild.*;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * OpenGuild's main command.
 * <p>
 * Usage: /guild [arguments]
 */
public class GuildCommand implements CommandExecutor {

    /**
     * This map stores all sub-commands (and their aliases) and their handlers.
     */
    private final Map<String[], Command> commands = new HashMap<String[], Command>();
    private final Cuboids cuboids;
    private final Guilds guilds;

    public GuildCommand(Cuboids cuboids, Guilds guilds, GuildHomeTeleporter teleporter) {
        this.cuboids = cuboids;
        this.guilds = guilds;

        // Register 'guild' command sub-commands.
        this.commands.put(new String[]{"create", "zaloz", "stworz"}, new GuildCreateCommand(cuboids, guilds));
        this.commands.put(new String[]{"accept", "akceptuj"}, new GuildInvitationAcceptCommand());
        this.commands.put(new String[]{"help", "pomoc"}, new GuildHelpCommand());
        this.commands.put(new String[]{"info", "informacja"}, new GuildInfoCommand());
        this.commands.put(new String[]{"invite", "zapros"}, new GuildInviteCommand());
        this.commands.put(new String[]{"kick", "wyrzuc"}, new GuildKickCommand());
        this.commands.put(new String[]{"reload", "przeladuj"}, new GuildReloadCommand());
        this.commands.put(new String[]{"items", "itemy", "przedmioty"}, new GuildItemsCommand());
        this.commands.put(new String[]{"version", "wersja", "ver", "about"}, new GuildVersionCommand());
        this.commands.put(new String[]{"leave", "opusc", "wyjdz"}, new GuildLeaveCommand());
        this.commands.put(new String[]{"disband", "rozwiaz", "zamknij"}, new GuildDisbandCommand());
        this.commands.put(new String[]{"dom", "home", "house"}, new GuildHomeCommand(teleporter));
        this.commands.put(new String[]{"list", "lista"}, new GuildListCommand());
        this.commands.put(new String[]{"description", "desc", "opis"}, new GuildDescriptionCommand());
        this.commands.put(new String[]{"ally", "sojusz",}, new GuildAllyCommand());
        this.commands.put(new String[]{"enemy", "wrog",}, new GuildEnemyCommand());
        this.commands.put(new String[]{"unbanplayer", "odbanujgracza",}, new GuildUnbanPlayerCommand());
        this.commands.put(new String[]{"randomtp", "randomtp",}, new GuildRandomTPCommand());


    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0) {
            GuildHelpCommand helpCommand = new GuildHelpCommand();
            helpCommand.execute(sender, args);
        } else {
            String subCommand = args[0];

            boolean subCommandFound = false;
            for (String[] aliases : this.commands.keySet()) {
                for (String alias : aliases) {
                    if (subCommand.equalsIgnoreCase(alias)) {
                        Command executor = this.commands.get(aliases);
                        if (executor.hasPermission() && !sender.hasPermission(executor.getPermission())) {
                            sender.sendMessage(MsgManager.get("permission"));
                        } else if (args.length >= executor.minArgs()) {
                            try {
                                executor.execute(sender, args);
                            } catch (CommandException ex) {
                                sender.sendMessage(MsgManager.get("cmdsyntaxerr"));
                                if (ex.getMessage() != null) sender.sendMessage(ChatColor.RED + ex.getMessage());
                            }
                        } else {
                            sender.sendMessage(MsgManager.get("cmdsyntaxerr"));
                            sender.sendMessage(MsgManager.get("seehelp"));
                        }
                        subCommandFound = true;
                    }
                }
            }

            if (!subCommandFound) {
                sender.sendMessage(MsgManager.get("cmdnotfound").replace("{COMMAND}", "/" + label + " " + subCommand));
            }
        }

        return true;
    }
}
