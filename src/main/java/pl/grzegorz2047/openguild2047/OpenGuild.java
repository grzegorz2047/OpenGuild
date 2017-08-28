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
package pl.grzegorz2047.openguild2047;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import pl.grzegorz2047.openguild2047.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild2047.commands.GuildCommand;
import pl.grzegorz2047.openguild2047.commands.SpawnCommand;
import pl.grzegorz2047.openguild2047.commands.TeamCommand;
import pl.grzegorz2047.openguild2047.commands.TpaCommand;
import pl.grzegorz2047.openguild2047.configuration.GenConf;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.database.interfaces.SQLImplementationStrategy;
import pl.grzegorz2047.openguild2047.database.interfaces.SQLTables;
import pl.grzegorz2047.openguild2047.database.mysql.MySQLImplementationStrategy;
import pl.grzegorz2047.openguild2047.database.mysql.MySQLTables;
import pl.grzegorz2047.openguild2047.database.sqlite.SQLiteImplementationStrategy;
import pl.grzegorz2047.openguild2047.database.sqlite.SQLiteTables;
import pl.grzegorz2047.openguild2047.dropstone.DropConfigLoader;
import pl.grzegorz2047.openguild2047.dropstone.DropFromBlocks;
import pl.grzegorz2047.openguild2047.dropstone.DropProperties;
import pl.grzegorz2047.openguild2047.files.FileValidator;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.listeners.CuboidAndSpawnManipulationListeners;
import pl.grzegorz2047.openguild2047.listeners.ListenerLoader;
import pl.grzegorz2047.openguild2047.managers.TagManager;
import pl.grzegorz2047.openguild2047.hardcore.HardcoreHandler;
import pl.grzegorz2047.openguild2047.hardcore.HardcoreSQLHandler;
import pl.grzegorz2047.openguild2047.randomtp.RandomTPHandler;
import pl.grzegorz2047.openguild2047.relations.Relations;
import pl.grzegorz2047.openguild2047.tasks.Watcher;
import pl.grzegorz2047.openguild2047.teleporters.Teleporter;
import pl.grzegorz2047.openguild2047.teleporters.TpaRequester;
import pl.grzegorz2047.openguild2047.tntguildblocker.TntGuildBlocker;
import pl.grzegorz2047.openguild2047.updater.Updater;

import java.io.File;
import java.util.List;


/**
 * @author Grzegorz
 */
public class OpenGuild extends JavaPlugin {

    private static OGLogger logger = new OGLogger();

    private Guilds guilds;

    private TagManager tagManager;

    private SQLHandler sqlHandler;
    private Cuboids cuboids;
    private AntiLogoutManager logout;
    private BukkitTask watcher;
    private Teleporter teleporter;
    private TpaRequester tpaRequester;
    private DropFromBlocks drop;
    private TntGuildBlocker tntGuildBlocker;

    /**
     * Instance of built-in permissions manager main class.
     */
    @Override
    public void onEnable() {
        // We use UUID, which were not available in Bukkit < 1.7.5.
        System.out.print("Your Minecraft server version is " + Bukkit.getVersion());

        long startTime = System.currentTimeMillis();


        // Setup API


        // Check for updates
        Updater updater = new Updater();
        updater.checkForUpdates();

        // Validate files
        FileValidator fileValidator = new FileValidator();

        fileValidator.validateFile(getResource("config.yml"), "config");
        fileValidator.validateFile(getResource("commands.yml"), "commands");
        fileValidator.validateFile(getResource("drop.yml"), "drop");

        // Load configuration
        GenConf.loadConfiguration(getConfig());

        // Validate language file
        String translation = "messages_" + GenConf.lang.toLowerCase();
        fileValidator.validateFile(getResource(translation + ".yml"), translation);

        /*
         * If some server admin doesn't want to use PermissionsEX or other
         * permission plugin - he can use our built-in permissions manager.
         */
        //if(GenConf.useNativePermissionsManager) {
        //  TODO   
        //}
        List<DropProperties> loadedDrops = new DropConfigLoader().getLoadedListDropPropertiesFromConfig();
        this.drop = new DropFromBlocks(GenConf.ELIGIBLE_DROP_BLOCKS, loadedDrops);
        loadDB();
        this.cuboids = new Cuboids();
        this.guilds = new Guilds(sqlHandler, this, cuboids);
        sqlHandler.startWork(cuboids, guilds);
        Relations relations = new Relations();

        this.logout = new AntiLogoutManager();
        // Setup Tag Manager
        this.tagManager = new TagManager(guilds);
        teleporter = new Teleporter();
        tpaRequester = new TpaRequester();
        tntGuildBlocker = new TntGuildBlocker();

        // Load database

        HardcoreSQLHandler hardcoreSQLHandler = new HardcoreSQLHandler(sqlHandler);

        loadCommands(cuboids, guilds, teleporter, tagManager, sqlHandler, relations, hardcoreSQLHandler);

        loadAllListeners();
        sqlHandler.loadRelations(guilds);

        // Load required items section.
        CuboidAndSpawnManipulationListeners.loadItems();

        // Load default plugin-modules

        // Register all hooks to this plugin
        HardcoreHandler hardcoreHandler = new HardcoreHandler(hardcoreSQLHandler);
        hardcoreHandler.enable();
        RandomTPHandler randomTPHandler = new RandomTPHandler();
        randomTPHandler.enable(this);
        watcher = Bukkit.getScheduler().runTaskTimer(this, new Watcher(logout, teleporter, tpaRequester, guilds, relations, tntGuildBlocker), 0, 20);

        getServer().getConsoleSender().sendMessage("§a" + this.getName() + "§6 by §3grzegorz2047§6 has been enabled in " + String.valueOf(System.currentTimeMillis() - startTime) + " ms!");
    }

    @Override
    public void onDisable() {

        this.logout.dispose();
        this.watcher.cancel();
        try {
            this.sqlHandler.getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.sqlHandler = null;
        this.tagManager = null;

        int deletedFiles = 0;

        for (File file : getOGLogger().getLoggingDirectory().listFiles()) {
            String format = file.getName().substring(file.getName().length() - 4, file.getName().length());
            if (!format.equals(".log")) {
                file.delete();
                deletedFiles++;
            }
        }

        System.out.println("Deleted " + deletedFiles + " files from 'plugins/OpenGuild2047/logger'");
    }

    /**
     * This method sets executors of all commands, and
     * registers them in our API.
     */
    private void loadCommands(Cuboids cuboids, Guilds guilds, Teleporter teleporter, TagManager tagManager, SQLHandler sqlHandlers, Relations relations, HardcoreSQLHandler hardcoreSQLHandler) {
        getCommand("team").setExecutor(new TeamCommand(guilds));
        getCommand("guild").setExecutor(new GuildCommand(cuboids, guilds, teleporter, tagManager, sqlHandler, relations, hardcoreSQLHandler, this));
        if (GenConf.SPAWN_COMMAND_ENABLED) {
            getCommand("spawn").setExecutor(new SpawnCommand(teleporter));
        }
        getCommand("tpa").setExecutor(new TpaCommand(teleporter, tpaRequester));
    }

    /**
     * This method connects plugin with database using informations from
     * configuration file.
     */
    private void loadDB() {
        String host = getConfig().getString("mysql.address");
        int port = getConfig().getInt("mysql.port");
        String user = getConfig().getString("mysql.login");
        String pass = getConfig().getString("mysql.password");
        String name = getConfig().getString("mysql.database");

        SQLImplementationStrategy sqlImplementation;
        SQLTables tables;
        switch (GenConf.DATABASE) {
            case FILE:
                OpenGuild.getOGLogger().info("[SQLite] Connecting to SQLite database ...");
                sqlImplementation = new SQLiteImplementationStrategy();
                tables = new SQLiteTables();
                OpenGuild.getOGLogger().info("[SQLite] Connected to SQLite successfully!");
                break;
            case MYSQL:
                sqlImplementation = new MySQLImplementationStrategy(host, port, user, pass, name);
                tables = new MySQLTables();
                break;
            default:
                OpenGuild.getOGLogger().severe("[MySQL] Invalid database type '" + GenConf.DATABASE.name() + "'!");
                sqlImplementation = new SQLiteImplementationStrategy();
                tables = new SQLiteTables();
                OpenGuild.getOGLogger().severe("[MySQL] Invalid database type! Setting db to SQLite!");
                break;
        }

        this.sqlHandler = new SQLHandler(this, sqlImplementation, tables);
    }

    /**
     * This method registers all events.
     */
    private void loadAllListeners() {
        PluginManager pm = getServer().getPluginManager();
        ListenerLoader listenerLoader =
                new ListenerLoader
                        (this, guilds, tagManager, sqlHandler, teleporter, tpaRequester, cuboids, logout, drop, tntGuildBlocker);
        listenerLoader.loadListeners(pm);
    }

    /**
     * @return instance of OGLogger class.
     */
    public static OGLogger getOGLogger() {
        return logger;
    }


}
