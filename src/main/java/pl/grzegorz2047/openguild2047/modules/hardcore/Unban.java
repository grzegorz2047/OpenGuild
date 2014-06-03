/*
 * The MIT License
 *
 * Copyright 2014 Aleksander.
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

package pl.grzegorz2047.openguild2047.modules.hardcore;

import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.command.PermException;
import com.github.grzegorz2047.openguild.command.UsageException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class Unban implements Command {
    
    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!GenConf.hcBans) {
            throw new CommandException(MsgManager.get("hcnotenabled"));
        }
        else if(args.length != 2) {
            throw new UsageException(MsgManager.get("wrongcmdargument"));
        }
        
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if(player == null || !player.hasPlayedBefore()) {
            throw new CommandException(MsgManager.get("notplayedbefore").replace("{PLAYER}", args[1]));
        }
        
        long banned = SQLHandler.getBan(player.getUniqueId());
        if(banned != 0) {
            SQLHandler.update(player.getUniqueId(), SQLHandler.PType.BAN_TIME, 0);
            Guilds.getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") was unbanned by " + sender.getName());
            sender.sendMessage(MsgManager.get("hcub").replace("{PLAYER}", player.getName()));
        } else {
            throw new CommandException(MsgManager.get("notbanned").replace("{PLAYER}", player.getName()));
        }
        
    }
    
}
