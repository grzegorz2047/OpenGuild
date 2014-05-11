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

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.api.Guild;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class InviteArg {

    public static boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return true;
        }
        Player p = (Player) sender;
        if(args.length >= 2) {
            if(Data.getInstance().isPlayerInGuild(p.getUniqueId())) {
                //TODO: System zapraszania do gildii
                String nick = args[1];
                OfflinePlayer op = Bukkit.getOfflinePlayer(nick);
                if(!op.hasPlayedBefore()){
                    p.sendMessage(MsgManager.playerneverplayed);
                    return true;
                }
                if(!Data.getInstance().isPlayerInGuild(op.getUniqueId())) {
                    SimpleGuild sg = Data.getInstance().getPlayersGuild(p.getUniqueId());
                    if(!sg.getLeader().equals(p.getUniqueId())){
                        p.sendMessage(MsgManager.playernotleader);
                        return true;
                    }
                    if(sg.getInvitedPlayers().contains(op.getUniqueId())) {
                        p.sendMessage(MsgManager.notyetaccepted);
                        return true;
                    }else{
                        sg.getInvitedPlayers().add(op.getUniqueId());
                        if(op.isOnline()){
                            ((Player)op).sendMessage(MsgManager.askforaccept+" "+sg.getTag()); 
                        }
                        p.sendMessage(MsgManager.invitesendsuccess + op.getName());
                        return true;
                    }
                } else {
                    p.sendMessage(MsgManager.playerhasguild);
                    return true;
                }

            } else {
                p.sendMessage(MsgManager.notinguild);
                return true;
            }
        } else {
            p.sendMessage(MsgManager.wrongcmdargument);
            return true;
        }

    }

    private static void savetodb(Player player, Guild g) {
        MySQLHandler.update(player.getUniqueId(), MySQLHandler.PType.GUILD, g.getTag());
    }

}
