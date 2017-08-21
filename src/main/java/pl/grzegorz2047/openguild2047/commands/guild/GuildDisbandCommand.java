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
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used by guild's leaders to disband their guild.
 * <p>
 * Usage: /guild disband
 */
public class GuildDisbandCommand extends Command {
    public GuildDisbandCommand() {
        setPermission("openguild.command.disband");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }

        Guilds guilds = this.getPlugin().getGuilds();

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
                if (Bukkit.getPlayer(uuid) != null) {
                    Bukkit.getPlayer(uuid).setScoreboard(OpenGuild.getInstance().getTagManager().getGlobalScoreboard());
                }
            }
            getPlugin().getCuboids().removeGuildCuboid(guild.getName());
            getPlugin().getSQLHandler().removeGuild(guild.getName().toUpperCase());
            guilds.getGuilds().remove(guild.getName());
            getPlugin().getTagManager().playerDisbandGuild(guild);
            guilds.removeOnlineGuild(guild.getName());
            getPlugin().broadcastMessage(MsgManager.get("broadcast-disband").replace("{TAG}", guild.getName().toUpperCase()).replace("{PLAYER}", player.getDisplayName()));

        }

    }

    @Override
    public int minArgs() {
        return 1;
    }

}
