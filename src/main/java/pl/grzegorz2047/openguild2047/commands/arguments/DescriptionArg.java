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
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.utils.GenUtil;

/**
 *
 * @author Grzegorz
 */
public class DescriptionArg {
    
    public static boolean execute(CommandSender sender, String args[]) {
        if(!(sender instanceof Player)){
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return false;
        }
        Player p = (Player) sender;
        if(args.length>1){
            if(args[1].equalsIgnoreCase("zmien")){
                if(args.length>3){
                    if(Data.getInstance().isPlayerInGuild(p.getName())){
                        SimpleGuild sg = Data.getInstance().getPlayersGuild(p.getName());
                        if(sg.getLeader().equals(p.getName())){
                            String desc = GenUtil.argsToString(args, 3, args.length);
                            sg.setDescription(desc);
                            return true;
                        }else{
                            p.sendMessage(GenConf.prefix+MsgManager.playernotleader);
                            return false;
                        }
                    }else{
                        p.sendMessage(GenConf.prefix+MsgManager.notinguild);
                        return false;
                    }
                }else{
                    p.sendMessage(GenConf.prefix+MsgManager.wronguseddesccmd);
                    return false;
                }
            }else{
                p.sendMessage(GenConf.prefix+MsgManager.wrongcmdargument);
                return false;
            }
        }else{
            if(Data.getInstance().isPlayerInGuild(p.getName())){
                SimpleGuild sg = Data.getInstance().getPlayersGuild(p.getName());
                p.sendMessage("Opis gildii to: "+sg.getDescription());
                return true;
            }else{
                p.sendMessage(GenConf.prefix+MsgManager.notinguild);
                return false;
            }
        }
    }
    
}
