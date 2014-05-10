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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.api.Guild;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.handlers.MySQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.tagmanager.TagManager;

/**
 *
 * @author Grzegorz
 */
public class KickArg {
    
    public static boolean execute(CommandSender sender,String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(GenConf.prefix + MsgManager.cmdonlyforplayer);
            return true;
        }
        Player leader = (Player) sender;
        OfflinePlayer op = Bukkit.getOfflinePlayer(args[1]);
        if(!op.hasPlayedBefore()){
            leader.sendMessage(MsgManager.playerneverplayed);
            return false;
        }
        if(!Data.getInstance().isPlayerInGuild(leader.getUniqueId())){
            leader.sendMessage(MsgManager.notinguild);
            return false;
        }
        SimpleGuild sgl = Data.getInstance().getPlayersGuild(leader.getUniqueId());
        if(!sgl.getLeader().equals(leader.getUniqueId())){
            leader.sendMessage(MsgManager.playernotleader);
            return false;
        }
        if(Data.getInstance().isPlayerInGuild(op.getUniqueId())) {
            if(sgl.containsMember(op.getUniqueId())){
                TagManager.removeTag(op.getUniqueId());
                Data.getInstance().getPlayersGuild(op.getUniqueId()).removeMember(op.getUniqueId());
                Data.getInstance().guildsplayers.remove(op.getUniqueId());
                saveDb(op);
                ((Player)op).sendMessage(MsgManager.playerkicked);
                leader.sendMessage(MsgManager.playerkicksuccess);
            }else{
                leader.sendMessage(MsgManager.playernotinthisguild);
            }

        } else {
            leader.sendMessage(MsgManager.kickplayernotinguild);
        }
        return true;
    }

    private static void saveDb(OfflinePlayer player) {
        // TODO Tu trzeba zrobic pobieranie gildii, String -> ArrayList, potem usuwanie gracza i ArrayList<String>
        //Nie za bardzo wiem co z TODO, ale na razie tyle wystarczy
        MySQLHandler.update(player.getUniqueId(), MySQLHandler.PType.GUILD, "");
    }
}
