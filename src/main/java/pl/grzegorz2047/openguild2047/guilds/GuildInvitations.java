package pl.grzegorz2047.openguild2047.guilds;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by grzeg on 23.08.2017.
 */
public class GuildInvitations {
    private final List<GuildInvitation> pendingInvitations = new ArrayList<GuildInvitation>();
    private final SQLHandler sqlHandler;
    private final Guilds guilds;
    private List<GuildInvitation> toDelete = new ArrayList<>();

    public GuildInvitations(SQLHandler sqlHandler, Guilds guilds) {
        this.sqlHandler = sqlHandler;
        this.guilds = guilds;
    }

    public void checkPlayerInvitations() {
        HashMap<String, GuildInvitation> playersToInform = new HashMap<>();
        for (GuildInvitation invitation : pendingInvitations) {
            boolean expired = invitation.isExpired(System.currentTimeMillis());
            if (expired) {
                toDelete.add(invitation);
                playersToInform.put(invitation.getInviteePlayer(), invitation);
            }
        }
        if (playersToInform.size() > 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                String onlineName = p.getName();
                if (playersToInform.containsKey(onlineName)) {
                    GuildInvitation invitation = playersToInform.get(onlineName);
                    p.sendMessage(MsgManager.get("guild-invitation-expired").replace("{TAG}", invitation.getHostGuild()));
                }
            }
        }
        playersToInform.clear();
        pendingInvitations.removeAll(toDelete);
    }

    public GuildInvitation getPlayerInvitation(String playerName, String guildName) {
        for (GuildInvitation invitation : pendingInvitations) {
            if (invitation.getInviteePlayer().equals(playerName)) {
                if (invitation.getHostGuild().equals(guildName)) {
                    return invitation;
                }
            }
        }
        return null;
    }


    public void acceptGuildInvitation(Player player, Guild guild) {
        GuildInvitation playerInvitation = getPlayerInvitation(player.getName(), guild.getName());
        if (playerInvitation != null) {
            toDelete.add(playerInvitation);

            guilds.addPlayer(player.getUniqueId(), guild);
            sqlHandler.
                    updatePlayerTag(
                            player.getUniqueId(),
                            guild.getName());
            guild.addMember(player.getUniqueId());
            guild.addMembersName(player.getName());

        }
    }

    void addGuildInvitation(Player player, Player who, Guild guild, String guildName) {
        if (getPlayerInvitation(player.getName(), guildName) == null) {
            pendingInvitations.add(new GuildInvitation(guildName, player.getName(), System.currentTimeMillis()));
            player.sendMessage(MsgManager.get("guild-invitation").replace("{WHO}", who.getName()).replace("{TAG}", guild.getName().toUpperCase()));
        }
    }
}
