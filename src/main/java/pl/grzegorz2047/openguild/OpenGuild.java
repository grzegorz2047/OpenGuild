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
package pl.grzegorz2047.openguild;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import pl.grzegorz2047.openguild.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild.commands.GuildCommand;
import pl.grzegorz2047.openguild.commands.SpawnCommand;
import pl.grzegorz2047.openguild.commands.TeamCommand;
import pl.grzegorz2047.openguild.commands.TpaCommand;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.database.TempPlayerData;
import pl.grzegorz2047.openguild.dropstone.DropConfigLoader;
import pl.grzegorz2047.openguild.dropstone.DropFromBlocks;
import pl.grzegorz2047.openguild.dropstone.DropProperties;
import pl.grzegorz2047.openguild.files.FileDataUpdater;
import pl.grzegorz2047.openguild.files.FileNotValidetedException;
import pl.grzegorz2047.openguild.files.YamlFileCreator;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.hardcore.HardcoreHandler;
import pl.grzegorz2047.openguild.hardcore.HardcoreSQLHandler;
import pl.grzegorz2047.openguild.listeners.*;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.managers.TagManager;
import pl.grzegorz2047.openguild.relations.Relations;
import pl.grzegorz2047.openguild.spawn.ModuleSpawn;
import pl.grzegorz2047.openguild.spawn.SpawnChecker;
import pl.grzegorz2047.openguild.tasks.Watcher;
import pl.grzegorz2047.openguild.teleporters.Teleporter;
import pl.grzegorz2047.openguild.teleporters.TpaRequester;
import pl.grzegorz2047.openguild.tntguildblocker.TntGuildBlocker;
import pl.grzegorz2047.openguild.updater.Updater;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.logging.Level;


/**
 * @author Grzegorz
 */
public class OpenGuild extends JavaPlugin {

    private static OGLogger logger = new OGLogger();
    private Guilds guilds;

    private TagManager tagManager;

    private Cuboids cuboids;
    private AntiLogoutManager logout;
    private BukkitTask watcher;
    private Teleporter teleporter;
    private TpaRequester tpaRequester;
    private DropFromBlocks drop;
    private TntGuildBlocker tntGuildBlocker;
    private Updater updater = new Updater(getConfig());

    private static String rootPath = "plugins/";
    private static String openGuildPluginFolderName = "OpenGuild";
    private String fullPath = rootPath + openGuildPluginFolderName;

    /**
     * Instance of built-in permissions manager main class.
     */
    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        updater.checkForUpdates();
        try {
            loadConfigFiles();
        } catch (IOException | FileNotValidetedException e) {
            disableOnCriticalError();
            e.printStackTrace();
            return;
        }
        FileConfiguration mainConfig = getConfig();
        SpawnChecker.loadSpawnCoords(mainConfig.getList("spawn.location-max"), mainConfig.getList("spawn.location-min"));

        try {
            loadMessagesFile();
        } catch (IOException e) {
            e.printStackTrace();
            disableOnCriticalError();
            return;
        }
        logger.setDebugMode(mainConfig.getBoolean("debug", false));
        /*
         * If some server admin doesn't want to use PermissionsEX or other
         * permission plugin - he can use our built-in permissions manager.
         */
        //if(GenConf.useNativePermissionsManager) {
        //  TODO   
        //}
        loadDropFromBlocks(mainConfig);
        SQLHandler sqlHandler = new SQLHandler(this);
        sqlHandler.loadSQLNames(mainConfig.getString("sql-table-prefix", "openguild"));
        sqlHandler.loadDB(mainConfig.getString("mysql.address"), mainConfig.getInt("mysql.port"), mainConfig.getString("mysql.login"), mainConfig.getString("mysql.password"), mainConfig.getString("mysql.database"));

        this.cuboids = new Cuboids();
        this.guilds = new Guilds(sqlHandler, this, cuboids);
        this.guilds.loadRequiredItemsForGuild(mainConfig.getStringList("required-items"));
        sqlHandler.startWork(cuboids, guilds);

        this.logout = new AntiLogoutManager();
        // Setup Tag Manager
        this.tagManager = new TagManager(guilds, mainConfig);
        teleporter = new Teleporter();
        tpaRequester = new TpaRequester(mainConfig);
        tntGuildBlocker = new TntGuildBlocker();


        HardcoreSQLHandler hardcoreSQLHandler = new HardcoreSQLHandler(sqlHandler);
        HardcoreHandler hardcoreHandler = new HardcoreHandler(hardcoreSQLHandler, this);
        hardcoreHandler.loadBans(mainConfig.getBoolean("hardcore-bans.enabled"), mainConfig.getString("hardcore-bans.kick-message").replace("&", "§"), mainConfig.getString("hardcore-bans.login-message").replace("&", "§"), mainConfig.getString("hardcore-bans.ban-time"));


        ModuleSpawn moduleSpawn = new ModuleSpawn();
        moduleSpawn.enable(this);

        Relations relations = new Relations();
        loadCommands(cuboids, guilds, teleporter, tagManager, sqlHandler, relations, hardcoreSQLHandler, hardcoreHandler);

        loadAllListeners(sqlHandler);
        sqlHandler.loadRelations(guilds);

        loadWatcherTask(relations);

        String enabledMsg = "§a" + this.getName() + "§6 by §3grzegorz2047§6 has been enabled in " + String.valueOf(System.currentTimeMillis() - startTime) + " ms!";
        showFancyMessageInConsole(enabledMsg);
    }

    private void loadMessagesFile() throws IOException {
        String language = getConfig().getString("language").toUpperCase();
        MsgManager.setLANG(language);
        String translation = "messages_" + language.toLowerCase();

        File translationFile = new File(fullPath + "/" + translation + ".yml");
        InputStream jarTranslationFile = this.getResource(translation + ".yml");
        final InputStreamReader inputStreamReader = new InputStreamReader(jarTranslationFile, StandardCharsets.UTF_8);

        YamlFileCreator yamlFileCreator = new YamlFileCreator();
        FileDataUpdater fileDataUpdater = new FileDataUpdater();
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        yamlFileCreator.prepareFileToLoadYamlConfiguration(translationFile, bufferedReader);
        fileDataUpdater.updateFile(jarTranslationFile, translationFile);
        FileConfiguration newestVersionOfTranslationConfig = fileDataUpdater.getUpdatedConfig();
        MsgManager.loadMessages(newestVersionOfTranslationConfig);
    }

    private void disableOnCriticalError() {
        logger.log(Level.SEVERE, "Critical error: File didnt load correctly! Plugin shuts down.");
        Bukkit.getPluginManager().disablePlugin(this);
    }

    private void showFancyMessageInConsole(String message) {
        getServer().getConsoleSender().sendMessage(message);
    }

    private void loadWatcherTask(Relations relations) {
        watcher = Bukkit.getScheduler().runTaskTimer(this, new Watcher(logout, teleporter, tpaRequester, guilds, relations, tntGuildBlocker, getConfig()), 0, 20);
    }

    private void loadDropFromBlocks(FileConfiguration config) {
        List<DropProperties> loadedDrops = new DropConfigLoader().getLoadedListDropPropertiesFromConfig();
        this.drop = new DropFromBlocks();
        this.drop.loadMainDropData(config.getStringList("blocks-from-where-item-drops"), loadedDrops, getConfig());
    }

    private void loadConfigFiles() throws IOException, FileNotValidetedException {
        InputStream jarConfigFile = getResource("config.yml");
        InputStream jarCommandsFile = getResource("commands.yml");
        InputStream jarDropFile = getResource("drop.yml");
        YamlFileCreator yamlFileCreator = new YamlFileCreator();

        File localConfigFile = new File(fullPath + File.separator + "config" + ".yml");
        File localCommandsFile = new File(fullPath + File.separator + "commands" + ".yml");
        File localDropFile = new File(fullPath + File.separator + "drop" + ".yml");

        Files.copy(jarConfigFile, localConfigFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(jarCommandsFile, localCommandsFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(jarDropFile, localDropFile.toPath(), StandardCopyOption.REPLACE_EXISTING);


       /* FileDataUpdater.updateFile(jarConfigFile, localConfigFile);
        if (!FileDataUpdater.isValidated()) {
            throw new FileNotValidetedException("File config was not loaded");
        }
        FileDataUpdater.updateFile(jarCommandsFile, localCommandsFile);
        if (!FileDataUpdater.isValidated()) {
            throw new FileNotValidetedException("File commands was not loaded");
        }
        FileDataUpdater.updateFile(jarDropFile, localDropFile);
        if (!FileDataUpdater.isValidated()) {
            throw new FileNotValidetedException("File drop was not loaded");
        }*/
    }
/*
    public static void loadConfiguration(FileConfiguration config) {
        boolean SQL_DEBUG = config.getBoolean("mysql.debug", false);
        boolean SNOOPER = config.getBoolean("snooper", true);
        // System.out.print("Pobral cubnotifymem "+config.getBoolean("cuboid.notify-enter-members")+" a jest "+NOTIFY_GUILD_MEMBERS_ABOUT_SOMEONE_ENTERED_CUBOID);
        boolean cubNotifyPerm = config.getBoolean("cuboid.notify-permission", false);
        String CHAT_PREFIX = config.getString("chat.prefix", "&6[{TAG}] ");
        boolean NEW_COMMAND_API_USED = config.getBoolean("use-new-command-api", false);
        int SPAWN_EXTRA_SPACE = config.getInt("spawn.extra", 50);
    }
*/


    @Override
    public void onDisable() {
        try {
            this.logout.dispose();
            this.watcher.cancel();
        } catch (Exception ignored) {

        }
        this.tagManager = null;
        removeUnnessessaryLogFiles();
    }

    private void removeUnnessessaryLogFiles() {
        int deletedFiles = 0;

        for (File file : getOGLogger().getLoggingDirectory().listFiles()) {
            String fileName = file.getName();
            int fileNameLength = fileName.length();
            String format = fileName.substring(fileNameLength - 4, fileNameLength);
            if (!".log".equals(format)) {
                file.delete();
                deletedFiles++;
            }
        }

        System.out.println("Deleted " + deletedFiles + " files from 'plugins/OpenGuild/logger'");
    }

    /**
     * This method sets executors of all commands, and
     * registers them in our API.
     */
    private void loadCommands(Cuboids cuboids, Guilds guilds, Teleporter teleporter, TagManager tagManager, SQLHandler sqlHandler, Relations relations, HardcoreSQLHandler hardcoreSQLHandler, HardcoreHandler hardcoreHandler) {
        getCommand("team").setExecutor(new TeamCommand(guilds, getConfig()));
        getCommand("guild").setExecutor(new GuildCommand(cuboids, guilds, teleporter, tagManager, sqlHandler, relations, hardcoreSQLHandler, hardcoreHandler, this));
        boolean spawnCommand = getConfig().getBoolean("spawn-command", false);

        if (spawnCommand) {
            getCommand("spawn").setExecutor(new SpawnCommand(teleporter, getConfig()));
        }
        getCommand("tpa").setExecutor(new TpaCommand(teleporter, tpaRequester, getConfig()));
    }


    /**
     * This method registers all events.
     */
    private void loadAllListeners(SQLHandler sqlHandler) {
        PluginManager pm = getServer().getPluginManager();
        TempPlayerData tempPlayerData = new TempPlayerData();
        pm.registerEvents(new PlayerJoinListener(guilds, tagManager, sqlHandler, tempPlayerData, updater), this);
        pm.registerEvents(new PlayerChatListener(guilds, getConfig()), this);
        pm.registerEvents(new PlayerDeathListener(sqlHandler, logout, guilds), this);
        pm.registerEvents(new PlayerKickListener(teleporter, cuboids, tpaRequester, guilds), this);
        pm.registerEvents(new PlayerQuitListener(guilds, cuboids, logout, teleporter, tpaRequester, tempPlayerData), this);
        pm.registerEvents(new PlayerCacheListenersController(tempPlayerData, sqlHandler), this);
        boolean isStrength2Disabled = getConfig().getBoolean("block-strong-strength-2", true);
        if (isStrength2Disabled) {
            pm.registerEvents(new EnchantInsertListener(), this);
        }

        pm.registerEvents(new CuboidAndSpawnManipulationListeners(cuboids, drop, guilds, getConfig()), this);

        pm.registerEvents(new EntityDamageByEntityListener(logout, guilds, getConfig()), this);

        boolean enabledPlayerMoveListener = getConfig().getBoolean("listener.player-move-event", false);

        if (enabledPlayerMoveListener) {
            pm.registerEvents(new PlayerMoveListener(guilds, cuboids, getConfig()), this);
        }
        boolean blockPlacingBlocksOnTntExplodeEnabled = getConfig().getBoolean("listener.tnt-block-enabled", true);
        if (blockPlacingBlocksOnTntExplodeEnabled) {
            pm.registerEvents(new TNTExplode(guilds, drop, tntGuildBlocker, getConfig()), this);
        }
    }

    /**
     * @return instance of OGLogger class.
     */
    public static OGLogger getOGLogger() {
        return logger;
    }


}
