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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.guilds.Guild;
import pl.grzegorz2047.openguild2047.commands.command.Command;
import pl.grzegorz2047.openguild2047.commands.command.CommandException;
import pl.grzegorz2047.openguild2047.events.guild.GuildInvitationEvent;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used to invite player to guild.
 * 
 * Usage: /guild invite [player name]
 */
public class GuildInviteCommand extends Command {
    public GuildInviteCommand() {
        setPermission("openguild.command.invite");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {        
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        
        Guilds guilds = this.getPlugin().getGuilds();
        
        Player player = (Player) sender;
        if(!guilds.hasGuild(player)) {
            sender.sendMessage(MsgManager.get("notinguild"));
            return;
        }
        
        Guild guild = guilds.getPlayerGuild(player.getUniqueId());
        if(!guild.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(MsgManager.get("playernotleader"));
            return;
        }
        
        String playerToInvite = args[1];
        if(!getPlugin().getServer().getOfflinePlayer(playerToInvite).isOnline()) {
            player.sendMessage(MsgManager.get("playeroffline"));
            return;
        }
        
        Player invite = getPlugin().getServer().getPlayer(playerToInvite);
        
        GuildInvitationEvent event = new GuildInvitationEvent(guild, invite);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            return;
        }
        
        if(guilds.hasGuild(event.getInvite())) {
            player.sendMessage(MsgManager.playerhasguild);
            return;
        }
        
        guilds.invitePlayer(event.getInvite(), player, guild);
        player.sendMessage(MsgManager.get("invitesent").replace("{PLAYER}", event.getInvite().getName()));
    }

    @Override
    public int minArgs() {
        return 2;
    }

}
