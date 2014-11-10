/*
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
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

import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class GuildAllyCommand extends Command{

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
            
        GuildHelper guildHelper = getPlugin().getGuildHelper();
        
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return;
        }
        Player player = (Player) sender;
        if(args.length>=2){
            String guildToCheck = args[1].toUpperCase();
            if(!guildHelper.doesGuildExists(guildToCheck)) {
                sender.sendMessage(MsgManager.get("guilddoesntexists"));
                return;
            }
            Guild requestingGuild = guildHelper.getPlayerGuild(player.getUniqueId());
            if(!requestingGuild.getLeader().equals(player.getUniqueId())) {
                player.sendMessage(MsgManager.get("playernotleader"));
                return;
            }
            Guild guild = guildHelper.getGuilds().get(guildToCheck);
            OfflinePlayer leader = Bukkit.getOfflinePlayer(guild.getLeader());

            if(!leader.isOnline()){
                sender.sendMessage(MsgManager.get("leadernotonline"));
                return;
            }
            if(guild.getPendingRelationChanges().contains(requestingGuild.getTag())){
                if(!requestingGuild.getLeader().equals(player.getUniqueId())){
                    player.sendMessage(MsgManager.get("playernotleader"));
                    return;
                }
                guild.getPendingRelationChanges().remove(requestingGuild.getTag());
                Relation r = new Relation();
                r.setWho(guild.getTag());
                r.setWithWho(requestingGuild.getTag());
                r.setState(Relation.STATUS.ALLY);
                boolean result = OpenGuild.getInstance().getSQLHandler().addAlliance(guild, requestingGuild, Relation.STATUS.ALLY);
                if(result != true){
                    System.out.println("Nie udalo sie dodac rekordu!");
                }
                guild.getAlliances().add(r);
                requestingGuild.getAlliances().add(r);             
                Bukkit.broadcastMessage("Gildia "+guild.getTag()+" zawarla sojusz z "+requestingGuild.getTag());
                return;
            }
            requestingGuild.changeRelationRequest(requestingGuild, guild, leader, Relation.STATUS.ALLY);
        }
        
    }

    @Override
    public int minArgs() {
        return 1;
    }
    
}
