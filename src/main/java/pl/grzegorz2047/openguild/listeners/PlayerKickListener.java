package pl.grzegorz2047.openguild.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.teleporters.Teleporter;
import pl.grzegorz2047.openguild.teleporters.TpaRequester;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;

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
        cuboids.clearCuboidEnterNotification(player);
        teleporter.removeRequest(uuid);
        tpaRequester.removeRequest(player.getName());
    }


}
