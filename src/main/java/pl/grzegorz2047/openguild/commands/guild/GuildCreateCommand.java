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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboid;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.events.guild.GuildCreateEvent;
import pl.grzegorz2047.openguild.events.guild.GuildCreatedEvent;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.managers.TagManager;
import pl.grzegorz2047.openguild.metadata.PlayerMetadataController;
import pl.grzegorz2047.openguild.spawn.SpawnChecker;
import pl.grzegorz2047.openguild.utils.GenUtil;

import java.util.List;
import java.util.UUID;

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
    private final boolean blockGuildCreationWhenPlayersTooClose;
    private final int maxclantag = 6;
    private final int minclantag = 4;
    private final int MIN_CUBOID_SIZE;
    private final boolean FORCE_DESC;
    private List<String> badWords;
    private boolean playerGuildTagsEnabled;

    public GuildCreateCommand(String[] aliases, Cuboids cuboids, Guilds guilds, SQLHandler sqlHandler, TagManager tagManager, FileConfiguration config) {
        super(aliases);
        setPermission("openguild.command.create");
        this.cuboids = cuboids;
        this.guilds = guilds;
        this.sqlHandler = sqlHandler;
        this.tagManager = tagManager;
        blockGuildCreationWhenPlayersTooClose = config.getBoolean("cuboid.block-guild-creation-when-players-are-too-close", false);
        badWords = config.getStringList("forbiddenguildnames");
        playerGuildTagsEnabled = config.getBoolean("tags.enabled", true);
        MIN_CUBOID_SIZE = config.getInt("cuboid.min-cube-size", 15);
        FORCE_DESC = config.getBoolean("forcedesc", false);
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
        Location playerLocation = player.getLocation();
        World playerLocationWorld = playerLocation.getWorld();
        UUID worldUUID = playerLocationWorld.getUID();
        Cuboid cuboid = cuboids.previewCuboid(playerLocation, tag, MIN_CUBOID_SIZE, worldUUID);

        if (!fufilledRequirements(player, tag, description, cuboid, args.length)) return;


        GuildCreateEvent event = invokeGuildCreateEvent(player, tag, description);
        if (event.isCancelled()) {
            return;
        }

        guilds.removeRequiredItemsForGuild(player.getInventory());
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
        cuboids.addCuboid(player.getLocation(), tag, MIN_CUBOID_SIZE);
        Guild guild = guilds.addGuild(player.getLocation(), player.getUniqueId(), tag, description);
        guilds.updatePlayerMetadata(player.getUniqueId(), PlayerMetadataController.PlayerMetaDataColumn.GUILD.name(), guild.getName());
        guilds.addOnlineGuild(guild.getName());

        if (playerGuildTagsEnabled) {
            tagManager.playerCreatedGuild(guild, player);
        }

        OpenGuild.getOGLogger().info("Player '" + player.getName() + "' successfully created new guild '" + tag.toUpperCase() + "'.");

        /*
         @TODO:
         - cuboids
         - call MessageBroadcastEvent
         */
        addDataToDatabase(player, tag, description, cuboid, guild);
        return guild;
    }

    private void addDataToDatabase(Player player, String tag, String description, Cuboid cuboid, Guild guild) {
        sqlHandler.insertGuild(tag, description, player.getUniqueId(), player.getLocation(), player.getLocation().getWorld().getName());
        sqlHandler.addGuildCuboid(cuboid.getCenter(), cuboid.getCuboidSize(), cuboid.getOwner(), cuboid.getWorldName());
        sqlHandler.updatePlayerTag(player.getUniqueId(), guild.getName());
    }

    private GuildCreateEvent invokeGuildCreateEvent(Player player, String tag, String description) {
        GuildCreateEvent event = new GuildCreateEvent(tag, description, player, player.getLocation());
        Bukkit.getServer().getPluginManager().callEvent(event);
        return event;
    }

    private boolean fufilledRequirements(Player player, String tag, String description, Cuboid cuboid, int argsLength) {
        Location playerLocation = player.getLocation();
        if (isDescriptionForced()) {
            if (!isDescriptionAdded(argsLength)) {
                player.sendMessage(MsgManager.get("descrequired"));
                return false;
            }
        }
        if (guilds.hasGuild(player)) {
            player.sendMessage(MsgManager.get("alreadyinguild"));
            return false;
        }
        //Bukkit.broadcastMessage("Is on Spawn = " + SpawnChecker.isSpawn(player.getLocation()));
        if (SpawnChecker.cantDoItOnSpawn(player, playerLocation)) {
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return false;
        }

        if (!hasLegalCharactersInTag(tag)) {
            player.sendMessage(MsgManager.get("unsupportedchars"));
            return false;
        }

        if (!hasTagCorrectLength(tag)) {
            player.sendMessage(MsgManager.get("toolongshorttag")
                    .replace("{MIN}", String.valueOf(minclantag))
                    .replace("{MAX}", String.valueOf(maxclantag)));
            return false;
        }

        if (hasBadWords(tag)) {
            player.sendMessage(MsgManager.get("illegaltag"));
            return false;
        }

        if (guilds.doesGuildExists(tag)) {
            player.sendMessage(MsgManager.get("guildexists"));
            return false;
        }

        if (isDescriptionToLong(description)) {
            player.sendMessage(MsgManager.get("desctoolong"));
            return false;
        }

        if (cuboids.isCuboidInterferingWithOtherCuboid(playerLocation)) {
            player.sendMessage(MsgManager.get("guildtocloseothers"));
            return false;
        }

        String playerWorldName = player.getWorld().getName();
        if (guilds.isPlayerInForbiddenWorld(playerWorldName)) {
            player.sendMessage(MsgManager.get("forbiddenworld"));
            return false;
        }

        if (isOtherPlayerTooClose(player)) {
            player.sendMessage(MsgManager.get("playerstooclose"));
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

    private boolean isDescriptionForced() {
        return FORCE_DESC;
    }

    private boolean isOtherPlayerTooClose(Player player) {

        return blockGuildCreationWhenPlayersTooClose && GenUtil.isPlayerNearby(player, MIN_CUBOID_SIZE);
    }


    private boolean isDescriptionToLong(String description) {
        return description.length() > 32;
    }

    private boolean hasBadWords(String tag) {
        return badWords != null && badWords.contains(tag);
    }

    private boolean hasTagCorrectLength(String tag) {
        return tag.length() <= maxclantag && tag.length() >= minclantag;
    }

    private boolean hasLegalCharactersInTag(String tag) {
        return tag.matches("[0-9a-zA-Z]*");
    }


    private boolean isDescriptionAdded(int argsLength) {
        return argsLength >= 3;
    }


    @Override
    public int minArgs() {
        return 2;
    }

}
