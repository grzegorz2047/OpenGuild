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
package pl.grzegorz2047.openguild2047.modules.hardcore;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    enum COLUMN {
        BAN_TIME,
        UUID,
        NICK
    };
    static String tablename = "bans";
    static boolean createTables(){
        String query = "CREATE TABLE IF NOT EXISTS "+tablename+" (UUID VARCHAR(36) NOT NULL primary key, NICK VARCHAR(16) NOT NULL, BAN_TIME DEC NOT NULL)";
        OpenGuild.getInstance().getOGLogger().debug(query);
        return OpenGuild.getInstance().getSQLHandler().execute(query);
    }
    
    
    static void update(UUID uniqueId, COLUMN column, String value) {
        if(playerExists(uniqueId)){
            OpenGuild.getInstance().getOGLogger().debug("User "+Bukkit.getOfflinePlayer(uniqueId).getName()+" with uuid "+uniqueId.toString()+" exists in table"+tablename);
        }
        String query = "UPDATE "+tablename+" SET "+column.toString()+"='"+value+"' WHERE "+COLUMN.UUID+"='"+uniqueId+"'";
        OpenGuild.getInstance().getSQLHandler().execute(query);
        OpenGuild.getInstance().getOGLogger().debug("Updating player "+Bukkit.getOfflinePlayer(uniqueId).getName()+" with uuid "+uniqueId+" selecting column "+column.toString()+" and setting "+value);
    }

    static long getBan(UUID uniqueId) {
        if(playerExists(uniqueId)){
            String query = "Select "+COLUMN.BAN_TIME+" FROM "+tablename+" WHERE "+COLUMN.UUID+"='"+uniqueId+"'";
            ResultSet rs = OpenGuild.getInstance().getSQLHandler().executeQuery(query);
            try {
                double value = rs.getDouble(1);
                OpenGuild.getInstance().getOGLogger().debug("Result of "+query+" is "+value);
                return (long)value;
            }
            catch (SQLException ex) {
                OpenGuild.getInstance().getOGLogger().exceptionThrown(ex);
                return -1;
            }
        }else{
            String query = "INSERT INTO "+tablename+" VALUES("+uniqueId+","+Bukkit.getOfflinePlayer(uniqueId).getName()+","+0+")";
            boolean answer = OpenGuild.getInstance().getSQLHandler().execute(query);
            OpenGuild.getInstance().getOGLogger().debug("Answer for "+query+" is "+answer);
            return -1;
        }
    }

    private static boolean playerExists(UUID uniqueId){
        String query = "Select COUNT("+COLUMN.UUID+") FROM "+tablename+" WHERE "+COLUMN.UUID+"='"+uniqueId+"'";
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
        OpenGuild.getInstance().getOGLogger().debug("Counting player "+Bukkit.getOfflinePlayer(uniqueId).getName()+" with uuid "+uniqueId+" returns "+rowCount);
        return rowCount == 0;
    }
    
    
    
}
