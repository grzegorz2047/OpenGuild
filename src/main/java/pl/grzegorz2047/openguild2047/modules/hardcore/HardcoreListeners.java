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

package pl.grzegorz2047.openguild2047.modules.hardcore;

import com.github.grzegorz2047.openguild.OpenGuild;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.grzegorz2047.openguild2047.GenConf;

public class HardcoreListeners implements Listener {
    
    @EventHandler
    public void onPlayerDead(PlayerDeathEvent e) {
        if(!GenConf.hcBans) {
            return;
        }
        
        if(GenConf.hcLightning) {
            e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
        }
        
        long ban = System.currentTimeMillis() + GenConf.hcBantime;
        HardcoreSQLHandler.update(e.getEntity().getUniqueId(), HardcoreSQLHandler.Column.BAN_TIME, String.valueOf(ban));
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if(!GenConf.hcBans) {
            return;
        }
        if(e.getPlayer().hasPermission("openguild.hardcore.bypass")) {
            return;
        }
        
        long ban = HardcoreSQLHandler.getBan(e.getPlayer().getUniqueId());
        if(System.currentTimeMillis() < ban) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            Date date = new Date(ban);
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, GenConf.hcLoginMsg.replace("%TIME", dateFormat.format(date)));
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        if(!GenConf.hcBans) {
            return;
        }
        
        final Player player = e.getPlayer();
        HardcoreSQLHandler.getBan(player.getUniqueId()); // We must insert this player into the hardcore-table.
        final String time = new SimpleDateFormat("dd-MM-yyy HH:mm").format(new Date(System.currentTimeMillis() + GenConf.hcBantime));
        Bukkit.getScheduler().runTaskLater(OpenGuild.getBukkit(), new Runnable() {
            
            @Override
            public void run() {
                if (player != null) { // If the player logout and the Player object is null, this scheduler will do nothing
                    player.kickPlayer(GenConf.hcLoginMsg.replace("%TIME", time));
                }
            }
        }, 5 * 20L); // 5 seconds to say "Good bye"
    }
    
}