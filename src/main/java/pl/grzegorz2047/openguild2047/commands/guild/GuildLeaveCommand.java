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
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.Guilds;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.event.guild.GuildLeaveEvent;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used by players to leave current guild.
 * 
 * Usage: /guild leave
 */
public class GuildLeaveCommand extends Command {
    public GuildLeaveCommand() {
        setPermission("openguild.command.leave");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return;
        }
        
        Guilds guilds = this.getPlugin().getGuilds();
        
        Player player = (Player) sender;
        if(!guilds.hasGuild(player)) {
            player.sendMessage(MsgManager.notinguild);
            return;
        }
        
        Guild guild = guilds.getPlayerGuild(player.getUniqueId());
        if(guild.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(MsgManager.get("kickleader"));
            return;
        }
        
        GuildLeaveEvent event = new GuildLeaveEvent(guild, player);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return;
        }
        
        this.getPlugin().getTagManager().playerLeaveGuild(player);
        guild.removeMember(player.getUniqueId());
        guilds.getPlayers().remove(player.getUniqueId());
        guilds.getPlayers().put(player.getUniqueId(), null);
        for(UUID member : guild.getMembers()) {
            OfflinePlayer opp = this.getPlugin().getServer().getOfflinePlayer(member);
            if(opp.isOnline()) {
                opp.getPlayer().sendMessage(MsgManager.get("broadcast-leave").replace("{PLAYER}", player.getDisplayName()).replace("{TAG}", guild.getTag().toUpperCase()));
            }
        }

        getPlugin().getSQLHandler().updatePlayerTag(player.getUniqueId());
        player.sendMessage(MsgManager.leaveguildsuccess);
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
