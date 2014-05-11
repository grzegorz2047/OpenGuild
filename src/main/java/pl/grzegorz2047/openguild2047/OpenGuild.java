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

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import org.mcstats.Metrics;

import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.commands.GuildCommand;
import pl.grzegorz2047.openguild2047.commands.TeamCommand;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler;
import pl.grzegorz2047.openguild2047.listeners.CuboidListeners;
import pl.grzegorz2047.openguild2047.listeners.EntityDamageByEntity;
import pl.grzegorz2047.openguild2047.listeners.Hardcore;
import pl.grzegorz2047.openguild2047.listeners.Monitors;
import pl.grzegorz2047.openguild2047.listeners.PlayerChat;
import pl.grzegorz2047.openguild2047.listeners.PlayerMove;
import pl.grzegorz2047.openguild2047.tagmanager.TagManager;

/**
 *
 * @author Grzegorz
 */
public class OpenGuild extends JavaPlugin {

    private static OpenGuild instance;
    private File log = new File("plugins/OpenGuild2047/logger/openguild.log");
    private File logDir = new File("plugins/OpenGuild2047/logger");
    private String address;
    private String database;
    private String login;
    private String password;

    @Override
    public void onEnable() {
        long init = System.currentTimeMillis();
        instance = this;
        
        copyDefaultFiles();
        loadAllListeners();
        Data pd = new Data();
        Data.setDataInstance(pd);
        loadDb();
        getCommand("guild").setExecutor(new GuildCommand());
        getCommand("team").setExecutor(new TeamCommand());
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
            Bukkit.getOfflinePlayer("test").getUniqueId();
        }
        catch(NoSuchMethodError ex){
            Guilds.getLogger().severe("Your Minecraft server version is below 1.7.5!/Masz starego bukkita ponizej 1.7.5!");
            getServer().getConsoleSender().sendMessage("§4Your Minecraft server version is below 1.7.5!/Masz starego bukkita ponizej 1.7.5! Closing! Wylaczam!");
            getServer().getPluginManager().disablePlugin(this);
        }
        getServer().getConsoleSender().sendMessage("§a" + this.getName() + "§6 by §3grzegorz2047§6 has been enabled in " + String.valueOf(System.currentTimeMillis() - init) + " ms!");
    }

    @Override
    public void onDisable() {
        try {
            MySQLHandler.getConnection().close();
        }
        catch (SQLException ex) {
            
        }

        int logFiles = 0;
        for(File file : logDir.listFiles()) {
            String format = file.getName().substring(file.getName().length() - 4, file.getName().length());
            if(!format.equals(".log")) {
                file.delete();
                logFiles++;
            }
        }
        System.out.println("Deleted " + logFiles + " files in OpenGuild2047/logger");
    }

    private void copyDefaultFiles() {
        saveDefaultConfig();//Najprostsza opcja, ale nie aktualizuje configu.
        loadConfig();
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
        Guilds.getLogger().info("Loading configuration from config.yml...");
        GenConf.loadConfiguration();
        saveResource("messages_" + GenConf.lang.name().toLowerCase() + ".yml", false);
        Guilds.getLogger().info("Configuration loaded!");
    }

    private void loadConfig() {
        // MySQL
        address = getConfig().getString("mysql.address");
        database = getConfig().getString("mysql.database");
        login = getConfig().getString("mysql.login");
        password = getConfig().getString("mysql.password");
        // Inne ustawienia
        GenConf.teampvp = this.getConfig().getBoolean("teampvp");
        GenConf.homecommand = this.getConfig().getBoolean("home-command");
        GenConf.reqitems = this.getConfig().getStringList("required-items");
        GenConf.playerprefixenabled = this.getConfig().getBoolean("playerprefixtag");
        GenConf.guildprefixinchat = this.getConfig().getBoolean("guildprefixinchat");
        GenConf.colortagu = this.getConfig().getString("tag-color").replace('&', '§');
        if(GenConf.colortagu.length() > 2) {
            Guilds.getLogger().severe("Tags color length must have 2 chars: & and bukkit color id!");
            Guilds.getLogger().warning("Using default color!");
            GenConf.colortagu = "§6";
        }
    }

    private void loadDb() {
        switch(GenConf.DATABASE) {
            case FILE:
                new MySQLHandler(address, database, login, password).createFirstConnectionSQLite();
                break;
            case MYSQL:
                new MySQLHandler(address, database, login, password).createFirstConnection(login, password);
                break;
            default:
                Guilds.getLogger().severe("Could not load database type! Please fix it in your config.yml file!");
                getServer().getPluginManager().disablePlugin(this);
                break;
        }
    }

    void loadAllListeners() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new CuboidListeners(), this);
        pm.registerEvents(new PlayerChat(), this);
        pm.registerEvents(new Monitors(), this);
        if(!GenConf.teampvp)
            pm.registerEvents(new EntityDamageByEntity(), this);
        if(GenConf.hcBans)
            pm.registerEvents(new Hardcore(), this);
        if(GenConf.playerMoveEvent)
            pm.registerEvents(new PlayerMove(), this);
    }

    private void loadPlayers() {
        for(SimpleGuild guild : Data.getInstance().guilds.values()) { // Pobieranie gildii
            for(UUID member : MySQLHandler.getGuildMembers(guild.getTag())) { // Pobieranie graczy w gildii
                guild.addMember(member); // Dodawanie gracza do listy
            }
        }
    }

    public static OpenGuild get() {
        return instance;
    }

}
