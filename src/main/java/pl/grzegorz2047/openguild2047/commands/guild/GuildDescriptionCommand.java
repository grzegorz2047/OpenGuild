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

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.event.guild.GuildDescriptionChangeEvent;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.utils.GenUtil;

/**
 * Command used to change guilds' description.
 * 
 * Usage: /guild desc set [new description]
 */
public class GuildDescriptionCommand extends Command {
    public GuildDescriptionCommand() {
        setPermission("openguild.command.description");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        
        GuildHelper guildHelper = this.getPlugin().getGuildHelper();
        
        Player player = (Player) sender;
        if(!guildHelper.hasGuild(player)) {
            player.sendMessage(MsgManager.get("notinguild"));
            return;
        }
        
        Guild guild = guildHelper.getPlayerGuild(player.getUniqueId());
        
        if(args.length > 2) {
            String subCommand = args[1];
            if(subCommand.equalsIgnoreCase("set") || subCommand.equalsIgnoreCase("ustaw")) {
                if(!guild.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(MsgManager.get("playernotleader"));
                    return;
                }

                String newDescription = GenUtil.argsToString(args, 2, args.length);
                if(newDescription.length() > 32) {
                    player.sendMessage(MsgManager.get("desctoolong"));
                    return;
                }
                
                GuildDescriptionChangeEvent event = new GuildDescriptionChangeEvent(guild, newDescription);
                Bukkit.getPluginManager().callEvent(event);
                if(event.isCancelled()) {
                    return;
                }
                
                guild.setDescription(event.getNewDescription());
                getPlugin().getSQLHandler().updateGuildDescription(guild);

                for(UUID uuid : guild.getMembers()) {
                    OfflinePlayer offlineMember = Bukkit.getOfflinePlayer(uuid);
                    if(offlineMember.isOnline()) {
                        offlineMember.getPlayer().sendMessage(MsgManager.get("guild-description-changed").replace("{NEW}", event.getNewDescription()));
                    }
                }
                
                return;
            }
        }
        
        player.sendMessage(ChatColor.GRAY + guild.getDescription());
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
