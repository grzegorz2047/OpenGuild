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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.Guilds;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.managers.TagManager;

public class SQLHandler {

    private final Guilds guilds;
    private OpenGuild plugin;

    private Statement statement;
    private SQLImplementationStrategy implementation;

    public SQLHandler(OpenGuild plugin, SQLImplementationStrategy implementation, Guilds guilds) {
        this.plugin = plugin;
        this.guilds = guilds;
        try {
            this.implementation = implementation;
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.startWork();
    }

    public Connection getConnection() throws Exception {
        return implementation.getConnection();
    }

    private void startWork() {
        // Create tables is they doesn't exists
        this.createTables();

        // Load guilds and players from database
        loadGuildsFromDB(plugin.getCuboids(), guilds);
        this.loadPlayers();
        OpenGuild.getOGLogger().info("Loaded " + guilds.getNumberOfGuilds() + " guilds from database.");
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
                    + "("
                    + "tag VARCHAR(11),"
                    + "cuboid_center_x INT,"
                    + "cuboid_center_z INT,"
                    + "cuboid_size INT,"
                    + "cuboid_worldname VARCHAR(60),"
                    + "PRIMARY KEY(tag));";
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
        } catch (Exception ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }
    }

    public void addKill(Player killer) {
        incrementStats(killer.getUniqueId(), "kills");
    }

    public void addDeath(Player player) {
        incrementStats(player.getUniqueId(), "deaths");
    }

    private void loadGuildsFromDB(Cuboids cuboids, Guilds guilds) {
        try {
            createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM `" + GenConf.sqlTablePrefix + "guilds` JOIN `" + GenConf.sqlTablePrefix + "cuboids` USING(tag)");
            loopTroughGuildAndCuboidResults(cuboids, guilds, result);
            result.close();
            statement.close();
        } catch (Exception ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);

        }
    }

    private void createStatement() throws Exception {
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
    }

    private boolean hasGuildValidWorldNameHome(String homeWorld) {
        return Bukkit.getWorld(homeWorld) == null;
    }

    private Location getCuboidHome(ResultSet result, String homeWorld) throws SQLException {
        int homeX = result.getInt("home_x");
        int homeY = result.getInt("home_y");
        int homeZ = result.getInt("home_z");
        return new Location(Bukkit.getWorld(homeWorld), homeX, homeY, homeZ);
    }

    private Location getCuboidCenter(ResultSet result) throws SQLException {
        int cuboidCenterX = result.getInt("cuboid_center_x");
        int cuboidCenterZ = result.getInt("cuboid_center_z");
        String cuboidWorldName = result.getString("cuboid_worldname");

        return new Location(Bukkit.getWorld(cuboidWorldName), cuboidCenterX, 0, cuboidCenterZ);
    }

    private void loadPlayers() {
        try {
            createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM `" + GenConf.sqlTablePrefix + "players`");
            readPlayersDataFromResult(result);
            result.close();
            statement.close();
            statement.getConnection().close();
        } catch (Exception ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }
    }

    private void readPlayersDataFromResult(ResultSet result) throws SQLException {
        while (anotherRecord(result)) {
            String guildTag = result.getString("guild");
            UUID uuid = UUID.fromString(result.getString("uuid"));
            if (!guilds.doesGuildExists(guildTag)) {
                continue;
            }
            Guild playersGuild = guilds.getGuild(guildTag);
            guilds.addPlayer(uuid, playersGuild);
        }
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


                if (guilds.doesGuildExists(who)) {
                    continue;
                }
                Guild whoGuild = guilds.getGuild(who);

                if (guilds.doesGuildExists(withwho)) {
                    continue;
                }
                Guild withWhoGuild = guilds.getGuild(withwho);

                whoGuild.addAlly(withwho, expires, relationStatus);
                withWhoGuild.addAlly(who, expires, relationStatus);
            }
            result.close();
            statement.close();
            statement.getConnection().close();
        } catch (Exception ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }

    }


    private boolean anotherRecord(ResultSet result) throws SQLException {
        return result.next();
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
            statement.getConnection().close();
        } catch (Exception ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }
    }

    /**
     * Updates player's guild tag in database.
     *
     * @param uuid uuid of player.
     */
    public void updatePlayerTag(UUID uuid, String guildTag) {
        try {
            createStatement();
            statement.executeUpdate("UPDATE `" + GenConf.sqlTablePrefix + "players` SET `guild` = '" + guildTag + "' WHERE `uuid` = '" + uuid.toString() + "'");
            statement.close();
            statement.getConnection().close();
        } catch (Exception ex) {
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
            statement.getConnection().close();
        } catch (Exception ex) {
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
            statement.executeUpdate("UPDATE `" + GenConf.sqlTablePrefix + "guilds` SET `description` = '" + guild.getDescription() + "' WHERE `tag` = '" + guild.getName().toUpperCase() + "'");

            statement.close();
            statement.getConnection().close();
        } catch (Exception ex) {
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
            statement.getConnection().close();
        } catch (Exception ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }
    }

    public boolean insertAlliance(Guild who, Guild withWho) {
        try {
            createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM `" + GenConf.sqlTablePrefix + "allies`" + " WHERE who='" + who + "'" + "OR withwho='" + who + "';");
            if (!containsAlliance(rs)) {
                statement.execute("INSERT INTO `" + GenConf.sqlTablePrefix + "allies` VALUES('" + who.getName() + "', '" + withWho.getName() + "', '" + Relation.Status.ALLY.toString() + "', 0);");
                rs.close();
                statement.close();
                statement.getConnection().close();
                return true;
            }
            while (anotherRecord(rs)) {
                String whoseguild = rs.getString("who");
                String withwho = rs.getString("withwho");
                if (isAlreadyAlliance(who, withWho, whoseguild, withwho)) {
                    rs.close();
                    statement.close();
                    statement.getConnection().close();
                    return false;
                } else {
                    statement.execute("INSERT INTO `" + GenConf.sqlTablePrefix + "allies` VALUES('" + who.getName() + "', '" + withWho.getName() + "', '" + Relation.Status.ALLY.toString() + "', 0);");
                    rs.close();
                    statement.close();
                    statement.getConnection().close();
                    return true;
                }
            }
            rs.close();
            statement.close();
            statement.getConnection().close();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        return false;
    }

    private boolean isAlreadyAlliance(Guild who, Guild withWho, String whoseguild, String withwho) {
        return (whoseguild.equals(who.getName()) && withwho.equals(withWho.getName())) || (whoseguild.equals(withWho.getName()) && withwho.equals(who.getName()));
    }

    private boolean containsAlliance(ResultSet rs) throws SQLException {
        return rs.isFirst();
    }

    public boolean removeAlliance(Guild who, Guild withWho) {
        try {
            createStatement();
            statement.addBatch("DELETE FROM `" + GenConf.sqlTablePrefix + "allies` WHERE who='" + who.getName() + "' AND withwho='" + withWho.getName() + "';");
            statement.addBatch("DELETE FROM `" + GenConf.sqlTablePrefix + "allies` WHERE who='" + withWho.getName() + "' AND withwho='" + who.getName() + "';");
            statement.executeBatch();

            statement.close();
            statement.getConnection().close();
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void incrementStats(UUID uuid, String column) {
        try {
            createStatement();
            try {
                statement.execute("UPDATE '" + GenConf.sqlTablePrefix + "players' SET '" + column +
                        "'=" + column + "+1 WHERE uuid='" + uuid.toString() + "';");
                statement.close();
                statement.getConnection().close();
            } catch (SQLException ex) {
                OpenGuild.getAPI().getLogger().exceptionThrown(ex);
            }
        } catch (Exception e) {
            OpenGuild.getAPI().getLogger().exceptionThrown(e);
        }

    }

    public ResultSet executeQuery(String query) {
        try {
            createStatement();
            return statement.executeQuery(query);
        } catch (Exception ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
            return null;
        }
    }

    public int executeUpdate(String query) {
        try {
            createStatement();
            return statement.executeUpdate(query);
        } catch (Exception ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
            return -1;
        }
    }

    public boolean execute(String query) {
        try {
            createStatement();
            return statement.execute(query);
        } catch (Exception ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
            return false;
        }
    }

    public void addGuildCuboid(Location loc, int size, String owner, String worldName) {
        try {
            createStatement();
            statement.execute("INSERT INTO `" + GenConf.sqlTablePrefix + "cuboids` " +
                    "VALUES(" +
                    "'" + owner + "'," +
                    "'" + loc.getBlockX() + "'," +
                    "'" + loc.getBlockZ() + "'," +
                    "'" + size + "'," +
                    "'" + worldName + "');");

            statement.close();
            statement.getConnection().close();
        } catch (Exception ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
        }

    }
}



