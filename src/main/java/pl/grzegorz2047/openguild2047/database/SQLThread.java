/*
 * Copyright 2014 Aleksander.
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
import java.sql.SQLException;
import java.sql.Statement;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.utils.Callback;

/**
 * All SQL statements are executed in threads.
 * 
 * How to use:
 * 
 * Thread thread = new Thread(new SQLThread(connection, "Query", new Callback<Object>() {
 *     @Override
 *     public void call(Object result, Exception exception) {
 *         if(exception == null) // Everything went fine!
 *             if(result instanceof Boolean) { // update statement
 *
 *             } else if(result instanceof ResultSet) { // other statements
 *
 *             } else {
 *                 // unknown result type.
 *             }
 *         } else {
 *             // show (or not) error.
 *         }
 *     }
 * })).start();
 */
public class SQLThread implements Runnable {
    private Connection connection;
    private final SQLData data = GenConf.getSqlData();
    private final String query;
    
    private Callback<Object> callback;
    
    public SQLThread(Connection connection, String query, Callback<Object> callback) {
        this.connection = connection;
        this.query = query;
        this.callback = callback;
    }
    
    @Override
    public void run() {
        try {
            if(connection.isClosed()) {
                connection = data.getDriver();
            }
            
            Statement statement = connection.createStatement();
            
            // Update query can only use statement.execute() method
            // Otherwise - it will throw an error.
            if(query.startsWith("UPDATE")) {
                callback.call(statement.execute(query), null);
            } else {
                callback.call(statement.executeQuery(query), null);
            }
        } catch (SQLException ex) {
            callback.call(null, ex);
        }
        
        callback.call(null, null);
    }
    
    public SQLData getData() {
        return this.data;
    }
    
    public String getQuery() {
        return this.query;
    }
    
}
