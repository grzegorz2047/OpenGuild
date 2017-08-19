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
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.UUID;

public class Guild extends GuildMembers {

    private String tag;
    private String description;

    private Location home;
    private Scoreboard sc;

    public Guild(pl.grzegorz2047.openguild2047.OpenGuild plugin, String tag, String description, Location home, UUID leaderUUID, Scoreboard scoreboard) {
        super(plugin);
        this.setMembersGuild(this);
        this.tag = tag;
        this.description = description;
        this.home = home;
        this.leader = leaderUUID;
        this.sc = scoreboard;
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


    public Scoreboard getSc() {
        return sc;
    }

    public void setSc(Scoreboard sc) {
        this.sc = sc;
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

    public void notifyMembersJoinedGame(Player player) {
        String msg = MsgManager.get("guildmemberjoined");
        notifyGuild(msg.replace("{PLAYER}", player.getDisplayName()));
    }

    private void notifyPlayer(OfflinePlayer om, String msg) {
        om.getPlayer().sendMessage(msg);
    }
}
