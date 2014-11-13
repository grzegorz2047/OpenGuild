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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used by guild's leaders to disband their guild.
 * 
 * Usage: /guild disband
 */
public class GuildDisbandCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        
        GuildHelper guildHelper = this.getPlugin().getGuildHelper();
        
        Player player = (Player) sender;
        if(args.length == 1){
            if(!guildHelper.hasGuild(player)) {
                player.sendMessage(MsgManager.get("notinguild"));
                return;
            }

            Guild guild = guildHelper.getPlayerGuild(player.getUniqueId());
            if(!guild.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(MsgManager.get("playernotleader"));
                return;
            }

            for(UUID uuid : guild.getMembers()) {
                this.
                    getPlugin().
                    getTagManager().playerLeaveGuild(Bukkit.getOfflinePlayer(uuid));
                guildHelper.getPlayers().remove(uuid);
                getPlugin().getSQLHandler().updatePlayer(uuid);
            }
            guildHelper.getCuboids().remove(guild.getTag());
            getPlugin().getSQLHandler().removeGuild(guild.getTag().toUpperCase());
            guildHelper.getGuilds().remove(guild.getTag());

            getPlugin().broadcastMessage(MsgManager.get("broadcast-disband").replace("{TAG}", guild.getTag().toUpperCase()).replace("{PLAYER}", player.getDisplayName()));
        }
        
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
