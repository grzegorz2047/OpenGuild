package pl.grzegorz2047.openguild2047.relations;

import com.github.grzegorz2047.openguild.Relation;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.grzegorz2047.openguild2047.guilds.Guild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by grzeg on 23.08.2017.
 */


public class Relations {

    private final List<RelationChange> pendingRelationChanges = new ArrayList<RelationChange>();
    private List<RelationChange> toDelete = new ArrayList<>();

    public Relation createRelation(String who, String withwho, long expires, Relation.Status relationStatus) {
        return new Relation(who, withwho, expires, relationStatus);
    }

    public void changeRelationRequest(Guild requestingGuild, Guild guild, final OfflinePlayer requestedLeader, Relation.Status status) {
        final String tag = guild.getName();
        final String requestingTag = requestingGuild.getName();
        final UUID requestingLeader = requestingGuild.getLeader();
        for (Relation r : requestingGuild.getAlliances()) {
            if (r.getAlliedGuildTag().equals(guild.getName()) || r.getBaseGuildTag().equals(guild.getName())) {
                if (r.getState().equals(status)) {
                    Bukkit.getPlayer(requestingGuild.getLeader()).sendMessage(MsgManager.get("allyexists"));
                    return;
                }
            }
        }
        RelationChange request = getRequest(requestingGuild.getName(), guild.getName());
        if (request == null) {
            pendingRelationChanges.add(new RelationChange(requestingGuild.getName(), guild.getName(), requestingLeader, status, System.currentTimeMillis()));
            Bukkit.getPlayer(requestingGuild.getLeader()).sendMessage(MsgManager.get("sentallyrequest"));
            if (requestedLeader.isOnline()) {
                Bukkit.getPlayer(requestedLeader.getUniqueId()).sendMessage(MsgManager.get("sentallyrequestfrom").replace("{GUILD}", requestingGuild.getName()));
                Bukkit.getPlayer(requestedLeader.getUniqueId()).sendMessage(MsgManager.get("toacceptallymsg").replace("{GUILD}", requestingGuild.getName()));
            } else {
                Bukkit.getPlayer(requestingGuild.getLeader()).sendMessage(MsgManager.get("sentallyrequestfrom").replace("{GUILD}", guild.getName()));
                return;
            }
        } else {
            Bukkit.getPlayer(requestingGuild.getLeader()).sendMessage("Juz wyslales prosbe o sojusz do " + requestingTag);
        }
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
            if (playersToInform.containsKey(p.getUniqueId())) {
                RelationChange relationChange = playersToInform.get(p.getUniqueId());
                p.sendMessage(MsgManager.get("allyrequestexpired").replace("{GUILD}", relationChange.getGuildName()));

            }
        }
        pendingRelationChanges.removeAll(toDelete);
        playersToInform.clear();
    }

    public RelationChange getRequest(String requestingGuildName, String guildName) {
        for (RelationChange relationChange : pendingRelationChanges) {
            if (relationChange.getRequestingGuildName().equals(requestingGuildName)) {
                if (relationChange.getGuildName().equals(guildName)) {
                    return relationChange;
                }
            }
        }
        return null;
    }

    public void removeRequest(RelationChange request) {
        toDelete.add(request);
    }
}
