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
package pl.grzegorz2047.openguild2047.configuration;

import org.bukkit.configuration.Configuration;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.database.Database;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.database.mysql.MySQLData;
import pl.grzegorz2047.openguild2047.database.interfaces.SQLData;
import pl.grzegorz2047.openguild2047.database.sqlite.SQLiteData;

/**
 * @author Grzegorz
 */
public class GenConf {

    public static String prefix = "§7[§6OpenGuild§7]§7 ";
    public static boolean teampvp = false;
    public static boolean homecommand = true;
    public static boolean playerprefixenabled = true;
    public static boolean guildprefixinchat = true;
    //Max 11, bo prefix jest 16 znakowy - 5 znaków kolorujacych
    public static int maxclantag = 6;
    public static int minclantag = 4;
    public static List<String> badwords;
    public static List<ItemStack> reqitems;
    public static int MIN_CUBOID_SIZE;
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
    public static String lang;
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
    public static boolean cubEnabled;
    public static boolean hcLightning;
    public static boolean newCmdApi;
    public static boolean ranTpEnabled;
    public static boolean ranTpButton;
    public static int spawnExtra;
    public static boolean blockEnter;
    public static int blockEnterTime;
    public static String joinMsg;
    public static String quitMsg;
    public static boolean FORCE_DESC;
    public static String channelOnlyGuildKey;
    public static String guildChatFormat;
    public static String channelAllyAndGuildKey;
    public static String allyChatFormat;
    public static boolean debug;
    public static SQLData sqlData;
    public static int defaultTNTBlockTime;
    public static boolean enableTNTExplodeListener;
    public static String allyTag;
    public static String enemyTag;
    public static String guildTag;
    public static List<String> forbiddenworlds;
    public static String sqlTablePrefix;
    public static boolean cuboidCheckPlayers;
    public static int ranTpRange;
    public static boolean ANTI_LOGOUT;
    public static boolean SPAWN_COMMAND_ENABLED;
    public static boolean PREVENT_GHOST_BLOCK_PLACE;
    public static int TPA_EXPIRE_TIME;
    public static boolean TPA_ENABLED;
    public static boolean NOTIFY_NO_DROP_FROM_THIS_TYPE_OF_BLOCK;
    public static boolean DROP_ENABLED;
    public static boolean DROP_TO_EQ;
    public static boolean BLOCK_STRENGTH_2;
    public static List<Material> ELIGIBLE_DROP_BLOCKS;
    public static String chatFormat;


    public static void loadConfiguration(FileConfiguration config) {

        forbiddenworlds = config.getStringList("forbidden-worlds");
        badwords = config.getStringList("forbiddenguildnames");
        MIN_CUBOID_SIZE = config.getInt("cuboid.min-cube-size", 15);
        TPA_EXPIRE_TIME = config.getInt("tpa-expire-time", 15);
        MAX_CUBOID_RADIUS = config.getInt("cuboid.max-cube-size", 50);
        TELEPORT_COOLDOWN = config.getInt("teleport-cooldown", 10);
        EXTRA_PROTECTION = config.getBoolean("cuboid.extra-protection", false);
        BLOCK_STRENGTH_2 = config.getBoolean("block-strong-strenght-2", true);
        System.out.println("block jest na " + BLOCK_STRENGTH_2);
        NOTIFY_NO_DROP_FROM_THIS_TYPE_OF_BLOCK = config.getBoolean("drop.notify-cant-drop-from-not-eligible-block", false);

        List<String> blockList = config.getStringList("blocks-from-where-item-drops");
        ELIGIBLE_DROP_BLOCKS = new ArrayList<>();
        for (String dropMat : blockList) {
            try {
                ELIGIBLE_DROP_BLOCKS.add(Material.valueOf(dropMat));
            } catch (IllegalArgumentException ex) {
                System.out.println("Incorrect drop block " + dropMat + ". Check Material bukkit google it!");
            }

        }
        DROP_ENABLED = config.getBoolean("drop.enabled", false);
        DROP_TO_EQ = config.getBoolean("drop.drop-to-eq", false);
        SPAWN_COMMAND_ENABLED = config.getBoolean("spawn-command", false);
        CANENTERAREA = config.getBoolean("cuboid.canenterarea", true);
        PREVENT_GHOST_BLOCK_PLACE = config.getBoolean("prevent-macro-ghost-block-placing", false);
        BREAKING_ITEMS = config.getStringList("cuboid.breaking-blocks.item-types");
        BREAKING_DAMAGE = Short.parseShort(config.getString("cuboid.breaking-blocks.damage", "0"));
        SQL_DEBUG = config.getBoolean("mysql.debug", false);
        TPA_ENABLED = config.getBoolean("tpa-command", false);
        DATABASE = Database.valueOf(config.getString("database", "FILE").toUpperCase());
        loadDatabase(config);
        FILE_DIR = config.getString("file-dir", "plugins/OpenGuild2047/og.db");
        SNOOPER = config.getBoolean("snooper", true);
        ANTI_LOGOUT = config.getBoolean("fight-antilogout", true);
        TEAMPVP_MSG = config.getBoolean("teampvp-msg", false);
        FORCE_DESC = config.getBoolean("forcedesc", false);
        lang = config.getString("language").toUpperCase();

        loadBans(config);
        List listMax = config.getList("spawn.location-max");
        List listMin = config.getList("spawn.location-min");
        spawnMin = new Location(Bukkit.getWorld((String) listMin.get(0)), (Integer) listMin.get(1), Integer.MIN_VALUE, (Integer) listMin.get(2));
        spawnMax = new Location(Bukkit.getWorld((String) listMax.get(0)), (Integer) listMax.get(1), Integer.MAX_VALUE, (Integer) listMax.get(2));

        spawnMessage = config.getString("spawn.message", "&4Message 'spawn.message' in config.yml file was not found! This is an error! Please notify an operator about it!").replace("&", "§");
        blockGuildCreating = config.getBoolean("spawn.block-guild-creating", true);
        playerMoveEvent = config.getBoolean("listener.player-move-event", false);
        cubNotify = config.getBoolean("cuboid.notify-enter", true);
        cubNotifyMem = config.getBoolean("cuboid.notify-enter-members", false);
        // System.out.print("Pobral cubnotifymem "+config.getBoolean("cuboid.notify-enter-members")+" a jest "+cubNotifyMem);
        cubNotifySound = config.getBoolean("cuboid.notify-enter-sound", false);
        try {
            cubNotifySoundType = Sound.valueOf(config.getString("cuboid.notify-enter-sound-type", "ENDERMAN_DEATH"));
        } catch (IllegalArgumentException ex) {
            cubNotifySoundType = Sound.ENTITY_ENDERDRAGON_DEATH;
            Guilds.getLogger().warning("Sound type " + config.getString("cuboid.notify-enter-sound-type") + " is incorrect! Please visit http://jd.bukkit.org/rb/apidocs/org/bukkit/Sound.html for help.");
        }
        cubNotifyPerm = config.getBoolean("cuboid.notify-permission", false);
        updater = config.getBoolean("updater", false);
        nicknameTag = config.getString("chat.prefix", "&6[{TAG}] ");
        cubEnabled = config.getBoolean("cuboid.enabled", true);
        teampvp = config.getBoolean("teampvp", false);
        homecommand = config.getBoolean("home-command", true);
        if (config.getStringList("required-items") != null || !config.getStringList("required-items").isEmpty()) {
            List<String> reqItems = config.getStringList("required-items");
            reqitems = new ArrayList<ItemStack>();
            if (reqItems.size() > 54) {
                Guilds.getLogger().warning("Too many specified items (required-items)! Maximum size is 54!");
            } else {
                for (String s : reqItems) {
                    String[] info = s.split(":");
                    if (info.length != 4) {
                        Guilds.getLogger().warning("Oops! It looks like you're using an old configuration file!/You have made mistake with required-items section! We changed pattern of required-items section. Now it looks like this: Material:Durability:Data:Amount (old was: Material:Amount) - please update your config.yml Exact line is " + s);
                        break;
                    }
                    Material material = Material.valueOf(info[0]);
                    if (material == null) {
                        Guilds.getLogger().warning("Invalid material: " + info[0] + "! Check your configuration file!");
                        continue;
                    }

                    for (ItemStack i : reqitems) {
                        if (i.getType().equals(material)) {
                            Guilds.getLogger().warning("Duplicate item found! Skipping ...");
                            continue;
                        }
                    }

                    ItemStack is = new ItemStack(Material.AIR);
                    short durability = 0;
                    try {
                        durability = Short.valueOf(info[1]);
                    } catch (NumberFormatException e) {
                        Guilds.getLogger().warning("Durability must be a number! Please fix 'required-items' section in your config.yml");
                    }

                    byte data = 0;
                    try {
                        data = Byte.valueOf(info[2]);
                    } catch (NumberFormatException e) {
                        Guilds.getLogger().warning("Data must be a number! Please fix 'required-items' section in your config.yml");
                    }

                    int amount = 1;
                    try {
                        amount = Integer.valueOf(info[3]);

                        if (amount > 64) {
                            amount = 64;
                        } else if (amount < 0) {
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        Guilds.getLogger().warning("Amount must be a number! Please fix 'required-items' section in your config.yml");
                    }

                    ItemStack item = new ItemStack(material, amount, durability, data);
                    reqitems.add(item);
                }
            }
        } else {
            reqitems = new ArrayList<ItemStack>();
        }
        playerprefixenabled = config.getBoolean("tags.enabled", true);
        enemyTag = config.getString("tags.enemy", "{TAG}").replace("&", "§");
        allyTag = config.getString("tags.ally", "{TAG}").replace("&", "§");
        guildTag = config.getString("tags.guild", "{TAG}").replace("&", "§");
        guildprefixinchat = config.getBoolean("chat.guildprefixinchat", true);
        chatFormat = config.getString("chat.chatFormat", "&8{&7GUILD&8} &7{PLAYER}&7: &f{MESSAGE}");
        hcLightning = config.getBoolean("hardcore-bans.lightning", true);
        newCmdApi = config.getBoolean("use-new-command-api", false);
        ranTpEnabled = config.getBoolean("random-tp.enabled", false);
        ranTpButton = config.getBoolean("random-tp.button", true);
        ranTpRange = config.getInt("random-tp.range", 3000);
        spawnExtra = config.getInt("spawn.extra", 50);
        blockEnter = config.getBoolean("spawn.block-enter", false);
        blockEnterTime = config.getInt("spawn.block-enter-time", 10);
        joinMsg = config.getString("join-msg", "").replace("&", "§");
        quitMsg = config.getString("quit-msg", "").replace("&", "§");
        channelOnlyGuildKey = config.getString("chat.guild-key", "guild:");
        guildChatFormat = config.getString("chat.guild-format", "&8[&aGuild&8] &b{PLAYER}&7: &f{MESSAGE}").replace("&", "§");
        channelAllyAndGuildKey = config.getString("chat.ally-key", "allies:");
        allyChatFormat = config.getString("chat.ally-format", "&8[&9Ally&8] &8[&9{GUILD}&8] &b{PLAYER}&7: &f{MESSAGE}").replace("&", "§");
        debug = config.getBoolean("debug", false);
        sqlTablePrefix = config.getString("sql-table-prefix", "openguild");
        if (sqlTablePrefix.length() > 10 || sqlTablePrefix.length() < 3) {
            OpenGuild.getAPI().getLogger().warning("Could not load SQL table prefix - too low (3 chars) or too long (10 chars).");
            sqlTablePrefix = "openguild";
        }
        cuboidCheckPlayers = config.getBoolean("cuboid.block-guild-creation-when-players-are-too-close", false);

        defaultTNTBlockTime = config.getInt("listener.tnt-block-time", 30);
        enableTNTExplodeListener = config.getBoolean("listener.tnt-block-enabled", true);
    }

    private static void loadBans(Configuration configuration) {
        hcBans = configuration.getBoolean("hardcore-bans.enabled");
        hcKickMsg = configuration.getString("hardcore-bans.kick-message").replace("&", "§");
        hcLoginMsg = configuration.getString("hardcore-bans.login-message").replace("&", "§");

        String time = configuration.getString("hardcore-bans.ban-time");
        String length = time.substring(0, time.length() - 1);
        long result;
        try {
            result = Long.parseLong(length);
        } catch (NumberFormatException ex) {
            Guilds.getLogger().warning("Could not load ban time, defaults using 1 minute. Check your ban-time in config.yml file.");
            hcBantime = 60 * 1000;
            return;
        }
        if (time.endsWith("s")) { // Seconds
            result = result * 1000;
        } else if (time.endsWith("m")) { // Minutes
            result = result * 60 * 1000;
        } else if (time.endsWith("h")) { // Hours
            result = result * 60 * 60 * 1000;
        } else if (time.endsWith("d")) { // Days
            result = result * 60 * 24 * 60 * 1000;
        } else {
        } // Ticks or null
        hcBantime = result;
    }

    private static void loadDatabase(FileConfiguration config) {
        String host = config.getString("mysql.address");
        int port = config.getInt("mysql.port");
        String user = config.getString("mysql.login");
        String pass = config.getString("mysql.password");
        String name = config.getString("mysql.database");

        switch (DATABASE) {
            case FILE:
                sqlData = new SQLiteData(FILE_DIR);
                break;
            case MYSQL:
                sqlData = new MySQLData(host, port, user, pass, name);
                break;
        }
    }

    public static SQLData getSqlData() {
        return sqlData;
    }
}
