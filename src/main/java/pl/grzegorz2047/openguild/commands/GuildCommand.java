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
package pl.grzegorz2047.openguild.commands;

import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild.hardcore.HardcoreHandler;
import pl.grzegorz2047.openguild.hardcore.HardcoreSQLHandler;
import pl.grzegorz2047.openguild.relations.Relations;
import pl.grzegorz2047.openguild.teleporters.Teleporter;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.commands.guild.*;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.managers.TagManager;

/**
 * OpenGuild's main command.
 * <p>
 * Usage: /guild [arguments]
 */
public class GuildCommand implements CommandExecutor {

    /**
     * This map stores all sub-commands (and their aliases) and their handlers.
     */
    private final List<Command> commands = new ArrayList<>();

    public GuildCommand(Cuboids cuboids, Guilds guilds, Teleporter teleporter, TagManager tagManager, SQLHandler sqlHandler, Relations relations, HardcoreSQLHandler hardcoreSQLHandler, HardcoreHandler hardcoreHandler, Plugin plugin) {
        registerCommands(cuboids, guilds, teleporter, tagManager, sqlHandler, relations, hardcoreSQLHandler, hardcoreHandler, plugin);
    }

    private void registerCommands(Cuboids cuboids, Guilds guilds, Teleporter teleporter, TagManager tagManager, SQLHandler sqlHandler, Relations relations, HardcoreSQLHandler hardcoreSQLHandler, HardcoreHandler hardcoreHandler, Plugin plugin) {
        // Register 'guild' command sub-commands.

        commands.add(new GuildCreateCommand(new String[]{"create", "zaloz", "stworz"}, cuboids, guilds, sqlHandler, tagManager, plugin.getConfig()));
        commands.add(new GuildInvitationAcceptCommand(new String[]{"accept", "akceptuj"}, tagManager, guilds));
        commands.add(new GuildHelpCommand(new String[]{"help", "pomoc"}));
        commands.add(new GuildInfoCommand(new String[]{"info", "informacja"}, guilds));
        commands.add(new GuildInviteCommand(new String[]{"invite", "zapros"}, guilds));
        commands.add(new GuildKickCommand(new String[]{"kick", "wyrzuc"}, tagManager, guilds, sqlHandler));
        commands.add(new GuildReloadCommand(new String[]{"reload", "przeladuj"}, plugin.getConfig()));
        commands.add(new GuildItemsCommand(new String[]{"items", "itemy", "przedmioty"}, plugin, guilds));
        commands.add(new GuildVersionCommand(new String[]{"version", "wersja", "ver", "about"}, plugin));
        commands.add(new GuildLeaveCommand(new String[]{"leave", "opusc", "wyjdz"}, guilds));
        commands.add(new GuildDisbandCommand(new String[]{"disband", "rozwiaz", "zamknij"}, guilds, cuboids, sqlHandler, tagManager));
        commands.add(new GuildHomeCommand(new String[]{"dom", "home", "house"}, teleporter, guilds, plugin.getConfig()));
        commands.add(new GuildChangeHomeCommand(new String[]{"ustawdom", "sethome", "sethouse"}, guilds, sqlHandler));
        commands.add(new GuildChangeLeaderCommand(new String[]{"zmienlidera", "changeleader"}, guilds, sqlHandler));
        commands.add(new GuildListCommand(new String[]{"list", "lista"}, guilds));
        commands.add(new GuildDescriptionCommand(new String[]{"description", "desc", "opis"}, guilds, sqlHandler));
        commands.add(new GuildAllyCommand(new String[]{"ally", "sojusz"}, relations, guilds, tagManager, sqlHandler));
        commands.add(new GuildEnemyCommand(new String[]{"enemy", "wrog"}, guilds, sqlHandler, tagManager));
        commands.add(new GuildUnbanPlayerCommand(new String[]{"unbanplayer", "odbanujgracza"}, hardcoreSQLHandler, hardcoreHandler));
        commands.add(new GuildRandomTPCommand(new String[]{"randomtp", "randomtp"}, plugin));
        commands.add(new GuildExpandCommand(new String[]{"expand", "powieksz", "enlarge"}, guilds, sqlHandler, cuboids, plugin.getConfig()));
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0) {
            GuildHelpCommand helpCommand = new GuildHelpCommand(new String[]{});
            helpCommand.execute(sender, args);
            return true;
        }
        String subCommand = args[0];
        boolean subCommandFound = false;
        for (Command executor : commands) {
            if (!executor.getAliases().contains(args[0].toLowerCase())) {
                continue;
            }
            if (executor.hasPermission() && !sender.hasPermission(executor.getPermission())) {
                sender.sendMessage(MsgManager.get("permission"));
                break;
            }
            if (args.length >= executor.minArgs()) {
                try {
                    executor.execute(sender, args);
                    return true;
                } catch (CommandException ex) {
                    sender.sendMessage(MsgManager.get("cmdsyntaxerr"));
                    if (ex.getMessage() != null) sender.sendMessage(ChatColor.RED + ex.getMessage());
                    return true;
                }
            }
            sender.sendMessage(MsgManager.get("cmdsyntaxerr"));
            sender.sendMessage(MsgManager.get("seehelp"));
            subCommandFound = true;
            break;
        }
        if (!subCommandFound) {
            String cmdnotfound = MsgManager.get("cmdnotfound").replace("{COMMAND}", "/" + label + " " + subCommand);
            sender.sendMessage(cmdnotfound);
        }
        return true;
    }
}
