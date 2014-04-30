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
package pl.grzegorz2047.openguild2047;

import java.util.List;

import pl.grzegorz2047.openguild2047.api.Guilds;

/**
 *
 * @author Grzegorz
 */
public class GenConf {

    public static String prefix = "§7[§6OpenGuild§7]§7 ";//Mozna pokolorowac
    public static boolean teampvp = false;
    public static boolean homecommand = true;
    public static boolean playerprefixenabled = true;
    public static boolean guildprefixinchat = true;
    //Max 11, bo prefix jest 16 znakowy - 5 znaków kolorujacych
    public static int maxclantag = 6;
    public static int minclantag = 4;
    public static String colortagu = "§6";
    public static List<String> badwords;
    public static List<String> reqitems;
    public static int MIN_CUBOID_RADIUS;
    public static int MAX_CUBOID_RADIUS;
    public static int TELEPORT_COOLDOWN;
    public static boolean EXTRA_PROTECTION;
    public static boolean CANENTERAREA;
    public static List<String> BREAKING_ITEMS;
    public static short BREAKING_DAMAGE;
    public static boolean SQL_DEBUG;
    public static Database DATABASE;
    public static String FILE_DIR;
    public static boolean SNOOPER;
    public static boolean TEAMPVP_MSG;
    public static boolean hcBans;
    public static long hcBantime;
    public static String hcKickMsg;
    public static String hcLoginMsg;

    protected static void loadConfiguration() {
        badwords = OpenGuild.get().getConfig().getStringList("forbiddenguildnames");
        MIN_CUBOID_RADIUS = OpenGuild.get().getConfig().getInt("cuboid.min-radius");
        MAX_CUBOID_RADIUS = OpenGuild.get().getConfig().getInt("cuboid.max-radius");
        TELEPORT_COOLDOWN = OpenGuild.get().getConfig().getInt("teleport-cooldown");
        EXTRA_PROTECTION = OpenGuild.get().getConfig().getBoolean("cuboid.extra-protection");
        CANENTERAREA = OpenGuild.get().getConfig().getBoolean("cuboid.canenterarea");
        BREAKING_ITEMS = OpenGuild.get().getConfig().getStringList("cuboid.breaking-blocks.item-types");
        BREAKING_DAMAGE = Short.parseShort(OpenGuild.get().getConfig().getString("cuboid.breaking-blocks.damage"));
        SQL_DEBUG = OpenGuild.get().getConfig().getBoolean("mysql.debug", false);
        DATABASE = Database.valueOf(OpenGuild.get().getConfig().getString("database", "MYSQL").toUpperCase());
        FILE_DIR = OpenGuild.get().getConfig().getString("file-dir", "plugins/OpenGuild2047/guilds.yml");
        SNOOPER = OpenGuild.get().getConfig().getBoolean("snooper", true);
        TEAMPVP_MSG = OpenGuild.get().getConfig().getBoolean("teampvp-msg", false);
        loadBans();
    }

    private static void loadBans() {
        hcBans = OpenGuild.get().getConfig().getBoolean("hardcore-bans.enabled");
        hcKickMsg = OpenGuild.get().getConfig().getString("hardcore-bans.kick-message").replace("&", "");
        hcLoginMsg = OpenGuild.get().getConfig().getString("hardcore-bans.login-message").replace("&", "");
        
        String time = OpenGuild.get().getConfig().getString("hardcore-bans.ban-time");
        String lenght = time.substring(0, time.length() - 1);
        long result;
        try {
            result = Long.parseLong(lenght);
        } catch(NumberFormatException ex) {
            Guilds.getLogger().warning("Nie udalo sie wczytac czasu bana, domyslne uzywanie 1 minuty. Sprawdz czy ban-time w config.yml zostal poprawnie wpisany.");
            hcBantime = 60 * 20;
            return;
        }
        if(time.endsWith("s")) { // Seconds
            result = result * 20;
        }
        else if(time.endsWith("m")) { // Minutes
            result = result * 20 * 60;
        }
        else if(time.endsWith("h")) { // Hours
            result = result * 20 * 60 * 60;
        }
        else if(time.endsWith("d")) { // Days
            result = result * 20 * 60 * 24;
        } else {} // Ticks or null
        hcBantime = result;
    }

}
