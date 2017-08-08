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
package com.github.grzegorz2047.openguild;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class GuildMembers extends GuildRelations {

    protected final pl.grzegorz2047.openguild2047.OpenGuild plugin;
    private Guild guild;

    private UUID leader;

    private final List<UUID> members = new ArrayList<UUID>();
    private final List<UUID> pendingInvitations = new ArrayList<UUID>();

    public GuildMembers(pl.grzegorz2047.openguild2047.OpenGuild plugin) {
        super(plugin);
        this.plugin = plugin;
        this.setRelationsGuild(guild);
    }

    protected void setMembersGuild(Guild guild) {
        this.guild = guild;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public UUID getLeader() {
        return leader;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public List<UUID> getPendingInvitations() {
        return pendingInvitations;
    }

    public void acceptInvitation(Player player) {
        if(pendingInvitations.contains(player.getUniqueId())) {
            pendingInvitations.remove(player.getUniqueId());

            this.plugin.getGuildHelper().getPlayers().put(player.getUniqueId(), guild);
            this.plugin.getSQLHandler().updatePlayerTag(player.getUniqueId());
            this.members.add(player.getUniqueId());
        }
    }

    public void invitePlayer(final Player player, Player who) {
        final UUID uuid = player.getUniqueId();

        if(!pendingInvitations.contains(uuid)) {
            pendingInvitations.add(uuid);

            player.sendMessage(MsgManager.get("guild-invitation").replace("{WHO}", who.getName()).replace("{TAG}", guild.getTag().toUpperCase()));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(pendingInvitations.contains(uuid)) {
                        pendingInvitations.remove(uuid);
                        player.sendMessage(MsgManager.get("guild-invitation-expired").replace("{TAG}", guild.getTag().toUpperCase()));
                    }
                }
            }.runTaskLater(this.plugin, 20L * 25);
        }
    }

    public void addMember(UUID member) {
        if(!members.contains(member)) {
            members.add(member);
        }
    }

    public void removeMember(UUID member) {
        if(members.contains(member)) {
            members.remove(member);
        }
    }

    public boolean containsMember(UUID member) {
        return members.contains(member);
    }
}
