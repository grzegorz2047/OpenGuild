/*
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.grzegorz2047.openguild.managers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.packets.ScoreboardPackets;
import pl.grzegorz2047.openguild.relations.Relation;

import java.util.List;
import java.util.UUID;

/**
 * @author Grzegorz
 */
public class TagManager {

    private final String ENEMY_GUILD_TAG_FORMAT;
    private final String ALLY_GUILD_TAG_FORMAT;
    private final String OWN_GUILD_TAG_FORMAT;
    private ScoreboardPackets scoreboardPackets = new ScoreboardPackets();

    public TagManager(FileConfiguration config) {
        ENEMY_GUILD_TAG_FORMAT = config.getString("tags.enemy", "{TAG}").replace("&", "§");
        ALLY_GUILD_TAG_FORMAT = config.getString("tags.ally", "{TAG}").replace("&", "§");
        OWN_GUILD_TAG_FORMAT = config.getString("tags.guild", "{TAG}").replace("&", "§");
    }

    public void playerDisbandGuild(String guildName) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendDeleteTeamTag(p, guildName);
        }
    }
    
    public void sendDeleteTeamTag(Player player, String guildName) {
        scoreboardPackets.sendDeleteTeamTag(player, guildName);
    }

    public void sendCreateDefaultTeamTag(Player p, String guildName, List<String> guildMemberNames) {
        String guildTagTemplate = ENEMY_GUILD_TAG_FORMAT.replace("%GUILD%", guildName);
        scoreboardPackets.sendCreateTeamTag(p, guildTagTemplate, guildName, guildMemberNames);

    }

    public void sendCreateOwnTeamTag(Player p, String guildName, List<String> guildMemberNames) {
        String guildTagTemplate = OWN_GUILD_TAG_FORMAT.replace("%GUILD%", guildName);
        scoreboardPackets.sendCreateTeamTag(p, guildTagTemplate, guildName, guildMemberNames);
    }

    public void sendCreateAllyTeamTag(Player p, String guildName, List<String> guildMemberNames) {
        String guildTagTemplate = ALLY_GUILD_TAG_FORMAT.replace("%GUILD%", guildName);
        scoreboardPackets.sendCreateTeamTag(p, guildTagTemplate, guildName, guildMemberNames);
    }

    public void sendUpdateAllyTeamTag(Player p, String guildName, List<String> guildMemberNames) {
        scoreboardPackets.sendUpdateTeamTag(p, ALLY_GUILD_TAG_FORMAT.replace("%GUILD%", guildName), guildName, guildMemberNames);
    }

    public void sendUpdateEnemyTeamTag(Player p, String guildName, List<String> guildMemberNames) {
        scoreboardPackets.sendUpdateTeamTag(p, ENEMY_GUILD_TAG_FORMAT.replace("%GUILD%", guildName), guildName, guildMemberNames);
    }

    public void sendUpdateOwnTeamTag(Player p, String guildName, List<String> guildMemberNames) {
        scoreboardPackets.sendUpdateTeamTag(p, OWN_GUILD_TAG_FORMAT.replace("%GUILD%", guildName), guildName, guildMemberNames);
    }
}
