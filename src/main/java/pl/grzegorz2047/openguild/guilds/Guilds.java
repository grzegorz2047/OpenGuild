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
package pl.grzegorz2047.openguild.guilds;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild.InventoryManipulator;
import pl.grzegorz2047.openguild.ItemsLoader;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboid;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.events.guild.GuildCreateEvent;
import pl.grzegorz2047.openguild.events.guild.GuildCreatedEvent;
import pl.grzegorz2047.openguild.events.guild.GuildLeaveEvent;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.managers.TagManager;
import pl.grzegorz2047.openguild.metadata.PlayerMetadataController;
import pl.grzegorz2047.openguild.ranking.RankDifference;
import pl.grzegorz2047.openguild.relations.Relation;
import pl.grzegorz2047.openguild.spawn.SpawnChecker;
import pl.grzegorz2047.openguild.utils.GenUtil;

import java.util.*;

public class Guilds {

    private final GuildInvitations guildInvitations;
    private final Plugin plugin;
    private final Cuboids cuboids;
    private final String playerTemplateNameLabel = "{PLAYER}";
    private final PlayerMetadataController playerMetadataController;
    private final List<String> FORBIDDEN_WORLDS;
    private final SQLHandler sqlHandler;
    private final boolean blockGuildCreationWhenPlayersTooClose;
    private final List<String> badWords;
    private final boolean playerGuildTagsEnabled;
    private final int MIN_CUBOID_SIZE;
    private final TagManager tagManager;
    private final boolean FORCE_DESC;
    private Map<String, Guild> guilds = new HashMap<>();
    private List<String> onlineGuilds = new ArrayList<>();
    private List<ItemStack> requiredItemStacks;
    private final boolean playSoundWHenSomeoneEnteredCuboidEnabled;
    private Sound cuboidEnterSound;
    private final int maxclantag = 6;
    private final int minclantag = 4;
    private final int defaultEloPoints = 1000;


    public Guilds(final SQLHandler sqlHandler, Plugin plugin, Cuboids cuboids, TagManager tagManager) {
        this.cuboids = cuboids;
        this.plugin = plugin;
        this.playerMetadataController = new PlayerMetadataController(plugin);
        this.guildInvitations = new GuildInvitations(sqlHandler, playerMetadataController);
        this.sqlHandler = sqlHandler;
        this.tagManager = tagManager;
        FileConfiguration config = plugin.getConfig();
        playSoundWHenSomeoneEnteredCuboidEnabled = config.getBoolean("cuboid.notify-enter-sound", false);
        FORBIDDEN_WORLDS = config.getStringList("forbidden-worlds");
        try {
            cuboidEnterSound = Sound.BLOCK_ANVIL_BREAK;
        } catch (Exception ex) {
            cuboidEnterSound = Sound.valueOf("ANVIL_BREAK");
        }
        try {
            String entity_enderman_death = config.getString("cuboid.notify-enter-sound-type", "ENTITY_ENDERMAN_DEATH");
            cuboidEnterSound = Sound.valueOf(entity_enderman_death);
        } catch (IllegalArgumentException ex) {
            OpenGuild.getOGLogger().warning("Sound type " + config.getString("cuboid.notify-enter-sound-type") + " is incorrect! Please visit https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html for help.");
        }

        blockGuildCreationWhenPlayersTooClose = config.getBoolean("cuboid.block-guild-creation-when-players-are-too-close", false);
        badWords = config.getStringList("forbiddenguildnames");
        playerGuildTagsEnabled = config.getBoolean("tags.enabled", true);
        MIN_CUBOID_SIZE = config.getInt("cuboid.min-cube-size", 15);
        FORCE_DESC = config.getBoolean("forcedesc", false);

    }


    public void notifyMembersAboutSomeoneEnteringTheirCuboid(Player player, String guildscuboidtag, Guild enemy) {
        Guild guild = getGuild(guildscuboidtag);
        for (UUID mem : guild.getMembers()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(mem);
            if (op.isOnline()) {
                if (enemy != null) {
                    notifySomeoneEnteredCuboid(op, player, enemy);
                } else {
                    notifySomeoneEnteredCuboid(op, player);
                }
                playSoundOnSomeoneEnteredCuboid(op);
            }
        }
    }

    private void playSoundOnSomeoneEnteredCuboid(OfflinePlayer op) {
        if (playSoundWHenSomeoneEnteredCuboidEnabled) {
            op.getPlayer().playSound(op.getPlayer().getLocation(), cuboidEnterSound, 10f, 5f);
        }
    }

    private void notifySomeoneEnteredCuboid(OfflinePlayer op, Player player) {
        op.getPlayer().sendMessage(
                MsgManager.get("entercubmemsnoguild")
                        .replace(playerTemplateNameLabel, player.getName()));
    }

    private void notifySomeoneEnteredCuboid(OfflinePlayer op, Player player, Guild enemy) {
        op.getPlayer().sendMessage(
                MsgManager.get("entercubmems")
                        .replace(playerTemplateNameLabel, player.getName())
                        .replace("{GUILD}", enemy.getName().toUpperCase()));

    }

    public List<Guild> getAllyGuilds(Guild g) {
        List<Guild> allies = new ArrayList<>();
        for (Relation r : g.getAlliances()) {
            String alliedGuildTag = r.getAlliedGuildTag();
            if (r.getAlliedGuildTag().equals(g.getName())) {
                alliedGuildTag = r.getBaseGuildTag();
            }
            Guild allyGuild = this.getGuild(alliedGuildTag);
            allies.add(allyGuild);
        }
        return allies;
    }

    public void guildMemberLeftServer(Player player, UUID uuid) {
        Guild guild = getPlayerGuild(uuid);
        if (guild == null) {
            return;
        }
        notifyGuildThatMemberLeft(player, guild);
        verifyOnlineGuild(player, guild);
    }

    public void notifyMembersJoinedGame(Player player, Guild guild) {
        String msg = MsgManager.get("guildmemberjoined");
        guild.notifyOtherGuildMembers(msg.replace(playerTemplateNameLabel, player.getDisplayName()), player);
    }

    private void notifyGuildThatMemberLeft(Player player, Guild guild) {
        String msg = MsgManager.get("guildmemberleft");
        guild.notifyGuild(msg.replace(playerTemplateNameLabel, player.getDisplayName()));
    }

    public boolean isPlayerInGuild(Player player) {
        return hasGuild(player);
    }


    private void verifyOnlineGuild(Player player, Guild guild) {
        List<String> onlineMembers = guild.getOnlineMembers();
        if (onlineMembers.size() == 0) {
            removeOnlineGuild(guild.getName());
        } else if (onlineMembers.size() == 1) {
            if (onlineMembers.contains(player.getName())) {
                removeOnlineGuild(guild.getName());
            }
        }
    }

    public Guild getPlayerGuild(UUID uuid) {
        String guildTag = playerMetadataController.getPlayerGuildTagFromMetaData(uuid);
        return guilds.get(guildTag);
    }


    public Guild getGuild(String guildTag) {
        return guilds.get(guildTag);
    }

    public Map<String, Guild> getGuilds() {
        return guilds;
    }

    /**
     * @param uuid UUID of player, who should be checked.
     * @return boolean
     */
    public boolean hasGuild(UUID uuid) {
        String guildTag = playerMetadataController.getPlayerGuildTagFromMetaData(uuid);
        return !Objects.equals(guildTag, "");
    }

    /**
     * @param player Player class instance, of player, who should be checked.
     * @return boolean
     */
    public boolean hasGuild(Player player) {
        return hasGuild(player.getUniqueId());
    }

    /**
     * @param tag tag of guild, for which existance should map be searched.
     * @return boolean
     */
    public boolean doesGuildExists(String tag) {
        return !tag.isEmpty() && guilds.containsKey(tag);
    }

    public void setGuilds(Map<String, Guild> guilds) {
        this.guilds = guilds;
    }


    /**
     * @return map which contains all players, who are members of guilds.
     */

    public Guild addGuild(Location home, UUID owner, String tag, String description) {
        Guild guild = new Guild(tag, description, home, owner);
        guild.addMember(owner);
        guilds.put(tag, guild);
        return guild;
    }

    public void invitePlayer(final Player player, Player who, Guild guild) {
        String guildName = guild.getName();
        guildInvitations.addGuildInvitation(player, who, guild, guildName);
    }


    public void addOnlineGuild(String guild) {
        if (onlineGuilds.contains(guild)) {
            return;
        }
        this.onlineGuilds.add(guild);
    }

    public void removeOnlineGuild(String guild) {
        if (!onlineGuilds.contains(guild)) {
            return;
        }
        this.onlineGuilds.remove(guild);
    }

    public boolean isGuildOnline(String guild) {
        return onlineGuilds.contains(guild);
    }

    public int getNumberOfGuilds() {
        return guilds.size();
    }

    public void addPlayer(UUID uuid, String guildName) {
        playerMetadataController.updatePlayerGuildMetadata(uuid, guildName);
    }

    public List<String> getOnlineGuilds() {
        return onlineGuilds;
    }

    public void checkPlayerInvitations() {
        guildInvitations.checkPlayerInvitations();
    }

    public GuildInvitation getGuildInvitation(String playerName, String guildName) {
        return guildInvitations.getPlayerInvitation(playerName, guildName);
    }

    public void acceptInvitation(Player player, Guild guild) {
        this.guildInvitations.acceptGuildInvitation(player, guild);
        refreshScoreboardTagsForBecauseOfNewClanMember(player, guild);


    }

    private void refreshScoreboardTagsForBecauseOfNewClanMember(Player player, Guild guild) {
        String guildName = guild.getName();
        List<String> guildMemberNames = guild.getMemberNames();
        tagManager.sendDeleteTeamTag(player, guildName);
        tagManager.sendCreateOwnTeamTag(player, guildName, guildMemberNames);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (guild.isMember(p.getUniqueId())) {
                tagManager.sendDeleteTeamTag(p, guildName);
                tagManager.sendCreateOwnTeamTag(p, guildName, guildMemberNames);
                tagManager.sendUpdateOwnTeamTag(p, guildName, guildMemberNames);
            }
        }
    }

    public Guild getGuild(Location location) {
        return guilds.get(cuboids.getGuildTagInLocation(location));
    }


    public boolean isPlayerInForbiddenWorld(String playerWorldName) {
        return FORBIDDEN_WORLDS.contains(playerWorldName);
    }

    public void updatePlayerMetadata(UUID uuid, String column, Object value) {
        playerMetadataController.updatePlayerMetadata(uuid, column, value);
    }

    public void updatePlayerMetadata(Player player, String column, Object value) {
        playerMetadataController.updatePlayerMetadata(player, column, value);
    }

    public void createDefaultPlayerMetaData(UUID uuid) {
        updatePlayerMeta(uuid, "", defaultEloPoints, 0, 0);
    }

    public void updatePlayerMeta(UUID uuid, String guildName, int eloPoints, int playerKills, int playerDeaths) {
        playerMetadataController.updatePlayerMetaAll(uuid, guildName, eloPoints, playerKills, playerDeaths);
    }

    public void loadRequiredItemsForGuild(List<Map<?, ?>> itemList) {
        ItemsLoader itemsLoader = new ItemsLoader();
        this.requiredItemStacks = itemsLoader.loadItems(itemList);
    }

    public boolean hasEnoughItemsForGuild(PlayerInventory inventory) {
        InventoryManipulator inventoryManipulator = new InventoryManipulator();
        return inventoryManipulator.hasEnoughItems(inventory, requiredItemStacks);
    }

    public void removeRequiredItemsForGuild(PlayerInventory inventory) {
        InventoryManipulator itemManipulator = new InventoryManipulator();
        itemManipulator.removeRequiredItemsForGuild(inventory, requiredItemStacks);
    }

    public Inventory prepareItemGuildWindowInventory(PlayerInventory inventory) {
        InventoryManipulator inventoryManipulator = new InventoryManipulator();
        return inventoryManipulator.prepareItemGuildWindowInventory(inventory, requiredItemStacks, plugin);
    }

    public int getRequiredItemsSize() {
        return requiredItemStacks.size();
    }

    public void updatePlayersElo(Player killer, Player lost, RankDifference rankDifference) {
        PlayerMetadataController.PlayerMetaDataColumn eloColumn = PlayerMetadataController.PlayerMetaDataColumn.ELO;
        String eloColumnName = eloColumn.name();
        updatePlayerMetadata(killer, eloColumnName, rankDifference.getWinNewPoints());
        updatePlayerMetadata(lost, eloColumnName, rankDifference.getLostNewPoints());
    }

    public boolean isLeader(UUID playerUniqueId, Guild playerGuild) {
        return playerGuild.getLeader().equals(playerUniqueId);
    }

    public boolean changeHome(Player player, UUID playerUniqueId, Location playerLocation) {
        Guild playerGuild = getPlayerGuild(playerUniqueId);
        if (playerGuild == null) {
            player.sendMessage(MsgManager.get("notinguild"));
            return false;
        }
        if (!isLeader(playerUniqueId, playerGuild)) {
            player.sendMessage(MsgManager.get("playernotleader"));
            return false;
        }

        playerGuild.setHome(playerLocation);
        sqlHandler.changeHome(playerGuild.getName(), playerLocation);
        player.sendMessage(MsgManager.get("successfullychangedhomeposition"));
        return true;
    }

    public boolean changeLeader(Player player, String newLeaderName) {
        UUID playerUniqueId = player.getUniqueId();
        Guild playerGuild = getPlayerGuild(playerUniqueId);
        if (playerGuild == null) {
            player.sendMessage(MsgManager.get("notinguild"));
            return false;
        }
        UUID playerGuildLeader = playerGuild.getLeader();
        if (!playerGuildLeader.equals(playerUniqueId)) {
            player.sendMessage(MsgManager.get("playernotleader"));
            return false;
        }
        if (playerGuildLeader.equals(playerUniqueId)) {
            player.sendMessage(MsgManager.get("cantchangeleadertoyourself"));
            return false;
        }

        if (!playerGuild.getMemberNames().contains(newLeaderName)) {
            player.sendMessage(MsgManager.get("playernotinyourguild"));
            return false;
        }
        Player newLeader = Bukkit.getPlayer(newLeaderName);
        if (newLeader == null) {
            player.sendMessage(MsgManager.get("playeroffline"));
            return false;
        }
        UUID leaderUniqueId = newLeader.getUniqueId();
        playerGuild.setLeader(leaderUniqueId);
        sqlHandler.changeLeader(newLeader.getName(), playerGuild.getName());
        player.sendMessage(MsgManager.get("successfullychangedleader"));
        return true;
    }

    public boolean createGuild(Player player, String tag, String description, boolean descriptionAdded) {
        Location playerLocation = player.getLocation();
        World playerLocationWorld = playerLocation.getWorld();
        UUID worldUUID = playerLocationWorld.getUID();
        Cuboid cuboid = cuboids.previewCuboid(playerLocation, tag, MIN_CUBOID_SIZE, worldUUID);
        if (!fufilledGuildCreationRequirements(player, tag, description, cuboid, descriptionAdded)) return false;


        GuildCreateEvent event = invokeGuildCreateEvent(player, tag, description);
        if (event.isCancelled()) {
            return false;
        }

        removeRequiredItemsForGuild(player.getInventory());
        Guild guild = insertGuildData(player, tag, description, cuboid);

        String guildCreatedMsg = MsgManager.get("broadcast-create").replace("{TAG}", tag.toUpperCase()).replace("{PLAYER}", player.getDisplayName());
        Bukkit.broadcastMessage(guildCreatedMsg);
        invokeGuildCreatedEvent(guild);
        return true;
    }

    private void invokeGuildCreatedEvent(Guild guild) {
        GuildCreatedEvent createdEvent = new GuildCreatedEvent(guild);
        Bukkit.getPluginManager().callEvent(createdEvent);
    }

    private Guild insertGuildData(Player player, String tag, String description, Cuboid cuboid) {
        cuboids.addCuboid(player.getLocation(), tag, MIN_CUBOID_SIZE);
        UUID playerUniqueId = player.getUniqueId();
        Guild guild = addGuild(player.getLocation(), playerUniqueId, tag, description);
        String guildName = guild.getName();
        playerMetadataController.updatePlayerGuildMetadata(playerUniqueId, guildName);
        addOnlineGuild(guildName);

        if (playerGuildTagsEnabled) {
            playerCreatedGuild(guild, player);
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

    public void guildBrokeAlliance(Guild firstGuild, Guild secondGuild) {
        for (Relation r : firstGuild.getAlliances()) {
            if (r.getBaseGuildTag().equals(secondGuild.getName()) || r.getAlliedGuildTag().equals(secondGuild.getName())) {//Trzeba to odzielic jakos na 2 przypadki (else if) zamiast ||
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (firstGuild.getMembers().contains(p.getUniqueId())) {
                        tagManager.sendUpdateEnemyTeamTag(p, secondGuild.getName(), secondGuild.getMemberNames());
                    }
                    if (secondGuild.getMembers().contains(p.getUniqueId())) {
                        tagManager.sendUpdateEnemyTeamTag(p, firstGuild.getName(), firstGuild.getMemberNames());
                    }
                }
            }
        }
    }

    public void guildMakeAlliance(Guild whoGuild, Guild withWhoGuild) {

        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID playerUniqueId = p.getUniqueId();
            if (whoGuild.getMembers().contains(playerUniqueId)) {
                tagManager.sendUpdateAllyTeamTag(p, withWhoGuild.getName(), withWhoGuild.getMemberNames());
            }
            if (withWhoGuild.getMembers().contains(playerUniqueId)) {
                tagManager.sendUpdateAllyTeamTag(p, whoGuild.getName(), whoGuild.getMemberNames());
            }
        }
    }

    public void playerCreatedGuild(Guild g, Player player) {
        tagManager.sendCreateOwnTeamTag(player, g.getName(), g.getMemberNames());
        //System.out.println("Liczba obiektow team "+this.getGlobalScoreboard().getTeams().size());
        //String enemyTagTemplate = ENEMY_GUILD_TAG_FORMAT;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (g.getMembers().contains(p.getUniqueId())) {
                continue;
            }
            tagManager.sendCreateDefaultTeamTag(p, g.getName(), g.getMemberNames());
        }
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

    private boolean fufilledGuildCreationRequirements(Player player, String tag, String description, Cuboid cuboid, boolean descriptionAdded) {
        Location playerLocation = player.getLocation();
        if (isDescriptionForced()) {
            if (!descriptionAdded) {
                player.sendMessage(MsgManager.get("descrequired"));
                return false;
            }
        }
        if (hasGuild(player)) {
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

        if (doesGuildExists(tag)) {
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
        if (isPlayerInForbiddenWorld(playerWorldName)) {
            player.sendMessage(MsgManager.get("forbiddenworld"));
            return false;
        }

        if (isOtherPlayerTooClose(player)) {
            player.sendMessage(MsgManager.get("playerstooclose"));
            return false;
        }
        if (!hasEnoughItemsForGuild(player.getInventory())) {
            player.sendMessage(MsgManager.get("notenoughitems"));
            return false;
        }
        if (cuboids.isCuboidInterferingWithOtherCuboid(cuboid)) {
            player.sendMessage(MsgManager.get("guildtocloseothers"));
            return false;
        }
        return true;
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

    public boolean playerLeaveGuild(Player player, String leavePlayerReason) {
        if (!hasGuild(player)) {
            player.sendMessage(MsgManager.get("notinguild"));
            return false;
        }

        UUID playerUniqueId = player.getUniqueId();
        Guild guild = getPlayerGuild(playerUniqueId);
        if (guild.isLeader(playerUniqueId)) {
            player.sendMessage(MsgManager.get("kickleader"));
            return false;
        }

        GuildLeaveEvent event = new GuildLeaveEvent(guild, player);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        String guildName = guild.getName();
        List<String> guildMemberNames = guild.getMemberNames();
        guild.removeMember(playerUniqueId);
        tagManager.sendDeleteTeamTag(player, guildName);
        tagManager.sendCreateDefaultTeamTag(player, guildName, guildMemberNames);
        playerMetadataController.updatePlayerGuildMetadata(playerUniqueId, "");
        for (Player p : Bukkit.getOnlinePlayers()) {
            UUID uniqueId = p.getUniqueId();
            if (guild.getMembers().contains(uniqueId)) {
                tagManager.sendDeleteTeamTag(p, guildName);
                tagManager.sendCreateOwnTeamTag(p, guildName, guildMemberNames);
                tagManager.sendUpdateOwnTeamTag(p, guildName, guildMemberNames);
                p.sendMessage(leavePlayerReason);
            }
            for (Guild ally : getAllyGuilds(guild)) {
                List<UUID> whoGuildMembers = guild.getMembers();
                if (whoGuildMembers.contains(uniqueId)) {
                    tagManager.sendUpdateAllyTeamTag(p, guildName, guildMemberNames);
                } else if (ally.getMembers().contains(uniqueId)) {
                    tagManager.sendUpdateAllyTeamTag(p, ally.getName(), ally.getMemberNames());
                }

            }

        }
        sqlHandler.updatePlayerTag(playerUniqueId, "");
        player.sendMessage(MsgManager.get("leaveguildsuccess"));
        return true;
    }

    public void refreshScoreboardTagsForAllPlayers(Guild guildToUpdate) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            Guild playerGuild = getPlayerGuild(p.getUniqueId());
            String guildName = guildToUpdate.getName();
            List<String> guildMemberNames = guildToUpdate.getMemberNames();
            if (playerGuild == null) {
                tagManager.sendCreateDefaultTeamTag(p, guildName, guildMemberNames);
                continue;
            }
            OpenGuild.getOGLogger().debug("Przechodze przez gildie " + playerGuild.getName());
            if (guildToUpdate.equals(playerGuild)) {
                OpenGuild.getOGLogger().debug("Odswieza team u gracza ze swojej gildii");
                tagManager.sendCreateOwnTeamTag(p, guildName, guildMemberNames);
                tagManager.sendUpdateOwnTeamTag(p, guildName, guildMemberNames);
            } else if (guildToUpdate.isAlly(playerGuild)) {
                tagManager.sendCreateAllyTeamTag(p, guildName, guildMemberNames);
                tagManager.sendUpdateAllyTeamTag(p, guildName, guildMemberNames);
            } else {
                tagManager.sendCreateDefaultTeamTag(p, guildName, guildMemberNames);
                tagManager.sendUpdateEnemyTeamTag(p, guildName, guildMemberNames);
            }


        }
    }

    private boolean isDescriptionForced() {
        return FORCE_DESC;
    }

    public void prepareScoreboardTagForPlayerOnJoin(Player p) {
        Guild playerGuild = getPlayerGuild(p.getUniqueId());
        for (String onlineGuildTag : getOnlineGuilds()) {
            Guild g = getGuild(onlineGuildTag);
            if (g == null) {
                System.out.println("Gildia " + onlineGuildTag + " jest nulem! Czemu?");
                continue;
            }
            String guildName = g.getName();
            List<String> guildMemberNames = g.getMemberNames();
            if (playerGuild != null) {
                if (g.equals(playerGuild)) {
                    tagManager.sendCreateOwnTeamTag(p, guildName, guildMemberNames);
                }
                if (playerGuild.isAlly(g)) {
                    tagManager.sendCreateAllyTeamTag(p, guildName, guildMemberNames);
                } else {
                    tagManager.sendCreateDefaultTeamTag(p, guildName, guildMemberNames);
                }
            } else {
                tagManager.sendCreateDefaultTeamTag(p, guildName, guildMemberNames);
            }
        }
    }

}
