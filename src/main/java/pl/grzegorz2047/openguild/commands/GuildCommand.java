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

import java.util.HashMap;
import java.util.Map;

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
    private final Map<String[], Command> commands = new HashMap<>();

    public GuildCommand(Cuboids cuboids, Guilds guilds, Teleporter teleporter, TagManager tagManager, SQLHandler sqlHandler, Relations relations, HardcoreSQLHandler hardcoreSQLHandler, HardcoreHandler hardcoreHandler, Plugin plugin) {
        registerCommands(cuboids, guilds, teleporter, tagManager, sqlHandler, relations, hardcoreSQLHandler, hardcoreHandler, plugin);
    }

    private void registerCommands(Cuboids cuboids, Guilds guilds, Teleporter teleporter, TagManager tagManager, SQLHandler sqlHandler, Relations relations, HardcoreSQLHandler hardcoreSQLHandler, HardcoreHandler hardcoreHandler, Plugin plugin) {
        // Register 'guild' command sub-commands.
        String[] createAliases = {"create", "zaloz", "stworz"};
        commands.put(createAliases, new GuildCreateCommand(cuboids, guilds, sqlHandler, tagManager));
        String[] acceptAliases = {"accept", "akceptuj"};
        commands.put(acceptAliases, new GuildInvitationAcceptCommand(tagManager, guilds));
        String[] helpAliases = {"help", "pomoc"};
        commands.put(helpAliases, new GuildHelpCommand());
        String[] infoAliases = {"info", "informacja"};
        commands.put(infoAliases, new GuildInfoCommand(guilds));
        String[] inviteAliases = {"invite", "zapros"};
        commands.put(inviteAliases, new GuildInviteCommand(guilds));
        String[] kickAliases = {"kick", "wyrzuc"};
        commands.put(kickAliases, new GuildKickCommand(tagManager, guilds, sqlHandler));
        String[] reloadAliases = {"reload", "przeladuj"};
        commands.put(reloadAliases, new GuildReloadCommand(plugin.getConfig()));
        String[] itemsAliases = {"items", "itemy", "przedmioty"};
        commands.put(itemsAliases, new GuildItemsCommand(plugin, guilds));
        String[] versionAliases = {"version", "wersja", "ver", "about"};
        commands.put(versionAliases, new GuildVersionCommand(plugin));
        String[] leaveAliases = {"leave", "opusc", "wyjdz"};
        commands.put(leaveAliases, new GuildLeaveCommand(guilds, tagManager, sqlHandler));
        String[] disbandAliases = {"disband", "rozwiaz", "zamknij"};
        commands.put(disbandAliases, new GuildDisbandCommand(guilds, cuboids, sqlHandler, tagManager));
        String[] homeAliases = {"dom", "home", "house"};
        commands.put(homeAliases, new GuildHomeCommand(teleporter, guilds));
        String[] setHomeAliases = {"ustawdom", "sethome", "sethouse"};
        commands.put(setHomeAliases, new GuildChangeHomeCommand(guilds, sqlHandler));
        String[] changeLeaderAliases = {"zmienlidera", "changeleader"};
        commands.put(changeLeaderAliases, new GuildChangeLeaderCommand(guilds, sqlHandler));
        String[] listAliases = {"list", "lista"};
        commands.put(listAliases, new GuildListCommand(guilds));
        String[] descriptionAliases = {"description", "desc", "opis"};
        commands.put(descriptionAliases, new GuildDescriptionCommand(guilds, sqlHandler));
        String[] allyAliases = {"ally", "sojusz",};
        commands.put(allyAliases, new GuildAllyCommand(relations, guilds, tagManager, sqlHandler));
        String[] enemyAliases = {"enemy", "wrog",};
        commands.put(enemyAliases, new GuildEnemyCommand(guilds, sqlHandler, tagManager));
        String[] unbanPlayerAliases = {"unbanplayer", "odbanujgracza",};
        commands.put(unbanPlayerAliases, new GuildUnbanPlayerCommand(hardcoreSQLHandler, hardcoreHandler));
        String[] randomTPAliases = {"randomtp", "randomtp"};
        commands.put(randomTPAliases, new GuildRandomTPCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (args.length == 0) {
            GuildHelpCommand helpCommand = new GuildHelpCommand();
            helpCommand.execute(sender, args);
        } else {
            String subCommand = args[0];

            boolean subCommandFound = false;
            for (String[] aliases : commands.keySet()) {
                for (String alias : aliases) {
                    if (subCommand.equalsIgnoreCase(alias)) {
                        Command executor = commands.get(aliases);
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
                String cmdnotfound = MsgManager.get("cmdnotfound").replace("{COMMAND}", "/" + label + " " + subCommand);
                sender.sendMessage(cmdnotfound);
            }
        }

        return true;
    }
}
