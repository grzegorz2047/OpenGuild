package pl.grzegorz2047.openguild.guilds;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.metadata.PlayerMetadataController;

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
    private final PlayerMetadataController playerMetadataController;
    private List<GuildInvitation> toDelete = new ArrayList<>();

    public GuildInvitations(SQLHandler sqlHandler, PlayerMetadataController playerMetadataController) {
        this.sqlHandler = sqlHandler;
        this.playerMetadataController = playerMetadataController;
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
        String playerName = player.getName();
        String guildName = guild.getName();
        GuildInvitation playerInvitation = getPlayerInvitation(playerName, guildName);
        if (playerInvitation != null) {
            toDelete.add(playerInvitation);

            UUID playerUniqueId = player.getUniqueId();
            playerMetadataController.updatePlayerGuildMetadata(playerUniqueId, guildName);
            sqlHandler.updatePlayerTag(playerUniqueId, guildName);
            guild.addMember(playerUniqueId);


        }
    }

    void addGuildInvitation(Player player, Player who, Guild guild, String guildName) {
        if (getPlayerInvitation(player.getName(), guildName) == null) {
            pendingInvitations.add(new GuildInvitation(guildName, player.getName(), System.currentTimeMillis()));
            player.sendMessage(MsgManager.get("guild-invitation").replace("{WHO}", who.getName()).replace("{TAG}", guild.getName().toUpperCase()));
        }
    }
}
