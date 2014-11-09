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

import java.util.UUID;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.OpenGuild;
import com.github.grzegorz2047.openguild.Guild;
import pl.grzegorz2047.openguild2047.commands.CommandHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used to kick player out of players' guild.
 * 
 * Usage: /guild kick [player name]
 */
public class GuildKickCommand extends CommandHandler {

    public GuildKickCommand(OpenGuild plugin) {
        super(plugin);
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
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
        guild.removeMember(op.getUniqueId());
        guildHelper.getPlayers().remove(op.getUniqueId());
        guildHelper.getPlayers().put(op.getUniqueId(), null);
        if(op.isOnline()) {
            op.getPlayer().sendMessage(MsgManager.playerkicked);
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
    public int getMinimumArguments() {
        return 2;
    }

}
