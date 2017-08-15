package pl.grzegorz2047.openguild2047.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import pl.grzegorz2047.openguild2047.Teleporter;
import pl.grzegorz2047.openguild2047.TpaRequester;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;

public class PlayerKickListener implements Listener {


    private final TpaRequester tpaRequester;
    private Cuboids cuboids;
    private Teleporter teleporter;

    public PlayerKickListener(Teleporter teleporter, Cuboids cuboids, TpaRequester tpaRequester) {
        this.teleporter = teleporter;
        this.cuboids = cuboids;
        this.tpaRequester = tpaRequester;
    }

    @EventHandler
    private void onPlayerKick(PlayerKickEvent e) {
        cuboids.clearCuboidEnterNotification(e.getPlayer());
        teleporter.removeRequest(e.getPlayer().getUniqueId());
        tpaRequester.removeRequest(e.getPlayer().getName());
    }


}
