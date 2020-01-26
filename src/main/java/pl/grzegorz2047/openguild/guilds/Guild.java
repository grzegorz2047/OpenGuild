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
package pl.grzegorz2047.openguild.guilds;

import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.relations.Relation;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import pl.grzegorz2047.openguild.relations.Relations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Guild {

    private String tag;
    private String description;
    private List<Relation> alliances = new ArrayList<>();
    private Location home;
    private UUID leader;

    private final List<UUID> members = new ArrayList<>();
    private List<String> membersNames = new ArrayList<>();

    public Guild(String tag, String description, Location home, UUID leaderUUID) {

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

    public void notifyGuild(String msg) {
        for (UUID mem : this.getMembers()) {
            OfflinePlayer om = Bukkit.getOfflinePlayer(mem);
            if (om.isOnline()) {
                notifyPlayer(om, msg);
            }
        }
    }

    public void notifyOtherGuildMembers(String msg, Player player) {
        for (UUID mem : this.getMembers()) {
            if (mem.equals(player.getUniqueId())) {
                continue;
            }
            OfflinePlayer om = Bukkit.getOfflinePlayer(mem);
            if (om.isOnline()) {
                notifyPlayer(om, msg);
            }
        }
    }

    private void notifyPlayer(OfflinePlayer om, String msg) {
        om.getPlayer().sendMessage(msg);
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

    public void addAlly(String withwho, long expires, Relation.Status relationStatus) {
        Relations r = new Relations();
        Relation relation = r.createRelation(tag, withwho, expires, relationStatus);
        getAlliances().add(relation);
    }

    public List<Relation> getAlliances() {
        return alliances;
    }

    public boolean isAlly(Guild g) {
        if (g == null) {
            return false;
        }
        for (Relation r : this.getAlliances()) {
            if (!r.getState().equals(Relation.Status.ALLY)) {
                continue;
            }
            if (r.
                    getAlliedGuildTag()
                    .equals(
                            g.
                                    getName()) || r.
                    getBaseGuildTag().
                    equals(g.
                            getName())) {
                return true;
            }
        }
        return false;
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


    public void addMember(UUID member) {
        if (!isMember(member)) {
            members.add(member);
            membersNames.add(Bukkit.getOfflinePlayer(member).getName());
        }
    }

    public void removeMember(UUID member) {
        if (isMember(member)) {
            members.remove(member);
            membersNames.remove(Bukkit.getOfflinePlayer(member).getName());
        }
    }

    public boolean isLeader(UUID playerUUID) {
        return leader.equals(playerUUID);
    }

    public List<String> getMemberNames() {
        return membersNames;
    }

    public boolean isMember(UUID uniqueId) {
        return members.contains(uniqueId);
    }
}
