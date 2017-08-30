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

    private final List<RelationChange> pendingRelationChanges = new ArrayList<>();
    private List<RelationChange> toDelete = new ArrayList<>();

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
        RelationChange request = getRequest(requestingGuildName, requestedGuildName);
        if (request == null) {
            pendingRelationChanges.add(new RelationChange(requestingGuildName, requestedGuildName, requestingGuildLeader, status, System.currentTimeMillis()));
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
        HashMap<UUID, RelationChange> playersToInform = new HashMap<>();
        for (RelationChange relationChange : pendingRelationChanges) {
            if (relationChange.isExpired(System.currentTimeMillis())) {
                this.toDelete.add(relationChange);
                playersToInform.put(relationChange.getRequestingLeader(), relationChange);
            }
        }
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID playerUUID = p.getUniqueId();
            if (playersToInform.containsKey(playerUUID)) {
                RelationChange relationChange = playersToInform.get(playerUUID);
                p.sendMessage(MsgManager.get("allyrequestexpired").replace("{GUILD}", relationChange.getGuildName()));
            }
        }
        pendingRelationChanges.removeAll(toDelete);
        playersToInform.clear();
    }

    public RelationChange getRequest(String requestingGuildName, String guildName) {
        for (RelationChange relationChange : pendingRelationChanges) {
            if (relationChange.getRequestingGuildName().equals(requestingGuildName) && relationChange.getGuildName().equals(guildName)) {
                return relationChange;
            }
        }
        return null;
    }

    public void removeRequest(RelationChange request) {
        toDelete.add(request);
    }
}
