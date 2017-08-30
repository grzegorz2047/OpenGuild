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

package pl.grzegorz2047.openguild.commands.guild;

import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.events.guild.GuildJoinEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.guilds.GuildInvitation;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild.managers.TagManager;

/**
 * Command used to accept invitation to guild.
 * <p>
 * Usage: /guild accept [optional: tag (required only if there's more than 2 invitations)]
 */
public class GuildInvitationAcceptCommand extends Command {
    private final TagManager tagManager;
    private final Guilds guilds;

    public GuildInvitationAcceptCommand(TagManager tagManager, Guilds guilds) {
        setPermission("openguild.command.invitationaccept");
        this.tagManager = tagManager;
        this.guilds = guilds;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }

        Player player = (Player) sender;
        if (guilds.hasGuild((player).getUniqueId())) {
            sender.sendMessage(MsgManager.get("alreadyinguild"));
            return;
        }

        List<Guild> invitationsFrom = new ArrayList<>();
        for (Guild guild : guilds.getGuilds().values()) {
            GuildInvitation guildInvitation = guilds.getGuildInvitation(sender.getName(), guild.getName());
            if (guildInvitation != null) {
                invitationsFrom.add(guild);
            }
        }

        if (args.length == 1) {
            int invitationsNumber = invitationsFrom.size();
            if (invitationsNumber > 1) {
                sender.sendMessage(MsgManager.get("invmore"));
                for (Guild guild : invitationsFrom) {
                    sender.sendMessage(ChatColor.BOLD + guild.getName().toUpperCase() + ChatColor.GRAY + " - " + guild.getDescription());
                }
            } else if (invitationsNumber == 1) {
                Guild onlyInvitationGuild = invitationsFrom.get(0);
                accept(player, onlyInvitationGuild);
            } else {
                sender.sendMessage(MsgManager.get("noinv"));
            }
        } else if (args.length >= 2) {
            String tag = args[1].toUpperCase();
            Guild target = guilds.getGuild(tag);
            if (target != null && guilds.getGuilds().containsKey(tag) && invitationsFrom.contains(target)) {
                accept(player, target);

            }
        }
    }

    @Override
    public int minArgs() {
        return 1;
    }

    private void accept(Player player, Guild guild) {
        GuildJoinEvent event = new GuildJoinEvent(guild, player);
        if (event.isCancelled()) {
            return;
        }

        guilds.acceptInvitation(player, guild);
        tagManager.refreshScoreboardTagsForAllPlayersOnServerApartFromJoiner(player, guild);
        Bukkit.broadcastMessage(MsgManager.get("broadcast-join")
                .replace("{PLAYER}", player.getName())
                .replace("{TAG}", guild.getName().toUpperCase()));
    }

}
