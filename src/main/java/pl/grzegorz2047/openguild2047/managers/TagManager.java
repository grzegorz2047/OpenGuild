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
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

/**
 *
 * @author Grzegorz
 */
public class TagManager {
    
    private OpenGuild plugin;
   
    private static Scoreboard guildExistsInfoScoreboard;//Mozna trzymac tu defaultowe prefixy gildii dla bezgildyjnych
    
    public TagManager(OpenGuild plugin){
        this.plugin = plugin;
        
        if(!TagManager.isInitialised()){//Kiedy trzeba to mozna zainicjowac scoreboard np. przy onEnable()
            guildExistsInfoScoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
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
    public static boolean isInitialised(){
        return guildExistsInfoScoreboard!= null; 
    }
   
    public void guildBrokeAlliance(Guild guild, Guild tobrokewith){
        for(Relation r : guild.getAlliances()){
            if(r.getWho().equals(tobrokewith.getTag()) || r.getWithWho().equals(tobrokewith.getTag())){//Trzeba to odzielic jakos na 2 przypadki (else if) zamiast ||
                Guild enemy = tobrokewith;
                Scoreboard sc = enemy.getSc();
                sc.getTeam(guild.getTag()).unregister();
                for(UUID allypl : enemy.getMembers()){
                    Player plon = Bukkit.getPlayer(allypl);
                    if(plon != null){
                        plon.setScoreboard(sc);
                    }
                }
                
                Guild enemy2 = guild;
                Scoreboard sc2 = enemy2.getSc();
                if(sc2.getTeam(tobrokewith.getTag()) == null){
                    System.out.print("tag "+tobrokewith.getTag()+" nie rejestruje i wywala null");
                    Bukkit.broadcastMessage("Wystapil blad, bo Grzegorz(Ja) nie dokonczyl tego! Wstyd!");
                    return;
                }
                sc2.getTeam(tobrokewith.getTag()).unregister();
                for(UUID allypl : enemy2.getMembers()){
                    Player plon = Bukkit.getPlayer(allypl);
                    if(plon != null){
                        plon.setScoreboard(sc2);
                    }
                }
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

        whoT = withWhoSc.registerNewTeam(who);
        whoT.setPrefix(ChatColor.BLUE+who);
        whoT.setDisplayName(ChatColor.BLUE+who);
        for(UUID whop : whoGuild.getMembers()){
            whoT.addPlayer(Bukkit.getOfflinePlayer(whop));
        }


        Team withWhoT;

        withWhoT = whoSc.registerNewTeam(withwho);
        withWhoT.setPrefix(ChatColor.BLUE+withwho);
        withWhoT.setDisplayName(ChatColor.BLUE+withwho);
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
                whoT.setPrefix(ChatColor.GREEN+who);
                whoT.setDisplayName(ChatColor.GREEN+who);
                System.out.print("whoT to "+whoT.getName()+" z dn "+whoT.getDisplayName());

                whoT = whoSc.getTeam(joinerGuild.getTag());
                whoT.addPlayer(joiner);

                
            }
            for(UUID member : joinerGuild.getMembers()){
                Player memon = Bukkit.getPlayer(member);
                if(memon != null){
                    memon.setScoreboard(sc);
                }
            }
        for(Relation r : joinerGuild.getAlliances()){
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
    }
    public void playerLeaveGuild(OfflinePlayer joiner){
        Guild joinerGuild = plugin.getGuildHelper().getPlayerGuild(joiner.getUniqueId());
        for(Relation r : joinerGuild.getAlliances()){
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
        } 
    }
    
    public void playerJoinServer(Player joiner){
        Guild joinerGuild = plugin.getGuildHelper().getPlayerGuild(joiner.getUniqueId());
        if(joinerGuild == null){
            Scoreboard sc = joiner.getScoreboard();
            for(Team t : sc.getTeams()){
                t.unregister();
            }
        }else{
            joiner.setScoreboard(joinerGuild.getSc());
            System.out.println("Liczba obiektow team "+joinerGuild.getSc().getTeams().size());
            for (Team t : joiner.getScoreboard().getTeams()) {
                System.out.print("Nazwa "+t.getName()+" displayname "+t.getDisplayName()+" prefix "+t.getPrefix());
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
