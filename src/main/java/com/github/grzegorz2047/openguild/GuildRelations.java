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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class GuildRelations {
    
    protected final pl.grzegorz2047.openguild2047.OpenGuild plugin;
    private Guild guild;

    private List<Relation> alliances = new ArrayList<Relation>();
    private List<Relation> enemies = new ArrayList<Relation>();
    private final List<String> pendingRelationChanges = new ArrayList<String>();
    
    public GuildRelations(pl.grzegorz2047.openguild2047.OpenGuild plugin) {
        this.plugin = plugin;
    }

    protected void setRelationsGuild(Guild guild) {
        this.guild = guild;
    }

    public List<String> getPendingRelationChanges() {
        return pendingRelationChanges;
    }

    public void setAlliances(List<Relation> alliances) {
        this.alliances = alliances;
    }

    public List<Relation> getAlliances() {
        return alliances;
    }

    public void setEnemies(List<Relation> enemies) {
        this.enemies = enemies;
    }

    public List<Relation> getEnemies() {
        return enemies;
    }
    public boolean isAlly(Guild g){
        for(Relation r : this.getAlliances()){
            if(!r.getState().equals(Relation.Status.ALLY)){
                continue;
            }
            if(r.getWithWho().equals(g.getTag()) || r.getWho().equals(g.getTag())){
                return true;
            }
        }
        return false;
    }
    
    public void changeRelationRequest(Guild requestingGuild , Guild guild, final OfflinePlayer requestedLeader, Relation.Status status) {
        final String tag = guild.getTag();
        final String requestingTag = requestingGuild.getTag();
        final UUID requestingLeader = requestingGuild.getLeader();
        for(Relation r : requestingGuild.getAlliances()){
            if(r.getWithWho().equals(guild.getTag()) || r.getWho().equals(guild.getTag())){
                if(r.getState().equals(status)){
                    Bukkit.getPlayer(requestingGuild.getLeader()).sendMessage(MsgManager.get("allyexists"));
                    return;
                }
            }
        }
        if(!pendingRelationChanges.contains(tag)) {
            pendingRelationChanges.add(tag);
            Bukkit.getPlayer(requestingGuild.getLeader()).sendMessage(MsgManager.get("sentallyrequest"));
            if(requestedLeader.isOnline()){
                Bukkit.getPlayer(requestedLeader.getUniqueId()).sendMessage(MsgManager.get("sentallyrequestfrom").replace("{GUILD}", requestingGuild.getTag()));
                Bukkit.getPlayer(requestedLeader.getUniqueId()).sendMessage(MsgManager.get("toacceptallymsg").replace("{GUILD}", requestingGuild.getTag()));
            }else{
                Bukkit.getPlayer(requestingGuild.getLeader()).sendMessage(MsgManager.get("sentallyrequestfrom").replace("{GUILD}", guild.getTag()));
                return;
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(pendingRelationChanges.contains(tag)) {
                        pendingRelationChanges.remove(tag);
                        if(requestedLeader.isOnline()){
                            Bukkit.getPlayer(requestedLeader.getUniqueId()).sendMessage(MsgManager.get("allyrequestexpired").replace("{GUILD}", requestingTag));                            
                        }
                        if(Bukkit.getPlayer(requestingLeader).isOnline()){
                            Bukkit.getPlayer(requestingLeader).sendMessage(MsgManager.get("allyrequestexpired").replace("{GUILD}", requestingTag));                            
                        }
                        
                    }
                }
            }.runTaskLater(this.plugin, 20L * 25);
        }else{
            Bukkit.getPlayer(requestingGuild.getLeader()).sendMessage("Juz wyslales prosbe o sojusz do "+requestingTag);
        }
    }
}
