/**
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
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

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import pl.grzegorz2047.openguild2047.Data;

import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.cuboidmanagement.CuboidStuff;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class CuboidListeners implements Listener {
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if(!isAllowed(e.getPlayer(), e.getBlock().getLocation())) {
			e.setCancelled(true);//Mozna dac tu, ze mozna niszczyc, ale to duzo zabiera wytrzymalosc diamentowego kilofa
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(!isAllowed(e.getPlayer(), e.getBlock().getLocation())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock() != null) {
			if(GenConf.EXTRA_PROTECTION) {
				if(!isAllowed(e.getPlayer(), e.getClickedBlock().getLocation())) {
					e.setCancelled(true);
				}
			}
		}
	}
	
	private boolean isAllowed(Player player, Location location) {
            if(CuboidStuff.checkIfInAnyCuboid(Data.getInstance().cuboids.entrySet().iterator(), location)) {
                if(Data.getInstance().isPlayerInGuild(player.getName())) {
                    String tag = Data.getInstance().getPlayersGuild(player.getName()).getTag();
                    if(Data.getInstance().cuboids.get(tag).isinCuboid(location)) {
                        return true;//Gdzies tu budowanie sojusznikow, ale na razie czarna magia
                    }else{
                        player.sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
                        return false;
                    }   
                }else{
                   return false;
                }
            }else{
                return true;
            }
	}
	
}
