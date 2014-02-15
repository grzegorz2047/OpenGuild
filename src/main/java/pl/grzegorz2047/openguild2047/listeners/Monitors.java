package pl.grzegorz2047.openguild2047.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import pl.grzegorz2047.openguild2047.handlers.MySQLHandler;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler.PType;

public class Monitors implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e) {
		MySQLHandler.update(e.getEntity().getName(), PType.DEADS, (Integer) MySQLHandler.select(e.getEntity().getName()).get(5) + 1);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(!e.getPlayer().hasPlayedBefore()) {
			MySQLHandler.insert(e.getPlayer().getName(), null, 0, 0);
		}
	}
	
}
