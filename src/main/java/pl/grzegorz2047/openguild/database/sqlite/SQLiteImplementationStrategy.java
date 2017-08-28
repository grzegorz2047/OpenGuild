package pl.grzegorz2047.openguild.database.sqlite;

import pl.grzegorz2047.openguild.configuration.GenConf;
import pl.grzegorz2047.openguild.database.interfaces.SQLImplementationStrategy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * File created by grzegorz2047 on 13.08.2017.
 */
public class SQLiteImplementationStrategy implements SQLImplementationStrategy {
    @Override
    public Connection getConnection() throws Exception {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            connection = DriverManager.getConnection("jdbc:sqlite:" + GenConf.FILE_DIR);
        } catch (InstantiationException | IllegalAccessException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (connection == null) {
            throw new Exception("Connection is null1");
        }
        return connection;
    }
}
