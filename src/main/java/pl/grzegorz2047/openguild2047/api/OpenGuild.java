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

package pl.grzegorz2047.openguild2047.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;

public class OpenGuild implements com.github.grzegorz2047.openguild.Guild {
    
    private List<String> ally;
    private List<String> enemy;
    private List<UUID> invited;
    private List<UUID> members;
    private String tag;
    private String desc;
    private Location home;
    private UUID leader;
    private double points;
    
    public OpenGuild(String tag, String desc, Location home, UUID leader) {
        this.tag = tag;
        this.desc = desc;
        this.home = home;
        this.leader = leader;
        this.ally = new ArrayList<String>();
        this.enemy = new ArrayList<String>();
        this.invited = new ArrayList<UUID>();
        this.members = new ArrayList<UUID>();
    }
    
    @Override
    public List<String> getAllyGuilds() {
        return ally;
    }
    
    @Override
    public String getDescription() {
        return desc;
    }
    
    @Override
    public List<String> getEnemyGuilds() {
        return enemy;
    }
    
    @Override
    public Location getHome() {
        return home;
    }
    
    @Override
    public List<UUID> getInvitedPlayers() {
        return invited;
    }
    
    @Override
    public UUID getLeader() {
        return leader;
    }
    
    @Override
    public List<UUID> getMembers() {
        return members;
    }
    
    @Override
    public double getPoints() {
        if(points == 0) {
            loadPoints();
        }
        return points;
    }
    
    @Override
    public String getTag() {
        return tag;
    }
    
    @Override
    public void reloadPoints() {
        loadPoints();
    }
    
    @Override
    public void setDesciption(String description) {
        this.desc = description;
    }
    
    @Override
    public void setHome(Location home) {
        this.home = home;
    }
    
    @Override
    public void setInvitedPlayers(List<UUID> invited) {
        this.invited = invited;
    }
    
    @Override
    public void setLeader(UUID leader) {
        this.leader = leader;
    }
    
    @Override
    public void setMembers(List<UUID> members) {
        this.members = members;
    }
    
    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }
    
    private void loadPoints() {
        points = 0;
        double memPoints = 0;
        for(UUID member : getMembers()) {
            memPoints = memPoints + com.github.grzegorz2047.openguild.OpenGuild.getUser(member).getKD();
        }
        points = memPoints / getMembers().size();
    }
    
}
