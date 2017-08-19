package pl.grzegorz2047.openguild2047.packets;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.github.grzegorz2047.openguild.Guild;
import org.bukkit.Bukkit;

/**
 * Created by grzeg on 19.08.2017.
 */
public class ScoreboardTagPackets {

    public WrapperPlayServerScoreboardTeam prepareTeamTag(Guild guild, String tagTemplate) {
        WrapperPlayServerScoreboardTeam tagPacket = new WrapperPlayServerScoreboardTeam();
        tagPacket.setPrefix(tagTemplate.replace("%TAG%", guild.getName()));
        tagPacket.setPlayers(guild.getMembersNames());

        return tagPacket;
    }

}
