/*
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.grzegorz2047.openguild2047.handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.api.Logger;

/**
 *
 * @author Grzegorz
 */
public class SQLite
{
    private static final String DRIVER = "org.sqlite.JDBC";
    private static final String DB_URL = "jdbc:sqlite:plugins/OpenGuild/og.db";
    
    public Connection conn;
    public Statement st;
    
    private static String tableGuilds = "openguild_guilds";
    private static String tablePlayers = "openguild_players";
    private static Logger log = Guilds.getLogger();
    
    public void createSQLiteConn(){
    try{
        Class.forName(DRIVER);
    }
    catch (ClassNotFoundException e){
        System.err.println("Brak wymaganego sterownika JDBC");
        e.printStackTrace();
    }
    try{
        this.conn = DriverManager.getConnection(DB_URL);
        this.st = this.conn.createStatement();
    }
    catch (SQLException e){
        System.err.println("Problem z polaczeniem");
        e.printStackTrace();
    }
    //Tu moze tworzyc tablice
    }
    private boolean createTables(){
        // Tabela z gildiami
        log.info("[SQLite] Tworzenie tabeli " + tableGuilds + " jezeli nie istnieje...");
        try {
            String query = "CREATE TABLE IF NOT EXISTS " + tableGuilds
                    + "(id INT AUTO_INCREMENT,"
                    + "tag VARCHAR(11),"
                    + "description VARCHAR(100),"
                    + "leader VARCHAR(37),"
                    + "sojusze VARCHAR(255),"
                    + "home_x INT,"
                    + "home_y INT,"
                    + "home_z INT,"
                    + "home_w VARCHAR(16),"
                    + "cuboid_radius INT,"
                    + "PRIMARY KEY(id,tag));";
            st = conn.createStatement();
            //log(query);
            st.execute(query);
        } catch(SQLException ex) {
            log.severe("Nie udalo sie stworzyc tabel");
            ex.printStackTrace();
            return false;
        }

        // Tabela z graczami
        log.info("[SQLite] Tworzenie tabeli " + tablePlayers + " jezeli nie istnieje...");
        try {
            String query = "CREATE TABLE IF NOT EXISTS " + tablePlayers
                    + "(id INT AUTO_INCREMENT,"
                    + "guild VARCHAR(11),"
                    + "isleader VARCHAR(5),"
                    + "kills INT,"
                    + "deads INT,"
                    + "uuid VARCHAR(37)," // UUID gracza z myślnikami ma 35 znaków? Więc dla pewności dam 37
                    + "ban_time BIGINT,"
                    + "PRIMARY KEY(id,uuid));";
            st = conn.createStatement();
            //logger(query);
            st.execute(query);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return true;
    }
    
 }