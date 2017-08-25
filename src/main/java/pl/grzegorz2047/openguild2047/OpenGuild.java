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

import pl.grzegorz2047.openguild2047.configuration.GenConf;
import pl.grzegorz2047.openguild2047.files.FileValidator;
import pl.grzegorz2047.openguild2047.guilds.Guild;
import pl.grzegorz2047.openguild2047.interfaces.Logger;
import pl.grzegorz2047.openguild2047.interfaces.OpenGuildPlugin;
import pl.grzegorz2047.openguild2047.addons.Hooks;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import pl.grzegorz2047.openguild2047.antilogout.AntiLogoutManager;
import pl.grzegorz2047.openguild2047.api.OpenGuildBukkitPlugin;
import pl.grzegorz2047.openguild2047.api.command.OpenCommandManager;
import pl.grzegorz2047.openguild2047.api.module.OpenModuleManager;
import pl.grzegorz2047.openguild2047.commands.GuildCommand;
import pl.grzegorz2047.openguild2047.commands.SpawnCommand;
import pl.grzegorz2047.openguild2047.commands.TeamCommand;
import pl.grzegorz2047.openguild2047.commands.TpaCommand;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.database.*;
import pl.grzegorz2047.openguild2047.database.interfaces.SQLImplementationStrategy;
import pl.grzegorz2047.openguild2047.database.interfaces.SQLTables;
import pl.grzegorz2047.openguild2047.database.mysql.MySQLImplementationStrategy;
import pl.grzegorz2047.openguild2047.database.mysql.MySQLTables;
import pl.grzegorz2047.openguild2047.database.sqlite.SQLiteImplementationStrategy;
import pl.grzegorz2047.openguild2047.database.sqlite.SQLiteTables;
import pl.grzegorz2047.openguild2047.dropstone.DropConfigLoader;
import pl.grzegorz2047.openguild2047.dropstone.DropFromBlocks;
import pl.grzegorz2047.openguild2047.dropstone.DropProperties;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.listeners.*;
import pl.grzegorz2047.openguild2047.managers.TagManager;
import pl.grzegorz2047.openguild2047.modules.hardcore.HardcoreSQLHandler;
import pl.grzegorz2047.openguild2047.relations.Relations;
import pl.grzegorz2047.openguild2047.tasks.Watcher;
import pl.grzegorz2047.openguild2047.teleporters.Teleporter;
import pl.grzegorz2047.openguild2047.teleporters.TpaRequester;
import pl.grzegorz2047.openguild2047.updater.Updater;


/**
 * @author Grzegorz
 */
public class OpenGuild extends JavaPlugin {

    private static OpenGuildPlugin ogAPI;

    private Guilds guilds;

    private TagManager tagManager;

    private SQLHandler sqlHandler;
    private Cuboids cuboids;
    private AntiLogoutManager logout;
    private BukkitTask watcher;
    private Teleporter teleporter;
    private TpaRequester tpaRequester;
    private DropFromBlocks drop;
    private Relations relations;

    /**
     * Instance of built-in permissions manager main class.
     */
    @Override
    public void onEnable() {
        // We use UUID, which were not available in Bukkit < 1.7.5.
        try {
            if (getServer().getOfflinePlayer("Notch").getUniqueId() == null) {
                Bukkit.getLogger().warning("Your Minecraft server version is lower than 1.7.5!");
                Bukkit.getLogger().warning("This plugin is not compatibile with your version of Minecraft server!");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Your Minecraft server version is lower than 1.7.5!");
            Bukkit.getLogger().warning("This plugin is not compatibile with your version of Minecraft server!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        System.out.print("Your Minecraft server version is " + Bukkit.getVersion());

        long startTime = System.currentTimeMillis();


        // Setup API
        OpenGuildBukkitPlugin ogBP = new OpenGuildBukkitPlugin();
        BagOfEverything.setOpenGuild(ogBP);
        OpenGuild.ogAPI = ogBP.getPlugin();

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

        this.guilds = new Guilds(sqlHandler, this);
        this.relations = new Relations();
        this.cuboids = new Cuboids(guilds);
        this.logout = new AntiLogoutManager();
        // Setup Tag Manager
        this.tagManager = new TagManager(guilds);
        teleporter = new Teleporter();
        tpaRequester = new TpaRequester();

        // Load database
        loadDB();
        HardcoreSQLHandler hardcoreSQLHandler = new HardcoreSQLHandler(sqlHandler);

        loadCommands(cuboids, guilds, teleporter, tagManager, sqlHandler, relations, hardcoreSQLHandler);

        loadAllListeners();
        sqlHandler.loadRelations();

        // Load required items section.
        CuboidAndSpawnManipulationListeners.loadItems();

        // Load default plugin-modules
        ((OpenModuleManager) ogAPI.getModules()).defaultModules(hardcoreSQLHandler);

        // Register all hooks to this plugin
        Hooks.registerDefaults(this);
        watcher = Bukkit.getScheduler().runTaskTimer(this, new Watcher(logout, teleporter, tpaRequester, guilds, relations), 0, 20);

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
        OpenCommandManager.registerPluginCommands(this);
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

        this.sqlHandler = new SQLHandler(this, sqlImplementation, tables, guilds, cuboids);
    }

    /**
     * This method registers all events.
     */
    private void loadAllListeners() {
        PluginManager pm = getServer().getPluginManager();
        ListenerLoader listenerLoader =
                new ListenerLoader
                        (this, guilds, tagManager, sqlHandler, teleporter, tpaRequester, cuboids, logout, drop);
        listenerLoader.loadListeners(pm);
    }

    /**
     * @return instance of OpenGuildPlugin (API) class.
     */
    public static OpenGuildPlugin getAPI() {
        return ogAPI;
    }

    /**
     * @return instance of OGLogger class.
     */
    public static Logger getOGLogger() {
        return BagOfEverything.getLogger();
    }


}
