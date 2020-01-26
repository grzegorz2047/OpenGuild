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

import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.managers.TagManager;
import pl.grzegorz2047.openguild.relations.Relation;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.events.guild.GuildRelationEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.relations.RelationChangeRequest;
import pl.grzegorz2047.openguild.relations.Relations;

/**
 * @author Grzegorz
 */
public class GuildAllyCommand extends Command {
    private final Relations relations;
    private final Guilds guilds;
    private final TagManager tagManager;
    private SQLHandler sqlHandler;

    public GuildAllyCommand(String[] aliases, Relations relations, Guilds guilds, TagManager tagManager, SQLHandler sqlHandler) {
        super(aliases);
        setPermission("openguild.command.ally");
        this.relations = relations;
        this.guilds = guilds;
        this.tagManager = tagManager;
        this.sqlHandler = sqlHandler;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            sender.sendMessage("/g ally <guild>");
            return;
        }
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

        if (guild.getName().equals(requestingGuild.getName())) {
            sender.sendMessage(MsgManager.get("allyyourselferror"));
            return;
        }

        if (!leader.isOnline()) {
            sender.sendMessage(MsgManager.get("leadernotonline"));
            return;
        }
        RelationChangeRequest request = relations.getRequest(guild.getName(), requestingGuild.getName());
        if (wasRequestedBefore(request)) {
            acceptChangeREquest(player, requestingGuild, guild, request);
            return;
        }
        createNewRelationChangeRequest(requestingGuild, guild, leader);
    }

    private void createNewRelationChangeRequest(Guild requestingGuild, Guild guild, OfflinePlayer leader) {
        relations.changeRelationRequest(requestingGuild, guild, leader, Relation.Status.ALLY);
    }

    private void acceptChangeREquest(Player player, Guild requestingGuild, Guild guild, RelationChangeRequest request) {
        if (!requestingGuild.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(MsgManager.get("playernotleader"));
            return;
        }

        if (invokeRelationEventCancelled(requestingGuild, guild)) return;

        clearRequest(request);
        addRelationData(requestingGuild, guild);
        broadcastRelationChangeToAll(requestingGuild, guild);
    }

    private boolean wasRequestedBefore(RelationChangeRequest request) {
        return request != null;
    }

    private void clearRequest(RelationChangeRequest request) {
        relations.removeRequest(request);
    }

    private void broadcastRelationChangeToAll(Guild requestingGuild, Guild guild) {
        Bukkit.broadcastMessage(MsgManager.get("broadcast-ally")
                .replace("{GUILD1}", guild.getName())
                .replace("{GUILD2}", requestingGuild.getName()));
    }

    private void addRelationData(Guild requestingGuild, Guild guild) {
        Relation r = new Relation(guild.getName(), requestingGuild.getName(), 0, Relation.Status.ALLY);
        boolean result = sqlHandler.insertAlliance(guild, requestingGuild);
        if (!result) {
            OpenGuild.getOGLogger().warning("Could not register the ally for " + guild.getName() + " guild!");
        }
        guilds.guildMakeAlliance(guilds.getGuild(r.getBaseGuildTag()), guilds.getGuild(r.getAlliedGuildTag()));
        guild.getAlliances().add(r);
        requestingGuild.getAlliances().add(r);
    }

    private boolean invokeRelationEventCancelled(Guild requestingGuild, Guild guild) {
        GuildRelationEvent event = new GuildRelationEvent(requestingGuild, guild, Relation.Status.ALLY);
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
