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

package pl.grzegorz2047.openguild2047.commands.guild;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.guilds.Guild;
import pl.grzegorz2047.openguild2047.commands.command.Command;
import pl.grzegorz2047.openguild2047.commands.command.CommandException;
import pl.grzegorz2047.openguild2047.events.guild.GuildLeaveEvent;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.managers.TagManager;

/**
 * Command used by players to leave current guild.
 * <p>
 * Usage: /guild leave
 */
public class GuildLeaveCommand extends Command {
    private final Guilds guilds;
    private final TagManager tagManager;
    private final SQLHandler sqlHandler;

    public GuildLeaveCommand(Guilds guilds, TagManager tagManager, SQLHandler sqlHandler) {
        setPermission("openguild.command.leave");
        this.guilds = guilds;
        this.tagManager = tagManager;
        this.sqlHandler = sqlHandler;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return;
        }


        Player player = (Player) sender;
        if (!guilds.hasGuild(player)) {
            player.sendMessage(MsgManager.notinguild);
            return;
        }

        Guild guild = guilds.getPlayerGuild(player.getUniqueId());
        if (guild.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(MsgManager.get("kickleader"));
            return;
        }

        GuildLeaveEvent event = new GuildLeaveEvent(guild, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        guild.removeMember(player.getUniqueId());
        guilds.updatePlayerMetadata(player.getUniqueId(), "", "");
        for (UUID member : guild.getMembers()) {
            OfflinePlayer opp = Bukkit.getOfflinePlayer(member);
            if (opp.isOnline()) {
                opp.getPlayer().sendMessage(MsgManager.get("broadcast-leave").replace("{PLAYER}", player.getDisplayName()).replace("{TAG}", guild.getName().toUpperCase()));
            }
        }
        tagManager.playerLeaveGuild(player, guild);
        sqlHandler.updatePlayerTag(player.getUniqueId(), "");
        player.sendMessage(MsgManager.leaveguildsuccess);
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
