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

import pl.grzegorz2047.openguild2047.guilds.Guild;
import pl.grzegorz2047.openguild2047.commands.command.Command;
import pl.grzegorz2047.openguild2047.commands.command.CommandException;
import pl.grzegorz2047.openguild2047.events.guild.GuildCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.configuration.GenConf;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboid;
import pl.grzegorz2047.openguild2047.events.guild.GuildCreatedEvent;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.modules.spawn.SpawnChecker;
import pl.grzegorz2047.openguild2047.utils.GenUtil;

/**
 * Command used to create new guild.
 * <p>
 * Usage: /guild create [tag] [description]
 */
public class GuildCreateCommand extends Command {
    private final Cuboids cuboids;
    private final Guilds guilds;
    private final SQLHandler sqlHandler;

    public GuildCreateCommand(Cuboids cuboids, Guilds guilds, SQLHandler sqlHandler) {
        setPermission("openguild.command.create");
        this.cuboids = cuboids;
        this.guilds = guilds;
        this.sqlHandler = sqlHandler;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        Player player = (Player) sender;

        if (GenConf.FORCE_DESC) {
            if (args.length < 3) {
                player.sendMessage(MsgManager.get("descrequired"));
                return;
            }
        }
        if (guilds.hasGuild(player)) {
            player.sendMessage(MsgManager.get("alreadyinguild"));
            return;
        }
        //Bukkit.broadcastMessage("Is on Spawn = " + SpawnChecker.isSpawn(player.getLocation()));
        if (SpawnChecker.isSpawn(player.getLocation()) && !player.hasPermission("openguild.spawn.bypass")) {
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return;
        }

        String tag = args[1].toUpperCase();
        if (!tag.matches("[0-9a-zA-Z]*")) {
            player.sendMessage(MsgManager.unsupportedchars);
            return;
        }

        if (tag.length() > GenConf.maxclantag || tag.length() < GenConf.minclantag) {
            player.sendMessage(MsgManager.toolongshorttag);
            return;
        }

        if (GenConf.badwords != null && GenConf.badwords.contains(tag)) {
            player.sendMessage(MsgManager.illegaltag);
            return;
        }

        if (guilds.doesGuildExists(tag)) {
            player.sendMessage(MsgManager.get("guildexists"));
            return;
        }

        String description = GenUtil.argsToString(args, 2, args.length);
        if (description.length() > 32) {
            player.sendMessage(MsgManager.get("desctoolong"));
            return;
        }

        if (cuboids.isCuboidInterferingWithOtherCuboid(player.getLocation())) {
            player.sendMessage(MsgManager.get("guildtocloseothers"));
            return;
        }

        if (GenConf.forbiddenworlds.contains(player.getWorld().getName())) {
            player.sendMessage(MsgManager.get("forbiddenworld"));
            return;
        }

        if (GenConf.cuboidCheckPlayers && GenUtil.isPlayerNearby(player, GenConf.MIN_CUBOID_SIZE)) {
            player.sendMessage(MsgManager.playerstooclose);
            return;
        }
        Cuboid cuboid = cuboids.previewCuboid(player.getLocation(), tag, GenConf.MIN_CUBOID_SIZE);
        if (cuboids.isCuboidInterferingWithOtherCuboid(cuboid)) {
            player.sendMessage(MsgManager.get("guildtocloseothers"));
            return;
        }
        boolean reqitems = false;
        if (GenConf.reqitems != null && !GenConf.reqitems.isEmpty()) {
            reqitems = true;
            if (!GenUtil.hasEnoughItemsForGuild(player.getInventory())) {
                player.sendMessage(MsgManager.get("notenoughitems"));
                return;
            }
        }

        GuildCreateEvent event = new GuildCreateEvent(tag, description, player, player.getLocation());
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        } else if (reqitems) {
            GenUtil.removeRequiredItemsForGuild(player.getInventory());
        }


        cuboids.addCuboid(player.getLocation(), tag, GenConf.MIN_CUBOID_SIZE);
        Guild guild = guilds.addGuild(getPlugin(), player.getLocation(), player.getUniqueId(), tag, description);
        guilds.updatePlayerGuild(player.getUniqueId(), guild);
        guilds.addOnlineGuild(guild.getName());

        if (GenConf.playerprefixenabled) {
            this.getPlugin().getTagManager().playerCreatedGuild(guild, player);
        }

        OpenGuild.getOGLogger().info("Player '" + player.getName() + "' successfully created new guild '" + tag.toUpperCase() + "'.");

        /**
         @TODO:
         - cuboids
         - call MessageBroadcastEvent
         */
        sqlHandler.insertGuild(tag, description, player.getUniqueId(), player.getLocation(), player.getLocation().getWorld().getName());
        sqlHandler.addGuildCuboid(cuboid.getCenter(), cuboid.getCuboidSize(), cuboid.getOwner(), cuboid.getWorldName());
        sqlHandler.updatePlayerTag(player.getUniqueId(), guild.getName());

        this.getPlugin().broadcastMessage(MsgManager.get("broadcast-create").replace("{TAG}", tag.toUpperCase()).replace("{PLAYER}", player.getDisplayName()));

        GuildCreatedEvent createdEvent = new GuildCreatedEvent(guild);
        Bukkit.getPluginManager().callEvent(createdEvent);
    }


    @Override
    public int minArgs() {
        return 2;
    }

}
