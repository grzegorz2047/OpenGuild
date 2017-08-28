package pl.grzegorz2047.openguild.database.sqlite;

import pl.grzegorz2047.openguild.configuration.GenConf;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.database.interfaces.SQLTables;

import java.sql.Statement;

/**
 * Created by grzeg on 23.08.2017.
 */
public class SQLiteTables implements SQLTables {

    private String cuboidsTableName = "`" + GenConf.sqlTablePrefix + "cuboids`";
    private String playersTableName = "`" + GenConf.sqlTablePrefix + "players`";
    private String alliesTableName = "`" + GenConf.sqlTablePrefix + "allies`";
    private String guildsTableName = "`" + GenConf.sqlTablePrefix + "guilds`";

    @Override
    public void createTables(SQLHandler handler) {
        OpenGuild.getOGLogger().info("[DB] Creating tables if not exists ...");

        try {
            Statement statement = handler.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS " + guildsTableName
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

            query = "CREATE TABLE IF NOT EXISTS " + cuboidsTableName
                    + "("
                    + "tag VARCHAR(11),"
                    + "cuboid_center_x INT,"
                    + "cuboid_center_z INT,"
                    + "cuboid_size INT,"
                    + "cuboid_worldname VARCHAR(60),"
                    + "PRIMARY KEY(tag));";
            statement.addBatch(query);

            query = "CREATE TABLE IF NOT EXISTS " + playersTableName
                    + "(guild VARCHAR(11),"
                    + "uuid VARCHAR(37),"
                    + "kills INT,"
                    + "deaths INT,"
                    + "points INT,"
                    + "elo INT,"
                    + "lastseenname VARCHAR(16),"
                    + "PRIMARY KEY(uuid));";
            statement.addBatch(query);

            query = "CREATE TABLE IF NOT EXISTS " + alliesTableName
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
}
