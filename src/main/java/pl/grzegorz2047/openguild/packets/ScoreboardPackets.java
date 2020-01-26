package pl.grzegorz2047.openguild.packets;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * File created by grzegorz2047 on 19.08.2017.
 */
public class ScoreboardPackets {
    public void sendCreateTeamTag(Player player, String guildTagTemplate, String guildName, List<String> guildMemberNames) {
        WrapperPlayServerScoreboardTeam tagPacket = new WrapperPlayServerScoreboardTeam();
        tagPacket.setPrefix(WrappedChatComponent.fromText(guildTagTemplate.replace("{TAG}", guildName)));
        tagPacket.setName(guildName);
        tagPacket.setDisplayName(tagPacket.getPrefix());
        tagPacket.setPlayers(guildMemberNames);
        tagPacket.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED);
        tagPacket.sendPacket(player);
    }
    public void sendUpdateTeamTag(Player player, String guildTagTemplate, String guildName, List<String> guildMembersNames) {
        WrapperPlayServerScoreboardTeam tagPacket = new WrapperPlayServerScoreboardTeam();
        tagPacket.setPrefix(WrappedChatComponent.fromText(guildTagTemplate.replace("{TAG}", guildName)));
        tagPacket.setName(guildName);
        tagPacket.setDisplayName(tagPacket.getPrefix());
        tagPacket.setPlayers(guildMembersNames);
        tagPacket.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_UPDATED);
        tagPacket.sendPacket(player);
    }

    public void sendDeleteTeamTag(Player p, String guildName) {
        WrapperPlayServerScoreboardTeam tagPacket = new WrapperPlayServerScoreboardTeam();
        tagPacket.setName(guildName);
        tagPacket.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_REMOVED);
        tagPacket.sendPacket(p);

    }

}
