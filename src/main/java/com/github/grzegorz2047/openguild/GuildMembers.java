/*
 * The MIT License
 *
 * Copyright 2014 Aleksander.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.grzegorz2047.openguild;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class GuildMembers {

    protected final pl.grzegorz2047.openguild2047.OpenGuild plugin;
    private Guild guild;

    private UUID leader;

    private final List<UUID> members = new ArrayList<UUID>();
    private final List<UUID> pendingInvitations = new ArrayList<UUID>();

    public GuildMembers(pl.grzegorz2047.openguild2047.OpenGuild plugin) {
        this.plugin = plugin;
    }

    protected void setGuild(Guild guild) {
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
            this.plugin.getSQLHandler().updatePlayer(player.getUniqueId());
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
                        player.sendMessage(MsgManager.get("guild-invitation-expired"));
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
