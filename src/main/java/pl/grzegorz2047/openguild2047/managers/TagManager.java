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
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.api.Guilds;

/**
 *
 * @author Grzegorz
 */
public class TagManager {
    
    private OpenGuild plugin;
   
    private Scoreboard guildExistsInfoScoreboard;//Mozna trzymac tu defaultowe prefixy gildii dla bezgildyjnych
    
    public TagManager(OpenGuild plugin){
        this.plugin = plugin;
        
        if(!this.isInitialised()){//Kiedy trzeba to mozna zainicjowac scoreboard np. przy onEnable()
            this.guildExistsInfoScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
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
        String tag = sg.getTag().toUpperCase();
        if(sc.getTeam(tag)== null){
            Team teamtag = sc.registerNewTeam(tag);
            teamtag.setPrefix(com.github.grzegorz2047.openguild.OpenGuild.getGuildManager().getNicknameTag().replace("{TAG}", sg.getTag().toUpperCase()));
            teamtag.setDisplayName(com.github.grzegorz2047.openguild.OpenGuild.getGuildManager().getNicknameTag().replace("{TAG}", sg.getTag().toUpperCase()));
            for(UUID uuid : sg.getMembers()){
                teamtag.addPlayer(Bukkit.getOfflinePlayer(uuid));
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
                Team t = sc.getTeam(g.getTag().toUpperCase());
                if(t == null){
                    //System.out.println("Brak team pref");
                    registerTeamTag(g);
                    return true;
                }else{
                    //System.out.println("gracz w gildii team pref istnieje");
                    if(!t.getPlayers().contains(Bukkit.getOfflinePlayer(player))){
                        t.addPlayer(Bukkit.getOfflinePlayer(player));
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
                Team t = sc.getTeam(g.getTag().toUpperCase());
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
    private boolean isInitialised(){
        return guildExistsInfoScoreboard!= null; 
    }
    public Scoreboard getGlobalScoreboard(){
        return guildExistsInfoScoreboard;
    }
    public void playerDisbandGuild(Guild guild){
        this.getGlobalScoreboard().getTeam(guild.getTag()).unregister();
        for(Map.Entry<String, Guild> gs : plugin.getGuildHelper().getGuilds().entrySet()){
            if(gs.getValue().getTag().equals(guild.getTag())){
                continue;
            }
            Team t = gs.getValue().getSc().getTeam(guild.getTag());
            if(t != null){
                t.unregister();
            }
        }
    }
    public void guildBrokeAlliance(Guild guild, Guild tobrokewith){
        for(Relation r : guild.getAlliances()){
            if(r.getWho().equals(tobrokewith.getTag()) || r.getWithWho().equals(tobrokewith.getTag())){//Trzeba to odzielic jakos na 2 przypadki (else if) zamiast ||
                Guild enemy = tobrokewith;
                Scoreboard sc = enemy.getSc();
                sc.getTeam(guild.getTag()).setPrefix(GenConf.enemyTag.replace("{TAG}", guild.getTag()));
                sc.getTeam(guild.getTag()).setDisplayName(GenConf.enemyTag.replace("{TAG}", guild.getTag()));
                
                
                Guild enemy2 = guild;
                Scoreboard sc2 = enemy2.getSc();//GenConf.enemyTag.replace("{TAG}", guild.getTag())
                sc2.getTeam(tobrokewith.getTag()).setPrefix(GenConf.enemyTag.replace("{TAG}", tobrokewith.getTag()));
                sc2.getTeam(tobrokewith.getTag()).setDisplayName(GenConf.enemyTag.replace("{TAG}", tobrokewith.getTag()));
            }
        } 
    }
    public void guildMakeAlliance(Relation r){
        
        String who = r.getWho();
        String withwho = r.getWithWho();
        
        Guild whoGuild = plugin.getGuildHelper().getGuilds().get(who);

        Scoreboard whoSc = whoGuild.getSc();

        Guild withWhoGuild = plugin.getGuildHelper().getGuilds().get(withwho);

        Scoreboard withWhoSc = withWhoGuild.getSc();

        Team whoT;

        whoT = withWhoSc.getTeam(who);
        whoT.setPrefix(GenConf.allyTag.replace("{TAG}", who));
        whoT.setDisplayName(GenConf.allyTag.replace("{TAG}", who));
        for(UUID whop : whoGuild.getMembers()){
            whoT.addPlayer(Bukkit.getOfflinePlayer(whop));
        }

        Team withWhoT;

        withWhoT = whoSc.getTeam(withwho);
        withWhoT.setPrefix(GenConf.allyTag.replace("{TAG}", withwho));
        withWhoT.setDisplayName(GenConf.allyTag.replace("{TAG}", withwho));
        for(UUID whop : withWhoGuild.getMembers()){
            withWhoT.addPlayer(Bukkit.getOfflinePlayer(whop));
        }
    }
    
    public void playerJoinGuild(OfflinePlayer joiner){
        Guild joinerGuild = plugin.getGuildHelper().getPlayerGuild(joiner.getUniqueId());
            Scoreboard sc = joinerGuild.getSc();
            Team t = sc.getTeam(joinerGuild.getTag());
            if(t != null){
                t.addPlayer(joiner);
            }else{
                Guild whoGuild = joinerGuild;
                Scoreboard whoSc = whoGuild.getSc();
                String who = joinerGuild.getTag();
                Team whoT;
                
                whoT = whoSc.registerNewTeam(who);
                whoT.setPrefix(GenConf.guildTag.replace("{TAG}", who));
                whoT.setDisplayName(GenConf.guildTag.replace("{TAG}", who));

                whoT = whoSc.getTeam(joinerGuild.getTag());
                whoT.addPlayer(joiner);    
            }
           /* for(UUID member : joinerGuild.getMembers()){
                Player memon = Bukkit.getPlayer(member);
                if(memon != null){
                    memon.setScoreboard(sc);
                }
            }*/
            
        /*for(Relation r : joinerGuild.getAlliances()){
            if(r.getWho().equals(joinerGuild.getTag())){
                Guild ally = plugin.getGuildHelper().getGuilds().get(r.getWithWho());
                Scoreboard sca = ally.getSc();
                sca.getTeam(joinerGuild.getTag()).removePlayer(joiner);
                for(UUID allypl : ally.getMembers()){
                    Player plon = Bukkit.getPlayer(allypl);
                    if(plon != null){
                        plon.setScoreboard(sca);
                    }
                }
            }
        } 
        for(Player players : Bukkit.getOnlinePlayers()){
            if(joiner.equals(players)){
                continue;
            }
            Team t2 = players.getScoreboard().getTeam(joinerGuild.getTag());
            t2.addPlayer(joiner);
        }*/
        for(Map.Entry<String, Guild> gs : plugin.getGuildHelper().getGuilds().entrySet()){
            if(gs.getValue().getTag().equals(joinerGuild.getTag())){
                continue;
            }
            Team t2 = gs.getValue().getSc().getTeam(joinerGuild.getTag());
            if(t2 != null){
                t2.addPlayer(joiner);
            }
        }
    }
    public void playerLeaveGuild(OfflinePlayer joiner){
        Guild joinerGuild = plugin.getGuildHelper().getPlayerGuild(joiner.getUniqueId());
        /*for(Relation r : joinerGuild.getAlliances()){
            if(r.getWho().equals(joinerGuild.getTag())){
                Guild ally = plugin.getGuildHelper().getGuilds().get(r.getWithWho());
                Scoreboard sc = ally.getSc();
                sc.getTeam(joinerGuild.getTag()).removePlayer(joiner);
                for(UUID allypl : ally.getMembers()){
                    Player plon = Bukkit.getPlayer(allypl);
                    if(plon != null){
                        plon.setScoreboard(sc);
                    }
                }
            }
        } */
        for(Map.Entry<String, Guild> gs : plugin.getGuildHelper().getGuilds().entrySet()){
            if(gs.getValue().getTag().equals(joinerGuild.getTag())){
                continue;
            }
            Team t2 = gs.getValue().getSc().getTeam(joinerGuild.getTag());
            if(t2 != null){
                t2.removePlayer(joiner);
            }
        }
        plugin.getTagManager().getGlobalScoreboard().getTeam(joinerGuild.getTag()).removePlayer(joiner);
        //System.out.println("Liczba obiektow team "+this.getGlobalScoreboard().getTeams().size());
        //System.out.print("Gracz opuszcza gildie");
        if(joiner.isOnline()){
            //System.out.println("Jest online");
            Player p = Bukkit.getPlayer(joiner.getUniqueId());
            p.setScoreboard(guildExistsInfoScoreboard);
        }
    }
    
    public void playerJoinServer(Player joiner){
        Guild joinerGuild = plugin.getGuildHelper().getPlayerGuild(joiner.getUniqueId());
        if(joinerGuild == null){
            joiner.setScoreboard(guildExistsInfoScoreboard);
            //System.out.println("Liczba obiektow team "+this.getGlobalScoreboard().getTeams().size());
        }else{
            joiner.setScoreboard(joinerGuild.getSc());
        }
    }
    public void playerMakeGuild(Guild g, Player p){
        Team tg = g.getSc().registerNewTeam(g.getTag());
        tg.setPrefix(GenConf.guildTag.replace("{TAG}", g.getTag()));
        tg.setDisplayName(GenConf.guildTag.replace("{TAG}", g.getTag()));
        g.getSc().getTeam(g.getTag()).addPlayer(p);
        p.setScoreboard(g.getSc());
        //***********
        Team t = this.getGlobalScoreboard().registerNewTeam(g.getTag());
        //System.out.println("Liczba obiektow team "+this.getGlobalScoreboard().getTeams().size());
        t.setPrefix(GenConf.enemyTag.replace("{TAG}", g.getTag()));
        t.setDisplayName(GenConf.enemyTag.replace("{TAG}", g.getTag()));
        t.addPlayer(p);
        for(Map.Entry<String, Guild> gs : plugin.getGuildHelper().getGuilds().entrySet()){
            if(gs.getValue().getTag().equals(g.getTag())){
                continue;
            }
            Team t2 = gs.getValue().getSc().registerNewTeam(g.getTag());
            t2.setPrefix(GenConf.enemyTag.replace("{TAG}", g.getTag()));
            t2.setDisplayName(GenConf.enemyTag.replace("{TAG}", g.getTag()));
            t2.addPlayer(p);
            
            Team t3 = g.getSc().registerNewTeam(gs.getKey());
            t3.setPrefix(GenConf.enemyTag.replace("{TAG}", gs.getKey()));
            t3.setDisplayName(GenConf.enemyTag.replace("{TAG}", gs.getKey()));
            for(UUID member : gs.getValue().getMembers()){
                t3.addPlayer(Bukkit.getOfflinePlayer(member));
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
