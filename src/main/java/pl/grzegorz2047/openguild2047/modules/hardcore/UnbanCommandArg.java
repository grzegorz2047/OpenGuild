/*
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.grzegorz2047.openguild2047.modules.hardcore;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * We are using the new command API
 * Command has been moved also to the new class {@link Unban}
 * @author Aleksander
 * @deprecated New command API
 */
@Deprecated
public class UnbanCommandArg  {
    
    public static boolean execute(CommandSender sender, String[] args) {
        if(!GenConf.hcBans) {
            sender.sendMessage(MsgManager.get("hcnotenabled"));
            return true;
        }
        else if(!sender.hasPermission("openguild.hardcore.unban")) {
            sender.sendMessage(MsgManager.get("permission"));
            return true;
        }
        else if(args.length != 2) {
            sender.sendMessage(MsgManager.get("wrongcmdargument"));
            sender.sendMessage(ChatColor.RED + "/g unban <player>");
            return true;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        if(player == null || !player.hasPlayedBefore()) {
            sender.sendMessage(MsgManager.get("notplayedbefore").replace("{PLAYER}", args[1]));
            return true;
        }
        
        long banned = HardcoreSQLHandler.getBan(player.getUniqueId());
        if(banned != 0) {
            HardcoreSQLHandler.update(player.getUniqueId(), HardcoreSQLHandler.Column.BAN_TIME, "0");
            Guilds.getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") was unbanned by " + sender.getName());
            sender.sendMessage(MsgManager.get("hcub").replace("{PLAYER}", player.getName()));
        } else {
            sender.sendMessage(MsgManager.get("notbanned").replace("{PLAYER}", player.getName()));
        }
        return true;
    }
    
}