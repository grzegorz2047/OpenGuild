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

import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.event.guild.GuildJoinEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import pl.grzegorz2047.openguild2047.GenConf;

/**
 * Command used to accept invitation to guild.
 * 
 * Usage: /guild accept [optional: tag (required only if there's more than 2 invitations)]
 */
public class GuildInvitationAcceptCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return;
        }

        GuildHelper guildHelper = getPlugin().getGuildHelper();
        if(guildHelper.hasGuild(((Player) sender).getUniqueId())) {
            sender.sendMessage(MsgManager.alreadyinguild);
            return;
        }

        List<Guild> invitationsFrom = new ArrayList<Guild>();
        for (Guild guild : guildHelper.getGuilds().values()) {
            if (guild.getPendingInvitations().contains(((Player) sender).getUniqueId())) {
                invitationsFrom.add(guild);
            }
        }

        if(args.length == 1) {
            if (invitationsFrom.size() > 1) {
                sender.sendMessage(MsgManager.get("invmore"));
                for (Guild guild : invitationsFrom) {
                    sender.sendMessage(ChatColor.BOLD + guild.getTag().toUpperCase() + ChatColor.GRAY + " - " + guild.getDescription());
                }
            } else if(invitationsFrom.size() == 1 ){
                accept((Player) sender, invitationsFrom.get(0));
            } else {
                sender.sendMessage(MsgManager.get("noinv"));
            }
        } else if(args.length >= 2) {
            String tag = args[1].toUpperCase();
            Guild target = guildHelper.getGuilds().get(tag);
            if(target != null && guildHelper.getGuilds().containsKey(tag)) {
                if(invitationsFrom.contains(target)) {
                    accept((Player) sender, target);
                }
            }
        }
    }

    @Override
    public int minArgs() {
        return 1;
    }

    private void accept(Player player, Guild guild) {
        GuildJoinEvent event = new GuildJoinEvent(guild, player);
        if(event.isCancelled()) {
            return;
        }
        
        guild.acceptInvitation(player);
        getPlugin().getTagManager().playerJoinGuild(player);
        Bukkit.broadcastMessage(MsgManager.get("broadcast-join")
                .replace("{PLAYER}", player.getName())
                .replace("{TAG}", guild.getTag().toUpperCase()));
    }

}
