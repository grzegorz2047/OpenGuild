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

package pl.grzegorz2047.openguild2047.modules.spawn;

import com.github.grzegorz2047.openguild.event.guild.GuildCreateEvent;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.grzegorz2047.openguild2047.configuration.GenConf;

public class SpawnListeners implements Listener {
    
    private static final HashMap<UUID, Long> blocked = new HashMap<UUID, Long>();
    
    @EventHandler
    public void onGuildCreate(GuildCreateEvent e) {
        if(GenConf.blockGuildCreating && SpawnChecker.isSpawnExtra(e.getHome())) {
            e.setCancelled(true);
            e.getLeader().sendMessage(GenConf.prefix + ChatColor.RED + GenConf.spawnMessage);
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        /*
         * TODO Blokowanie wejscia osobom
         * które wyszly zza spawna na czas
         * podany w configu jako 'block-enter-time'
         */
        
        if(GenConf.blockEnter) {
            if(e.getFrom().getBlock().equals(e.getTo().getBlock())) {
                return;
            }
            Player player = e.getPlayer();
            
            if(SpawnChecker.isSpawn(e.getFrom()) && !SpawnChecker.isSpawn(e.getTo())) {
                blocked.put(player.getUniqueId(), System.currentTimeMillis());
            }
            else if(SpawnChecker.isSpawn(e.getTo()) && !SpawnChecker.isSpawn(e.getFrom())) {
                long ms = 0;
                
                if(blocked.containsKey(player.getUniqueId())) {
                    ms = GenConf.blockEnterTime * 1000 + blocked.get(player.getUniqueId());
                }
                
                if(System.currentTimeMillis() < ms) {
                    player.teleport(e.getFrom());
                    player.sendMessage(ChatColor.RED + "Can not enter!");
                }
            }
        }
    }
    
}
