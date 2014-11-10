/*
 * The MIT License
 *
 * Copyright 2014 Adam.
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

package pl.grzegorz2047.openguild2047.commands.guild;

import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;
import com.github.grzegorz2047.openguild.Relation.STATUS;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.GenConf;

/**
 * Command used to accept invitation to guild.
 * 
 * Usage: /guild accept [optional: tag (required only if there's more than 2 invitations)]
 */
public class GuildRelationAcceptCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return;
        }
        Player p = (Player) sender;
        if(args.length > 1){
            GuildHelper guildHelper = getPlugin().getGuildHelper();
            String withWho = args[1].toUpperCase();   
            Guild wanted = guildHelper.getPlayerGuild(p.getUniqueId());
            Guild whoWant = guildHelper.getGuilds().get(withWho);
            if(whoWant != null){
                if(whoWant.getPendingRelationChanges().contains(wanted.getTag())){
                    if(!wanted.getLeader().equals(p.getUniqueId())){
                        p.sendMessage(MsgManager.get("playernotleader"));
                        return;
                    }
                    whoWant.getPendingRelationChanges().remove(wanted.getTag());
                    Relation r = new Relation();
                    r.setWithWho(withWho);
                    r.setState(STATUS.ALLY);
                    
                    Relation r2 = new Relation();
                    r.setWithWho(wanted.getTag());
                    r.setState(STATUS.ALLY);
                    
                    whoWant.getAlliances().add(r);
                    wanted.getAlliances().add(r2);
                    
                    Bukkit.broadcastMessage("Gildia "+whoWant.getTag()+" zawarla sojusz z "+wanted.getTag());
                }
               
                
            }else{
                sender.sendMessage(MsgManager.notinguild);
            }
        }

        
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
