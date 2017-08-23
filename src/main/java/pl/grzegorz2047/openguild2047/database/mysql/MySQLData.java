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
package pl.grzegorz2047.openguild2047.database.mysql;

import pl.grzegorz2047.openguild2047.database.interfaces.SQLData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Aleksander
 */
public class MySQLData implements SQLData {
    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String name;
    
    public MySQLData(String host, int port, String user, String password, String name) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.name = name;
    }
    
    @Override
    public Connection getDriver() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + name + "?autoReconnect=true", user, password);
        } catch(SQLException ex) {
            ex.printStackTrace();
        } catch(ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return connection;
    }
    
    @Override
    public String name() {
        return "mysql";
    }
    
    public String getHost() {
        return this.host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public String getUser() {
        return this.user;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public String getName() {
        return this.name;
    }
}
