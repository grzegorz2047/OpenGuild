/**
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.github.grzegorz2047.openguild;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.SimpleCuboid;

public class Guild {

    private final OpenGuild plugin;

    private String tag;
    private String description;

    private Location home;

    private UUID leader;

    private final List<UUID> members = new ArrayList<UUID>();

    private String alliancesString = "";
    private List<Guild> alliances = new ArrayList<Guild>();

    private String enemiesString = "";
    private List<Guild> enemies = new ArrayList<Guild>();

    private final List<UUID> pendingInvitations = new ArrayList<UUID>();

    private SimpleCuboid cuboid;

    public Guild(OpenGuild plugin) {
        this.plugin = plugin;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public Location getHome() {
        return home;
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

    public void setAlliances(List<Guild> alliances) {
        this.alliances = alliances;
    }

    public List<Guild> getAlliances() {
        return alliances;
    }

    public void setEnemies(List<Guild> enemies) {
        this.enemies = enemies;
    }

    public List<Guild> getEnemies() {
        return enemies;
    }

    public List<UUID> getPendingInvitations() {
        return pendingInvitations;
    }

    public void acceptInvitation(Player player) {
        if(pendingInvitations.contains(player.getUniqueId())) {
            pendingInvitations.remove(player.getUniqueId());

            this.plugin.getGuildHelper().getPlayers().put(player.getUniqueId(), this);
            this.plugin.getSQLHandler().updatePlayer(player.getUniqueId());
            this.members.add(player.getUniqueId());
        }
    }

    public void invitePlayer(final Player player, Player who) {
        final UUID uuid = player.getUniqueId();

        if(!pendingInvitations.contains(uuid)) {
            pendingInvitations.add(uuid);

            player.sendMessage(MsgManager.get("guild-invitation").replace("{WHO}", who.getName()).replace("{TAG}", getTag().toUpperCase()));

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

    public void setCuboid(SimpleCuboid cuboid) {
        this.cuboid = cuboid;
    }

    public SimpleCuboid getCuboid() {
        return cuboid;
    }

    public void setAlliancesString(String alliancesString) {
        this.alliancesString = alliancesString;
    }

    public String getAlliancesString() {
        return alliancesString;
    }

    public void setEnemiesString(String enemiesString) {
        this.enemiesString = enemiesString;
    }

    public String getEnemiesString() {
        return enemiesString;
    }

    public boolean containsMember(UUID member) {
        return members.contains(member);
        
    }
}
