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

import ca.wacos.nametagedit.NametagAPI;
import ca.wacos.nametagedit.NametagEdit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.PluginData;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.SimplePlayerGuild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class CreateArg {
    
    private CreateArg(){
    
    }
    
    public static boolean execute(CommandSender sender, String[] args){
        String clantag = args[1];
        if(!(sender instanceof Player)){
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return false;
        }
        Player p = (Player) sender;
        if(!PluginData.getDataInstance().guildsplayers.containsKey(p.getName())){
            if(clantag.matches("[0-9a-zA-Z]*")){
                if(clantag.length()<=GenConf.maxclantag && clantag.length()>=GenConf.minclantag){
                    if(GenConf.badwords == null || !GenConf.badwords.contains(clantag)){
                        SimpleGuild sg = new SimpleGuild(clantag);
                        sg.setLeader(p.getName());
                        sg.setHome(p.getLocation());
                        if(args.length>2){
                            sg.setDescription(PluginData.argsToString(args, 2, args.length));
                        }else{
                            sg.setDescription("Domyslny opis gildii :<");
                        }
                        SimplePlayerGuild spg = new SimplePlayerGuild(p.getName(),sg.getTag(),true);
                        PluginData.getDataInstance().guilds.put(sg.getTag(), sg);
                        PluginData.getDataInstance().guildsplayers.put(p.getName(), spg);
                        if(NametagAPI.hasCustomNametag(p.getName())){
                            NametagAPI.resetNametag(p.getName());
                        }
                        NametagAPI.setNametagHard(p.getName(), sg.getTag(), "");
                        p.sendMessage(GenConf.prefix+MsgManager.createguildsuccess);
                        return true;
                    }else{
                        p.sendMessage(GenConf.prefix+MsgManager.illegaltag);
                        return false;
                    }
                }else{
                    p.sendMessage(GenConf.prefix+MsgManager.toolongshorttag);
                    return false;
                }
            }else{
                p.sendMessage(GenConf.prefix+MsgManager.usupportedchars);
                return false;
            }
        }else{
            p.sendMessage(GenConf.prefix+MsgManager.alreadyinguild);
            return false;
        }
    }
}
