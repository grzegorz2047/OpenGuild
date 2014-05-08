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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerRespawnEvent;

import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler;

public class Hardcore implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        if(!GenConf.hcBans) return;
        if(e.getPlayer().hasPermission("openguild.hardcore.bypass")) return;
        
        long ban = MySQLHandler.getBan(e.getPlayer().getUniqueId());
        long timebanned = System.currentTimeMillis() - ban;
        if( timebanned < GenConf.hcBantime ) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            long remain = GenConf.hcBantime - timebanned;
            Date date = new Date(remain+System.currentTimeMillis());
            e.disallow(Result.KICK_OTHER, GenConf.hcLoginMsg.replace("%TIME", dateFormat.format(date)));
        }
    }

    @EventHandler
    public void onPlayerDead(PlayerDeathEvent e) {
        if(!GenConf.hcBans) return;
        if(e.getEntity().hasPermission("openguild.hardcore.bypass")) return;
        
        long ban = MySQLHandler.getBan(e.getEntity().getUniqueId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        Date date = new Date(ban);
        MySQLHandler.update(e.getEntity().getUniqueId(), MySQLHandler.PType.BAN_TIME, System.currentTimeMillis()+GenConf.hcBantime);
        e.getEntity().kickPlayer(GenConf.hcLoginMsg.replace("%TIME", dateFormat.format(date)));
    }

}
