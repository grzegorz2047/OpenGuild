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

import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.event.guild.GuildRelationEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Guilds;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * @author Grzegorz
 */
public class GuildAllyCommand extends Command {
    public GuildAllyCommand() {
        setPermission("openguild.command.ally");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        Guilds guilds = getPlugin().getGuilds();

        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return;
        }
        Player player = (Player) sender;
        if (args.length >= 2) {
            String guildToCheck = args[1].toUpperCase();
            if (!guilds.doesGuildExists(guildToCheck)) {
                sender.sendMessage(MsgManager.get("guilddoesntexists"));
                return;
            }
            Guild requestingGuild = guilds.getPlayerGuild(player.getUniqueId());
            if (!requestingGuild.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(MsgManager.get("playernotleader"));
                return;
            }
            Guild guild = guilds.getGuild(guildToCheck);
            OfflinePlayer leader = Bukkit.getOfflinePlayer(guild.getLeader());

            if (guild.getTag().equals(requestingGuild.getTag())) {
                sender.sendMessage(MsgManager.get("allyyourselferror"));
                return;
            }

            if (!leader.isOnline()) {
                sender.sendMessage(MsgManager.get("leadernotonline"));
                return;
            }
            if (guild.getPendingRelationChanges().contains(requestingGuild.getTag())) {
                if (!requestingGuild.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(MsgManager.get("playernotleader"));
                    return;
                }

                GuildRelationEvent event = new GuildRelationEvent(requestingGuild, guild, Relation.Status.ALLY);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }

                guild.getPendingRelationChanges().remove(requestingGuild.getTag());
                Relation r = new Relation(guild.getTag(), requestingGuild.getTag(), 0, Relation.Status.ALLY);
                boolean result = OpenGuild.getInstance().getSQLHandler().insertAlliance(guild, requestingGuild);
                if (!result) {
                    this.getPlugin();
                    OpenGuild.getOGLogger().warning("Could not register the ally for " + guild.getTag() + " guild!");
                }
                OpenGuild.getInstance().getTagManager().guildMakeAlliance(r);
                guild.getAlliances().add(r);
                requestingGuild.getAlliances().add(r);
                Bukkit.broadcastMessage(MsgManager.get("broadcast-ally")
                        .replace("{GUILD1}", guild.getTag())
                        .replace("{GUILD2}", requestingGuild.getTag()));
                return;
            }
            requestingGuild.changeRelationRequest(requestingGuild, guild, leader, Relation.Status.ALLY);
        } else {
            sender.sendMessage("/g ally <guild>");
        }

    }

    @Override
    public int minArgs() {
        return 1;
    }

}
