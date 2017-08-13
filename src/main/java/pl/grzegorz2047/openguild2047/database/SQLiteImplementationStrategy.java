package pl.grzegorz2047.openguild2047.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pl.grzegorz2047.openguild2047.GenConf;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by grzeg on 13.08.2017.
 */
public class SQLiteImplementationStrategy implements SQLImplementationStrategy {
    @Override
    public Connection getConnection() throws Exception {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            connection = DriverManager.getConnection("jdbc:sqlite:" + GenConf.FILE_DIR);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (connection == null) {
            throw new Exception("Connection is null1");
        }
        return connection;
    }
}
