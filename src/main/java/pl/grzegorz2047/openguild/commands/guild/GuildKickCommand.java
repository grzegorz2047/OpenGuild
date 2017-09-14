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

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.events.guild.GuildKickEvent;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.managers.TagManager;
import pl.grzegorz2047.openguild.metadata.PlayerMetadataController;

/**
 * Command used to kick player out of players' guild.
 * <p>
 * Usage: /guild kick [player name]
 */
public class GuildKickCommand extends Command {
    private final TagManager tagManager;
    private final Guilds guilds;
    private final SQLHandler sqlHandler;

    public GuildKickCommand(String[] aliases, TagManager tagManager, Guilds guilds, SQLHandler sqlHandler) {
        super(aliases);
        setPermission("openguild.command.kick");
        this.tagManager = tagManager;
        this.guilds = guilds;
        this.sqlHandler = sqlHandler;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }

        Player player = (Player) sender;
        if (!guilds.hasGuild(player)) {
            sender.sendMessage(MsgManager.get("notinguild"));
            return;
        }

        Guild guild = guilds.getPlayerGuild(player.getUniqueId());
        if (!guild.getLeader().equals(player.getUniqueId())) {
            sender.sendMessage(MsgManager.get("playernotleader"));
            return;
        }

        String toKick = args[1];
        OfflinePlayer op = Bukkit.getOfflinePlayer(toKick);
        if (!guild.getMembers().contains(op.getUniqueId())) {
            sender.sendMessage(MsgManager.get("playernotinthisguild"));
            return;
        }
        if (guild.getLeader().equals(op.getUniqueId())) {
            sender.sendMessage(MsgManager.get("cantkickleader", "You cant kick yourself from your own guild!"));
            return;
        }

        GuildKickEvent event = new GuildKickEvent(guild, op);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }


        guild.removeMember(op.getUniqueId());

        guilds.updatePlayerMetadata(op.getUniqueId(), PlayerMetadataController.PlayerMetaDataColumn.GUILD.name(), "");
        if (op.isOnline()) {
            op.getPlayer().sendMessage(MsgManager.get("playerkicked").replace("{GUILD}", guild.getName()));
            tagManager.playerLeaveGuild(((Player) op), guild);
        }

        for (UUID member : guild.getMembers()) {
            OfflinePlayer opp = Bukkit.getOfflinePlayer(member);
            if (opp.isOnline()) {
                opp.getPlayer().sendMessage(MsgManager.get("broadcast-kick").replace("{PLAYER}", player.getDisplayName()).replace("{MEMBER}", op.getName()).replace("{TAG}", guild.getName().toUpperCase()));
            }
        }

        sqlHandler.updatePlayerTag(op.getUniqueId(), "");
        tagManager.playerLeaveGuild(player, guild);
        player.sendMessage(MsgManager.get("playerkicksuccess"));
    }

    @Override
    public int minArgs() {
        return 2;
    }

}
