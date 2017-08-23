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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Guild extends GuildMembers {

    private String tag;
    private String description;

    private Location home;

    public Guild(pl.grzegorz2047.openguild2047.OpenGuild plugin, String tag, String description, Location home, UUID leaderUUID, Scoreboard scoreboard) {
        super(plugin);
        this.setMembersGuild(this);
        this.tag = tag;
        this.description = description;
        this.home = home;
        this.leader = leaderUUID;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName() {
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

    public void notifyGuildThatMemberLeft(Player player) {
        String msg = MsgManager.get("guildmemberleft");
        notifyGuild(msg.replace("{PLAYER}", player.getDisplayName()));
    }

    public void notifyGuild(String msg) {
        for (UUID mem : this.getMembers()) {
            OfflinePlayer om = Bukkit.getOfflinePlayer(mem);
            if (om.isOnline()) {
                notifyPlayer(om, msg);
            }
        }
    }

    public void notifyMembersAboutSomeoneEnteringTheirCuboid(Player player, String tag, boolean foundCuboid) {
        for (UUID mem : getMembers()) {
            OfflinePlayer op = plugin.getServer().getOfflinePlayer(mem);
            if (op.isOnline()) {
                notifySomeoneEnteredCuboid(player, tag, op, foundCuboid);
                playSoundOnSomeoneEnteredCuboid(op);
            }
        }
    }

    private void playSoundOnSomeoneEnteredCuboid(OfflinePlayer op) {
        if (GenConf.cubNotifySound) {
            op.getPlayer().playSound(op.getPlayer().getLocation(), GenConf.cubNotifySoundType, 10f, 5f);
        }
    }

    private void notifySomeoneEnteredCuboid(Player player, String tag, OfflinePlayer op, boolean foundCuboid) {
        if (GenConf.cubNotifyMem) {
            if (!foundCuboid) {

                op.getPlayer().sendMessage(MsgManager.get("entercubmemsnoguild").
                        replace("{PLAYER}", player.getName()));
            } else {
                op.getPlayer().sendMessage(MsgManager.get("entercubmems").
                        replace("{PLAYER}", player.getName()).
                        replace("{GUILD}", tag.toUpperCase()));
            }

        }
    }


    public void notifyMembersJoinedGame(Player player) {
        String msg = MsgManager.get("guildmemberjoined");
        notifyGuild(msg.replace("{PLAYER}", player.getDisplayName()));
    }

    private void notifyPlayer(OfflinePlayer om, String msg) {
        om.getPlayer().sendMessage(msg);
    }

    public List<Guild> getAllyGuilds() {
        List<Guild> allies = new ArrayList<>();
        for (Relation r : getAlliances()) {
            String alliedGuildTag = r.getAlliedGuildTag();
            if (r.getAlliedGuildTag().equals(this.tag)) {
                alliedGuildTag = r.getWho();
            }
            Guild allyGuild = plugin.getGuilds().getGuild(alliedGuildTag);
            allies.add(allyGuild);
        }
        return allies;
    }

    public List<String> getOnlineMembers() {
        List<String> onlineMemebers = new ArrayList<>();
        for (UUID mem : this.getMembers()) {
            OfflinePlayer om = Bukkit.getOfflinePlayer(mem);
            if (om.isOnline()) {
                onlineMemebers.add(om.getName());
            }
        }
        return onlineMemebers;
    }
    private Relation createRelation(String who, String withwho, long expires, Relation.Status relationStatus) {
        return new Relation(who, withwho, expires, relationStatus);
    }
    public void addAlly(String withwho, long expires, Relation.Status relationStatus) {
        Relation relation = createRelation(tag, withwho, expires, relationStatus);
        getAlliances().add(relation);
    }
}
