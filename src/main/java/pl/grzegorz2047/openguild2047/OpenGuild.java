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

/*
 TODO:
 Stworzenie klasy od cuboidów
 Ogarnięcie co byłoby dobre do sprawdzania czy gracz jest na cuboidzie gildii
 Tutaj są przydatne linki dla zainteresowanych tworzeniem cuboidów:
 https://github.com/sk89q/worldguard/blob/master/src/main/java/com/sk89q/worldguard/bukkit/WorldGuardPlayerListener.java
 https://forums.bukkit.org/threads/protection-region-cuboid-creation.164161/



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
import pl.grzegorz2047.openguild2047.listeners.PlayerQuit;
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
        if(!checkPlugins()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        copyDefaultFiles();
        loadAllListeners();
        Data pd = new Data();
        Data.setDataInstance(pd);
        loadDb();
        getCommand("guild").setExecutor(new GuildCommand());
        getCommand("team").setExecutor(new TeamCommand());
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
            Guilds.getLogger().severe("Your minecraft server version is below 1.7.5!/Masz starego bukkita ponizej 1.7.5!");
            getServer().getConsoleSender().sendMessage("§c Your minecraft server version is below 1.7.5!/Masz starego bukkita ponizej 1.7.5! Closing! Wylaczam!");
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

    private boolean checkPlugins() {
        return true;
       /* if(getServer().getPluginManager().getPlugin("NametagEdit") == null) {
            Guilds.getLogger().severe("Plugin NametagEdit was not found! Download it at http://dev.bukkit.org/bukkit-plugins/nametagedit/");
            Guilds.getLogger().severe("Disabling OpenGuild2047...");
            return false;
        } else {
            return true;
        }*/
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
        GenConf.loadConfiguration();
        saveResource("messages_" + GenConf.lang.name().toLowerCase() + ".yml", false);
        saveResource("players.yml", false);
        Guilds.getLogger().info("Loading configuration from config.yml...");

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
            Guilds.getLogger().severe("Tags color length must have 2 chars: & oraz znaku identyfikujacy kolor!");
            Guilds.getLogger().warning("Uzywam domyslnego koloru gildii!");
            GenConf.colortagu = "§6";
        }
    }

    private void loadDb() {
        switch(GenConf.DATABASE) {
            case FILE:
                //Guilds.getLogger().warning("We are so sorry! Files database system doesn't work now! Connecting via MySQL...");
                new MySQLHandler(address, database, login, password).createFirstConnectionSQLite(); // TODO Files...
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
        pm.registerEvents(new PlayerMove(), this);
        pm.registerEvents(new PlayerQuit(), this);
        pm.registerEvents(new Monitors(), this);
        pm.registerEvents(new EntityDamageByEntity(), this);
        pm.registerEvents(new Hardcore(), this);
    }

    private void loadPlayers() {
        for(SimpleGuild guild : Data.getInstance().guilds.values()) { // Pobieranie gildii
            for(UUID member : MySQLHandler.getGuildMembers(guild.getTag())) { // Pobieranie graczy w gildii
                guild.addMember(member); // Dodawanie gracza do listy
                // getName() -> https://github.com/Xephi/Bukkit/commit/f6a3abaa35f4b9ff16427a82be8f818d212b3927
            }
        }
    }

    public static OpenGuild get() {
        return instance;
    }

}
