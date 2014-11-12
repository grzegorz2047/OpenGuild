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

/**
 *
 * @author Grzegorz
 */
public class TagManager {
    
    private OpenGuild plugin;
   
    private static Scoreboard sc;//Mozna trzymac w pamieci tej klasy, zeby nie bawic sie tym za bardzo.
    
    public TagManager(OpenGuild plugin){
        this.plugin = plugin;
        
        if(!TagManager.isInitialised()){//Kiedy trzeba to mozna zainicjowac scoreboard np. przy onEnable()
            sc = Bukkit.getScoreboardManager().getNewScoreboard();
        }
    }
    /*
      Uzycie tagow z teamow z Bukkit API zamiast NametagEdit API.
      Potrzeba metoda do rejestracji, do czyszczenia teamow przy wylaczaniu pluginow
      oraz dodawaniu/usuwaniu gracza z teamtagu.
    
      A ponizej przykladowa metoda rejestracji teamtagu
    */
    public static Scoreboard getScoreboard(){
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
    
    public static boolean isInitialised(){
        return sc!= null; 
    }
    
}
