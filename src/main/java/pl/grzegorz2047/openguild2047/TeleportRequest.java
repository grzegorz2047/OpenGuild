package pl.grzegorz2047.openguild2047;

import org.bukkit.Location;

import java.util.UUID;

public class TeleportRequest {

    private Location destination;
    private Location source;
    private UUID requester;
    private Long teleportTime;

    public TeleportRequest(UUID username, Location source, Location destination, int delay) {
        this.requester = username;
        this.teleportTime = System.currentTimeMillis() + 1000 * delay;
        this.destination = destination;
        this.source = source;
    }

    public Location getDestination() {
        return destination;
    }

    public UUID getRequester() {
        return requester;
    }

    public Long getTeleportTime() {
        return teleportTime;
    }

    public Location getSource() {
        return source;
    }

}