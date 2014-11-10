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

import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.Relation;
import com.github.grzegorz2047.openguild.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GuildHelper;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * This command shows informations about specified or players' guild.
 * 
 * Usage: /guild info [optional: tag (if you're member of a guild)]
 */
public class GuildRelationCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) {
        GuildHelper guildHelper = getPlugin().getGuildHelper();
        
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return;
        }
        Player player = (Player) sender;
        if(args.length >= 3) {
            String guildToCheck = args[1].toUpperCase();
            String status = args[2];
            if((status.equalsIgnoreCase("accept") || status.equalsIgnoreCase("akceptuj"))){
                String withWho = args[1].toUpperCase();   
                Guild wanted = guildHelper.getPlayerGuild(player.getUniqueId());
                Guild whoWant = guildHelper.getGuilds().get(withWho);
                if(whoWant != null){
                    if(whoWant.getPendingRelationChanges().contains(wanted.getTag())){
                        if(!wanted.getLeader().equals(player.getUniqueId())){
                            player.sendMessage(MsgManager.get("playernotleader"));
                            return;
                        }
                        whoWant.getPendingRelationChanges().remove(wanted.getTag());
                        Relation r = new Relation();
                        r.setWho(wanted.getTag());
                        r.setWithWho(withWho);
                        r.setState(Relation.STATUS.ALLY);
                        OpenGuild.getInstance().getSQLHandler().addAlliance(whoWant, wanted, Relation.STATUS.ALLY);
                        whoWant.getAlliances().add(r);
                        wanted.getAlliances().add(r);             
                        Bukkit.broadcastMessage("Gildia "+whoWant.getTag()+" zawarla sojusz z "+wanted.getTag());
                    }else{
                        sender.sendMessage("Gildia "+wanted.getTag()+" nie wyslala zapytania o sojusz!");
                    }
                }else{
                    sender.sendMessage(MsgManager.notinguild);
                }
            }else if(status.equalsIgnoreCase("ally") || status.equalsIgnoreCase("enemy")){
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
                if(status.equalsIgnoreCase("ally")){
                    requestingGuild.changeRelationRequest(requestingGuild, guild, leader, status.toUpperCase());
                }else{
                    for(Relation r : requestingGuild.getAlliances()){
                        if(r.getWithWho().equals(guild.getTag()) ){
                            requestingGuild.getAlliances().remove(r);
                            guild.getAlliances().remove(r);
                            //Ustaw lub usun z sqla jakos
                            Bukkit.broadcastMessage("Gildia "+requestingGuild+" zerwala sojusz z "+guild.getTag());
                            break;
                        }
                    }
                    sender.sendMessage("There is no such ally!");
                }
                
            }else{
                player.sendMessage("incorrect status");
            } 
        } else { 
            player.sendMessage("Usage: /g relation <who> ally/enemy/accept");
        }
    }

    @Override
    public int minArgs() {
        return 1;
    }

}