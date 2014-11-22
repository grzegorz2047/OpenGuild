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
import pl.grzegorz2047.openguild2047.GuildHelper;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.event.guild.GuildKickEvent;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used to kick player out of players' guild.
 * 
 * Usage: /guild kick [player name]
 */
public class GuildKickCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return;
        }
        
        GuildHelper guildHelper = this.getPlugin().getGuildHelper();
        
        Player player = (Player) sender;
        if(!guildHelper.hasGuild(player)) {
            sender.sendMessage(MsgManager.notinguild);
            return;
        }
        
        Guild guild = guildHelper.getPlayerGuild(player.getUniqueId());
        if(!guild.getLeader().equals(player.getUniqueId())) {
            sender.sendMessage(MsgManager.playernotleader);
            return;
        }
        
        String toKick = args[1];
        OfflinePlayer op = this.getPlugin().getServer().getOfflinePlayer(toKick);
        if(!guild.getMembers().contains(op.getUniqueId())) {
            sender.sendMessage(MsgManager.playernotinthisguild);
            return;
        }
        if(guild.getLeader().equals(op.getUniqueId())) {
            sender.sendMessage(MsgManager.get("cantkickleader", "You cant kick yourself from your own guild!"));
            return;
        }
        
        GuildKickEvent event = new GuildKickEvent(guild, op);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return;
        }
            
        this.getPlugin().getTagManager().playerLeaveGuild(op);
        guild.removeMember(op.getUniqueId());
        guildHelper.getPlayers().remove(op.getUniqueId());
        guildHelper.getPlayers().put(op.getUniqueId(), null);
        if(op.isOnline()) {
            op.getPlayer().sendMessage(MsgManager.playerkicked.replace("{GUILD}", guild.getTag()));
        }
        
        for(UUID member : guild.getMembers()) {
            OfflinePlayer opp = this.getPlugin().getServer().getOfflinePlayer(member);
            if(opp.isOnline()) {
                opp.getPlayer().sendMessage(MsgManager.get("broadcast-kick").replace("{PLAYER}", player.getDisplayName()).replace("{MEMBER}", op.getName()).replace("{TAG}", guild.getTag().toUpperCase()));
            }
        }

        getPlugin().getSQLHandler().updatePlayer(op.getUniqueId());
        
        player.sendMessage(MsgManager.playerkicksuccess);
    }

    @Override
    public int minArgs() {
        return 2;
    }

}
