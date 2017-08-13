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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Location;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.Guilds;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;

public class SQLHandler {

    private OpenGuild plugin;

    private Statement statement;
    private HikariDataSource hikari;

    // public String generateStringAutoInc(){
    //     if(!GenConf.DATABASE.equals(Database.FILE)) return "INT PRIMARY KEY AUTO_INCREMENT"; else return "INTEGER PRIMARY KEY AUTOINCREMENT";
    // }
    //public String generateDefVal(){
    //     if(!GenConf.DATABASE.equals(Database.FILE)) return "0"; else return "null";
    // }
    public SQLHandler(OpenGuild plugin, SQLImplementationStrategy implementation) {
        this.plugin = plugin;
        this.hikari = implementation.getDataSource();
        this.startWork();
    }

    private void connectDBSQLite() {
        hikari = new HikariDataSource();
        HikariConfig config = new HikariConfig();
        config.setPoolName("OpenGUildSQLitePool");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + GenConf.FILE_DIR);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000); // 60 Sec
        config.setIdleTimeout(45000); // 45 Sec
        config.setMaximumPoolSize(50); // 50 Connections (including idle connections)
        hikari = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return hikari.getConnection();
    }

    private void startWork() {
        // Create tables is they doesn't exists
        this.createTables();

        // Load guilds and players from database
        loadGuildsFromDB(plugin.getCuboids(), plugin.getGuilds());
        plugin.getGuilds().setPlayers(this.loadPlayers());
        OpenGuild.getOGLogger().info("Loaded " + plugin.getGuilds().getNumberOfGuilds() + " guilds from database.");
    }

    private void createTables() {
        OpenGuild.getOGLogger().info("[DB] Creating tables if not exists ...");

        try {
            createStatement();
            String query = "CREATE TABLE IF NOT EXISTS `" + GenConf.sqlTablePrefix + "guilds`"
                    + "(tag VARCHAR(11),"
                    + "description VARCHAR(100),"
                    + "leader VARCHAR(37),"
                    + "lives INT,"
                    + "home_x INT,"
                    + "home_y INT,"
                    + "home_z INT,"
                    + "home_pitch INT,"
                    + "home_yaw INT,"
                    + "home_world VARCHAR(16),"
                    + "PRIMARY KEY(tag));";
            statement.addBatch(query);

            query = "CREATE TABLE IF NOT EXISTS `" + GenConf.sqlTablePrefix + "cuboids`"
                    + "(id INT AUTO_INCREMENT,"
                    + "tag VARCHAR(11),"
                    + "cuboid_center_x INT,"
                    + "cuboid_center_z INT,"
                    + "cuboid_size INT,"
                    + "cuboid_worldname VARCHAR(60),"
                    + "PRIMARY KEY(id));";
            statement.addBatch(query);

            query = "CREATE TABLE IF NOT EXISTS `" + GenConf.sqlTablePrefix + "players`"
                    + "(guild VARCHAR(11),"
                    + "uuid VARCHAR(37),"
                    + "kills INT,"
                    + "deaths INT,"
                    + "points INT,"
                    + "lastseenname VARCHAR(16),"
                    + "PRIMARY KEY(uuid));";
            statement.addBatch(query);

            query = "CREATE TABLE IF NOT EXISTS `" + GenConf.sqlTablePrefix + "allies`"
                    + "("
                    + "who VARCHAR(11),"
                    + "withwho VARCHAR(11),"
                    + "status VARCHAR(5),"
                    + "expires BIGINT,"
                    + "PRIMARY KEY(who,withwho)"
                    + ");";
            statement.addBatch(query);
            statement.executeBatch();
            statement.close();
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }
    }

    public void loadTags(Guilds guilds) {
        for (Map.Entry<String, Guild> gs : guilds.getGuilds().entrySet()) {
            Scoreboard sc = gs.getValue().getSc();
            for (Map.Entry<String, Guild> gs2 : guilds.getGuilds().entrySet()) {
                if (gs.getValue().getTag().equals(gs2.getValue().getTag())) {
                    continue;
                }
                Team t;
                if (tagForWhoDoesntExists(gs2.getKey(), sc)) {
                    t = sc.registerNewTeam(gs2.getValue().getTag());
                } else {
                    t = sc.getTeam(gs2.getValue().getTag());
                }
                t.setPrefix(GenConf.allyTag.replace("{TAG}", gs2.getValue().getTag()));
                t.setDisplayName(GenConf.allyTag.replace("{TAG}", gs2.getValue().getTag()));
                setTagsForMembers(gs2.getValue(), t);
            }
            //if(gs.getValue().getTag().equals(joinerGuild.getTag())){
            //     continue;
            //}
            // Team t2 = gs.getValue().getSc().getTeam(joinerGuild.getTag());
            //  if(t2 != null){
            //      t2.insertPlayer(joiner);
            ///  }
        }

    }

    private void loadGuildsFromDB(Cuboids cuboids, Guilds guilds) {
        try {
            createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM `" + GenConf.sqlTablePrefix + "guilds` JOIN `" + GenConf.sqlTablePrefix + "cuboids` USING(tag)");
            loopTroughGuildAndCuboidResults(cuboids, guilds, result);
            result.close();
            statement.close();
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);

        }
    }

    private void createStatement() throws SQLException {
        statement = this.getConnection().createStatement();
    }

    private void loopTroughGuildAndCuboidResults(Cuboids cuboids, Guilds guilds, ResultSet result) throws SQLException {
        while (anotherRecord(result)) {
            readGuildDataFromResult(cuboids, guilds, result);
        }
    }

    private void readGuildDataFromResult(Cuboids cuboids, Guilds guilds, ResultSet result) throws SQLException {
        String tag = result.getString("tag");
        String description = result.getString("description");
        UUID leaderUUID = UUID.fromString(result.getString("leader"));

        String homeWorld = result.getString("home_world");
        if (hasGuildValidWorldNameHome(homeWorld)) {
            OpenGuild.getOGLogger().warning("World '" + homeWorld + "' does not exists! Skipping guild '" + tag + "'!");
            return;
        }

        Location home = getCuboidHome(result, homeWorld);

        Location cuboidCenter = getCuboidCenter(result);

        int cuboidSize = result.getInt("cuboid_size");

        cuboids.addCuboid(cuboidCenter, tag, cuboidSize);
        guilds.addGuild(plugin, home, leaderUUID, tag, description);
        registerGuildTag(tag);
    }

    private boolean hasGuildValidWorldNameHome(String homeWorld) {
        return plugin.getServer().getWorld(homeWorld) == null;
    }

    private Location getCuboidHome(ResultSet result, String homeWorld) throws SQLException {
        int homeX = result.getInt("home_x");
        int homeY = result.getInt("home_y");
        int homeZ = result.getInt("home_z");
        return new Location(plugin.getServer().getWorld(homeWorld), homeX, homeY, homeZ);
    }

    private Location getCuboidCenter(ResultSet result) throws SQLException {
        int cuboidCenterX = result.getInt("cuboid_center_x");
        int cuboidCenterZ = result.getInt("cuboid_center_z");
        String cuboidWorldName = result.getString("cuboid_worldname");

        return new Location(Bukkit.getWorld(cuboidWorldName), cuboidCenterX, 0, cuboidCenterZ);
    }

    private void registerGuildTag(String tag) {
        Team t = plugin.getTagManager().getGlobalScoreboard().registerNewTeam(tag);
        t.setPrefix(GenConf.allyTag.replace("{TAG}", tag));
        t.setDisplayName(ChatColor.RED + tag + " ");
    }


    private Map<UUID, Guild> loadPlayers() {
        Map<UUID, Guild> players = new HashMap<UUID, Guild>();

        try {
            createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM `" + GenConf.sqlTablePrefix + "players`");
            readPlayersDataFromResult(players, result);
            result.close();
            statement.close();
            getConnection().close();
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }

        return players;
    }

    private void readPlayersDataFromResult(Map<UUID, Guild> players, ResultSet result) throws SQLException {
        while (anotherRecord(result)) {
            String guildTag = result.getString("guild");
            //int kills = result.getInt("kills");
            //int deaths = result.getInt("deaths");
            UUID uuid = UUID.fromString(result.getString("uuid"));


            if (!canTagBeRendered(guildTag)) {
                addPlayerWithoutGuild(players, uuid);
                continue;
            }
            Guild playersGuild = plugin.getGuilds().getGuilds().get(guildTag);
            players.put(uuid, playersGuild);
            if (guildDoesntExist(playersGuild)) {
                continue;
            }
            if (!isGuildTagRenederedAlready(guildTag)) {
                renderGuildTag(guildTag, uuid);
            } else {
                addGuildTagToPlayer(guildTag, uuid);
            }
            Scoreboard whoSc = playersGuild.getSc();

            Team whoT;
            if (tagForWhoDoesntExists(guildTag, whoSc)) {
                whoT = createGuildTag(guildTag, whoSc);
                //System.out.print("whoT to "+whoT.getName()+" z dn "+whoT.getDisplayName());
                //System.out.print("Dodaje gracza "+Bukkit.getOfflinePlayer(uuid).getName());
                addPlayerToGuildTag(uuid, whoT);
            } else {
                whoT = whoSc.getTeam(guildTag);
                //System.out.print("Dodaje gracza "+Bukkit.getOfflinePlayer(uuid).getName());
                addPlayerToGuildTag(uuid, whoT);
            }
        }
    }

    private void addPlayerWithoutGuild(Map<UUID, Guild> players, UUID uuid) {
        players.put(uuid, null);
    }

    private void addPlayerToGuildTag(UUID uuid, Team whoT) {
        whoT.addPlayer(Bukkit.getOfflinePlayer(uuid));
    }

    private Team createGuildTag(String guildTag, Scoreboard whoSc) {
        Team whoT;
        whoT = whoSc.registerNewTeam(guildTag);
        whoT.setPrefix(GenConf.guildTag.replace("{TAG}", guildTag));
        whoT.setDisplayName(GenConf.guildTag.replace("{TAG}", guildTag));
        return whoT;
    }

    private void addGuildTagToPlayer(String guildTag, UUID uuid) {
        Team t = plugin.getTagManager().getGlobalScoreboard().getTeam(guildTag);
        addPlayerToGuildTag(uuid, t);
    }

    private void renderGuildTag(String guildTag, UUID uuid) {
        Team t = plugin.getTagManager().getGlobalScoreboard().registerNewTeam(guildTag);
        t.setPrefix(GenConf.enemyTag.replace("{TAG}", guildTag));
        t.setDisplayName(GenConf.enemyTag.replace("{TAG}", guildTag));
        addPlayerToGuildTag(uuid, t);
    }

    private boolean isGuildTagRenederedAlready(String guildTag) {
        return plugin.getTagManager().getGlobalScoreboard().getTeam(guildTag) != null;
    }

    private boolean canTagBeRendered(String guildTag) {
        return !guildTag.isEmpty() && plugin.getGuilds().doesGuildExists(guildTag);
    }

    public Statement getStatement() {
        return statement;
    }

    public void loadRelations() {
        try {
            createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM `" + GenConf.sqlTablePrefix + "allies`");
            while (anotherRecord(result)) {
                String who = result.getString("who");
                //int kills = result.getInt("kills");
                //int deaths = result.getInt("deaths");
                String status = result.getString("status");
                String withwho = result.getString("withwho");
                long expires = result.getInt("expires");
                Relation.Status relationStatus = Relation.Status.valueOf(status.toUpperCase());
                Relation relation = createRelation(who, withwho, expires, relationStatus);

                Guild whoGuild = plugin.getGuilds().getGuilds().get(who);
                if (guildDoesntExist(whoGuild)) continue;
                Scoreboard whoSc = whoGuild.getSc();

                Guild withWhoGuild = plugin.getGuilds().getGuilds().get(withwho);
                if (guildDoesntExist(withWhoGuild)) {
                    continue;
                }
                Scoreboard withWhoSc = withWhoGuild.getSc();

                Team whoScoreboardTeamTag = prepareGuildTag(who, withWhoSc);
                setTagsForMembers(whoGuild, whoScoreboardTeamTag);


                Team withWhoScoreboardTag = prepareGuildTag(withwho, whoSc);
                setTagsForMembers(withWhoGuild, withWhoScoreboardTag);

                addGuildAlly(relation, whoGuild);
                addGuildAlly(relation, withWhoGuild);

            }
            result.close();
            statement.close();
            getConnection().close();
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }

    }

    private Relation createRelation(String who, String withwho, long expires, Relation.Status relationStatus) {
        return new Relation(who, withwho, expires, relationStatus);
    }

    private boolean anotherRecord(ResultSet result) throws SQLException {
        return result.next();
    }

    private void addGuildAlly(Relation r, Guild guild) {
        guild.getAlliances().add(r);
    }

    private boolean guildDoesntExist(Guild guild) {
        return guild == null;
    }

    private Team prepareGuildTag(String guildTag, Scoreboard guildScoreboard) {
        Team scoreboardTeamTag;
        if (tagForWhoDoesntExists(guildTag, guildScoreboard)) {
            scoreboardTeamTag = guildScoreboard.registerNewTeam(guildTag);
        } else {
            scoreboardTeamTag = guildScoreboard.getTeam(guildTag);
        }
        scoreboardTeamTag.setPrefix(GenConf.allyTag.replace("{TAG}", guildTag));
        scoreboardTeamTag.setDisplayName(GenConf.allyTag.replace("{TAG}", guildTag));
        return scoreboardTeamTag;
    }

    private void setTagsForMembers(Guild guild, Team team) {
        for (UUID member : guild.getMembers()) {
            addPlayerToGuildTag(member, team);
        }
    }

    private boolean tagForWhoDoesntExists(String who, Scoreboard withWhoSc) {
        return withWhoSc.getTeam(who) == null;
    }

    /**
     * Adds player to database.
     * It does not check if player already is in database!
     *
     * @param player instance of UUID class.
     */
    public void insertPlayer(UUID player) {
        final String uuid = player.toString();
        try {
            createStatement();
            statement.execute("INSERT INTO `" + GenConf.sqlTablePrefix + "players` VALUES( '', '" + uuid + "', '" + 0 + "', '" + 0 + "', '" + 0 + "' , '" + Bukkit.getPlayer(player).getName() + "');");
            statement.close();
            getConnection().close();
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }
    }

    /**
     * Updates player's guild tag in database.
     *
     * @param uuid uuid of player.
     */
    public void updatePlayerTag(UUID uuid) {
        String guildTag = "";
        if (plugin.getGuilds().getPlayers().containsKey(uuid) &&
                plugin.getGuilds().getPlayers().get(uuid) != null) {
            guildTag = plugin.getGuilds().getPlayers().get(uuid).getTag().toUpperCase();
        }

        try {
            createStatement();
            statement.executeUpdate("UPDATE `" + GenConf.sqlTablePrefix + "players` SET `guild` = '" + guildTag + "' WHERE `uuid` = '" + uuid.toString() + "'");
            statement.close();
            getConnection().close();
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }

    }

    /**
     * Adds guild to database.
     * It does not check if guild is already in database!
     */
    public void insertGuild(String tag, String description, UUID leader, Location guildHome, String homeWorld) {
        try {
            createStatement();
            statement.execute("INSERT INTO `" + GenConf.sqlTablePrefix + "guilds` VALUES(" +
                    "'" + tag.toUpperCase() + "'," +
                    "'" + description + "'," +
                    "'" + leader + "'," +
                    "'" + 0 + "'," +
                    "'" + guildHome.getX() + "'," +
                    "'" + guildHome.getY() + "'," +
                    "'" + guildHome.getZ() + "'," +
                    "'" + guildHome.getPitch() + "'," +
                    "'" + guildHome.getYaw() + "'," +
                    "'" + homeWorld + "');");

            statement.close();
            getConnection().close();
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }
    }

    /**
     * Updates guild's description in database.
     *
     * @param guild instance of SimpleGuild class.
     */
    public void updateGuildDescription(Guild guild) {
        try {
            createStatement();
            statement.executeUpdate("UPDATE `" + GenConf.sqlTablePrefix + "guilds` SET `description` = '" + guild.getDescription() + "' WHERE `tag` = '" + guild.getTag().toUpperCase() + "'");

            statement.close();
            getConnection().close();
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }
    }

    /**
     * Removes guild from database.
     *
     * @param tag tag of guild, which should be deleted.
     */
    public void removeGuild(String tag) {
        try {
            createStatement();
            statement.execute("DELETE FROM `" + GenConf.sqlTablePrefix + "guilds` WHERE `tag` = '" + tag + "'");

            statement.close();
            getConnection().close();
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }
    }

    public boolean insertAlliance(Guild who, Guild withWho) {
        try {
            createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM `" + GenConf.sqlTablePrefix + "allies`" + " WHERE who='" + who + "'" + "OR withwho='" + who + "';");
            if (!containsAlliance(rs)) {
                statement.execute("INSERT INTO `" + GenConf.sqlTablePrefix + "allies` VALUES('" + who.getTag() + "', '" + withWho.getTag() + "', '" + Relation.Status.ALLY.toString() + "', 0);");
                return true;
            }
            while (anotherRecord(rs)) {
                String whoseguild = rs.getString("who");
                String withwho = rs.getString("withwho");
                if (isAlreadyAlliance(who, withWho, whoseguild, withwho)) {
                    return false;
                } else {
                    statement.execute("INSERT INTO `" + GenConf.sqlTablePrefix + "allies` VALUES('" + who.getTag() + "', '" + withWho.getTag() + "', '" + Relation.Status.ALLY.toString() + "', 0);");
                    return true;
                }
            }
            rs.close();
            statement.close();
            getConnection().close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean isAlreadyAlliance(Guild who, Guild withWho, String whoseguild, String withwho) {
        return (whoseguild.equals(who.getTag()) && withwho.equals(withWho.getTag())) || (whoseguild.equals(withWho.getTag()) && withwho.equals(who.getTag()));
    }

    private boolean containsAlliance(ResultSet rs) throws SQLException {
        return rs.isFirst();
    }

    public boolean removeAlliance(Guild who, Guild withWho) {
        try {
            createStatement();
            statement.addBatch("DELETE FROM `" + GenConf.sqlTablePrefix + "allies` WHERE who='" + who.getTag() + "' AND withwho='" + withWho.getTag() + "';");
            statement.addBatch("DELETE FROM `" + GenConf.sqlTablePrefix + "allies` WHERE who='" + withWho.getTag() + "' AND withwho='" + who.getTag() + "';");
            statement.executeBatch();

            statement.close();
            getConnection().close();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }


    public ResultSet executeQuery(String query) {
        try {
            createStatement();
            return statement.executeQuery(query);
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
            return null;
        }
    }

    public int executeUpdate(String query) {
        try {
            createStatement();
            return statement.executeUpdate(query);
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
            return -1;
        }
    }

    public boolean execute(String query) {
        try {
            createStatement();
            return statement.execute(query);
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
            return false;
        }
    }

    public void addGuildCuboid(Location loc, int size, String owner, String worldName) {
        try {
            createStatement();
            statement.execute("INSERT INTO `" + GenConf.sqlTablePrefix + "cuboids` " +
                    "VALUES(" +
                    "'0'," +
                    "'" + owner + "'," +
                    "'" + loc.getBlockX() + "'," +
                    "'" + loc.getBlockZ() + "'," +
                    "'" + size + "'," +
                    "'" + worldName + "');");

            statement.close();
            getConnection().close();
        } catch (SQLException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }

    }
}



