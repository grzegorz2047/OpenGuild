package pl.grzegorz2047.openguild.relations;

import java.util.UUID;

/**
 * Created by grzeg on 23.08.2017.
 */
public class RelationChange {

    private final String requestingGuildName;
    private final String guildName;
    private final Relation.Status status;
    private final long requestTime;
    private final int expirationTime = 20;
    private UUID requestingLeader;


    public RelationChange(String requestingGuildName, String guildName, UUID requestingLeader, Relation.Status status, long requestTime) {
        this.requestingGuildName = requestingGuildName;
        this.guildName = guildName;
        this.status = status;
        this.requestTime = requestTime;
        this.requestingLeader = requestingLeader;
    }

    public boolean isExpired(long currentTime) {
        return requestTime + expirationTime * 1000 < currentTime;
    }

    public String getRequestingGuildName() {
        return requestingGuildName;
    }

    public String getGuildName() {
        return guildName;
    }

    public Relation.Status getStatus() {
        return status;
    }

    public UUID getRequestingLeader() {
        return requestingLeader;
    }
}
