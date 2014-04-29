/*
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package pl.grzegorz2047.openguild2047.tagmanager;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.SimpleGuild;

/**
 *
 * @author Grzegorz
 */
public class TagManager {
   
    private static Scoreboard sc;//Mozna trzymac w pamieci tej klasy, zeby nie bawic sie tym za bardzo.
    
    public TagManager(){
        if(sc == null){//Kiedy trzeba to mozna zainicjowac scoreboard np. przy onEnable()
            sc = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        
    }
    /*
      Uzycie tagow z teamow z Bukkit API zamiast NametagEdit API.
      Potrzeba metoda do rejestracji, do czyszczenia teamow przy wylaczaniu pluginow
      oraz dodawaniu/usuwaniu gracza z teamtagu.
    
      A ponizej przykladowa metoda rejestracji teamtagu
    */
    public void registerTeamTag(String tag, SimpleGuild sg){
        if(sc.getTeam(tag)== null){
            Team teamtag = sc.registerNewTeam(tag);
            teamtag.setPrefix(GenConf.colortagu + tag + "§r ");
            teamtag.setDisplayName(GenConf.colortagu + tag + "§r ");
            for(UUID uuid : sg.getMembers()){
                teamtag.addPlayer(Bukkit.getOfflinePlayer(uuid));
            }
        }
        
    }
    
}
