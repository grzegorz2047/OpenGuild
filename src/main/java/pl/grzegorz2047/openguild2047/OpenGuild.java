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
import com.github.grzegorz2047.openguild.command.CommandDescription;
import com.github.grzegorz2047.openguild.command.CommandInfo;
import com.github.grzegorz2047.openguild.event.ModuleLoadEvent;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.api.OpenGuildBukkitPlugin;
import pl.grzegorz2047.openguild2047.api.command.OpenCommandManager;
import pl.grzegorz2047.openguild2047.commands.ErrorCommand;
import pl.grzegorz2047.openguild2047.commands.GuildCommand;
import pl.grzegorz2047.openguild2047.commands.NewGuildCommand;
import pl.grzegorz2047.openguild2047.commands.TeamCommand;
import pl.grzegorz2047.openguild2047.commands2.def.HelpCmd;
import pl.grzegorz2047.openguild2047.commands2.def.ReloadCmd;
import pl.grzegorz2047.openguild2047.commands2.def.VersionCmd;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.listeners.CuboidListeners;
import pl.grzegorz2047.openguild2047.listeners.EntityDamageByEntity;
import pl.grzegorz2047.openguild2047.listeners.Monitors;
import pl.grzegorz2047.openguild2047.listeners.PlayerChat;
import pl.grzegorz2047.openguild2047.listeners.PlayerMove;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.managers.TagManager;

/**
 *
 * @author Grzegorz
 */
public class OpenGuild extends JavaPlugin {

    public static final File CMDS = new File("plugins/OpenGuild2047/commands.yml");
    private static OpenGuildPlugin api;
    private static OpenGuild instance;
    private final File log = new File("plugins/OpenGuild2047/logger/openguild.log");
    private final File logDir = new File("plugins/OpenGuild2047/logger");
    private String address;
    private String database;
    private String login;
    private String password;

    @Override
    public void onEnable() {
        long init = System.currentTimeMillis();
        instance = this;
        com.github.grzegorz2047.openguild.OpenGuild.setOpenGuild(new OpenGuildBukkitPlugin()); // Setup API
        api = com.github.grzegorz2047.openguild.OpenGuild.getPlugin();
        getCommand("guild").setExecutor(new ErrorCommand());
        getCommand("team").setExecutor(new ErrorCommand());
        copyDefaultFiles();
        loadCommands();
        checkForUpdates();
        loadAllListeners();
        Data pd = new Data();
        Data.setDataInstance(pd);
        loadDb();
        new TagManager();
        for(Player p : getServer().getOnlinePlayers()) {
            TagManager.setTag(p.getUniqueId());
        }
        loadPlayers();
        CuboidListeners.loadItems();
        
        // Metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch(IOException ex) {
            // Failed to submit the stats :-(
        }
        try{
            if(getServer().getOfflinePlayer("Notch").getUniqueId() ==null){
                Guilds.getLogger().severe("Your Minecraft server version is below 1.7.5!/Masz starego bukkita ponizej 1.7.5!");
                getServer().getConsoleSender().sendMessage("§4Your Minecraft server version is below 1.7.5!/Masz starego bukkita ponizej 1.7.5! Closing! Wylaczam!");
                getServer().getPluginManager().disablePlugin(this);
            }
        }
        catch(Exception ex){
            Guilds.getLogger().severe("Your Minecraft server version is below 1.7.5!/Masz starego bukkita ponizej 1.7.5!");
            getServer().getConsoleSender().sendMessage("§4Your Minecraft server version is below 1.7.5!/Masz starego bukkita ponizej 1.7.5! Closing! Wylaczam!");
            getServer().getPluginManager().disablePlugin(this);
        }
        getCommand("team").setExecutor(new TeamCommand());
        getServer().getConsoleSender().sendMessage("§a" + this.getName() + "§6 by §3grzegorz2047§6 has been enabled in " + String.valueOf(System.currentTimeMillis() - init) + " ms!");
    }

    @Override
    public void onDisable() {
        try {
            SQLHandler.getConnection().close();
        } catch(SQLException ex) {}
        
        int logFiles = 0;
        for(File file : logDir.listFiles()) {
            String format = file.getName().substring(file.getName().length() - 4, file.getName().length());
            if(!format.equals(".log")) {
                file.delete();
                logFiles++;
            }
        }
        System.out.println("Deleted " + logFiles + " files in 'plugins/OpenGuild2047/logger'");
    }

    private void checkForUpdates() {
        if(!GenConf.updater) {
            Guilds.getLogger().info("Updater are disabled.");
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
        Guilds.getLogger().info("Enabling OpenGuild2047 v" + getDescription().getVersion() + "...");
    }

    private void copyDefaultFiles() {
        if(!logDir.exists()) {
            logDir.mkdirs();
        }
        if(!log.exists()) {
            try {
                log.createNewFile();
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        saveDefaultConfig();//Najprostsza opcja, ale nie aktualizuje configu.
        loadConfig();
        saveResource("commands.yml", false);
        Guilds.getLogger().info("Loading configuration from config.yml...");
        GenConf.loadConfiguration();
        saveResource("messages_" + GenConf.lang.name().toLowerCase() + ".yml", true);
        Guilds.getLogger().info("Configuration loaded!");
    }

    private String[] getAliases(String cmd, String[] def) {
        List<String> aliases = getAPI().getCmdManager().getAliases(cmd);
        if(aliases == null)
            return new String[] {};
        if(def != null)
            aliases.addAll(Arrays.asList(def));
        return aliases.toArray(new String[aliases.size()]);
        
    }

    private void loadCommands() {
        if(!GenConf.newCmdApi) {
            getCommand("guild").setExecutor(new GuildCommand());
            return;
        }
        
        CommandDescription help = new CommandDescription();
        CommandDescription reload = new CommandDescription();
        CommandDescription version = new CommandDescription();
        
        help.set(MsgManager.getIgnorePref("cmd-help"));
        reload.set(MsgManager.getIgnorePref("cmd-reload"));
        version.set(MsgManager.getIgnorePref("cmd-version"));
        
        api.registerCommand(new CommandInfo(
                (String[]) getAliases("help", new String[] {"?"}),
                "help",
                help,
                new HelpCmd(),
                null,
                "[command|page]"));
        api.registerCommand(new CommandInfo(
                (String[]) getAliases("reload", null),
                "reload",
                reload,
                new ReloadCmd(),
                "openguild.command.reload",
                null));
        api.registerCommand(new CommandInfo(
                (String[]) getAliases("version", new String[] {"v", "ver", "about"}),
                "version",
                version,
                new VersionCmd(),
                null,
                null));
        
        OpenCommandManager.registerPluginCommands();
        getCommand("guild").setExecutor(new NewGuildCommand());
    }

    private void loadConfig() {
        // MySQL
        address = getConfig().getString("mysql.address");
        database = getConfig().getString("mysql.database");
        login = getConfig().getString("mysql.login");
        password = getConfig().getString("mysql.password");
    }

    private void loadDb() {
        switch(GenConf.DATABASE) {
            case FILE:
                new SQLHandler(address, database, login, password).createFirstConnectionSQLite();
                break;
            case MYSQL:
                new SQLHandler(address, database, login, password).createFirstConnection(login, password);
                break;
            default:
                Guilds.getLogger().severe("Could not load database type! Please fix it in your config.yml file!");
                getServer().getPluginManager().disablePlugin(this);
                break;
        }
    }

    void loadAllListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerChat(), this);
        pm.registerEvents(new Monitors(), this);
        if(GenConf.cubEnabled) {
            pm.registerEvents(new CuboidListeners(), this);
        }
        if(!GenConf.teampvp) {
            pm.registerEvents(new EntityDamageByEntity(), this);
        }
        if(GenConf.playerMoveEvent) {
            pm.registerEvents(new PlayerMove(), this);
        }
        
        getServer().getPluginManager().callEvent(new ModuleLoadEvent());
    }

    private void loadPlayers() {
        for(SimpleGuild guild : Data.getInstance().guilds.values()) { // Pobieranie gildii
            for(UUID member : SQLHandler.getGuildMembers(guild.getTag())) { // Pobieranie graczy w gildii
                guild.addMember(member); // Dodawanie gracza do listy
            }
        }
    }

    public static OpenGuild get() {
        return instance;
    }

    public static OpenGuildPlugin getAPI() {
        return api;
    }

}
