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

package pl.grzegorz2047.openguild2047.modules.spawn;

import com.github.grzegorz2047.openguild.event.guild.GuildCreateEvent;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.grzegorz2047.openguild2047.GenConf;

public class SpawnListeners implements Listener {
    
    private static final HashMap<UUID, Long> blocked = new HashMap<UUID, Long>();
    
    @EventHandler
    public void onGuildCreate(GuildCreateEvent e) {
        if(GenConf.blockGuildCreating && SpawnChecker.isSpawnExtra(e.getHome())) {
            e.setCancelled(true);
            e.getOwner().sendMessage(GenConf.prefix + ChatColor.RED + GenConf.spawnMessage);
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
