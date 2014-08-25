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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.SimpleGuild;
import pl.grzegorz2047.openguild2047.commands.CommandHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.utils.GenUtil;

/**
 * Command used to change guilds' description.
 * 
 * Usage: /guild desc set [new description]
 */
public class GuildDescriptionCommand extends CommandHandler {

    public GuildDescriptionCommand(OpenGuild plugin) {
        super(plugin);
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
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
        
        SimpleGuild guild = guildHelper.getPlayerGuild(player.getUniqueId());
        if(!guild.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(MsgManager.get("playernotleader"));
            return;
        }
        
        if(args.length > 3) {
            String subCommand = args[2];
            if(subCommand.equalsIgnoreCase("set") || subCommand.equalsIgnoreCase("ustaw")) {
                String newDescription = GenUtil.argsToString(args, 2, args.length);
                if(newDescription.length() > 32) {
                    player.sendMessage(MsgManager.get("desctoolong"));
                    return;
                }
                
                guild.setDescription(newDescription);
                
                for(UUID uuid : guild.getMembers()) {
                    OfflinePlayer offlineMember = Bukkit.getOfflinePlayer(uuid);
                    if(offlineMember.isOnline()) {
                        offlineMember.getPlayer().sendMessage(MsgManager.get("guild-description-changed").replace("{NEW}", newDescription));
                    }
                }
                
                return;
            }
        }
        
        player.sendMessage(ChatColor.GRAY + guild.getDescription());
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

}
