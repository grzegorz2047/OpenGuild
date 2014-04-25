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
package pl.grzegorz2047.openguild2047;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;

import org.bukkit.Location;

import pl.grzegorz2047.openguild2047.api.Guild;

public class SimpleGuild implements Guild {

    private String tag;
    private String description;
    private Location home;
    private UUID leader;
    private List<UUID> members;
    private List<UUID> invitedplayers;
    private List<String> allyguilds;
    private List<String> enemyguilds;

    public SimpleGuild(String tag) {
        this.members = new ArrayList<UUID>();
        this.invitedplayers = new ArrayList<UUID>();
        this.allyguilds = new ArrayList<String>();
        this.enemyguilds = new ArrayList<String>();
        this.tag = tag;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public Location getHome() {
        return this.home;
    }

    @Override
    public List<UUID> getInvitedPlayers() {
        return this.invitedplayers;
    }

    @Override
    public UUID getLeader() {
        return this.leader;
    }

    @Override
    public List<UUID> getMembers() {
        return this.members;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setHome(Location home) {
        this.home = home;
    }

    @Override
    public void setInvitedPlayers(List<UUID> invitedPlayers) {
        this.invitedplayers = invitedPlayers;
    }

    @Override
    public void setLeader(UUID leader) {
        this.leader = Bukkit.getOfflinePlayer(leader).getUniqueId();
    }

    @Override
    public void addMember(UUID member) {
        this.members.add(member);

    }

    @Override
    public void removeMember(UUID member) {
        this.members.remove(member);
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean containsMember(UUID member) {
        return this.members.contains(member);
    }

    @Override
    public List<String> getAllyGuilds() {
        return this.allyguilds;
    }

    @Override
    public List<String> getEnemyGuilds() {
        return this.enemyguilds;
    }

    @Override
    public void setMembers(List<UUID> members) {
        this.members = members;
    }

}
