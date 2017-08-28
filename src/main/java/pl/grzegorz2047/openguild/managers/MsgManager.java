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

import java.io.File;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.grzegorz2047.openguild.configuration.GenConf;

/**
 *
 * @author Grzegorz
 */
public class MsgManager {

    private static HashMap<String, String> messages;

    public static File file = new File("plugins/OpenGuild/messages_" + GenConf.lang.toLowerCase() + ".yml");
    public static FileConfiguration config = YamlConfiguration.loadConfiguration(file);

    public static String createguildsuccess = get("createguildsuccess", "Gildia zostala pomysnie stworzona.");
    public static String cmdonlyforplayer = get("cmdonlyforplayer", "Komenda jedynie dla graczy na serwerze!");
    public static String alreadyinguild = get("alreadyinguild", "Nie mozesz tego wykonac, poniewaz jestes w gildii!");
    public static String unsupportedchars = get("unsupportedchars", "Nazwa gildii moze zawierac jedynie znaki z tego zakresu [0-9a-zA-Z]");
    public static String toolongshorttag = get("toolongshorttag", "Twoja nazwa gildii jest za dluga badz za krotka!"
            + "\n min. ilosc: " + GenConf.minclantag
            + "\n max. ilosc: " + GenConf.maxclantag).replace("{MIN}", String.valueOf(GenConf.minclantag)).replace("{MAX}", String.valueOf(GenConf.maxclantag));
    public static String illegaltag = get("illegaltag", "Twoja nazwa posiada zabronione slowa!");
    public static String notinguild = get("notinguild", "Aby to wykonac musisz byc w gildii!");
    public static String errornotinguild = get("errornotinguild", "Nie jestes w gildii?! Czyszcze bledne dane!");
    public static String teleportsuccess = get("teleportsuccess", "Zostales pomyslnie przeteleportowany!");
    public static String leaveguildsuccess = get("leaveguildsuccess", "Pomyslnie opusciles gildie!");
    public static String nomembersinguild = get("nomembersinguild", "Aktualnie nie posiadasz zadnych czlonkow w gildii.");
    public static String notenoughitems = get("notenoughitems", "Posiadasz nie wystarczaczajaca liczbe przedmiotow na stworzenie gildii");
    public static String homenotenabled = get("homenotenabled", "Na tym serwerze komenda /gildia dom jest wylaczona!");
    public static String wrongcmdargument = get("wrongcmdargument", "Podales bledny argument!");
    public static String wronguseddesccmd = get("wronguseddesccmd", "Poprawne uzycie: /gildia dom zmien <Tresc>");
    public static String playernotleader = get("playernotleader", "Nie mozesz tego wykonac, poniewaz nie jestes liderem gildii!");
    public static String playerneverplayed = get("playerneverplayed", "Nie mozesz przekazac lidera osobie, ktora nigdy nie grala na serwerze!");
    public static String guilddisbandsuccess = get("guilddisbandsuccess", "Pomyslnie usunieto gildie!");
    public static String gildtocloseothers = get("gildtocloseothers", "Nie mozesz tworzyc gildii blisko innej gildii!");
    public static String playerstooclose = get("playerstooclose", "Nie mozesz stworzyc gildii blisko innych graczy!");
    public static String guildexists = get("guildexists", "Gildia o takiej nazwie juz istnieje!");
    public static String cantdoitonsomeonearea = get("cantdoitonsomeonearea", "Nie mozesz budowac na terenie obcej gildii.");
    public static String guildjoinsuccess = get("guildjoinsuccess", "Pomyslnie dolaczyles do gildii");
    public static String playernotinvited = get("playernotinvited", "Nie jestes zaproszony! Wysylam zapytanie do lidera!");
    public static String askforaccept = get("askforaccept", "Lider gildii prosi o dolaczenie do gildii: ");
    public static String playerhasguild = get("playerhasguild", "Gracz, ktorego probujesz zaprosic jest juz w gildii!");
    public static String invitedplayersuccessfullyjoined = get("invitedplayersuccessfullyjoined", "Pomyslnie dolaczyles do gildii!");
    public static String guildinviteaccepted = get("guildinviteaccepted", "twoja prosba o dolaczenie zostala pomyslnie rozpatrzona!");
    public static String playernotoninvitedlist = get("playernotoninvitedlist", "Ten gracz nie jest na liscie zaproszonych!");
    public static String guilddoesntexists = get("guilddoesntexists", "Gildia do ktorej probujesz dolaczyc nie istnieje!");
    public static String leadernotonline = get("leadernotonline", "Lider aktualnie nie jest na serwerze!");
    public static String notyetaccepted = get("notyetaccepted", "Twoja prosba nie zostala jeszcze zaakceptowana");
    public static String desctoolong = get("desctoolong", "Opis gildii jest za dlugi");
    public static String invitesendsuccess = get("invitesendsuccess", " Pomyslnie wyslano zaproszenie do gracza");
    public static String kickplayernotinguild = get("kickplayernotinguild", " Gracz, ktorego kickujesz nie jest w zadnej gildii");
    public static String playernotinthisguild = get("playernotinthisguild", " Gracz nie jest w twojej gildii");
    public static String playerkicked = get("playerkicked", " Zostales wyrzucony z gildii!");
    public static String playerkicksuccess = get("playerkicksuccess", " Gracz zostal pomyslnie wyrzucony z gildii!");
    public static String addedAllyGuild = get("addedAllyGuild", " Gildia {Guild} zostala dodana do listy sojusznikow!");
    public static String hometpdontmove = get("hometpdontmove", "Juz trwa odliczanie! Prosze sie nie ruszac.");
    public static String timetotpnotify = get("timetotpnotify","Zostaniesz teleportowany/a do gildii {GUILD} za {HOMETPSECONDS} sekund!");
    
    public static String get(String path) {
        return GenConf.prefix + getIgnorePref(path);
    }

    public static String getIgnorePref(String path) {
        return getIgnorePref(path, getNullMessage(GenConf.lang, path));
    }

    public static String get(String path, String def) {
        return GenConf.prefix + getIgnorePref(path, getNullMessage(GenConf.lang, path));
    }
    
    public static String getIgnorePref(String path, String def) {
        if(messages == null) {
            loadMessages();
        }
        if(messages.get(path) == null) {
            return def;
        } else {
            return messages.get(path);
        }
    }

    private static void loadMessages() {
        messages = new HashMap<String, String>();
        for(String path : config.getConfigurationSection("").getKeys(false)) {
            messages.put(path, config.getString(path).replace("&", "§"));
        }
    }

    public static String getNullMessage(String lang, String path) {
        String result = "";
        
        if(lang.equalsIgnoreCase("PL"))
            result = "Wiadomosc nie znaleziona";
        else if(lang.equalsIgnoreCase("SV"))
            result = "Meddelande har inte hittas";
        else
            result = "Message not found";
        
        return ChatColor.RED + result + " :( MSG: " + path;
    }

}
