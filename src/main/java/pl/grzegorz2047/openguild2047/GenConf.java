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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import pl.grzegorz2047.openguild2047.api.Guilds;

/**
 *
 * @author Grzegorz
 */
public class GenConf {

    public enum Lang {
        EN, PL, SV;
    }

    public static String prefix = "§7[§6OpenGuild§7]§7 ";
    public static boolean teampvp = false;
    public static boolean homecommand = true;
    public static boolean playerprefixenabled = true;
    public static boolean guildprefixinchat = true;
    //Max 11, bo prefix jest 16 znakowy - 5 znaków kolorujacych
    public static int maxclantag = 6;
    public static int minclantag = 4;
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
    public static Lang lang;
    public static Location spawnMax;
    public static Location spawnMin;
    public static String spawnMessage;
    public static boolean blockGuildCreating;
    public static boolean playerMoveEvent;
    public static boolean cubNotify;
    public static boolean cubNotifyMem;
    public static boolean cubNotifySound;
    public static Sound cubNotifySoundType;
    public static boolean cubNotifyPerm;
    public static boolean updater;
    public static String nicknameTag;

    protected static void loadConfiguration() {
        FileConfiguration config = OpenGuild.get().getConfig();

        badwords = OpenGuild.get().getConfig().getStringList("forbiddenguildnames");
        MIN_CUBOID_RADIUS = OpenGuild.get().getConfig().getInt("cuboid.min-radius", 15);
        MAX_CUBOID_RADIUS = OpenGuild.get().getConfig().getInt("cuboid.max-radius", 50);
        TELEPORT_COOLDOWN = OpenGuild.get().getConfig().getInt("teleport-cooldown", 10);
        EXTRA_PROTECTION = OpenGuild.get().getConfig().getBoolean("cuboid.extra-protection", false);
        CANENTERAREA = OpenGuild.get().getConfig().getBoolean("cuboid.canenterarea", true);
        BREAKING_ITEMS = OpenGuild.get().getConfig().getStringList("cuboid.breaking-blocks.item-types");
        BREAKING_DAMAGE = Short.parseShort(OpenGuild.get().getConfig().getString("cuboid.breaking-blocks.damage", "0"));
        SQL_DEBUG = OpenGuild.get().getConfig().getBoolean("mysql.debug", false);
        DATABASE = Database.valueOf(OpenGuild.get().getConfig().getString("database", "FILE").toUpperCase());
        FILE_DIR = OpenGuild.get().getConfig().getString("file-dir", "plugins/OpenGuild2047/og.db");
        SNOOPER = OpenGuild.get().getConfig().getBoolean("snooper", true);
        TEAMPVP_MSG = OpenGuild.get().getConfig().getBoolean("teampvp-msg", false);

        String langString = OpenGuild.get().getConfig().getString("language").toUpperCase();
        try {
            lang = Lang.valueOf(langString);
        } catch(IllegalArgumentException ex) {
            lang = Lang.PL;
            Guilds.getLogger().warning("Could not load " + langString + " as language - using Polish.");
        }

        loadBans();
        List listMax = config.getList("spawn.location-max");
        List listMin = config.getList("spawn.location-min");
        spawnMax = new Location(Bukkit.getWorld((String) listMax.get(0)), (Integer )listMax.get(1), 0, (Integer) listMax.get(2));
        spawnMin = new Location(Bukkit.getWorld((String) listMin.get(0)), (Integer )listMin.get(1), 0, (Integer) listMin.get(2));

        spawnMessage = config.getString("spawn.message", "&4Message 'spawn.message' in config.yml file was not found! This is an error! Please notify an operator about it!").replace("&", "§");
        blockGuildCreating = config.getBoolean("spawn.block-guild-creating", true);
        playerMoveEvent = config.getBoolean("player-move-event", false);
        cubNotify = config.getBoolean("cuboid.notify-enter", true);
        cubNotifyMem = config.getBoolean("cuboid.notify-enter-members", false);
        cubNotifySound = config.getBoolean("cuboid.notify-enter-sound", false);
        try {
            cubNotifySoundType = Sound.valueOf(config.getString("cuboid.notify-enter-sound-type", "ENDERMAN_DEATH"));
        } catch(IllegalArgumentException ex) {
            cubNotifySoundType = Sound.ENDERDRAGON_DEATH;
            Guilds.getLogger().warning("Sound type " + config.getString("cuboid.notify-enter-sound-type") + " is incorrect! Please visit http://jd.bukkit.org/rb/apidocs/org/bukkit/Sound.html for help.");
        }
        cubNotifyPerm = config.getBoolean("cuboid.notify-permission", false);
        updater = config.getBoolean("updater", false);
        nicknameTag = config.getString("nickname-tag", "&6[{TAG}] ");
    }

    private static void loadBans() {
        hcBans = OpenGuild.get().getConfig().getBoolean("hardcore-bans.enabled");
        hcKickMsg = OpenGuild.get().getConfig().getString("hardcore-bans.kick-message").replace("&", "§");
        hcLoginMsg = OpenGuild.get().getConfig().getString("hardcore-bans.login-message").replace("&", "§");

        String time = OpenGuild.get().getConfig().getString("hardcore-bans.ban-time");
        String length = time.substring(0, time.length() - 1);
        long result;
        try {
            result = Long.parseLong(length);
        } catch(NumberFormatException ex) {
            Guilds.getLogger().warning("Could not load ban time, defaults using 1 minute. Check your ban-time in config.yml file.");
            hcBantime = 60 * 1000;
            return;
        }
        if(time.endsWith("s")) { // Seconds
            result = result * 1000;
        }
        else if(time.endsWith("m")) { // Minutes
            result = result * 60 * 1000;
        }
        else if(time.endsWith("h")) { // Hours
            result = result * 60 * 60 * 1000;
        }
        else if(time.endsWith("d")) { // Days
            result = result * 60 * 24 * 60 * 1000;
        } else {} // Ticks or null
        hcBantime = result;
    }

}
