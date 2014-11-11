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
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used by players to leave current guild.
 * 
 * Usage: /guild leave
 */
public class GuildLeaveCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return;
        }
        
        GuildHelper guildHelper = this.getPlugin().getGuildHelper();
        
        Player player = (Player) sender;
        if(!guildHelper.hasGuild(player)) {
            player.sendMessage(MsgManager.notinguild);
            return;
        }
        
        Guild guild = guildHelper.getPlayerGuild(player.getUniqueId());
        if(guild.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(MsgManager.get("kickleader"));
            return;
        }

        guild.removeMember(player.getUniqueId());
        guildHelper.getPlayers().remove(player.getUniqueId());
        guildHelper.getPlayers().put(player.getUniqueId(), null);
        for(UUID member : guild.getMembers()) {
            OfflinePlayer opp = this.getPlugin().getServer().getOfflinePlayer(member);
            if(opp.isOnline()) {
                opp.getPlayer().sendMessage(MsgManager.get("broadcast-leave").replace("{PLAYER}", player.getDisplayName()).replace("{TAG}", guild.getTag().toUpperCase()));
            }
        }

        getPlugin().getSQLHandler().updatePlayer(player.getUniqueId());
                this.
                    getPlugin().
                    getTagManager().
                    removeTag(player.getUniqueId());
        player.sendMessage(MsgManager.leaveguildsuccess);
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
