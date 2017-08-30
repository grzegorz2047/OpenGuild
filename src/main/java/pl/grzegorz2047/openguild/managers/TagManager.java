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
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.configuration.GenConf;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.packets.ScoreboardPackets;
import pl.grzegorz2047.openguild.relations.Relation;

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


    public void guildBrokeAlliance(Guild firstGuild, Guild secondGuild) {
        for (Relation r : firstGuild.getAlliances()) {
            if (r.getBaseGuildTag().equals(secondGuild.getName()) || r.getAlliedGuildTag().equals(secondGuild.getName())) {//Trzeba to odzielic jakos na 2 przypadki (else if) zamiast ||
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (firstGuild.getMembers().contains(p.getUniqueId())) {
                        scoreboardPackets.sendUpdateTeamTag(p, secondGuild, GenConf.ENEMY_GUILD_TAG_FORMAT);
                    }
                    if (secondGuild.getMembers().contains(p.getUniqueId())) {
                        scoreboardPackets.sendUpdateTeamTag(p, firstGuild, GenConf.ENEMY_GUILD_TAG_FORMAT);
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
                scoreboardPackets.sendUpdateTeamTag(p, withWhoGuild, GenConf.ALLY_GUILD_TAG_FORMAT);
            }
            if (withWhoGuild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, whoGuild, GenConf.ALLY_GUILD_TAG_FORMAT);
            }
        }
    }
/*
    public void playerJoinGuild(Player player, Guild guild) {
        scoreboardPackets.sendUpdateTeamTag(player, guild, GenConf.OWN_GUILD_TAG_FORMAT);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (guild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, guild, GenConf.OWN_GUILD_TAG_FORMAT);
            } else {
                scoreboardPackets.sendUpdateTeamTag(p, guild, GenConf.ENEMY_GUILD_TAG_FORMAT);
                for (Guild ally : guilds.getAllyGuilds(guild)) {
                    updateTagsForGuildRelations(guild, ally, GenConf.ALLY_GUILD_TAG_FORMAT);
                }
            }
        }
    }*/

    public void playerLeaveGuild(Player joiner, Guild guild) {
        scoreboardPackets.sendDeleteTeamTag(joiner, guild.getName());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (guild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, guild, GenConf.OWN_GUILD_TAG_FORMAT);
            } else {
                for (Guild ally : guilds.getAllyGuilds(guild)) {
                    updateTagsForGuildRelations(guild, ally, GenConf.ALLY_GUILD_TAG_FORMAT);
                }
            }
        }
    }


    public void playerCreatedGuild(Guild g, Player player) {
        scoreboardPackets.sendCreateTeamTag(player, g, GenConf.OWN_GUILD_TAG_FORMAT);
        //System.out.println("Liczba obiektow team "+this.getGlobalScoreboard().getTeams().size());
        //String enemyTagTemplate = GenConf.ENEMY_GUILD_TAG_FORMAT;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (g.getMembers().contains(p.getUniqueId())) {
                continue;
            }
            scoreboardPackets.sendCreateTeamTag(p, g, GenConf.ENEMY_GUILD_TAG_FORMAT);
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
                            .sendCreateTeamTag(p, g, GenConf.OWN_GUILD_TAG_FORMAT.replace("%GUILD%", g.getName()));
                }
                if (playerGuild.isAlly(g)) {
                    scoreboardPackets
                            .sendCreateTeamTag(p, g, GenConf.ALLY_GUILD_TAG_FORMAT);
                } else {
                    scoreboardPackets
                            .sendCreateTeamTag(p, g, GenConf.ENEMY_GUILD_TAG_FORMAT);
                }
            } else {
                System.out.println("Gosciu " + p.getName() + " nie ma gildii");
                scoreboardPackets
                        .sendCreateTeamTag(p, g, GenConf.ENEMY_GUILD_TAG_FORMAT);
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
                        .sendCreateTeamTag(p, g, GenConf.ENEMY_GUILD_TAG_FORMAT.replace("%GUILD%", g.getName()));
                continue;
            }
            if (!guilds.isGuildOnline(g.getName())) {
                OpenGuild.getOGLogger().debug("Przechodze przez gildie " + playerGuild.getName());
                if (g.isAlly(playerGuild)) {
                    scoreboardPackets
                            .sendCreateTeamTag(p, g, GenConf.ALLY_GUILD_TAG_FORMAT.replace("%GUILD%", g.getName()));
                    scoreboardPackets.sendUpdateTeamTag(p, g, GenConf.ALLY_GUILD_TAG_FORMAT.replace("%GUILD%", g.getName()));
                } else {
                    scoreboardPackets
                            .sendCreateTeamTag(p, g, GenConf.ENEMY_GUILD_TAG_FORMAT.replace("%GUILD%", g.getName()));
                    scoreboardPackets.sendUpdateTeamTag(p, g, GenConf.ENEMY_GUILD_TAG_FORMAT.replace("%GUILD%", g.getName()));
                }
            } else {
                if (g.equals(playerGuild)) {
                    OpenGuild.getOGLogger().debug("Odswieza team u gracza ze swojej gildii");
                    scoreboardPackets.sendUpdateTeamTag(p, g, GenConf.OWN_GUILD_TAG_FORMAT.replace("%GUILD%", g.getName()));
                }
            }
        }
    }

    /*


    */
}
