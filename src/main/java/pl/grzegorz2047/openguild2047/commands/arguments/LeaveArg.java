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

package pl.grzegorz2047.openguild2047.commands.arguments;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.api.Guild;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class LeaveArg {
    
    	public static boolean execute(CommandSender sender) {
            if(!(sender instanceof Player)){
                sender.sendMessage(MsgManager.cmdonlyforplayer);
            }
            Player p = (Player) sender;
            if(Data.getInstance().isPlayerInGuild(p.getName())){
                if(Data.getInstance().getPlayersGuild(p.getName()).getLeader().equalsIgnoreCase(p.getName())){
                    p.sendMessage(GenConf.prefix+"Jak chcesz to zrobic wpisz /gidlia rozwiaz!");
                    return false;
                }
            	saveDb(Guilds.getGuild(p), p.getName());
                Data.getInstance().getPlayersGuild(p.getName()).removeMember(p.getName());
                Data.getInstance().guildsplayers.remove(p.getName());
                p.sendMessage(GenConf.prefix+MsgManager.leaveguildsuccess);
            }else{
                p.sendMessage(GenConf.prefix+MsgManager.notinguild);
            }
            
            return true;
	}
    
    private static void saveDb(Guild guild, String player) {
        // TODO Tu trzeba zrobic pobieranie gildii, String -> ArrayList, potem usuwanie gracza i ArrayList<String>
        //Nie za bardzo wiem co z TODO, ale na razie tyle wystarczy
        MySQLHandler.update(player, MySQLHandler.PType.GUILD, null);
    }
    
}
