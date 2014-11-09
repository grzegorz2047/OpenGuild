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

import com.github.grzegorz2047.openguild.event.guild.GuildCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.*;
import pl.grzegorz2047.openguild2047.commands.CommandHandler;
import pl.grzegorz2047.openguild2047.cuboidmanagement.CuboidStuff;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.utils.GenUtil;

/**
 * Command used to create new guild.
 * 
 * Usage: /guild create [tag] [description]
 */
public class GuildCreateCommand extends CommandHandler {

    public GuildCreateCommand(OpenGuild plugin) {
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
        if(guildHelper.hasGuild(player)) {
            player.sendMessage(MsgManager.get("alreadyinguild"));
            return;
        }
        
        String tag = args[1];
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

        SimpleCuboid cuboid = new SimpleCuboid();
        cuboid.setOwner(tag);
        cuboid.setCenter(player.getLocation());
        cuboid.setRadius(GenConf.MIN_CUBOID_RADIUS);

        guildHelper.getCuboids().put(tag, cuboid);

        SimpleGuild guild = new SimpleGuild(getPlugin());
        guild.setCuboid(cuboid);
        guild.setTag(tag);
        guild.setDescription(description);
        guild.addMember(player.getUniqueId());
        guild.setHome(player.getLocation());
        guild.setLeader(player.getUniqueId());
        guildHelper.getGuilds().put(tag, guild);
        guildHelper.getPlayers().put(player.getUniqueId(), guild);
        
        if(GenConf.playerprefixenabled) {
            this.getPlugin().getTagManager().setTag(player.getUniqueId());
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
    public int getMinimumArguments() {
        return 2;
    }

}
