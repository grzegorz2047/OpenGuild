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

package pl.grzegorz2047.openguild2047.modules.hardcore;

import com.github.grzegorz2047.openguild.OpenGuild;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.database.SQLHandler;

public class HardcoreListeners implements Listener {
    
    @EventHandler
    public void onPlayerDead(PlayerDeathEvent e) {
        if(!GenConf.hcBans) {
            return;
        }
        
        if(GenConf.hcLightninh) {
            e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
        }
        
        long ban = System.currentTimeMillis() + GenConf.hcBantime;
        SQLHandler.update(e.getEntity().getUniqueId(), SQLHandler.PType.BAN_TIME, ban);
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if(!GenConf.hcBans) {
            return;
        }
        if(e.getPlayer().hasPermission("openguild.hardcore.bypass")) {
            return;
        }
        
        long ban = SQLHandler.getBan(e.getPlayer().getUniqueId());
        if(System.currentTimeMillis() < ban) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy HH:mm");
            Date date = new Date(ban);
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, GenConf.hcLoginMsg.replace("%TIME", dateFormat.format(date)));
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent e) {
        if(!GenConf.hcBans) {
            return;
        }
        
        final long ban = SQLHandler.getBan(e.getPlayer().getUniqueId());
        if(ban == 0) {
            return;
        }
        
        Bukkit.getScheduler().runTaskLater(OpenGuild.getBukkit(), new Runnable() {
            
            @Override
            public void run() {
                SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy HH:mm");
                Date date = new Date(ban);
                e.getPlayer().kickPlayer(GenConf.hcLoginMsg.replace("%TIME", dateFormat.format(date)));
            }
        }, 5 * 20L); // 5 seconds to say "Good bye"
    }
    
}