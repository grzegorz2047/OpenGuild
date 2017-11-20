package pl.grzegorz2047.openguild.relations;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class RelationChangeRequestTest {
    private final String requestedGuild = "OMEGA";
    private final UUID requestingLeader = UUID.randomUUID();
    RelationChangeRequest relationChangeRequest = new RelationChangeRequest("ALFA", requestedGuild, requestingLeader, Relation.Status.ALLY, System.currentTimeMillis());

    @Test
    public void isExpired() throws Exception {
        boolean notExpired = relationChangeRequest.isExpired(System.currentTimeMillis());
        boolean isNowExpired = relationChangeRequest.isExpired(System.currentTimeMillis() + 30 * 1000);
        assertEquals(false, notExpired);
        assertEquals(true, isNowExpired);
    }

    @Test
    public void getGuildName() throws Exception {
        String guildName = relationChangeRequest.getGuildName();
        assertEquals(requestedGuild, guildName);
    }

    @Test
    public void getStatus() throws Exception {
        assertEquals(Relation.Status.ALLY, relationChangeRequest.getStatus());
    }

    @Test
    public void getRequestingLeader() throws Exception {
        assertEquals(requestingLeader, relationChangeRequest.getRequestingLeader());
    }

}