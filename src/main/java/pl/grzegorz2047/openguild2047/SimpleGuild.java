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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.grzegorz2047.openguild2047;

import java.util.List;

import org.bukkit.Location;

import pl.grzegorz2047.openguild2047.api.Guild;

public class SimpleGuild implements Guild {

    private String tag;
    private String description;
    private Location home;
    private String leader;
    private List<String> members;
    private List<String> invitedplayers;
    private List<String> allyguilds;
    private List<String> enemyguilds;
    
    public SimpleGuild(String tag) {
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
    public List<String> getInvitedPlayers() {
        return this.invitedplayers;
    }

    @Override
    public String getLeader() {
        return this.leader;
    }

    @Override
    public List<String> getMembers() {
        return this.members;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public void setDescription(String description) {
        this.description=description;
    }

    @Override
    public void setHome(Location home) {
        this.home=home;
    }

    @Override
    public void setInvitedPlayers(List<String> invitedPlayers) {

    }

    @Override
    public void setLeader(String leader) {
        this.leader=leader;
    }

    @Override
    public void addMember(String member) {
        if(!this.members.contains(member)){
            this.members.add(member);
        }

    }
    @Override
    public void removeMember(String member) {
        if(this.members.contains(member)){
            this.members.remove(member);
        }
    }

    @Override
    public void setTag(String tag) {
        this.tag=tag;
    }

    @Override
    public boolean containsMember(String member) {
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
	
}
