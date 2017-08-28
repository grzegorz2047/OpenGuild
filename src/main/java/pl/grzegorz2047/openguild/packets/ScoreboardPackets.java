package pl.grzegorz2047.openguild.packets;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import pl.grzegorz2047.openguild.guilds.Guild;
import org.bukkit.entity.Player;

/**
 * File created by grzegorz2047 on 19.08.2017.
 */
public class ScoreboardPackets {
    public void sendCreateTeamTag(Player player, Guild guild, String guildTagTemplate) {
        WrapperPlayServerScoreboardTeam tagPacket = new WrapperPlayServerScoreboardTeam();
        tagPacket.setPrefix(guildTagTemplate.replace("{TAG}", guild.getName()));
        tagPacket.setName(guild.getName());
        tagPacket.setDisplayName(tagPacket.getPrefix());
        tagPacket.setPlayers(guild.getMembersNames());
        tagPacket.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED);
        tagPacket.sendPacket(player);
    }
    public void sendUpdateTeamTag(Player player, Guild guild, String guildTagTemplate) {
        WrapperPlayServerScoreboardTeam tagPacket = new WrapperPlayServerScoreboardTeam();
        tagPacket.setPrefix(guildTagTemplate.replace("{TAG}", guild.getName()));
        tagPacket.setName(guild.getName());
        tagPacket.setDisplayName(tagPacket.getPrefix());
        tagPacket.setPlayers(guild.getMembersNames());
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
