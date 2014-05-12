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


import com.github.grzegorz2047.openguild.event.MessageBroadcastEvent;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.SimplePlayerGuild;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.managers.TagManager;

/**
 *
 * @author Grzegorz
 */
public class AcceptArg {

    public static boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return true;
        }
        Player p = (Player) sender;
        UUID uuid = p.getUniqueId();
        if(args.length >= 2) {
            if(!Data.getInstance().isPlayerInGuild(uuid)) {
                    String acceptedguild = args[1];
                    if(Data.getInstance().ClansTag.contains(acceptedguild)){
                        SimpleGuild sg = Data.getInstance().guilds.get(acceptedguild);
                        if(sg.getInvitedPlayers().contains(uuid)) {
                            sg.getInvitedPlayers().remove(uuid);
                            SimplePlayerGuild spg = new SimplePlayerGuild(uuid, sg.getTag(), true);
                            sg.addMember(uuid);
                            Data.getInstance().guilds.put(sg.getTag(), sg);
                            Data.getInstance().guildsplayers.put(uuid, spg);
                            if(GenConf.playerprefixenabled) {
                              TagManager.setTag(uuid);       
                            }
                            savetodb(uuid, spg.getClanTag());
                            p.sendMessage(MsgManager.invitedplayersuccessfullyjoined);
                            
                            // Event
                            MessageBroadcastEvent event = new MessageBroadcastEvent(MessageBroadcastEvent.Message.JOIN);
                            Bukkit.getPluginManager().callEvent(event);
                            if(!event.isCancelled()) {
                                Bukkit.broadcastMessage(event.getMessage().replace("{TAG}", sg.getTag()).replace("{PLAYER}", sender.getName()));
                            }
                            return true;
                        }else{
                            p.sendMessage(MsgManager.playernotinvited);
                            return true;
                        }
                    }else{
                        p.sendMessage(MsgManager.guilddoesntexists);
                        return true;
                    }
                } else {
                    p.sendMessage(MsgManager.playernotleader);
                    return true;
                }
            } else {
                p.sendMessage(MsgManager.wrongcmdargument);
            return false;
        }
    }

    private static void savetodb(UUID player, String tag) {
        SQLHandler.update(player, SQLHandler.PType.GUILD, tag);
    }
}
/*
                    if(sg.getInvitedPlayers().contains(uuid)) {
                        sg.getInvitedPlayers().remove(uuid);
                        SimplePlayerGuild spg = new SimplePlayerGuild(uuid, sg.getTag(), true);
                        sg.addMember(uuid);
                        Data.getInstance().guilds.put(sg.getTag(), sg);
                        Data.getInstance().ClansTag.add(sg.getTag());
                        Data.getInstance().guildsplayers.put(uuid, spg);
                        if(GenConf.playerprefixenabled) {
                          TagManager.setTag(uuid);       
                        }
                        Player playerobj = Bukkit.getPlayer(acceptedplayer);
                        savetodb(uuid, spg.getClanTag());
                        if(playerobj!=null){
                           playerobj.sendMessage(MsgManager.guildjoinsuccess);
                        }
                        p.sendMessage(MsgManager.invitedplayersuccessfullyjoined);
                        return true;
                    } else {
                        p.sendMessage(MsgManager.playernotoninvitedlist);
                        return true;
                    }

*/
