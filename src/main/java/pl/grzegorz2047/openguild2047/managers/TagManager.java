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

import java.util.List;
import java.util.UUID;

import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam;
import com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam.Mode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pl.grzegorz2047.openguild2047.Guilds;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;

import java.util.Map;

import org.bukkit.OfflinePlayer;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OGLogger;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.packets.ScoreboardPackets;

import static com.comphenix.packetwrapper.WrapperPlayServerScoreboardTeam.Mode.TEAM_CREATED;

/**
 * @author Grzegorz
 */
public class TagManager {


    private final Guilds guilds;
    private Scoreboard globalScoreboard;//Mozna trzymac tu defaultowe prefixy gildii dla bezgildyjnych
    private ScoreboardPackets scoreboardPackets = new ScoreboardPackets();

    public TagManager(Guilds guilds) {
        this.guilds = guilds;

        if (!this.isInitialised()) {//Kiedy trzeba to mozna zainicjowac scoreboard np. przy onEnable()
            this.globalScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
    }

    /*
      Uzycie tagow z teamow z Bukkit API zamiast NametagEdit API.
      Potrzeba metoda do rejestracji, do czyszczenia teamow przy wylaczaniu pluginow
      oraz dodawaniu/usuwaniu gracza z teamtagu.
    
      A ponizej przykladowa metoda rejestracji teamtagu
    */
   /* public static Scoreboard getScoreboard(){
        return TagManager.sc;
    }
    public void updateBoard(){
            for(Player p : Bukkit.getOnlinePlayers()){
                p.setScoreboard(TagManager.getScoreboard());
            }
    }
    
    private boolean registerTeamTag(Guild sg){
        if(!TagManager.isInitialised()){
            System.out.println("Scoreboard nie jest zainicjowany!");
            return false;
        }
        String tag = sg.getName().toUpperCase();
        if(sc.getTeam(tag)== null){
            Team teamtag = sc.registerNewTeam(tag);
            teamtag.setPrefix(com.github.grzegorz2047.openguild.OpenGuild.getGuildManager().getNicknameTag().replace("{TAG}", sg.getName().toUpperCase()));
            teamtag.setDisplayName(com.github.grzegorz2047.openguild.OpenGuild.getGuildManager().getNicknameTag().replace("{TAG}", sg.getName().toUpperCase()));
            for(UUID uuid : sg.getMembers()){
                teamtag.insertPlayer(Bukkit.getOfflinePlayer(uuid));
            }
            updateBoard();
            return true;
        }
        updateBoard();
        return false;
    }
    
    public boolean setTag(UUID player){
        if(TagManager.isInitialised()){
            if(plugin.getGuildHelper().hasGuild(player)){
                //System.out.println("gracz w gildii");
                Guild g =plugin.getGuildHelper().getPlayerGuild(player);
                Team t = sc.getTeam(g.getName().toUpperCase());
                if(t == null){
                    //System.out.println("Brak team pref");
                    registerTeamTag(g);
                    return true;
                }else{
                    //System.out.println("gracz w gildii team pref istnieje");
                    if(!t.getPlayers().contains(Bukkit.getOfflinePlayer(player))){
                        t.insertPlayer(Bukkit.getOfflinePlayer(player));
                        updateBoard();
                        return true;
                    }else{
                        updateBoard();
                        return true;
                    }
                }
            }else{
                Bukkit.getPlayer(player).setScoreboard(TagManager.getScoreboard());
                updateBoard();
            }
        }else{
            //System.out.println("Nie zainicjalizowano tag managera");
        }
        return false;
    }
    public boolean removeTag(UUID player){
        if(TagManager.isInitialised()){
            if(plugin.getGuildHelper().hasGuild(player)){
                Guild g = plugin.getGuildHelper().getPlayerGuild(player);
                Team t = sc.getTeam(g.getName().toUpperCase());
                if(t != null){
                    if(t.getPlayers().contains(Bukkit.getOfflinePlayer(player))){
                        t.removePlayer(Bukkit.getOfflinePlayer(player));
                        updateBoard();
                        return true;
                    }else{
                        updateBoard();
                        return true;// moze tak
                    }
                }
            }
        }
        return false;
    }
     */
    private boolean isInitialised() {
        return globalScoreboard != null;
    }

    public Scoreboard getGlobalScoreboard() {
        return globalScoreboard;
    }

    public void playerDisbandGuild(Guild guild) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            scoreboardPackets.sendDeleteTeamTag(p, guild.getName());
        }
    }

    private boolean isTheSameTag(String guildTag, String secondGuildTag) {
        return secondGuildTag.equals(guildTag);
    }

    private boolean isGuildTagRenederedAlready(String guildTag) {
        return this.getGlobalScoreboard().getTeam(guildTag) != null;
    }

    private Team createGuildScoreboardTag(String guildTag, Scoreboard whoSc) {
        Team whoT;
        whoT = whoSc.registerNewTeam(guildTag);
        String tagLabel = GenConf.guildTag.replace("{TAG}", guildTag);
        whoT.setPrefix(tagLabel);
        whoT.setDisplayName(tagLabel);
        return whoT;
    }

    private Team prepareGuildTag(String guildTag, Scoreboard guildScoreboard) {
        Team scoreboardTeamTag;
        if (tagForWhoDoesntExists(guildTag, guildScoreboard)) {
            scoreboardTeamTag = guildScoreboard.registerNewTeam(guildTag);
        } else {
            scoreboardTeamTag = guildScoreboard.getTeam(guildTag);
        }
        scoreboardTeamTag.setPrefix(GenConf.allyTag.replace("{TAG}", guildTag));
        scoreboardTeamTag.setDisplayName(GenConf.allyTag.replace("{TAG}", guildTag));
        return scoreboardTeamTag;
    }

    public void setTagsForGuildRelations(Guild whoGuild, Guild withWhoGuild) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (whoGuild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendCreateTeamTag(p, whoGuild, GenConf.allyTag);
            } else if (withWhoGuild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendCreateTeamTag(p, withWhoGuild, GenConf.allyTag);
            }
        }
    }

    public void updateTagsForGuildRelations(Guild whoGuild, Guild withWhoGuild, String guildTagTemplate) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (whoGuild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, whoGuild, guildTagTemplate);
            } else if (withWhoGuild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, withWhoGuild, guildTagTemplate);
            }
        }
    }

    private void setTagsForGuildMembers(Guild guild, Team team) {
        for (UUID member : guild.getMembers()) {
            addPlayerToGuildTag(member, team);
        }
    }

    private void addPlayerToGuildTag(UUID uuid, Team whoT) {
        whoT.addEntry(Bukkit.getOfflinePlayer(uuid).getName());
    }

    private void addGuildTagToPlayer(String guildTag, UUID uuid) {
        Team t = this.getGlobalScoreboard().getTeam(guildTag);
        addPlayerToGuildTag(uuid, t);
    }

    private void renderGuildTag(String guildTag, UUID uuid) {
        Team t = this.getGlobalScoreboard().registerNewTeam(guildTag);
        t.setPrefix(GenConf.enemyTag.replace("{TAG}", guildTag));
        t.setDisplayName(GenConf.enemyTag.replace("{TAG}", guildTag));
        addPlayerToGuildTag(uuid, t);
    }

    public void guildBrokeAlliance(Guild firstGuild, Guild secondGuild) {
        for (Relation r : firstGuild.getAlliances()) {
            if (r.getWho().equals(secondGuild.getName()) || r.getAlliedGuildTag().equals(secondGuild.getName())) {//Trzeba to odzielic jakos na 2 przypadki (else if) zamiast ||
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

        String who = r.getWho();
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

    private void addMembersToScoreboardGuildTag(Guild withWhoGuild, Team withWhoT) {
        for (UUID whop : withWhoGuild.getMembers()) {
            withWhoT.addEntry(Bukkit.getOfflinePlayer(whop).getName());
        }
    }

    public void playerJoinGuild(Player player, Guild guild) {
        scoreboardPackets.sendUpdateTeamTag(player, guild, GenConf.guildTag);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (guild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, guild, GenConf.guildTag);
            } else {
                for (Guild ally : guild.getAllyGuilds()) {
                    updateTagsForGuildRelations(guild, ally, GenConf.allyTag);
                }
            }
        }
    }

    public void playerLeaveGuild(Player joiner, Guild guild) {
        Guild joinerGuild = guilds.getPlayerGuild(joiner.getUniqueId());
        scoreboardPackets.sendDeleteTeamTag(joiner, guild.getName());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (guild.getMembers().contains(p.getUniqueId())) {
                scoreboardPackets.sendUpdateTeamTag(p, guild, GenConf.guildTag);
            } else {
                for (Guild ally : guild.getAllyGuilds()) {
                    updateTagsForGuildRelations(guild, ally, GenConf.allyTag);
                }
            }
        }
    }

    public void assignScoreboardToPlayer(Player player) {
        player.setScoreboard(globalScoreboard);
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

    private Team registerGuildTag(Guild g, Scoreboard guildScoreboard) {
        return guildScoreboard.registerNewTeam(g.getName());
    }

    private boolean tagForWhoDoesntExists(String who, Scoreboard withWhoSc) {
        return withWhoSc.getTeam(who) == null;
    }

    public void registerGuildTag(String tag) {
        Team t = this.getGlobalScoreboard().registerNewTeam(tag);
        t.setPrefix(GenConf.allyTag.replace("{TAG}", tag));
        t.setDisplayName(ChatColor.RED + tag + " ");
    }

    public void prepareScoreboardTagForPlayerOnJoin(Player p) {
        Guild playerGuild = guilds.getPlayerGuild(p.getUniqueId());
        for (String onlineGuildTag : guilds.getOnlineGuilds()) {
            Guild g = guilds.getGuild(onlineGuildTag);
            if (playerGuild != null) {
                if (playerGuild.isAlly(g)) {
                    scoreboardPackets.sendCreateTeamTag(p, g, GenConf.allyTag);
                } else {
                    scoreboardPackets.sendCreateTeamTag(p, g, GenConf.enemyTag);
                }
            } else {
                scoreboardPackets.sendCreateTeamTag(p, g, GenConf.enemyTag);
            }
        }
    }

    /*
    login
    usuwanie /g zamknij
    join /g akceptuj
    left /g opusc
    sojusz /g sojusz
    enemy /g wrog
    + TODO MA WYSWIETLAC NORMALNIE TAGI GILDII JAK NIE MA SOJUSZU!!!!!!! JAKOS
    */
}
