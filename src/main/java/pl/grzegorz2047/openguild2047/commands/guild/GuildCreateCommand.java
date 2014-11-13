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
import com.github.grzegorz2047.openguild.event.guild.GuildCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.GuildHelper;
import com.github.grzegorz2047.openguild.Cuboid;
import pl.grzegorz2047.openguild2047.cuboidmanagement.CuboidStuff;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.utils.GenUtil;

/**
 * Command used to create new guild.
 * 
 * Usage: /guild create [tag] [description]
 */
public class GuildCreateCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        GuildHelper guildHelper = this.getPlugin().getGuildHelper();
        
        Player player = (Player) sender;
        if(GenConf.FORCE_DESC){
            if(args.length<3){
                player.sendMessage(MsgManager.get("descrequired"));
                return;
            }
        }
        if(guildHelper.hasGuild(player)) {
            player.sendMessage(MsgManager.get("alreadyinguild"));
            return;
        }
        
        String tag = args[1].toUpperCase();
        if(!tag.matches("[0-9a-zA-Z]*")) {
            player.sendMessage(MsgManager.unsupportedchars);
            return;
        }
        
        if(tag.length() > GenConf.maxclantag || tag.length() < GenConf.minclantag) {
            player.sendMessage(MsgManager.toolongshorttag);
            return;
        }
        
        if(GenConf.badwords != null && GenConf.badwords.contains(tag)) {
            player.sendMessage(MsgManager.illegaltag);
            return;
        }
        
        if(this.getPlugin().getGuildHelper().doesGuildExists(tag)) {
            player.sendMessage(MsgManager.get("guildexists"));
            return;
        }
        
        String description = GenUtil.argsToString(args, 2, args.length);
        if(description.length() > 32) {
            player.sendMessage(MsgManager.get("desctoolong"));
            return;
        }
        if(!CuboidStuff.checkIfCuboidFarForGuild(player.getLocation())) {
            player.sendMessage(MsgManager.get("gildtocloseothers"));
            return;
        }
        if(GenUtil.isPlayerNearby(player, GenConf.MIN_CUBOID_RADIUS)) {
            player.sendMessage(MsgManager.playerstooclose);
            return;
        }
        if(GenConf.reqitems != null && !GenConf.reqitems.isEmpty()) {
            if(!GenUtil.hasEnoughItemsForGuild(player.getInventory())) {
                player.sendMessage(MsgManager.get("notenoughitems"));
                return;
            } else {
                GuildCreateEvent gce = new GuildCreateEvent(tag, description, player,player.getLocation());
                Bukkit.getServer().getPluginManager().callEvent(gce);
                if(gce.isCancelled()){
                    return;
                }
                GenUtil.removeRequiredItemsForGuild(player.getInventory());
            }
        }

        Cuboid cuboid = new Cuboid();
        cuboid.setOwner(tag);
        cuboid.setCenter(player.getLocation());
        cuboid.setRadius(GenConf.MIN_CUBOID_RADIUS);

        guildHelper.getCuboids().put(tag, cuboid);

        Guild guild = new Guild(getPlugin());
        guild.setCuboid(cuboid);
        guild.setTag(tag);
        guild.setDescription(description);
        guild.addMember(player.getUniqueId());
        guild.setHome(player.getLocation());
        guild.setLeader(player.getUniqueId());
        guildHelper.getGuilds().put(tag, guild);
        guildHelper.getPlayers().put(player.getUniqueId(), guild);
        guild.setSc(Bukkit.getScoreboardManager().getNewScoreboard());
        if(GenConf.playerprefixenabled) {
            this.getPlugin().
                    getTagManager().playerJoinGuild(player);
        }

        this.getPlugin().getOGLogger().info("Player '" + player.getName() + "' successfully created new guild '" + tag.toUpperCase() + "'.");
        
        /**
         @TODO:
         - cuboids
         - call MessageBroadcastEvent
        */
        getPlugin().getSQLHandler().addGuild(guild);
        getPlugin().getSQLHandler().updatePlayer(player.getUniqueId());

        this.getPlugin().broadcastMessage(MsgManager.get("broadcast-create").replace("{TAG}", tag.toUpperCase()).replace("{PLAYER}", player.getDisplayName()));
    }

    @Override
    public int minArgs() {
        return 2;
    }

}
