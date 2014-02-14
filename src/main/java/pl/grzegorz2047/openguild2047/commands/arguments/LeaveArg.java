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
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.PluginData;
import pl.grzegorz2047.openguild2047.SimplePlayerGuild;
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
            if(PluginData.getDataInstance().guildsplayers.containsKey(p.getName())){
                SimplePlayerGuild spg = PluginData.getDataInstance().guildsplayers.get(p.getName());
                if(PluginData.getDataInstance().guilds.containsKey(spg.getClanTag())){
                    PluginData.getDataInstance().guilds.get(spg.getClanTag()).removeMember(p.getName());
                    PluginData.getDataInstance().guildsplayers.remove(p.getName());
                    //TODO: Usuń też w mysqlu
                }else{
                    p.sendMessage(GenConf.prefix+MsgManager.errornotinguild);
                    PluginData.getDataInstance().guildsplayers.remove(p.getName());
                    //Mozliwe, ze wtedy tez jest mysql, ale nie wiem w sumie xd
                }
            }else{
                p.sendMessage(GenConf.prefix+MsgManager.notinguild);
            }
            
            return true;
	}
    
}
