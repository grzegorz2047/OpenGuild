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

package pl.grzegorz2047.openguild2047.commands;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Guilds;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.utils.GenUtil;

/**
 * This command is used to send message to members of guild.
 * 
 * Usage: /team [message]
 */
public class TeamCommand implements CommandExecutor {
    
    private OpenGuild plugin;
    
    public TeamCommand(OpenGuild plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return true;
        }
        
        if(args.length == 0) {
            sender.sendMessage(MsgManager.get("usage").replace("{USAGE}", "/t <msg>"));
            return true;
        }
        
        Guilds guilds = plugin.getGuilds();
        
        Player player = (Player) sender;
        if(!guilds.hasGuild(player)) {
            player.sendMessage(MsgManager.notinguild);
            return true;
        }
        
        String message = GenUtil.argsToString(args, 0, args.length);
        
        Guild guild = guilds.getPlayerGuild(player.getUniqueId());
        String format = GenConf.guildChatFormat
                .replace("{GUILD}", guild.getTag())
                .replace("{PLAYER}", player.getName())
                .replace("{MESSAGE}", message);
        for(UUID uuid : guild.getMembers()) {
            OfflinePlayer op = plugin.getServer().getOfflinePlayer(uuid);
            if(op.isOnline()) {
                op.getPlayer().sendMessage(format);
            }
        }
        
        return true;
    }
    
}
