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

package pl.grzegorz2047.openguild.commands.guild;

import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.events.guild.GuildCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.configuration.GenConf;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboid;
import pl.grzegorz2047.openguild.events.guild.GuildCreatedEvent;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.managers.TagManager;
import pl.grzegorz2047.openguild.spawn.SpawnChecker;
import pl.grzegorz2047.openguild.utils.GenUtil;

/**
 * Command used to create new guild.
 * <p>
 * Usage: /guild create [tag] [description]
 */
public class GuildCreateCommand extends Command {
    private final Cuboids cuboids;
    private final Guilds guilds;
    private final SQLHandler sqlHandler;
    private final TagManager tagManager;

    public GuildCreateCommand(Cuboids cuboids, Guilds guilds, SQLHandler sqlHandler, TagManager tagManager) {
        setPermission("openguild.command.create");
        this.cuboids = cuboids;
        this.guilds = guilds;
        this.sqlHandler = sqlHandler;
        this.tagManager = tagManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        Player player = (Player) sender;

        String tag = args[1].toUpperCase();
        String description = GenUtil.argsToString(args, 2, args.length);
        Cuboid cuboid = cuboids.previewCuboid(player.getLocation(), tag, GenConf.MIN_CUBOID_SIZE);

        if (!fufilledRequirements(args, player, tag, description, cuboid)) return;


        GuildCreateEvent event = invokeGuildCreateEvent(player, tag, description);
        if (event.isCancelled()) {
            return;
        }

        Guild guild = insertGuildData(player, tag, description, cuboid);

        String guildCreatedMsg = MsgManager.get("broadcast-create").replace("{TAG}", tag.toUpperCase()).replace("{PLAYER}", player.getDisplayName());
        Bukkit.broadcastMessage(guildCreatedMsg);

        invokeGuildCreatedEvent(guild);
    }

    private void invokeGuildCreatedEvent(Guild guild) {
        GuildCreatedEvent createdEvent = new GuildCreatedEvent(guild);
        Bukkit.getPluginManager().callEvent(createdEvent);
    }

    private Guild insertGuildData(Player player, String tag, String description, Cuboid cuboid) {
        guilds.removeRequiredItemsForGuild(player.getInventory());
        cuboids.addCuboid(player.getLocation(), tag, GenConf.MIN_CUBOID_SIZE);
        Guild guild = guilds.addGuild(player.getLocation(), player.getUniqueId(), tag, description);
        guilds.updatePlayerMetadata(player.getUniqueId(), "guild", guild.getName());
        guilds.addOnlineGuild(guild.getName());

        if (GenConf.PLAYER_GUILD_TAGS_ENABLED) {
            tagManager.playerCreatedGuild(guild, player);
        }

        OpenGuild.getOGLogger().info("Player '" + player.getName() + "' successfully created new guild '" + tag.toUpperCase() + "'.");

        /*
         @TODO:
         - cuboids
         - call MessageBroadcastEvent
         */
        sqlHandler.insertGuild(tag, description, player.getUniqueId(), player.getLocation(), player.getLocation().getWorld().getName());
        sqlHandler.addGuildCuboid(cuboid.getCenter(), cuboid.getCuboidSize(), cuboid.getOwner(), cuboid.getWorldName());
        sqlHandler.updatePlayerTag(player.getUniqueId(), guild.getName());
        return guild;
    }

    private GuildCreateEvent invokeGuildCreateEvent(Player player, String tag, String description) {
        GuildCreateEvent event = new GuildCreateEvent(tag, description, player, player.getLocation());
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    private boolean fufilledRequirements(String[] args, Player player, String tag, String description, Cuboid cuboid) {
        if (GenConf.FORCE_DESC) {
            if (args.length < 3) {
                player.sendMessage(MsgManager.get("descrequired"));
                return false;
            }
        }
        if (guilds.hasGuild(player)) {
            player.sendMessage(MsgManager.get("alreadyinguild"));
            return false;
        }
        //Bukkit.broadcastMessage("Is on Spawn = " + SpawnChecker.isSpawn(player.getLocation()));
        if (SpawnChecker.isSpawn(player.getLocation()) && !player.hasPermission("openguild.spawn.bypass")) {
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return false;
        }

        if (!tag.matches("[0-9a-zA-Z]*")) {
            player.sendMessage(MsgManager.unsupportedchars);
            return false;
        }

        if (tag.length() > GenConf.maxclantag || tag.length() < GenConf.minclantag) {
            player.sendMessage(MsgManager.toolongshorttag);
            return false;
        }

        if (GenConf.BAD_WORDS != null && GenConf.BAD_WORDS.contains(tag)) {
            player.sendMessage(MsgManager.illegaltag);
            return false;
        }

        if (guilds.doesGuildExists(tag)) {
            player.sendMessage(MsgManager.get("guildexists"));
            return false;
        }

        if (description.length() > 32) {
            player.sendMessage(MsgManager.get("desctoolong"));
            return false;
        }

        if (cuboids.isCuboidInterferingWithOtherCuboid(player.getLocation())) {
            player.sendMessage(MsgManager.get("guildtocloseothers"));
            return false;
        }

        if (GenConf.FORBIDDEN_WORLDS.contains(player.getWorld().getName())) {
            player.sendMessage(MsgManager.get("forbiddenworld"));
            return false;
        }

        if (GenConf.CHECK_PLAYERS_TOO_CLOSE_WHEN_CREATING_GUILD && GenUtil.isPlayerNearby(player, GenConf.MIN_CUBOID_SIZE)) {
            player.sendMessage(MsgManager.playerstooclose);
            return false;
        }
        if (!guilds.hasEnoughItemsForGuild(player.getInventory())) {
            player.sendMessage(MsgManager.get("notenoughitems"));
            return false;
        }
        if (cuboids.isCuboidInterferingWithOtherCuboid(cuboid)) {
            player.sendMessage(MsgManager.get("guildtocloseothers"));
            return false;
        }
        return true;
    }


    @Override
    public int minArgs() {
        return 2;
    }

}
