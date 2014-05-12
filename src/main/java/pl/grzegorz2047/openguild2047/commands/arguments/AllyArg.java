/*
 * The MIT License
 *
 * Copyright 2014 student_U190.
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
package pl.grzegorz2047.openguild2047.commands.arguments;

import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class AllyArg {
    
    public static boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return true;
        }
        String allyguild = args[1];
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();
        if(Data.getInstance().isPlayerInGuild(uuid)){
            if(Data.getInstance().guildExists(allyguild)){
                Data.getInstance().getPlayersGuild(uuid).getAllyGuilds().add(allyguild);
                player.sendMessage(MsgManager.addedAllyGuild.replace("{Guild}", allyguild));
                //Podpiac pod mysqla
                //Dodac akceptacje z drugiej strony chyba,zeby nie bylo klopotow
                return true;
            }else{
                player.sendMessage(MsgManager.guilddoesntexists);
                return true;
            }
            
            
        }
        return true;
    }
    
}
