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
package pl.grzegorz2047.openguild2047.handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.SimpleCuboid;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.SimplePlayerGuild;
import pl.grzegorz2047.openguild2047.api.Guild;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.api.Logger;

/**
 *
 * @author Grzegorz
 */
public class MySQLHandler {

    private static Connection con = null;
    private static Statement stat;
    private static String driver = "com.mysql.jdbc.Driver";
    private static String tableGuilds = "openguild_guilds";
    private static String tablePlayers = "openguild_players";
    private static Logger log = Guilds.getLogger();

    private String address;
    private String database;
    private String login;
    private String password;
    
    private static final String DRIVERSQLite = "org.sqlite.JDBC";
    private static final String DB_URLQLite = "jdbc:sqlite:" + GenConf.FILE_DIR;

    public MySQLHandler(String address, String database, String login, String password) {
        this.address = address;
        this.database = database;
        switch(GenConf.DATABASE) {
            case FILE:
                //Guilds.getLogger().warning("We are so sorry! Files database system doesn't work now! Connecting via MySQL...");
                //createConnectionSQLite();//Buguje
                break;
            case MYSQL:
                createConnection(login, password);
                break;
            default:
                Guilds.getLogger().severe("Could not load database type! Please fix it in your config.yml file!");
                Bukkit.getServer().getPluginManager().disablePlugin(OpenGuild.get());
                break;
        }
    }

    public enum Type {
        TAG,
        DESCRIPTION,
        LEADER,
        SOJUSZE,
        HOME_X,
        HOME_Y,
        HOME_Z,
        CUBOID_RADIUS
    }

    public enum PType {
        GUILD,
        KILLS,
        DEADS,
        ISLEADER,
        UUID,
        BAN_TIME
    }

    void loadDatabase() {
        Data.getInstance().guildsplayers= null;
        Data.getInstance().guilds = null;
        Data.getInstance().guildsplayers = MySQLHandler.getAllPlayers();
        Data.getInstance().guilds = MySQLHandler.getAllGuildswithCuboids();
    }
    public static Connection getConnection(){
        return con;
    }
    
    void checkIfConnIsClosed() {
        try {
            if(con == null || con.isClosed())
                createConnection(login, password);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static void log(String query) {
        if(GenConf.SQL_DEBUG) {
            Guilds.getLogger().log(Level.INFO, "[Server -> Database] " + query);
        }
    }

    public void createFirstConnection(String login, String password) {
        log.info("[MySQL] Connecting to MySQL database...");
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection("jdbc:mysql://" + address + ":3306/" + database, login, password);
            if(con != null) {
                log.info("[MySQL] Connected with MySQL!");
                createTables();
            }
        } catch(ClassNotFoundException ex) {
            Guilds.getLogger().severe("[MySQL] Could not connect to MySQL: Could not load driver " + driver + " to MySQL database!");
        } catch(InstantiationException ex) {
            ex.printStackTrace();
        } catch(IllegalAccessException ex) {
            log.info("[MySQL] Could not connect to MySQL: Permission error: " + ex.getMessage());
            ex.printStackTrace();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        if(con != null) {
            loadDatabase();
        } else {
            log.info("En error occured while connecting to MySQL.");
        }
    }
    public void createFirstConnectionSQLite() {
        log.info("[SQLite] Connecting to SQLite database...");
        try {
            Class.forName(DRIVERSQLite).newInstance();
            con = DriverManager.getConnection(DB_URLQLite);
            if(con != null) {
                log.info("[SQLite] Connected with SQLite.");
                createTables();
            }
        } catch(ClassNotFoundException ex) {
            Guilds.getLogger().severe("[SQLite] Could not connect to SQLite: Could not load driver " + DRIVERSQLite + " to SQLite database!");
        } catch(InstantiationException ex) {
            ex.printStackTrace();
        } catch(IllegalAccessException ex) {
            log.info("[SQLite] Could not connect to SQLite: Permission error: " + ex.getMessage());
            ex.printStackTrace();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        if(con != null) {
            loadDatabase();
        } else {
            log.info("En error occured while connecting to MySQL.");
        }
    }

    public void createConnection(String login, String password) {
        log.info("[MySQL] Connecting to MySQL database...");
        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection("jdbc:mysql://" + address + ":3306/" + database, login, password);
            log.info("[MySQL] Connected with MySQL.");
            createTables();
        } catch(ClassNotFoundException ex) {
            Guilds.getLogger().severe("[MySQL] Could not connect to MySQL: Could not load driver " + driver + " to MySQL database!");
        } catch(InstantiationException ex) {
            ex.printStackTrace();
        } catch(IllegalAccessException ex) {
            log.info("[MySQL] Could not connect to MySQL: Permission error: " + ex.getMessage());
            ex.printStackTrace();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        loadDatabase();
    }
    public void createConnectionSQLite() {
        log.info("[SQLite] Laczenie z baza SQLite...");
        try {
            Class.forName(DRIVERSQLite).newInstance();
            con = DriverManager.getConnection(DB_URLQLite);
            log.info("[SQLite] Connection success!");
            createTables();
        } catch(ClassNotFoundException ex) {
            Guilds.getLogger().severe("[SQLite] Could not connect to SQLite: Could not load driver " + DRIVERSQLite + " to SQLite database!");
        } catch(InstantiationException ex) {
            ex.printStackTrace();
        } catch(IllegalAccessException ex) {
            log.info("[SQLite] Could not connect to SQLite: Permission error: " + ex.getMessage());
            ex.printStackTrace();
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        loadDatabase();
    }

    public void createTables() {
        // Tabela z gildiami
        log.info("[Database] Creating table " + tableGuilds + " if not exists...");
        try {
            String query = "CREATE TABLE IF NOT EXISTS " + tableGuilds
                    + "(id INT AUTO_INCREMENT,"
                    + "tag VARCHAR(11),"
                    + "description VARCHAR(100),"
                    + "leader VARCHAR(37),"
                    + "sojusze VARCHAR(255),"
                    + "home_x INT,"
                    + "home_y INT,"
                    + "home_z INT,"
                    + "home_w VARCHAR(16),"
                    + "cuboid_radius INT,"
                    + "PRIMARY KEY(id,tag));";
            stat = con.createStatement();
            log(query);
            stat.execute(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }

        // Tabela z graczami
        log.info("[Database] Creating table " + tablePlayers + " if not exists...");
        try {
            String query = "CREATE TABLE IF NOT EXISTS " + tablePlayers
                    + "(id INT AUTO_INCREMENT,"
                    + "guild VARCHAR(11),"
                    + "isleader VARCHAR(5),"
                    + "kills INT,"
                    + "deads INT,"
                    + "uuid VARCHAR(37)," // UUID gracza z myślnikami ma 35 znaków? Więc dla pewności dam 37
                    + "ban_time BIGINT,"
                    + "PRIMARY KEY(id,uuid));";
            stat = con.createStatement();
            log(query);
            stat.execute(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void delete(Guild guild) {
        try {
            String query = "DELETE FROM " + tableGuilds + " WHERE tag='" + guild.getTag() + "';";
            stat = con.createStatement();
            log(query);
            stat.execute(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void delete(UUID uuid) {
        try {
            String query = "DELETE FROM " + tablePlayers + " WHERE uuid='" + uuid.toString() + "';";
            stat = con.createStatement();
            log(query);
            stat.execute(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void insert(String tag, String description, UUID leader, String sojusze, int homeX, int homeY, int homeZ, String homeW, int cuboidRadius) {
        try {
            String query = "INSERT INTO " + tableGuilds + " VALUES(NULL,"
                    + "'" + tag + "',"
                    + "'" + description + "',"
                    + "'" + leader.toString() + "',"
                    + "'" + sojusze + "',"
                    + homeX + ","
                    + homeY + ","
                    + homeZ + ","
                    + "'" + homeW + "',"
                    + cuboidRadius + ");";
            stat = con.createStatement();
            log(query);
            stat.execute(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void insert(Guild guild, String isLeader, int kills, int deads, UUID uuid) {
        try {
            String tag;
            if (guild == null) {
                tag = "";
            } else {
                tag = guild.getTag();
            }

            String query = "INSERT INTO " + tablePlayers + " VALUES(NULL,"
                    + "'" + tag + "',"
                    + "'" + isLeader + "',"
                    + kills + ","
                    + deads + ","
                    + "'" + uuid.toString() + "',"
                    + "NULL);";
            stat = con.createStatement();
            log(query);
            stat.execute(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void update(Guild guild, Type type, int value) {
        try {
            String query = "UPDATE " + tableGuilds + " SET " + type.toString().toLowerCase() + "=" + value + " WHERE tag='" + guild.getTag() + "';";
            stat = con.createStatement();
            log(query);
            stat.executeUpdate(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void update(Guild guild, Type type, String value) {
        try {
            String query = "UPDATE " + tableGuilds + " SET " + type.toString().toLowerCase() + "='" + value + "' WHERE tag='" + guild.getTag() + "';";
            stat = con.createStatement();
            log(query);
            stat.executeUpdate(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void update(UUID uuid, PType type, int value) {
        try {
            String query = "UPDATE " + tablePlayers + " SET " + type.toString().toLowerCase() + "=" + value + " WHERE uuid='" + uuid.toString() + "';";
            stat = con.createStatement();
            log(query);
            stat.executeUpdate(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void update(UUID uuid, PType type, long value) {
        try {
            String query = "UPDATE " + tablePlayers + " SET " + type.toString().toLowerCase() + "=" + value + " WHERE uuid='" + uuid.toString() + "';";
            stat = con.createStatement();
            log(query);
            stat.executeUpdate(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void update(UUID uuid, PType type, String value) {
        try {
            String query = "UPDATE " + tablePlayers + " SET " + type.toString().toLowerCase() + "='" + value + "' WHERE uuid='" + uuid.toString() + "';";
            stat = con.createStatement();
            log(query);
            stat.executeUpdate(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static HashMap<String, SimpleGuild> getAllGuildswithCuboids() {
        HashMap hm = new HashMap<String, SimpleGuild>();
        try {
            String query = "SELECT * FROM " + tableGuilds;
            stat = con.createStatement();
            log(query);
            ResultSet rs = stat.executeQuery(query);
            while(rs.next()) {
                String tag = rs.getString("tag");
                Data.getInstance().ClansTag.add(tag);
                SimpleGuild g = new SimpleGuild(tag);
                g.setLeader(UUID.fromString(rs.getString("leader")));
                g.setDescription(rs.getString("description"));
                g.setMembers(getGuildMembers(tag));
                Location loc = new Location(Bukkit.getWorld(rs.getString("home_w")), rs.getInt("home_x"), rs.getInt("home_y"), rs.getInt("home_z"));
                SimpleCuboid sc = new SimpleCuboid();
                sc.setCenter(loc);
                sc.setOwner(tag);
                sc.setRadius(rs.getInt("cuboid_radius"));
                Data.getInstance().cuboids.put(tag, sc);
                g.setHome(loc);
                System.out.println("Wczytalem "+tag);
                hm.put(g.getTag(), g);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return hm;
    }

    public static long getBan(UUID uuid) {
        long result = 0;
        try {
            String query = "SELECT ban_time FROM " + tablePlayers + " WHERE uuid='" + uuid.toString() + "';";
            stat = con.createStatement();
            log(query);
            ResultSet rs = stat.executeQuery(query);
            while(rs.next()) {
                long l = rs.getLong("ban_time");
                result = l;
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public static List<UUID> getGuildMembers(String tag) {
        try {
            String query = "SELECT uuid FROM " + tablePlayers + " WHERE guild='" + tag + "';";
            List<UUID> members = new ArrayList<UUID>();
            stat = con.createStatement();
            log(query);
            ResultSet rs = stat.executeQuery(query);
            while(rs.next()) {
                String p = rs.getString("uuid");
                members.add(UUID.fromString(p));
            }
            return members;
        } catch(SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static HashMap<UUID, SimplePlayerGuild> getAllPlayers() {
        HashMap hm = new HashMap<UUID, SimplePlayerGuild>();
        try {
            String query = "SELECT * FROM " + tablePlayers;
            stat = con.createStatement();
            log(query);
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                String player = rs.getString("uuid");
                String tag = rs.getString("guild");
                boolean isleader = Boolean.parseBoolean(rs.getString("isleader"));
                SimplePlayerGuild sg = new SimplePlayerGuild(UUID.fromString(player), tag, isleader);
                hm.put(UUID.fromString(player), sg);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
            return null;
        }
        return hm;
    }
    public static String getPlayer(UUID uuid) {//BROKEN Popsulem funkcjonalnosc tej metody, wiec mozna to potem ogarnac
        String player = null;
        try {
            String query = "SELECT player FROM " + tablePlayers + " WHERE uuid='" + uuid.toString() + "';";
            stat = con.createStatement();
            log(query);
            ResultSet rs = stat.executeQuery(query);
            while(rs.next()) {
                player = rs.getString(1);
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return null;//Na razie wywala null, zeby bylo wiadomo ze niedziala xd
    }

    public static boolean existsPlayer(UUID uuid) {
        try {
            String query = "SELECT COUNT(*) FROM " + tablePlayers + " WHERE uuid='" + uuid.toString() + "';";
            stat = con.createStatement();
            log(query);
            ResultSet rs = stat.executeQuery(query);
            while (rs.next()) {
                return rs.getInt(1) != 0;
            }
        } catch(SQLException ex) {
            java.util.logging.Logger.getLogger(MySQLHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void increaseValue(UUID uuid, PType typ, int value) {
        try {
            String query = "SELECT " + typ + " FROM " + tablePlayers + " WHERE uuid='" + uuid.toString() + "';";
            stat = con.createStatement();
            log(query);
            ResultSet rs = stat.executeQuery(query);
            int receivedvalue = 0;
            while(rs.next()) {
                receivedvalue = rs.getInt(typ.toString().toLowerCase());
            }
            receivedvalue += value;
            String query2 = "UPDATE " + tablePlayers + " SET " + typ + "=" + receivedvalue + " WHERE uuid=" + uuid.toString() + ";";
            log(query2);
            stat.execute(query2);

        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }

}
