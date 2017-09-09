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
package pl.grzegorz2047.openguild.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.metadata.MetadataValue;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.database.Database;

import java.util.HashMap;
import java.util.List;

/**
 * @author Grzegorz
 */
public class GenConf {

    private HashMap<String, MetadataValue> values = new HashMap<String, MetadataValue>();

    public static boolean TEAM_PVP_ENABLED = false;
     public static boolean PLAYER_GUILD_TAGS_ENABLED = true;
    public static boolean GUILD_PREFIX_IN_CHAT_ENABLED = true;
    //Max 11, bo prefix jest 16 znakowy - 5 znaków kolorujacych
    public static int maxclantag = 6;
    public static int minclantag = 4;
    public static List<String> BAD_WORDS;
    public static int MIN_CUBOID_SIZE;
    private static int MAX_CUBOID_RADIUS;
    public static int TELEPORT_COOLDOWN;
    public static boolean EXTRA_PROTECTION;
    public static boolean CANENTERAREA;
    public static List<String> BREAKING_ITEMS;
    public static short BREAKING_DAMAGE;
    private static boolean SQL_DEBUG;
    public static Database DATABASE;
    public static String FILE_DIR;
    private static boolean SNOOPER;
    public static Location SPAWN_MAX_CORNER_LOCATION;
    public static Location SPAWN_MIN_CORNER_LOCATION;
    public static boolean BLOCK_GUILD_CREATION_ON_SPAWN;
    public static boolean NOTIFY_GUILD_MEMBERS_ABOUT_SOMEONE_ENTERED_CUBOID;

    private static boolean cubNotifyPerm;
    public static boolean UPDATER_ENABLED;
    private static String CHAT_PREFIX;
    public static boolean LIGHTNING_ON_DEATH_ENABLED;
    private static boolean NEW_COMMAND_API_USED;
    public static boolean RANDOM_TP_ENABLED;
    public static boolean USE_BUTTON_FOR_RANDOM_TP;
    private static int SPAWN_EXTRA_SPACE;
    public static boolean BLOCK_ENTER_ON_SPAWN_ENABLED;
    public static int BLOCK_ENTER_ON_SPAWN_TIME;
    public static boolean FORCE_DESC;
    public static String GUILD_ONLY_CHAT_MSG_KEY;
    public static String GUILD_ONLY_CHAT_FORMAT;
    public static String GUILD_AND_ALLY_ONLY_CHAT_KEY;
    public static String GUILD_AND_ALLY_ONLY_CHAT_FORMAT;
    public static boolean IS_IN_DEBUG_MODE;
    private static int TNT_PLACE_BLOCK_CUBOID_TIME;
    public static String ALLY_GUILD_TAG_FORMAT;
    public static String ENEMY_GUILD_TAG_FORMAT;
    public static String OWN_GUILD_TAG_FORMAT;
    public static List<String> FORBIDDEN_WORLDS;
     public static int RANDOM_TP_RANGE;
    public static boolean ANTI_LOGOUT;
    public static boolean SPAWN_COMMAND_ENABLED;
    public static boolean PREVENT_GHOST_BLOCK_PLACE;
    public static int TPA_EXPIRE_TIME;
    public static boolean TPA_ENABLED;
    public static boolean NOTIFY_NO_DROP_FROM_THIS_TYPE_OF_BLOCK;
    public static boolean DROP_ENABLED;
    public static boolean DROP_TO_EQ;


    public static void loadConfiguration(FileConfiguration config) {
        FORBIDDEN_WORLDS = config.getStringList("forbidden-worlds");
        BAD_WORDS = config.getStringList("forbiddenguildnames");
        MIN_CUBOID_SIZE = config.getInt("cuboid.min-cube-size", 15);
        TPA_EXPIRE_TIME = config.getInt("tpa-expire-time", 15);
        MAX_CUBOID_RADIUS = config.getInt("cuboid.max-cube-size", 50);
        TELEPORT_COOLDOWN = config.getInt("teleport-cooldown", 10);
        EXTRA_PROTECTION = config.getBoolean("cuboid.extra-protection", false);
        NOTIFY_NO_DROP_FROM_THIS_TYPE_OF_BLOCK = config.getBoolean("drop.notify-cant-drop-from-not-eligible-block", false);

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
        FILE_DIR = config.getString("file-dir", "plugins/OpenGuild/og.db");
        SNOOPER = config.getBoolean("snooper", true);
        ANTI_LOGOUT = config.getBoolean("fight-antilogout", true);
        FORCE_DESC = config.getBoolean("forcedesc", false);

        loadSpawnCoords(config.getList("spawn.location-max"), config.getList("spawn.location-min"));

        BLOCK_GUILD_CREATION_ON_SPAWN = config.getBoolean("spawn.block-guild-creating", true);

        NOTIFY_GUILD_MEMBERS_ABOUT_SOMEONE_ENTERED_CUBOID = config.getBoolean("cuboid.notify-enter-members", false);
        // System.out.print("Pobral cubnotifymem "+config.getBoolean("cuboid.notify-enter-members")+" a jest "+NOTIFY_GUILD_MEMBERS_ABOUT_SOMEONE_ENTERED_CUBOID);


        cubNotifyPerm = config.getBoolean("cuboid.notify-permission", false);
        UPDATER_ENABLED = config.getBoolean("updater", false);
        CHAT_PREFIX = config.getString("chat.prefix", "&6[{TAG}] ");
        TEAM_PVP_ENABLED = config.getBoolean("teampvp", false);

        PLAYER_GUILD_TAGS_ENABLED = config.getBoolean("tags.enabled", true);
        ENEMY_GUILD_TAG_FORMAT = config.getString("tags.enemy", "{TAG}").replace("&", "§");
        ALLY_GUILD_TAG_FORMAT = config.getString("tags.ally", "{TAG}").replace("&", "§");
        OWN_GUILD_TAG_FORMAT = config.getString("tags.guild", "{TAG}").replace("&", "§");
        GUILD_PREFIX_IN_CHAT_ENABLED = config.getBoolean("chat.guildprefixinchat", true);
        LIGHTNING_ON_DEATH_ENABLED = config.getBoolean("hardcore-bans.lightning", true);
        NEW_COMMAND_API_USED = config.getBoolean("use-new-command-api", false);
        RANDOM_TP_ENABLED = config.getBoolean("random-tp.enabled", false);
        USE_BUTTON_FOR_RANDOM_TP = config.getBoolean("random-tp.button", true);
        RANDOM_TP_RANGE = config.getInt("random-tp.range", 3000);
        SPAWN_EXTRA_SPACE = config.getInt("spawn.extra", 50);
        BLOCK_ENTER_ON_SPAWN_ENABLED = config.getBoolean("spawn.block-enter", false);
        BLOCK_ENTER_ON_SPAWN_TIME = config.getInt("spawn.block-enter-time", 10);
        GUILD_ONLY_CHAT_MSG_KEY = config.getString("chat.guild-key", "guild:");
        GUILD_ONLY_CHAT_FORMAT = config.getString("chat.guild-format", "&8[&aGuild&8] &b{PLAYER}&7: &f{MESSAGE}").replace("&", "§");
        GUILD_AND_ALLY_ONLY_CHAT_KEY = config.getString("chat.ally-key", "allies:");
        GUILD_AND_ALLY_ONLY_CHAT_FORMAT = config.getString("chat.ally-format", "&8[&9Ally&8] &8[&9{GUILD}&8] &b{PLAYER}&7: &f{MESSAGE}").replace("&", "§");
        IS_IN_DEBUG_MODE = config.getBoolean("debug", false);

    }


    private static void loadSpawnCoords(List listMax, List listMin) {
        SPAWN_MIN_CORNER_LOCATION = new Location(Bukkit.getWorld((String) listMin.get(0)), (Integer) listMin.get(1), Integer.MIN_VALUE, (Integer) listMin.get(2));
        SPAWN_MAX_CORNER_LOCATION = new Location(Bukkit.getWorld((String) listMax.get(0)), (Integer) listMax.get(1), Integer.MAX_VALUE, (Integer) listMax.get(2));
    }


}
