package pl.grzegorz2047.openguild2047.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import pl.grzegorz2047.openguild2047.GenConf;

/**
 * Created by grzeg on 13.08.2017.
 */
public class SQLiteImplementationStrategy implements SQLImplementationStrategy {
    @Override
    public HikariDataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("OpenGUildSQLitePool");
        config.setDriverClassName("org.sqlite.JDBC");
        config.setJdbcUrl("jdbc:sqlite:" + GenConf.FILE_DIR);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000); // 60 Sec
        config.setIdleTimeout(45000); // 45 Sec
        config.setMaximumPoolSize(50); // 50 Connections (including idle connections)
        return new HikariDataSource(config);
    }
}
