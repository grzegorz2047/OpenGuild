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

import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.OpenGuild;
import java.util.HashMap;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class TNTExplode implements Listener {
    
    private static HashMap<String, Long> blocked = new HashMap<String, Long>();
    
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
            SimpleGuild guild = null;
            if(guild != null) {
                blocked.put(guild.getTag(), System.currentTimeMillis());
            }
        }
    }
    
}