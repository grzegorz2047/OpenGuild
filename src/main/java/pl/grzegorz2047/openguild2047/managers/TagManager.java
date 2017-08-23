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

package pl.grzegorz2047.openguild2047.managers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.guilds.Guild;
import com.github.grzegorz2047.openguild.Relation;

import pl.grzegorz2047.openguild2047.configuration.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.packets.ScoreboardPackets;

/**
 * @author Grzegorz
 */
public class TagManager {


    private final Guilds guilds;
    private ScoreboardPackets scoreboardPackets = new ScoreboardPackets();

    public TagManager(Guilds guilds) {
        this.guilds = guilds;
    }

    public void playerDisbandGuild(Guild guild) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            scoreboardPackets.sendDeleteTeamTag(p, guild.getName());
        }
    }

    private void updateTagsForGuildRelations(Guild whoGuild, Guild withWhoGuild, String guildTagTemplate) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (whoGuild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, whoGuild, guildTagTemplate);
            } else if (withWhoGuild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, withWhoGuild, guildTagTemplate);
            }
        }
    }

    private void addPlayerToGuildTag(UUID uuid, Team whoT) {
        whoT.addEntry(Bukkit.getOfflinePlayer(uuid).getName());
    }

    public void guildBrokeAlliance(Guild firstGuild, Guild secondGuild) {
        for (Relation r : firstGuild.getAlliances()) {
            if (r.getBaseGuildTag().equals(secondGuild.getName()) || r.getAlliedGuildTag().equals(secondGuild.getName())) {//Trzeba to odzielic jakos na 2 przypadki (else if) zamiast ||
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (firstGuild.getMembers().contains(p.getUniqueId())) {
                        scoreboardPackets.sendUpdateTeamTag(p, secondGuild, GenConf.enemyTag);
                    }
                    if (secondGuild.getMembers().contains(p.getUniqueId())) {
                        scoreboardPackets.sendUpdateTeamTag(p, firstGuild, GenConf.enemyTag);
                    }
                }
            }
        }
    }

    public void guildMakeAlliance(Relation r) {

        String who = r.getBaseGuildTag();
        String withwho = r.getAlliedGuildTag();

        Guild whoGuild = guilds.getGuild(who);

        Guild withWhoGuild = guilds.getGuild(withwho);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (whoGuild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, withWhoGuild, GenConf.allyTag);
            }
            if (withWhoGuild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, whoGuild, GenConf.allyTag);
            }
        }
    }

    public void playerJoinGuild(Player player, Guild guild) {
        scoreboardPackets.sendUpdateTeamTag(player, guild, GenConf.guildTag);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (guild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, guild, GenConf.guildTag);
            } else {
                scoreboardPackets.sendUpdateTeamTag(p, guild, GenConf.enemyTag);
                for (Guild ally : guilds.getAllyGuilds(guild)) {
                    updateTagsForGuildRelations(guild, ally, GenConf.allyTag);
                }
            }
        }
    }

    public void playerLeaveGuild(Player joiner, Guild guild) {
        scoreboardPackets.sendDeleteTeamTag(joiner, guild.getName());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (guild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, guild, GenConf.guildTag);
            } else {
                for (Guild ally : guilds.getAllyGuilds(guild)) {
                    updateTagsForGuildRelations(guild, ally, GenConf.allyTag);
                }
            }
        }
    }


    public void playerCreatedGuild(Guild g, Player player) {
        scoreboardPackets.sendCreateTeamTag(player, g, GenConf.guildTag);
        //System.out.println("Liczba obiektow team "+this.getGlobalScoreboard().getTeams().size());
        String enemyTagTemplate = GenConf.enemyTag;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (g.getMembers().contains(p.getUniqueId())) {
                continue;
            }
            scoreboardPackets.sendCreateTeamTag(p, g, GenConf.enemyTag);
        }
    }

    public void prepareScoreboardTagForPlayerOnJoin(Player p) {
        Guild playerGuild = guilds.getPlayerGuild(p.getUniqueId());
        for (String onlineGuildTag : guilds.getOnlineGuilds()) {
            Guild g = guilds.getGuild(onlineGuildTag);
            if (g == null) {
                System.out.println("Gildia " + onlineGuildTag + " jest nulem! Czemu?");
                continue;
            }
            if (playerGuild != null) {
                if (g.equals(playerGuild)) {
                    scoreboardPackets
                            .sendCreateTeamTag(p, g, GenConf.guildTag.replace("%GUILD%", g.getName()));
                }
                if (playerGuild.isAlly(g)) {
                    scoreboardPackets
                            .sendCreateTeamTag(p, g, GenConf.allyTag);
                } else {
                    scoreboardPackets
                            .sendCreateTeamTag(p, g, GenConf.enemyTag);
                }
            } else {
                System.out.println("Gosciu " + p.getName() + " nie ma gildii");
                scoreboardPackets
                        .sendCreateTeamTag(p, g, GenConf.enemyTag);
            }
        }
    }

    public void refreshScoreboardTagsForAllPlayersOnServerApartFromJoiner(Player player, Guild g) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (player.equals(p)) {
                continue;
            }
            Guild playerGuild = guilds.getPlayerGuild(p.getUniqueId());
            if (playerGuild == null) {
                scoreboardPackets
                        .sendCreateTeamTag(p, g, GenConf.enemyTag.replace("%GUILD%", g.getName()));
                continue;
            }
            if (!guilds.isGuildOnline(g.getName())) {
                OpenGuild.getOGLogger().debug("Przechodze przez gildie " + playerGuild.getName());
                if (g.isAlly(playerGuild)) {
                    scoreboardPackets
                            .sendCreateTeamTag(p, g, GenConf.allyTag.replace("%GUILD%", g.getName()));
                    scoreboardPackets.sendUpdateTeamTag(p, g, GenConf.allyTag.replace("%GUILD%", g.getName()));
                } else {
                    scoreboardPackets
                            .sendCreateTeamTag(p, g, GenConf.enemyTag.replace("%GUILD%", g.getName()));
                    scoreboardPackets.sendUpdateTeamTag(p, g, GenConf.enemyTag.replace("%GUILD%", g.getName()));
                }
            } else {
                if (g.equals(playerGuild)) {
                    OpenGuild.getOGLogger().debug("Odswieza team u gracza ze swojej gildii");
                    scoreboardPackets.sendUpdateTeamTag(p, g, GenConf.guildTag.replace("%GUILD%", g.getName()));
                }
            }
        }
    }

    /*


    */
}
