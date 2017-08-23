package pl.grzegorz2047.openguild2047.database.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by grzeg on 13.08.2017.
 */
public interface SQLImplementationStrategy {

    Connection getConnection() throws Exception;
}
