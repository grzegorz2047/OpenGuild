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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.SimplePlayerGuild;
import pl.grzegorz2047.openguild2047.api.Guild;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class AcceptArg {

    public static boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return false;
        }
        Player p = (Player) sender;
        if(args.length >= 2) {
            if(Data.getInstance().isPlayerInGuild(p.getName())) {
                SimpleGuild sg = Data.getInstance().getPlayersGuild(p.getName());
                if(sg.getLeader().equalsIgnoreCase(p.getName())) {
                    String acceptedplayer = args[1];
                    Player player = Bukkit.getPlayer(acceptedplayer);
                    if(sg.getInvitedPlayers().contains(acceptedplayer)) {
                        sg.getInvitedPlayers().remove(acceptedplayer);
                        SimplePlayerGuild spg = new SimplePlayerGuild(player.getName(), sg.getTag(), true);
                        Data.getInstance().guilds.put(sg.getTag(), sg);
                        Data.getInstance().ClansTag.add(sg.getTag());
                        Data.getInstance().guildsplayers.put(player.getName(), spg);
                        sg.addMember(player.getName());
                        if(GenConf.playerprefixenabled) {
                            if(NametagAPI.hasCustomNametag(player.getName())) {
                                NametagAPI.resetNametag(player.getName());
                            }
                            NametagAPI.setPrefix(player.getName(), GenConf.colortagu + spg.getClanTag() + "§r ");
                        }
                        savetodb(p, sg);
                        player.sendMessage(MsgManager.guildjoinsuccess);
                        p.sendMessage(MsgManager.invitedplayersuccessfullyjoined);
                        return true;
                    } else {
                        p.sendMessage(MsgManager.playernotoninvitedlist);
                        return true;
                    }
                } else {
                    p.sendMessage(MsgManager.playernotleader);
                    return false;
                }
            } else {
                p.sendMessage(MsgManager.notinguild);
                return false;
            }
        } else {
            p.sendMessage(MsgManager.wrongcmdargument);
            return false;
        }
    }

    private static void savetodb(Player player, Guild g) {
        MySQLHandler.update(player.getUniqueId(), MySQLHandler.PType.GUILD, g.getTag());
    }
}
