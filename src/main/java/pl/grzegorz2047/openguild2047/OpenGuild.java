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

import com.github.grzegorz2047.openguild.OpenGuildPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.api.OpenGuildBukkitPlugin;
import pl.grzegorz2047.openguild2047.api.command.OpenCommandManager;
import pl.grzegorz2047.openguild2047.commands.GuildCommand;
import pl.grzegorz2047.openguild2047.commands.TeamCommand;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.listeners.*;
import pl.grzegorz2047.openguild2047.managers.TagManager;

/**
 *
 * @author Grzegorz
 */
public class OpenGuild extends JavaPlugin {
    
    private static OpenGuildPlugin ogAPI;
    private static OpenGuild instance;
    
    private OGLogger ogLogger;
    
    private GuildHelper guildHelper;
    
    private TagManager tagManager;
    
    private SQLHandler sqlHandler;

    @Override
    public void onEnable() {
        // We use UUID, which were not available in Bukkit < 1.7.5.
        try {
            if(getServer().getOfflinePlayer("Notch").getUniqueId() == null) {
                Guilds.getLogger().severe("Your Minecraft server version is lower than 1.7.5!");
                Guilds.getLogger().severe("This plugin is not compatibile with your version of Minecraft server!");
                getServer().getPluginManager().disablePlugin(this);
            }
        } catch(Exception e) {
            Guilds.getLogger().severe("Your Minecraft server version is lower than 1.7.5!");
            Guilds.getLogger().severe("This plugin is not compatibile with your version of Minecraft server!");
            getServer().getPluginManager().disablePlugin(this);
        }
        
        long startTime = System.currentTimeMillis();
        
        instance = this;
        
        // Intialize logger
        this.ogLogger = new OGLogger();

        // Check for updates
        checkForUpdates();
        
        // Setup API
        com.github.grzegorz2047.openguild.OpenGuild.setOpenGuild(new OpenGuildBukkitPlugin());
        
        OpenGuildBukkitPlugin ogBP = new OpenGuildBukkitPlugin();
        this.ogAPI = ogBP.getPlugin();
        
        // Validate files
        validateFile("config");
        validateFile("commands");
        
        // Load configuration
        GenConf.loadConfiguration();
        
        // Validate language file
        validateFile("messages_" + GenConf.lang.toLowerCase());
        
        // Register commands
        loadCommands();
        
        // Register events
        loadAllListeners();
        
        // Intialize guild helper class
        this.guildHelper = new GuildHelper();
        
        // Load database
        loadDB();
        loadPlayers();
        
        // Setup Tag Manager
        this.tagManager = new TagManager(this);
        
        for(Player player : getServer().getOnlinePlayers()) {
            this.tagManager.setTag(player.getUniqueId());
        }
        
        // Load required items section.
        CuboidListeners.loadItems();
        
        // Metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch(IOException ex) {
            // Failed to submit the stats :-(
        }
        
        getServer().getConsoleSender().sendMessage("§a" + this.getName() + "§6 by §3grzegorz2047§6 has been enabled in " + String.valueOf(System.currentTimeMillis() - startTime) + " ms!");
    }

    @Override
    public void onDisable() {
        instance = null;
        this.sqlHandler.closeConnection();
        
        int deletedFiles = 0;
        
        for(File file : getOGLogger().getLoggingDirectory().listFiles()) {
            String format = file.getName().substring(file.getName().length() - 4, file.getName().length());
            if(!format.equals(".log")) {
                file.delete();
                deletedFiles++;
            }
        }
        
        System.out.println("Deleted " + deletedFiles + " files from 'plugins/OpenGuild2047/logger'");
    }

    /**
     * This method checks if any update is available, and shows notification
     * if there's new version of plugin.
     */
    private void checkForUpdates() {
        if(!GenConf.updater) {
            Guilds.getLogger().warning("Updater is disabled.");
        } else {
            if(com.github.grzegorz2047.openguild.OpenGuild.getUpdater().isAvailable()) {
                Guilds.getLogger().info(" ");
                Guilds.getLogger().info(" ==================== UPDATER ==================== ");
                Guilds.getLogger().info("Update found! Please update your plugin to the newest version!");
                Guilds.getLogger().info("Download it from https://github.com/grzegorz2047/OpenGuild2047/releases");
                Guilds.getLogger().info(" ==================== UPDATER ==================== ");
                Guilds.getLogger().info(" ");
            } else {
                Guilds.getLogger().info("No updates found! Good job! :D");
            }
        }
    }

    /**
     * This method is used to get all aliases of specified command.
     * 
     * @param cmd command, which aliases should be get.
     * @param def default aliases.
     * @return an array of strings.
     */
    private String[] getAliases(String cmd, String[] def) {
        List<String> aliases = getAPI().getCmdManager().getAliases(cmd);
        
        if(aliases == null) {
            return new String[]{};
        }
        
        if(def != null) {
            aliases.addAll(Arrays.asList(def));
        }
        
        return aliases.toArray(new String[aliases.size()]);
    }

    /**
     * This method sets executors of all commands, and
     * registers them in our API.
     */
    private void loadCommands() {
        getCommand("team").setExecutor(new TeamCommand(this));
        getCommand("guild").setExecutor(new GuildCommand(this));
        
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
        
        this.sqlHandler = new SQLHandler(this, host, port, name, user, pass);
    }

    /**
     * This method registers all events.
     */
    private void loadAllListeners() {
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvents(new PlayerJoinListener(this), this);
        pm.registerEvents(new PlayerChatListener(this), this);
        pm.registerEvents(new PlayerQuitListener(this), this);
        
        if(GenConf.cubEnabled) {
            pm.registerEvents(new CuboidListeners(this), this);
        }
        
        if(!GenConf.teampvp) {
            pm.registerEvents(new EntityDamageByEntityListener(this), this);
        }
        
        if(GenConf.playerMoveEvent) {
            pm.registerEvents(new PlayerMoveListener(this), this);
        }
    }

    /**
     * This method loads all players from database and adds 
     * them to their guild's member list.
     */
    private void loadPlayers() {
        for(SimpleGuild guild : this.guildHelper.getGuilds().values()) {
            for(UUID player : this.guildHelper.getPlayers().keySet()) {
                if(this.guildHelper.getPlayers().get(player) != null && this.guildHelper.getPlayers().get(player).equals(guild)) {
                    guild.addMember(player);
                }
            }
        }
    }
    
    /**
     * This method is used to validate YAML configuration file,
     * it compares file from plugin's JAR and file in plugin's folder,
     * and adds keys, which doesn't exist.
     * 
     * @param name name of file to validate (without extension)
     */
    public void validateFile(String name) {
        getOGLogger().info("Validating file '" + name + ".yml ...");
        
        YamlConfiguration c = new YamlConfiguration();
        try {
            File file = new File("plugins/OpenGuild2047/" + name + ".yml");
            if(!file.exists()) {
                getOGLogger().info("File plugins/OpenGuild2047/" + name + ".yml does not exists - creating ...");
                file.createNewFile();
            }
            
            c.load(file);
            
            YamlConfiguration configInside = new YamlConfiguration();
            
            if(getResource(name + ".yml") == null) {
                getOGLogger().info("File " + name + ".yml does not exists - skipping ...");
                file.delete();
                return;
            }
            
            configInside.load(getResource(name + ".yml"));

            for(String k : configInside.getKeys(true)) {
                if(!c.contains(k)) {
                    c.set(k, configInside.get(k));
                }
            }

            c.save(file);
        } catch(IOException e) {
            getOGLogger().exceptionThrown(e);
        }
        catch (InvalidConfigurationException e) {
            getOGLogger().exceptionThrown(e);
        }
    }
    
    /**
     * This method is used to broadcast message to all
     * online players.
     * We're not using Bukkit.broadcastMessage() method, because it sends
     * messages also to the console.
     * 
     * @param message message to be sent
     */
    public void broadcastMessage(String message) {
        for(Player player : getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    /**
     * @return instance of this class.
     */
    public static OpenGuild getInstance() {
        return instance;
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
    public OGLogger getOGLogger() {
        return ogLogger;
    }

    /**
     * @return instance of GuildHelper class.
     */
    public GuildHelper getGuildHelper() {
        return guildHelper;
    }
    
    /**
     * @return instance of TagManager class.
     */
    public TagManager getTagManager() {
        return tagManager;
    }

    /**
     * @return instance of SQLHandler class.
     */
    public SQLHandler getSQLHandler() {
        return sqlHandler;
    }
    
}
