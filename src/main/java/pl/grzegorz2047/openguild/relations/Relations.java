package pl.grzegorz2047.openguild.relations;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.managers.MsgManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by grzegorz2047 on 23.08.2017.
 */


public class Relations {

    private final List<RelationChangeRequest> pendingRelationChangeRequests = new ArrayList<>();
    private List<RelationChangeRequest> toDelete = new ArrayList<>();

    public Relation createRelation(String who, String withwho, long expires, Relation.Status relationStatus) {
        return new Relation(who, withwho, expires, relationStatus);
    }

    public void changeRelationRequest(Guild requestingGuild, Guild guild, final OfflinePlayer requestedLeader, Relation.Status status) {
        String requestingGuildName = requestingGuild.getName();
        final String requestingTag = requestingGuildName;
        String requestedGuildName = guild.getName();
        UUID requestingGuildLeader = requestingGuild.getLeader();
        Player requestingPlayer = Bukkit.getPlayer(requestingGuildLeader);

        for (Relation r : requestingGuild.getAlliances()) {
            String alliedGuildTag = r.getAlliedGuildTag();
            String baseGuildTag = r.getBaseGuildTag();
            if (isInRelation(guild, alliedGuildTag, baseGuildTag)) {
                Relation.Status currentState = r.getState();
                if (theSameStatus(status, currentState)) {
                    requestingPlayer.sendMessage(MsgManager.get("allyexists"));
                    return;
                }
            }
        }
        RelationChangeRequest request = getRequest(requestingGuildName, requestedGuildName);
        if (request == null) {
            pendingRelationChangeRequests.add(new RelationChangeRequest(requestingGuildName, requestedGuildName, requestingGuildLeader, status, System.currentTimeMillis()));
            requestingPlayer.sendMessage(MsgManager.get("sentallyrequest"));
            if (requestedLeader.isOnline()) {
                UUID requestedLeaderUniqueId = requestedLeader.getUniqueId();
                Player requestedPlayer = Bukkit.getPlayer(requestedLeaderUniqueId);
                requestedPlayer.sendMessage(MsgManager.get("sentallyrequestfrom").replace("{GUILD}", requestingGuildName));
                requestedPlayer.sendMessage(MsgManager.get("toacceptallymsg").replace("{GUILD}", requestingGuildName));
            } else {
                requestingPlayer.sendMessage(MsgManager.get("sentallyrequestfrom").replace("{GUILD}", requestedGuildName));
            }
        } else {
            requestingPlayer.sendMessage(MsgManager.get("").replace("{GUILD}", requestingTag));
        }
    }

    private boolean theSameStatus(Relation.Status status, Relation.Status currentState) {
        return currentState.equals(status);
    }

    private boolean isInRelation(Guild guild, String alliedGuildTag, String baseGuildTag) {
        return alliedGuildTag.equals(guild.getName()) || baseGuildTag.equals(guild.getName());
    }

    public void checkGuildPendingRelations() {
        HashMap<UUID, RelationChangeRequest> playersToInform = new HashMap<>();
        for (RelationChangeRequest relationChangeRequest : pendingRelationChangeRequests) {
            if (relationChangeRequest.isExpired(System.currentTimeMillis())) {
                this.toDelete.add(relationChangeRequest);
                playersToInform.put(relationChangeRequest.getRequestingLeader(), relationChangeRequest);
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = p.getUniqueId();
            if (playersToInform.containsKey(playerUUID)) {
                RelationChangeRequest relationChangeRequest = playersToInform.get(playerUUID);
                p.sendMessage(MsgManager.get("allyrequestexpired").replace("{GUILD}", relationChangeRequest.getGuildName()));
            }
        }
        pendingRelationChangeRequests.removeAll(toDelete);
        playersToInform.clear();
    }

    public RelationChangeRequest getRequest(String requestingGuildName, String guildName) {
        for (RelationChangeRequest relationChangeRequest : pendingRelationChangeRequests) {
            if (relationChangeRequest.getRequestingGuildName().equals(requestingGuildName) && relationChangeRequest.getGuildName().equals(guildName)) {
                return relationChangeRequest;
            }
        }
        return null;
    }

    public void removeRequest(RelationChangeRequest request) {
        toDelete.add(request);
    }
}
