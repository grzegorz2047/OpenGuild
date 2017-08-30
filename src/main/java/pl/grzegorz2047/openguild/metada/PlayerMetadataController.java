package pl.grzegorz2047.openguild.metada;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public class PlayerMetadataController {


    private final Plugin plugin;

    public enum PlayerMetaDataColumn {GUILD, ELO, KILLS, DEATHS}

    public PlayerMetadataController(Plugin plugin) {
        this.plugin = plugin;
    }

    public void updatePlayerMetadata(UUID uniqueId, String column, Object value) {
        Player player = Bukkit.getPlayer(uniqueId);
        if (player == null) return;
        player.setMetadata(column, new FixedMetadataValue(plugin, value));
    }

    public String getPlayerGuildTagFromMetaData(UUID uuid) {
        List<MetadataValue> metadata = Bukkit.getPlayer(uuid).getMetadata(PlayerMetaDataColumn.GUILD.name());
        if (metadata.size() == 0) {
            return "";
        }
        return metadata.get(0).asString();
    }
    public void updatePlayerMetaAll(UUID uuid, String guildName, int eloPoints, int playerKills, int playerDeaths) {
        updatePlayerMetadata(uuid, PlayerMetaDataColumn.GUILD.name(), guildName);
        updatePlayerMetadata(uuid, PlayerMetaDataColumn.ELO.name(), eloPoints);
        updatePlayerMetadata(uuid, PlayerMetaDataColumn.KILLS.name(), playerKills);
        updatePlayerMetadata(uuid, PlayerMetaDataColumn.DEATHS.name(), playerDeaths);
    }
}
