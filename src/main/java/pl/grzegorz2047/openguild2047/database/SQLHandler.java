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
package pl.grzegorz2047.openguild2047.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.bukkit.Location;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Cuboid;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class SQLHandler {
    
    private OpenGuild plugin;
    
    private Connection connection;
    private Statement statement;
    
   // public String generateStringAutoInc(){
   //     if(!GenConf.DATABASE.equals(Database.FILE)) return "INT PRIMARY KEY AUTO_INCREMENT"; else return "INTEGER PRIMARY KEY AUTOINCREMENT";
   // }
    //public String generateDefVal(){
   //     if(!GenConf.DATABASE.equals(Database.FILE)) return "0"; else return "null";
   // }
    public SQLHandler(OpenGuild plugin, String host, int port, String user, String password, String name) {
        this.plugin = plugin;
        
        switch(GenConf.DATABASE) {
            case FILE:
                plugin.getOGLogger().info("[SQLite] Connecting to SQLite database ...");
                try {
                    Class.forName("org.sqlite.JDBC").newInstance();
                    this.connection = DriverManager.getConnection("jdbc:sqlite:" + GenConf.FILE_DIR);
                    if(this.connection != null) {
                        plugin.getOGLogger().info("[SQLite] Connected to SQLite successfully!");
                        this.startWork();
                    }
                } catch(ClassNotFoundException ex) {
                    plugin.getOGLogger().info("[SQLite] Connecting with SQLite failed! We were unable to load driver 'org.sqlite.JDBC'.");
                } catch(SQLException ex) {
                    plugin.getOGLogger().exceptionThrown(ex);
                } catch(InstantiationException ex) {
                    plugin.getOGLogger().exceptionThrown(ex);
                } catch(IllegalAccessException ex) {
                    plugin.getOGLogger().info("[SQLite] Connecting with SQLite failed! Permission error: " + ex.getMessage());
                }
                break;
            case MYSQL:
                plugin.getOGLogger().info("[MySQL] Connecting to MySQL database ...");
                
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?autoReconnect=true", user, password);
                    this.statement = this.connection.createStatement();
                    
                    plugin.getOGLogger().info("[MySQL] Connected to MySQL successfully!");
                    this.startWork();
                } catch(SQLException ex) {
                    plugin.getOGLogger().exceptionThrown(ex);
                } catch(ClassNotFoundException ex) {
                    plugin.getOGLogger().info("[MySQL] Connecting with MySQL failed! We were unable to load driver 'com.mysql.jdbc.Driver'.");
                }
                break;
            default:
                plugin.getOGLogger().severe("[MySQL] Invalid database type '" + GenConf.DATABASE.name() + "'!");
                break;
        }
    }
    
    private void startWork() {
        // Create tables is they doesn't exists
        this.createTables();
        
        // Load guilds and players from database
        plugin.getGuildHelper().setGuilds(this.loadGuilds());
        plugin.getGuildHelper().setPlayers(this.loadPlayers());
        plugin.getOGLogger().info("Loaded " + plugin.getGuildHelper().getGuilds().size() + " guilds from database.");
    }
    
    private void createTables() {
        plugin.getOGLogger().info("[DB] Creating tables if not exists ...");
        
        try {
            String query = "CREATE TABLE IF NOT EXISTS `openguild_guilds`"
                    + "(tag VARCHAR(11),"
                    + "description VARCHAR(100),"
                    + "leader VARCHAR(37),"
                    + "home_x INT,"
                    + "home_y INT,"
                    + "home_z INT,"
                    + "home_world VARCHAR(16),"
                    + "cuboid_radius INT,"
                    + "PRIMARY KEY(tag));";
            statement = this.connection.createStatement();
            statement.execute(query);
            
            query = "CREATE TABLE IF NOT EXISTS `openguild_players`"
                    + "(guild VARCHAR(11),"
                    + "uuid VARCHAR(37)," // UUID gracza z myślnikami ma 35 znaków? Więc dla pewności dam 37
                    + "lastseenname VARCHAR(16),"
                    + "PRIMARY KEY(uuid));";
            statement = this.connection.createStatement();
            statement.execute(query);

            query = "CREATE TABLE IF NOT EXISTS `openguild_allies`"
                    + "("
                    + "who VARCHAR(11),"
                    + "withwho VARCHAR(11),"
                    + "status VARCHAR(5),"
                    + "expires BIGINT,"
                    + "PRIMARY KEY(who,withwho)" 
                    + ");";
            statement = this.connection.createStatement();
            statement.execute(query);
            
        } catch(SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
        }
    }
    public void loadTags(){
        for(Map.Entry<String, Guild> gs : plugin.getGuildHelper().getGuilds().entrySet()){
            Scoreboard sc = gs.getValue().getSc();
            for(Map.Entry<String, Guild> gs2 : plugin.getGuildHelper().getGuilds().entrySet()){
                if(gs.getValue().getTag().equals(gs2.getValue().getTag())){
                    continue;
                }
                Team t;
                if(sc.getTeam(gs2.getKey()) == null){
                    t = sc.registerNewTeam(gs2.getValue().getTag());
                }else{
                    t = sc.getTeam(gs2.getValue().getTag());
                }
                t.setPrefix(GenConf.allyTag.replace("{TAG}", gs2.getValue().getTag()));
                t.setDisplayName(GenConf.allyTag.replace("{TAG}", gs2.getValue().getTag()));
                for(UUID member : gs2.getValue().getMembers()){
                    t.addPlayer(Bukkit.getOfflinePlayer(member));
                }
            }
            //if(gs.getValue().getTag().equals(joinerGuild.getTag())){
           //     continue;
           //}
           // Team t2 = gs.getValue().getSc().getTeam(joinerGuild.getTag());
          //  if(t2 != null){
          //      t2.addPlayer(joiner);
          ///  }
        }
        
    }
    private Map<String, Guild> loadGuilds() {
        Map<String, Guild> guilds = new HashMap<String, Guild>();
        
        try {
            statement = this.connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM `openguild_guilds`");
            while(result.next()) {
                String tag = result.getString("tag");
                String description = result.getString("description");
                UUID leaderUUID = UUID.fromString(result.getString("leader"));
                
                String homeWorld = result.getString("home_world");
                if(plugin.getServer().getWorld(homeWorld) == null) {
                    plugin.getOGLogger().warning("World '" + homeWorld + "' does not exists! Skipping guild '" + tag + "'!");
                    continue;
                }

                int homeX = result.getInt("home_x");
                int homeY = result.getInt("home_y");
                int homeZ = result.getInt("home_z");
                Location home = new Location(plugin.getServer().getWorld(homeWorld), homeX, homeY, homeZ);

                int cuboidRadius = result.getInt("cuboid_radius");

                Cuboid cuboid = new Cuboid();
                cuboid.setOwner(tag);
                cuboid.setCenter(home);
                cuboid.setRadius(cuboidRadius);

                plugin.getGuildHelper().getCuboids().put(tag, cuboid);

                Guild guild = new Guild(plugin);
                guild.setCuboid(cuboid);
                guild.setTag(tag);
                guild.setDescription(description);
                guild.setHome(home);
                guild.setLeader(leaderUUID);
                guild.setSc(Bukkit.getScoreboardManager().getNewScoreboard());
                Team t = plugin.getTagManager().getGlobalScoreboard().registerNewTeam(tag);
                t.setPrefix(GenConf.allyTag.replace("{TAG}", tag));
                t.setDisplayName(ChatColor.RED+tag+" ");
                if(guild.getSc() == null){
                    //System.out.print("Scoreboard gildii "+guild.getTag()+" jest null!");
                }
                //guild.setAlliancesString(alliances);
                //guild.setEnemiesString(enemies);

                guilds.put(tag, guild);
            }
        } catch(SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
        }
        
        return guilds;
    }

    public Connection getConnection() {
        return connection;
    }
    
    private Map<UUID, Guild> loadPlayers() {
        Map<UUID, Guild> players = new HashMap<UUID, Guild>();
        
        try {
            statement = this.connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM `openguild_players`");
            while(result.next()) {
                String guildTag = result.getString("guild");
                //int kills = result.getInt("kills");
                //int deaths = result.getInt("deaths");
                UUID uuid = UUID.fromString(result.getString("uuid"));
                
                
                if(!(guildTag.isEmpty()) && plugin.getGuildHelper().doesGuildExists(guildTag)) {
                    players.put(uuid, plugin.getGuildHelper().getGuilds().get(guildTag));
                    Guild whoGuild = plugin.getGuildHelper().getGuilds().get(guildTag);
                    if(whoGuild == null){
                        //System.out.print("Gildia jest null!");
                        continue;
                    }
                    if(plugin.getTagManager().getGlobalScoreboard().getTeam(guildTag)== null){
                        Team t = plugin.getTagManager().getGlobalScoreboard().registerNewTeam(guildTag);
                        t.setPrefix(GenConf.enemyTag.replace("{TAG}", guildTag));
                        t.setDisplayName(GenConf.enemyTag.replace("{TAG}", guildTag));
                        t.addPlayer(Bukkit.getOfflinePlayer(uuid));
                    }else{
                        Team t = plugin.getTagManager().getGlobalScoreboard().getTeam(guildTag);
                        t.addPlayer(Bukkit.getOfflinePlayer(uuid));
                    }
                    Scoreboard whoSc = whoGuild.getSc();
                    
                    Team whoT;
                    if(whoSc.getTeam(guildTag) == null){
                        whoT = whoSc.registerNewTeam(guildTag);
                        whoT.setPrefix(GenConf.guildTag.replace("{TAG}", guildTag));
                        whoT.setDisplayName(GenConf.guildTag.replace("{TAG}", guildTag));
                        //System.out.print("whoT to "+whoT.getName()+" z dn "+whoT.getDisplayName());
                        //System.out.print("Dodaje gracza "+Bukkit.getOfflinePlayer(uuid).getName());
                        whoT.addPlayer(Bukkit.getOfflinePlayer(uuid));
                    }else{
                        whoT = whoSc.getTeam(guildTag);
                        //System.out.print("Dodaje gracza "+Bukkit.getOfflinePlayer(uuid).getName());
                        whoT.addPlayer(Bukkit.getOfflinePlayer(uuid));
                        
                    }
                } else {
                    players.put(uuid, null);

                    ///if(!guildTag.isEmpty()) {
                    //    plugin.getOGLogger().warning("Guild '" + guildTag.toUpperCase() + "' does not exist! Skipping player '" + uuid.toString() + "'");
                    //}
                }
            }
        } catch(SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
        }
        
        return players;
    }

    public Statement getStatement() {
        return statement;
    }
    public void loadRelations(){
        try {
            statement = this.connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM `openguild_allies`");
            while(result.next()) {
                String who = result.getString("who");
                //int kills = result.getInt("kills");
                //int deaths = result.getInt("deaths");
                String status = result.getString("status");
                String withwho = result.getString("withwho");
                long expires = result.getInt("expires");
                Relation r = new Relation();
                r.setWho(who);
                r.setWithWho(withwho);
                r.setExpireDate(expires);
                r.setState(Relation.Status.valueOf(status.toUpperCase()));
                Guild whoGuild = plugin.getGuildHelper().getGuilds().get(who);
                if(whoGuild == null){
                //System.out.print("gildia "+who+" nie istnieje!");
                    continue;
                }
                Scoreboard whoSc = whoGuild.getSc();
                
                Guild withWhoGuild = plugin.getGuildHelper().getGuilds().get(withwho);
                if(withWhoGuild == null){
                    //System.out.print("gildia "+withwho+" nie istnieje!");
                    continue;
                }
                Scoreboard withWhoSc = withWhoGuild.getSc();
                
                Team whoT;
                if(withWhoSc.getTeam(who) == null){
                    whoT = withWhoSc.registerNewTeam(who);
                }else{
                    whoT = withWhoSc.getTeam(who);
                }
                whoT.setPrefix(GenConf.allyTag.replace("{TAG}", who));
                whoT.setDisplayName(GenConf.allyTag.replace("{TAG}", who));
                for(UUID whop : whoGuild.getMembers()){
                    whoT.addPlayer(Bukkit.getOfflinePlayer(whop));
                }
                
                
                Team withWhoT;
                if(whoSc.getTeam(withwho) == null){
                    withWhoT = whoSc.registerNewTeam(withwho);
                }else{
                    withWhoT = whoSc.getTeam(withwho);
                }
                withWhoT.setPrefix(GenConf.allyTag.replace("{TAG}", withwho));
                withWhoT.setDisplayName(GenConf.allyTag.replace("{TAG}", withwho));
                for(UUID whop : withWhoGuild.getMembers()){
                    
                    withWhoT.addPlayer(Bukkit.getOfflinePlayer(whop));
                }
                
                whoGuild.getAlliances().add(r);
                withWhoGuild.getAlliances().add(r);
            }
        } catch(SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
        }
    }
    
    /**
     * Adds player to database.
     * It does not check if player already is in database!
     * 
     * @param player instance of UUID class.
     */
    public void addPlayer(UUID player) {
        final String uuid = player.toString();
        try {
            statement = this.connection.createStatement();
            statement.execute("INSERT INTO `openguild_players` VALUES( '', '" + uuid + "', '"+Bukkit.getPlayer(player).getName()+"');");
        } catch(SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
        }
    }

    /**
     * Updates player's guild tag in database.
     *
     * @param uuid uuid of player.
     */
    public void updatePlayer(UUID uuid) {
        String guildTag = "";
        if(plugin.getGuildHelper().getPlayers().containsKey(uuid) &&
            plugin.getGuildHelper().getPlayers().get(uuid) != null) {
            guildTag = plugin.getGuildHelper().getPlayers().get(uuid).getTag().toUpperCase();
        }

        try {
            statement = this.connection.createStatement();
            statement.executeUpdate("UPDATE `openguild_players` SET `guild` = '" + guildTag + "' WHERE `uuid` = '" + uuid.toString() + "'");
        } catch(SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
        }
    }

    /**
     * Adds guild to database.
     * It does not check if guild is already in database!
     *
     * @param guild instance of SimpleGuild class.
     */
    public void addGuild(Guild guild) {
        Location homeLocation = guild.getHome();

        try {
            statement = this.connection.createStatement();
            statement.execute("INSERT INTO `openguild_guilds` VALUES(" +
                    "'" + guild.getTag().toUpperCase() + "'," +
                    "'" + guild.getDescription() + "'," +
                    "'" + guild.getLeader().toString() + "'," +
                    "'" + (int) homeLocation.getX() + "'," +
                    "'" + (int) homeLocation.getY() + "'," +
                    "'" + (int) homeLocation.getZ() + "'," +
                    "'" + homeLocation.getWorld().getName() + "'," +
                    "'" + GenConf.MIN_CUBOID_RADIUS + "');");
        } catch(SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
        }
    }

    /**
     * Updates guild's description in database.
     *
     * @param guild instance of SimpleGuild class.
     */
    public void updateGuildDescription(Guild guild) {
        try {
            statement = this.connection.createStatement();
            statement.executeUpdate("UPDATE `openguild_guilds` SET `description` = '" + guild.getDescription() + "' WHERE `tag` = '" + guild.getTag().toUpperCase() + "'");
        } catch(SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
        }
    }

    /**
     * Removes guild from database.
     *
     * @param tag tag of guild, which should be deleted.
     */
    public void removeGuild(String tag) {
        try {
            statement = this.connection.createStatement();
            statement.execute("DELETE FROM `openguild_guilds` WHERE `tag` = '" + tag + "'");
        } catch(SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
        }
    }
    
    public boolean addAlliance(Guild who, Guild withWho, Relation.Status status){
        
        try {
            statement = this.connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM `openguild_allies`" +" WHERE who='"+who+"'" +"OR withwho='"+who+"';");
            if(!rs.isFirst()){
                statement.execute("INSERT INTO `openguild_allies` VALUES('"+who.getTag()+"', '"+withWho.getTag()+"', '"+status.ALLY.toString()+"', 0);");
                    return true;
            }
            while(rs.next()){
                String whoseguild = rs.getString("who");
                String withwho = rs.getString("withwho");
                if((whoseguild.equals(who.getTag()) && withwho.equals(withWho.getTag())) || (whoseguild.equals(withWho.getTag()) && withwho.equals(who.getTag()))){
                    return false;
                }else{
                    statement.execute("INSERT INTO `openguild_allies` VALUES('"+who.getTag()+"', '"+withWho.getTag()+"', '"+status.ALLY.toString()+"', 0);");
                    return true;
                }
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        return false;
    }
    public boolean removeAlliance(Guild who, Guild withWho){
        try {
            statement = this.connection.createStatement();
            statement.execute("DELETE FROM `openguild_allies` WHERE who='"+who.getTag()+"' AND withwho='"+withWho.getTag()+"';");
            statement.execute("DELETE FROM `openguild_allies` WHERE who='"+withWho.getTag()+"' AND withwho='"+who.getTag()+"';");
            return true;
                
            
        }
        catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
            
    public boolean isConnectionClosed() {
        try {
            return this.connection == null || this.connection.isClosed();
        } catch(SQLException ex) {
            return true;
        }
    }
    
    public void closeConnection() {
        try {
            if(!isConnectionClosed()) {
                this.connection.close();
            }
        } catch(SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
        }
    }
    public ResultSet executeQuery(String query){
        try {
            statement = this.connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            return result;
        }
        catch (SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
            return null;
        }
    }
    public int executeUpdate(String query){
        try {
            statement = this.connection.createStatement();
            return statement.executeUpdate(query);
        }
        catch (SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
            return -1;
        }
    }
    public boolean execute(String query){
        try {
            statement = this.connection.createStatement();
            return statement.execute(query);
        }
        catch (SQLException ex) {
            plugin.getOGLogger().exceptionThrown(ex);
            return false;
        }
    }
}



