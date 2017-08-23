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

import pl.grzegorz2047.openguild2047.guilds.Guild;
import pl.grzegorz2047.openguild2047.relations.Relation;
import pl.grzegorz2047.openguild2047.commands.command.Command;
import pl.grzegorz2047.openguild2047.commands.command.CommandException;
import pl.grzegorz2047.openguild2047.events.guild.GuildRelationEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * @author Grzegorz
 */
public class GuildEnemyCommand extends Command {
    public GuildEnemyCommand() {
        setPermission("openguild.command.enemy");
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

            if (!leader.isOnline()) {
                sender.sendMessage(MsgManager.get("leadernotonline"));
                return;
            }
            if (guild.getName().equals(requestingGuild.getName())) {
                sender.sendMessage(MsgManager.get("enemyyourselferror"));
                return;
            }

            GuildRelationEvent event = new GuildRelationEvent(requestingGuild, guild, Relation.Status.ENEMY);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            for (Relation r : requestingGuild.getAlliances()) {
                if (r.getAlliedGuildTag().equals(guild.getName()) || r.getBaseGuildTag().equals(guild.getName())) {
                    OpenGuild.getInstance().getTagManager().guildBrokeAlliance(requestingGuild, guild);
                    requestingGuild.getAlliances().remove(r);
                    guild.getAlliances().remove(r);
                    OpenGuild.getInstance().getSQLHandler().removeAlliance(requestingGuild, guild);
                    Bukkit.broadcastMessage(MsgManager.getIgnorePref("broadcast-enemy")
                            .replace("{GUILD1}", requestingGuild.getName())
                            .replace("{GUILD2}", guild.getName()));
                    return;
                }
            }
            sender.sendMessage(MsgManager.get("enemynotfound"));
        }
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
