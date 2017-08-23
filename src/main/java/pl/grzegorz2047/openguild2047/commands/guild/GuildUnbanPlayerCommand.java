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

package pl.grzegorz2047.openguild2047.commands.guild;

import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.command.UsageException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild2047.configuration.GenConf;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.modules.hardcore.HardcoreSQLHandler;

public class GuildUnbanPlayerCommand extends Command {

    public GuildUnbanPlayerCommand() {
        setPermission("openguild.command.hardcore.unban");
    }

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
        
        long banned = HardcoreSQLHandler.getBan(player.getUniqueId());
        if(banned != 0) {
            HardcoreSQLHandler.update(player.getUniqueId(), HardcoreSQLHandler.Column.BAN_TIME, "0");
            Guilds.getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") was unbanned by " + sender.getName());
            sender.sendMessage(MsgManager.get("hcub").replace("{PLAYER}", player.getName()));
        } else {
            throw new CommandException(MsgManager.get("notbanned").replace("{PLAYER}", player.getName()));
        }
    }
    
    @Override
    public int minArgs() {
        return 2;
    }
    
}