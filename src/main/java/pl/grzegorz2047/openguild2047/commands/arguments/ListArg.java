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

import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.grzegorz2047.openguild2047.Data;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class ListArg {

    public static boolean execute(CommandSender sender) {
        if(!(sender instanceof Player)) {
            return false;
        }
        Player p = (Player) sender;
        if(Data.getInstance().isPlayerInGuild(p.getUniqueId().toString())) {
            List<UUID> members = Data.getInstance().getPlayersGuild(p.getUniqueId().toString()).getMembers();
            StringBuilder sb = new StringBuilder();
            if(members.size() != 1) {
                sb.append("Lista czlonkow w twojej gildii:\n ");
                for(int i = 0; i < members.size(); i++) {
                    UUID member = Data.getInstance().getPlayersGuild(p.getUniqueId().toString()).getMembers().get(i);
                    String nick = Bukkit.getOfflinePlayer(member).getName(); // https://github.com/Xephi/Bukkit/commit/f6a3abaa35f4b9ff16427a82be8f818d212b3927
                    if(i % 5 != 0) {
                        sb.append(nick + ", ");
                    } else {
                        sb.append("\n");
                    }
                }
                p.sendMessage(sb.toString());
                return true;
            } else {
                p.sendMessage(MsgManager.nomembersinguild);
                return false;
            }

        } else {
            p.sendMessage(MsgManager.notinguild);
        }

        return false;
    }

}
