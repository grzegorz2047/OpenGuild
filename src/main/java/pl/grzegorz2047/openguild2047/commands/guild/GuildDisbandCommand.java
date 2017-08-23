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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Guilds;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.event.guild.GuildDisbandEvent;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.managers.TagManager;

/**
 * Command used by guild's leaders to disband their guild.
 * <p>
 * Usage: /guild disband
 */
public class GuildDisbandCommand extends Command {
    private final Guilds guilds;
    private final Cuboids cuboids;
    private final SQLHandler sqlHandler;
    private final TagManager tagManager;

    public GuildDisbandCommand(Guilds guilds, Cuboids cuboids, SQLHandler sqlHandler, TagManager tagManager) {
        setPermission("openguild.command.disband");
        this.guilds = guilds;
        this.cuboids = cuboids;
        this.sqlHandler = sqlHandler;
        this.tagManager = tagManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }


        Player player = (Player) sender;
        if (args.length == 1) {
            if (!guilds.hasGuild(player)) {
                player.sendMessage(MsgManager.get("notinguild"));
                return;
            }

            Guild guild = guilds.getPlayerGuild(player.getUniqueId());
            if (!guild.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(MsgManager.get("playernotleader"));
                return;
            }

            GuildDisbandEvent event = new GuildDisbandEvent(guild);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            for (Relation r : guild.getAlliances()) {
                Guild g1 = guilds.getGuild(r.getWho());
                Guild g2 = guilds.getGuild(r.getAlliedGuildTag());
                OpenGuild.getInstance().getTagManager().guildBrokeAlliance(g1, g2);
                if (!guild.equals(g1)) {
                    g1.getAlliances().remove(r);
                }
                if (!guild.equals(g2)) {
                    g2.getAlliances().remove(r);
                }
                OpenGuild.getInstance().getSQLHandler().removeAlliance(g1, g2);

            }
            for (UUID uuid : guild.getMembers()) {
                guilds.getMappedPlayersToGuilds().remove(uuid);
                guilds.getMappedPlayersToGuilds().put(uuid, null);
                getPlugin().getSQLHandler().updatePlayerTag(uuid, "");
            }
            cuboids.removeGuildCuboid(guild.getName());
            sqlHandler.removeGuild(guild.getName().toUpperCase());
            guilds.getGuilds().remove(guild.getName());
            tagManager.playerDisbandGuild(guild);
            guilds.removeOnlineGuild(guild.getName());
            Bukkit.broadcastMessage(MsgManager.get("broadcast-disband").replace("{TAG}", guild.getName().toUpperCase()).replace("{PLAYER}", player.getDisplayName()));

        }

    }

    @Override
    public int minArgs() {
        return 1;
    }

}
