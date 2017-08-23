package pl.grzegorz2047.openguild2047.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.teleporters.Teleporter;
import pl.grzegorz2047.openguild2047.teleporters.TpaRequester;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;

import java.util.UUID;

public class PlayerKickListener implements Listener {


    private final TpaRequester tpaRequester;
    private final Guilds guilds;
    private Cuboids cuboids;
    private Teleporter teleporter;

    public PlayerKickListener(Teleporter teleporter, Cuboids cuboids, TpaRequester tpaRequester, Guilds guilds) {
        this.teleporter = teleporter;
        this.cuboids = cuboids;
        this.tpaRequester = tpaRequester;
        this.guilds = guilds;
    }

    @EventHandler
    private void onPlayerKick(PlayerKickEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (guilds.isPlayerInGuild(player)) {
            guilds.guildMemberLeftServer(player, uuid);
        }
        cuboids.clearCuboidEnterNotification(e.getPlayer());
        teleporter.removeRequest(e.getPlayer().getUniqueId());
        tpaRequester.removeRequest(e.getPlayer().getName());
    }


}
