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
package pl.grzegorz2047.openguild2047.listeners;

import java.util.HashMap;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.OpenGuildPlugin;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.EnhancedRunnable;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class TNTExplode implements Listener {
    
    private final OpenGuildPlugin plugin;
    private final Map<String, Integer> blockedGuilds = new HashMap<String, Integer>();
    
    public TNTExplode(OpenGuildPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void handle(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        
        if(player.hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        
        Guild guild = plugin.getGuild(event.getBlock().getLocation());
        if(guild != null) {
            if(blockedGuilds.containsKey(guild.getTag())) {
                player.sendMessage(MsgManager.get("tntex").replace("{SEC}", String.valueOf(blockedGuilds.get(guild.getTag()))));
                event.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if(event.getEntityType().equals(EntityType.PRIMED_TNT)) {
            Location location = event.getLocation();
            final Guild guild = plugin.getGuild(location);
            
            if(guild != null) {
                if(!blockedGuilds.containsKey(guild.getTag())) {
                    EnhancedRunnable.startTask(plugin.getBukkit(), new EnhancedRunnable(){
                        /**
                         * Time left to 'free' guild.
                         */
                        private int blockTime = 30;
                        
                        @Override
                        public void run() {
                            blockTime = blockedGuilds.get(guild.getTag());
                            
                            if(blockTime == 0) {
                                blockedGuilds.remove(guild.getTag());
                                this.stopTask();
                                return;
                            }
                            
                            blockedGuilds.put(guild.getTag(), blockTime--);
                        }
                    }, 5L, 20L);
                }
                
                blockedGuilds.put(guild.getTag(), 30);
            }
        }
    }
    
}