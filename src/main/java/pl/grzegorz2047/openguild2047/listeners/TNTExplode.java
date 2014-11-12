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

package pl.grzegorz2047.openguild2047.listeners;

import com.github.grzegorz2047.openguild.OpenGuild;
import java.util.HashMap;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import com.github.grzegorz2047.openguild.Guild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class TNTExplode implements Listener {
    
    private static final HashMap<String, Long> blocked = new HashMap<String, Long>();
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getPlayer().hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        Guild guild = OpenGuild.getGuild(e.getBlock().getLocation());
        if(guild != null && blocked.containsKey(guild.getTag())) {
            long end = blocked.get(guild.getTag()) + 100 * 30;
            if(end > System.currentTimeMillis()) {
                e.setCancelled(true);
                long seconds = (System.currentTimeMillis() - end) / 100;
                e.getPlayer().sendMessage(MsgManager.get("tntex").replace("{SEC}", String.valueOf(seconds)));
            }
        }
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if(e.getEntityType() == EntityType.PRIMED_TNT) {
            Guild guild = null;
            if(guild != null) {
                blocked.put(guild.getTag(), System.currentTimeMillis());
            }
        }
    }
    
}