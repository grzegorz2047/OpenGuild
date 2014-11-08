/*
 * The MIT License
 *
 * Copyright 2014 Adam.
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

package pl.grzegorz2047.openguild2047.commands.guild;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.api.Guild;
import pl.grzegorz2047.openguild2047.commands.CommandHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Command used to accept invitation to guild.
 * 
 * Usage: /guild accept [optional: tag (required only if there's more than 2 invitations)]
 */
public class GuildInvitationAcceptCommand extends CommandHandler {

    public GuildInvitationAcceptCommand(OpenGuild plugin) {
        super(plugin);
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
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
                // TODO: move to language file
                sender.sendMessage("You have more than 1 invitation!/Masz wiecej niz 1 zaproszenie!");
                sender.sendMessage("Type/Wpisz /guild accept <tag> to accept invitation./Aby akceptowac.");
                sender.sendMessage("-------------------");

                for (Guild guild : invitationsFrom) {
                    sender.sendMessage(ChatColor.BOLD + guild.getTag().toUpperCase() + ChatColor.GRAY + " - " + guild.getDescription());
                }
            }else if(invitationsFrom.size() == 1 ){
                Guild g = invitationsFrom.get(0);
                g.acceptInvitation((Player) sender);
                getPlugin().getTagManager().setTag(((Player) sender).getUniqueId());
                sender.sendMessage(ChatColor.GREEN+"Guild join Accepted!/Dolaczono do gildii!");
            }else{
                sender.sendMessage(ChatColor.RED+"No invitation available!");
                sender.sendMessage(ChatColor.RED+"Brak zaproszen!");
            }
        } else if(args.length >= 2) {
            String tag = args[1];
            if(guildHelper.getGuilds().containsKey(tag)) {
                if(invitationsFrom.contains(guildHelper.getGuilds().get(tag))) {
                    guildHelper.getGuilds().get(tag).acceptInvitation((Player) sender);
                    getPlugin().getTagManager().setTag(((Player) sender).getUniqueId());
                }
            }
        }
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

}
