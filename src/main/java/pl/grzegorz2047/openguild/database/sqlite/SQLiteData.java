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
package pl.grzegorz2047.openguild.database.sqlite;

import pl.grzegorz2047.openguild.database.interfaces.SQLData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Aleksander
 */
public class SQLiteData implements SQLData {
    private final String dir;
    
    public SQLiteData(String dir) {
        this.dir = dir;
    }
    
    @Override
    public Connection getDriver() {
        Connection connection = null;
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
            connection = DriverManager.getConnection("jdbc:sqlite:" + getDir());
        } catch(ClassNotFoundException | SQLException | InstantiationException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
        return connection;
    }
    
    @Override
    public String name() {
        return "sqlite";
    }
    
    private String getDir() {
        return this.dir;
    }
}
