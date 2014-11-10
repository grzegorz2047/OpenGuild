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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
    
    public void changeRelationRequest(Guild requestingGuild , Guild guild, final OfflinePlayer player, String status) {
        final String tag = guild.getTag();
        final String requestingTag = requestingGuild.getTag();
        if(!pendingRelationChanges.contains(tag)) {
            pendingRelationChanges.add(tag);
            if(player.isOnline()){
                Bukkit.getPlayer(player.getUniqueId()).sendMessage("Guild ally request from "+requestingGuild.getTag());
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(pendingRelationChanges.contains(tag)) {
                        pendingRelationChanges.remove(tag);
                        if(player.isOnline()){
                            Bukkit.getPlayer(player.getUniqueId()).sendMessage("Guild ally request expired from "+requestingTag);                            
                        }
                        
                    }
                }
            }.runTaskLater(this.plugin, 20L * 25);
        }else{
            //already requested
        }
    }
}
