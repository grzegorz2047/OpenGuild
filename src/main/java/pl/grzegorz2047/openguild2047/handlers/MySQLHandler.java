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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import pl.grzegorz2047.openguild2047.Data;
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
	
	public MySQLHandler(String address, String database, String login, String password) {
		this.address = address;
		this.database = database;
		createConnection(login, password);
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
		PLAYER,
		PLAYER_LOWER,
		GUILD,
		KILLS,
		DEADS
	}
        void loadDatabase(){
            Data.getInstance().guildsplayers = MySQLHandler.getAllPlayers();
            Data.getInstance().guilds = MySQLHandler.getAllGuildswithCuboids();
        }
	private void createConnection(String login, String password) {
		log.info("[MySQL] Laczenie z baza MySQL...");
		try {
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection("jdbc:mysql://" + address + ":3306/" + database, login, password);
			log.info("[MySQL] Skutecznie polaczono!");
			createTables();
		} catch(ClassNotFoundException ex) {
			Guilds.getLogger().severe("[MySQL] Wystapil blad z zaladowaniem sterownika " + driver + " pod baze MySQL!");
		} catch(InstantiationException ex) {
			ex.printStackTrace();
		} catch(IllegalAccessException ex) {
			ex.printStackTrace();
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
                loadDatabase();
	}
	
	public void createTables() {
		// Tabela z gildiami
		log.info("[MySQL] Tworzenie tabeli " + tableGuilds + " jezeli nie istnieje...");
		try {
			stat = con.createStatement();
			stat.execute("CREATE TABLE IF NOT EXISTS " + tableGuilds +
					"(id INT AUTO_INCREMENT," +
					"tag VARCHAR(11)," +
					"description VARCHAR(100)," +
					"leader VARCHAR(16)," +
					"sojusze VARCHAR(255)," +
					"home_x INT," +
					"home_y INT," +
					"home_z INT," +
                                        "home_w VARCHAR(16)," +
					"cuboid_radius INT," +
					"PRIMARY KEY(id,tag));");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
		
		// Tabela z graczami
		log.info("[MySQL] Tworzenie tabeli " + tablePlayers + " jezeli nie istnieje...");
		try {
			stat = con.createStatement();
			stat.execute("CREATE TABLE IF NOT EXISTS " + tablePlayers +
					"(id INT AUTO_INCREMENT," +
					"player VARCHAR(16)," +
					"player_lower VARCHAR(16)," +
					"guild VARCHAR(11)," +
                                        "isleader VARCHAR(5)," +
					"kills INT," +
					"deads INT," +
					"PRIMARY KEY(id,player));");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void delete(Guild guild) {
		try {
			stat = con.createStatement();
			stat.execute("DELETE FROM " + tableGuilds + " WHERE tag='" + guild.getTag() + "';");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void delete(String player) {
		try {
			stat = con.createStatement();
			stat.execute("DELETE FROM " + tablePlayers + " WHERE player_lower='" + player.toLowerCase() + "';");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void insert(String tag, String description, String leader, String sojusze, int homeX, int homeY, int homeZ,String homeW, int cuboidRadius) {
		try {
			stat = con.createStatement();
			stat.execute("INSERT INTO " + tableGuilds + " VALUES(NULL," +
					"'" + tag + "'," +
					"'" + description + "'," +
					"'" + leader + "'," +
					"'" + sojusze + "'," +
					homeX + "," +
					homeY + "," +
					homeZ + "," +
                                        "'" +homeW + "'," +
					cuboidRadius + ");");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void insert(String player, Guild guild,String isleader,  int kills, int deads) {
		try {
			stat = con.createStatement();
			stat.execute("INSERT INTO " + tablePlayers + " VALUES(NULL," +
					"'" + player + "'," +
					"'" + player.toLowerCase() + "'," +
					"'" + guild.getTag() + "'," +
                                        "'" + isleader + "'," +
					kills + "," +
					deads + ");");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static List<Object> select(Guild guild) {
		return null; // TODO
	}
	
	public static List<Object> select(String player) {
		return null; // TODO
	}
	
	public static void update(Guild guild, Type type, int value) {
		try {
			stat = con.createStatement();
			stat.execute("UPDATE " + tableGuilds + " SET " + type.toString().toLowerCase() + "=" + value + " WHERE tag='" + guild.getTag() + "';");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void update(Guild guild, Type type, String value) {
		try {
			stat = con.createStatement();
			stat.execute("UPDATE " + tableGuilds + " SET " + type.toString().toLowerCase() + "='" + value + "' WHERE tag='" + guild.getTag() + "';");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void update(String player, PType type, int value) {
		try {
			stat = con.createStatement();
			stat.execute("UPDATE " + tablePlayers + " SET " + type.toString().toLowerCase() + "=" + value + " WHERE player_lower=" + player.toLowerCase() + ";");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void update(String player, PType type, String value) {
		try {
			stat = con.createStatement();
			stat.execute("UPDATE " + tablePlayers + " SET " + type.toString().toLowerCase() + "=" + value + " WHERE player_lower='" + player.toLowerCase() + "';");
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
        public static HashMap<String, SimpleGuild> getAllGuildswithCuboids(){
            HashMap hm = new HashMap<String, SimpleGuild>();
            try {
                stat = con.createStatement();
                ResultSet rs = stat.executeQuery("SELECT * FROM "+ tableGuilds);
                while(rs.next()){
                    String tag = rs.getString("tag");
                    Data.getInstance().ClansTag.add(tag);
                    SimpleGuild g = new SimpleGuild(tag);
                    g.setLeader(rs.getString("leader"));
                    g.setDescription(rs.getString("description"));
                    g.setMembers(getGuildMembers(tag));
                    Location loc = new Location(Bukkit.getWorld(rs.getString("home_w")), rs.getInt("home_x"),rs.getInt("home_y"),rs.getInt("home_z"));
                    SimpleCuboid sc = new SimpleCuboid();
                    sc.setCenter(loc);
                    sc.setOwner(tag);
                    sc.setRadius(rs.getInt("cuboid_radius"));
                    Data.getInstance().cuboids.put(tag, sc);
                    g.setHome(loc);
                    hm.put(g.getTag(), g);
                }          
            }
            catch (SQLException ex) {
                java.util.logging.Logger.getLogger(MySQLHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
            return hm;
        }
        
        public static List<String> getGuildMembers(String tag){
            try {
                List<String> members = new ArrayList<String>();
                stat = con.createStatement();
                ResultSet rs = stat.executeQuery("SELECT player FROM "+ tablePlayers + " WHERE guild='" + tag + "';");
                while(rs.next()){
                    String p = rs.getString("player");
                    members.add(p);
                }
                return members;
            }
            catch (SQLException ex) {
                
                java.util.logging.Logger.getLogger(MySQLHandler.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        
         
        public static HashMap<String, SimplePlayerGuild> getAllPlayers(){
            HashMap hm = new HashMap<String, SimplePlayerGuild>();
            try {
                stat = con.createStatement();
                ResultSet rs = stat.executeQuery("SELECT * FROM "+ tablePlayers);
                while(rs.next()){
                    String player = rs.getString("player");
                    String tag = rs.getString("guild");
                    boolean isleader = Boolean.parseBoolean(rs.getString("isleader"));
                    
                    SimplePlayerGuild sg = new SimplePlayerGuild(player,tag,isleader);
                    hm.put(player, sg);
                }
            }
            catch (SQLException ex) {       
                java.util.logging.Logger.getLogger(MySQLHandler.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
            return hm;
        }

	 
}
