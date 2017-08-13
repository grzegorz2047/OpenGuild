package pl.grzegorz2047.openguild2047.database;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Created by grzeg on 13.08.2017.
 */
public interface SQLImplementationStrategy {

    HikariDataSource getDataSource();
}
