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

/**
 * @author Grzegorz
 */
public class TagManager {


    private final Guilds guilds;
    private Scoreboard globalScoreboard;//Mozna trzymac tu defaultowe prefixy gildii dla bezgildyjnych

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
        getGuildScoreboardTag(this.getGlobalScoreboard(), guild).unregister();
        for (Map.Entry<String, Guild> gs : guilds.getGuilds().entrySet()) {
            if (isTheSameTag(guild.getName(), gs.getKey())) {
                continue;
            }
            Guild otherGuild = gs.getValue();
            Scoreboard otherGuildScoreboard = otherGuild.getSc();
            Team t = getGuildScoreboardTag(otherGuildScoreboard, guild);
            if (t != null) {
                t.unregister();
            }
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


    public void preparePlayerTag(String guildTag, UUID uuid, Guild playersGuild) {
        if (!isGuildTagRenederedAlready(guildTag)) {
            renderGuildTag(guildTag, uuid);
        } else {
            addGuildTagToPlayer(guildTag, uuid);
        }
        Scoreboard whoSc = playersGuild.getSc();

        Team whoT;
        if (tagForWhoDoesntExists(guildTag, whoSc)) {
            whoT = createGuildScoreboardTag(guildTag, whoSc);
            //System.out.print("whoT to "+whoT.getName()+" z dn "+whoT.getDisplayName());
            //System.out.print("Dodaje gracza "+Bukkit.getOfflinePlayer(uuid).getName());
            addPlayerToGuildTag(uuid, whoT);
        } else {
            whoT = whoSc.getTeam(guildTag);
            //System.out.print("Dodaje gracza "+Bukkit.getOfflinePlayer(uuid).getName());
            addPlayerToGuildTag(uuid, whoT);
        }
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

    public void setTagsForGuildRelations(String who, String withwho, Guild whoGuild, Guild withWhoGuild) {
        Scoreboard whoSc = whoGuild.getSc();
        Scoreboard withWhoSc = withWhoGuild.getSc();

        Team whoScoreboardTeamTag = prepareGuildTag(who, withWhoSc);
        setTagsForGuildMembers(whoGuild, whoScoreboardTeamTag);

        Team withWhoScoreboardTag = prepareGuildTag(withwho, whoSc);
        setTagsForGuildMembers(withWhoGuild, withWhoScoreboardTag);
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
            if (r.getWho().equals(secondGuild.getName()) || r.getWithWho().equals(secondGuild.getName())) {//Trzeba to odzielic jakos na 2 przypadki (else if) zamiast ||
                Scoreboard sc = secondGuild.getSc();
                getGuildScoreboardTag(sc, firstGuild).setPrefix(GenConf.enemyTag.replace("{TAG}", firstGuild.getName()));
                getGuildScoreboardTag(sc, firstGuild).setDisplayName(GenConf.enemyTag.replace("{TAG}", firstGuild.getName()));

                Scoreboard sc2 = firstGuild.getSc();//GenConf.enemyTag.replace("{TAG}", firstGuild.getName())
                getGuildScoreboardTag(sc2, secondGuild).setPrefix(GenConf.enemyTag.replace("{TAG}", secondGuild.getName()));
                getGuildScoreboardTag(sc2, secondGuild).setDisplayName(GenConf.enemyTag.replace("{TAG}", secondGuild.getName()));
            }
        }
    }

    public void guildMakeAlliance(Relation r) {

        String who = r.getWho();
        String withwho = r.getWithWho();

        Guild whoGuild = guilds.getGuild(who);

        Scoreboard whoSc = whoGuild.getSc();

        Guild withWhoGuild = guilds.getGuild(withwho);

        Scoreboard withWhoSc = withWhoGuild.getSc();

        setAllyScoreboardTag(who, withWhoSc, whoGuild);

        setAllyScoreboardTag(withwho, whoSc, withWhoGuild);
    }

    private void setAllyScoreboardTag(String withwho, Scoreboard whoSc, Guild withWhoGuild) {
        Team withWhoT = whoSc.getTeam(withwho);
        String allyWithWhoLabelTag = GenConf.allyTag.replace("{TAG}", withwho);
        withWhoT.setPrefix(allyWithWhoLabelTag);
        withWhoT.setDisplayName(allyWithWhoLabelTag);
        addMembersToScoreboardGuildTag(withWhoGuild, withWhoT);
    }

    private void addMembersToScoreboardGuildTag(Guild withWhoGuild, Team withWhoT) {
        for (UUID whop : withWhoGuild.getMembers()) {
            withWhoT.addEntry(Bukkit.getOfflinePlayer(whop).getName());
        }
    }

    public void playerJoinGuild(OfflinePlayer player, Guild guild) {
        Scoreboard sc = guild.getSc();
        Team t = getGuildScoreboardTag(sc, guild);
        if (t != null) {
            t.addEntry(player.getName());
        } else {
            Scoreboard whoSc = guild.getSc();
            String who = guild.getName();
            Team whoT;

            whoT = whoSc.registerNewTeam(who);
            whoT.setPrefix(GenConf.guildTag.replace("{TAG}", who));
            whoT.setDisplayName(GenConf.guildTag.replace("{TAG}", who));

            whoT = getGuildScoreboardTag(whoSc, guild);
            whoT.addEntry(player.getName());
        }
           /* for(UUID member : guild.getMembers()){
                Player memon = Bukkit.getPlayer(member);
                if(memon != null){
                    memon.setScoreboard(sc);
                }
            }*/
            
        /*for(Relation r : guild.getAlliances()){
            if(r.getWho().equals(guild.getName())){
                Guild ally = plugin.getGuildHelper().getGuilds().get(r.getWithWho());
                Scoreboard sca = ally.getSc();
                sca.getTeam(guild.getName()).removePlayer(player);
                for(UUID allypl : ally.getMembers()){
                    Player plon = Bukkit.getPlayer(allypl);
                    if(plon != null){
                        plon.setScoreboard(sca);
                    }
                }
            }
        } 
        for(Player players : Bukkit.getOnlinePlayers()){
            if(player.equals(players)){
                continue;
            }
            Team t2 = players.getScoreboard().getTeam(guild.getName());
            t2.insertPlayer(player);
        }*/
        for (Map.Entry<String, Guild> gs : guilds.getGuilds().entrySet()) {
            if (isTheSameTag(guild.getName(), gs.getKey())) {
                continue;
            }
            Team t2 = getGuildScoreboardTag(gs.getValue().getSc(), guild);
            if (t2 != null) {
                t2.addEntry(player.getName());
            }
        }
    }

    public void playerLeaveGuild(OfflinePlayer joiner) {
        Guild joinerGuild = guilds.getPlayerGuild(joiner.getUniqueId());
        /*for(Relation r : joinerGuild.getAlliances()){
            if(r.getWho().equals(joinerGuild.getName())){
                Guild ally = plugin.getGuildHelper().getGuilds().get(r.getWithWho());
                Scoreboard sc = ally.getSc();
                sc.getTeam(joinerGuild.getName()).removePlayer(joiner);
                for(UUID allypl : ally.getMembers()){
                    Player plon = Bukkit.getPlayer(allypl);
                    if(plon != null){
                        plon.setScoreboard(sc);
                    }
                }
            }
        } */
        for (Map.Entry<String, Guild> gs : guilds.getGuilds().entrySet()) {
            if (isTheSameTag(joinerGuild.getName(), gs.getKey())) {
                continue;
            }
            Team t2 = getGuildScoreboardTag(gs.getValue().getSc(), joinerGuild);
            if (t2 != null) {
                t2.removeEntry(joiner.getName());
            }
        }
        getGuildScoreboardTag(this.getGlobalScoreboard(), joinerGuild).removeEntry(joiner.getName());
        //System.out.println("Liczba obiektow team "+this.getGlobalScoreboard().getTeams().size());
        //System.out.print("Gracz opuszcza gildie");
        if (joiner.isOnline()) {
            //System.out.println("Jest online");
            Player p = Bukkit.getPlayer(joiner.getUniqueId());
            p.setScoreboard(globalScoreboard);
        }
    }

    public void assignScoreboardToPlayer(Player player, Scoreboard sc) {
        player.setScoreboard(sc);
    }

    public void assignScoreboardToPlayer(Player player) {
        player.setScoreboard(globalScoreboard);
    }

    public void playerMakeGuild(Guild g, Player p) {
        Team tg = registerGuildTag(g, g.getSc());
        tg.setPrefix(GenConf.guildTag.replace("{TAG}", g.getName()));
        tg.setDisplayName(GenConf.guildTag.replace("{TAG}", g.getName()));
        getGuildScoreboardTag(g.getSc(), g).addEntry(p.getName());
        p.setScoreboard(g.getSc());
        //***********
        Team t = registerGuildTag(g, this.getGlobalScoreboard());
        //System.out.println("Liczba obiektow team "+this.getGlobalScoreboard().getTeams().size());
        t.setPrefix(GenConf.enemyTag.replace("{TAG}", g.getName()));
        t.setDisplayName(GenConf.enemyTag.replace("{TAG}", g.getName()));
        t.addEntry(p.getName());
        for (Map.Entry<String, Guild> gs : guilds.getGuilds().entrySet()) {
            if (isTheSameTag(g.getName(), gs.getKey())) {
                continue;
            }
            Guild guild = gs.getValue();
            Scoreboard guildScoreboard = guild.getSc();
            Team t2 = registerGuildTag(g, guildScoreboard);
            t2.setPrefix(GenConf.enemyTag.replace("{TAG}", g.getName()));
            t2.setDisplayName(GenConf.enemyTag.replace("{TAG}", g.getName()));
            t2.addEntry(p.getName());

            Team t3 = g.getSc().registerNewTeam(gs.getKey());
            t3.setPrefix(GenConf.enemyTag.replace("{TAG}", gs.getKey()));
            t3.setDisplayName(GenConf.enemyTag.replace("{TAG}", gs.getKey()));
            addMembersToScoreboardGuildTag(guild, t3);
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

    public void loadTags(Guilds guilds) {
        for (Map.Entry<String, Guild> gs : guilds.getGuilds().entrySet()) {
            Scoreboard sc = gs.getValue().getSc();
            for (Map.Entry<String, Guild> gs2 : guilds.getGuilds().entrySet()) {
                Guild guild = gs2.getValue();
                if (isTheSameTag(guild.getName(), gs.getKey())) {
                    continue;
                }
                Team t;
                if (tagForWhoDoesntExists(gs2.getKey(), sc)) {
                    t = registerGuildTag(guild, sc);
                } else {
                    t = getGuildScoreboardTag(sc, guild);
                }
                t.setPrefix(GenConf.allyTag.replace("{TAG}", guild.getName()));
                t.setDisplayName(GenConf.allyTag.replace("{TAG}", guild.getName()));
                setTagsForGuildMembers(guild, t);
            }
            //if(gs.getValue().getName().equals(joinerGuild.getName())){
            //     continue;
            //}
            // Team t2 = gs.getValue().getSc().getTeam(joinerGuild.getName());
            //  if(t2 != null){
            //      t2.insertPlayer(joiner);
            ///  }
        }

    }

    private Team getGuildScoreboardTag(Scoreboard sc, Guild guild) {
        return sc.getTeam(guild.getName());
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
