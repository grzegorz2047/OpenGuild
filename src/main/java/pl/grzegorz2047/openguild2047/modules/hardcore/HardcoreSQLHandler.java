/*
 * Copyright 2014
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
package pl.grzegorz2047.openguild2047.modules.hardcore;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.database.SQLHandler;

/**
 *
 * @author Grzegorz
 */
public class HardcoreSQLHandler {
    /* Można zrobić API do dostania się na polaczenie openguilda */
    public enum Column {
        BAN_TIME,
        UUID,
        NICK
    };
    public static final String TABLENAME = "openguild_bans";
    public static boolean createTables(){
        String query = "CREATE TABLE IF NOT EXISTS '"+TABLENAME+"' (UUID VARCHAR(36) NOT NULL primary key, NICK VARCHAR(16) NOT NULL, BAN_TIME DEC NOT NULL)";
        OpenGuild.getInstance().getOGLogger().debug(query);
        return OpenGuild.getInstance().getSQLHandler().execute(query);
    }
    
    public static void update(UUID uniqueId, Column column, String value) {
        try {
            SQLHandler sql = OpenGuild.getInstance().getSQLHandler();
            Statement st = sql.getConnection().createStatement();
            String query;
            if(sql.isConnectionClosed()){
                System.out.print("Polaczenie jest zamkniete?!");
            }
            if(playerExists(uniqueId)){
                query = "UPDATE '"+TABLENAME+"' SET "+column.toString()+"='"+value+"' WHERE "+Column.UUID.toString()+"='"+uniqueId+"'";
                System.out.print("prop query to "+query);
            }else{
                query = "INSERT INTO '"+TABLENAME+"' VALUES('"+uniqueId+"', '"+Bukkit.getOfflinePlayer(uniqueId).getName()+"', '"+value+"')";
                System.out.print("prop query to "+query);
            }
            System.out.print("query to "+query);
            if(st.execute(query)){
                System.out.println("Ogarnieto rekord!");
            }else{
                System.out.println("Nie ddodano rekordu!");
            }
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static long getBan(UUID uniqueId) {
        if(playerExists(uniqueId)){
            String query = "SELECT "+Column.BAN_TIME.toString()+" FROM "+TABLENAME+" WHERE "+Column.UUID.toString()+"='"+uniqueId+"'";
            SQLHandler sql = OpenGuild.getInstance().getSQLHandler();
            try {
                Statement st = sql.getConnection().createStatement();
                ResultSet rs = st.executeQuery(query);
                rs.next();
                return (long) rs.getDouble("BAN_TIME");
            } catch (SQLException ex) {
                ex.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    public static boolean playerExists(UUID uniqueId){
        String query = "Select COUNT("+Column.UUID+") FROM "+TABLENAME+" WHERE "+Column.UUID.toString()+"='"+uniqueId+"'";
        ResultSet rs = OpenGuild.getInstance().getSQLHandler().executeQuery(query);
        int rowCount = -1;
        try {
          // get the number of rows from the result set
          rs.next();
          rowCount = rs.getInt(1);
        }
        catch (SQLException ex) {
            OpenGuild.getInstance().getOGLogger().exceptionThrown(ex);
        } finally {
            try {
                rs.close();
            }
            catch (SQLException ex) {
                OpenGuild.getInstance().getOGLogger().exceptionThrown(ex);
            }
        }
        OpenGuild.getInstance().getOGLogger().debug("Counting player "+Bukkit.getOfflinePlayer(uniqueId).getName()+" with UUID "+uniqueId+" returns "+rowCount);
        return rowCount != 0 && rowCount != -1;
    }
    
}
