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
import java.util.UUID;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.OpenGuild;

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
    public static final String TABLENAME = "bans";
    public static boolean createTables(){
        String query = "CREATE TABLE IF NOT EXISTS "+TABLENAME+" (UUID VARCHAR(36) NOT NULL primary key, NICK VARCHAR(16) NOT NULL, BAN_TIME DEC NOT NULL)";
        OpenGuild.getInstance().getOGLogger().debug(query);
        return OpenGuild.getInstance().getSQLHandler().execute(query);
    }
    
    public static void update(UUID uniqueId, Column column, String value) {
        String query = "UPDATE "+TABLENAME+" SET "+column.toString()+"='"+value+"' WHERE "+Column.UUID+"='"+uniqueId+"'";
        OpenGuild.getInstance().getSQLHandler().execute(query);
    }

    public static long getBan(UUID uniqueId) {
        if(playerExists(uniqueId)){
            String query = "Select "+Column.BAN_TIME+" FROM "+TABLENAME+" WHERE "+Column.UUID+"='"+uniqueId+"'";
            ResultSet rs = OpenGuild.getInstance().getSQLHandler().executeQuery(query);
            try {
                double value = rs.getDouble(1);
                return (long)value;
            }
            catch (SQLException ex) {
                OpenGuild.getInstance().getOGLogger().exceptionThrown(ex);
                return -1;
            }
        }else{
            String query = "INSERT INTO "+TABLENAME+" VALUES("+uniqueId+","+Bukkit.getOfflinePlayer(uniqueId).getName()+","+0+")";
            boolean answer = OpenGuild.getInstance().getSQLHandler().execute(query);
            return -1;
        }
    }

    public static boolean playerExists(UUID uniqueId){
        String query = "Select COUNT("+Column.UUID+") FROM "+TABLENAME+" WHERE "+Column.UUID+"='"+uniqueId+"'";
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
        return rowCount == 0;
    }
    
}
