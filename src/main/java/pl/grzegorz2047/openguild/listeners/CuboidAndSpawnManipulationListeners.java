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
package pl.grzegorz2047.openguild.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.grzegorz2047.openguild.OGLogger;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.dropstone.DropFromBlocks;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.spawn.SpawnChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CuboidAndSpawnManipulationListeners implements Listener {

    private final Cuboids cuboids;
    private final DropFromBlocks drop;
    private final Guilds guilds;
    private static List<Material> breakingItems;
    private final List<String> BREAKING_ITEMS;
    private final short BREAKING_DAMAGE;
    private final boolean EXTRA_PROTECTION;
    private final boolean PREVENT_GHOST_BLOCK_PLACE;
    private final boolean DROP_ENABLED;
    private final OGLogger ogLogger = OpenGuild.getOGLogger();
    private List<Material> allowedInteractItems = Arrays.asList(
            Material.CHEST,
            Material.ENDER_CHEST,
            Material.DARK_OAK_DOOR,
            Material.ACACIA_DOOR,
            Material.BIRCH_DOOR,
            Material.JUNGLE_DOOR,
            Material.OAK_DOOR);


    public CuboidAndSpawnManipulationListeners(Cuboids cuboids, DropFromBlocks drop, Guilds guilds, FileConfiguration config) {
        this.cuboids = cuboids;
        BREAKING_ITEMS = config.getStringList("cuboid.breaking-blocks.item-types");
        BREAKING_DAMAGE = Short.parseShort(config.getString("cuboid.breaking-blocks.damage", "0"));

        this.drop = drop;
        this.guilds = guilds;
        EXTRA_PROTECTION = config.getBoolean("cuboid.extra-protection", false);
        PREVENT_GHOST_BLOCK_PLACE = config.getBoolean("prevent-macro-ghost-block-placing", false);
        DROP_ENABLED = config.getBoolean("drop.enabled", false);
        loadSpecialItemsToDestroyEnemyCuboidBlocks();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Player player = e.getPlayer();
        Block brokenBlock = e.getBlock();
        if (checkIfOnSpawn(e, player, brokenBlock)) return;
        if (processBreakingBlockInEnemyArea(e, player, brokenBlock)) return;
        if (DROP_ENABLED) {
            drop.processDropFromBlocks(player, brokenBlock);
            if (!drop.isNotUsedInDropFromBlocks(brokenBlock.getType())) {
                try {
                    e.setDropItems(false);
                } catch (Exception ex) {
                    System.out.println("DROP NIE JEST WSPIERANY! Wylacz drop z configu");
                }
            }
        }
    }

    private boolean checkIfOnSpawn(BlockBreakEvent e, Player player, Block brokenBlock) {
        if (SpawnChecker.isSpawn(brokenBlock.getLocation()) && !player.hasPermission("openguild.spawn.bypass")) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return true;
        }
        return false;
    }

    private boolean processBreakingBlockInEnemyArea(BlockBreakEvent e, Player player, Block brokenBlock) {
        String playerGuildTag = "";
        UUID uniqueId = player.getUniqueId();
        if (guilds.hasGuild(uniqueId)) {
            Guild playerGuild = guilds.getPlayerGuild(uniqueId);
            playerGuildTag = playerGuild.getName();

        }
        if (player.hasPermission("openguild.cuboid.bypassbreak")) {
            return false;
        } else if (!cuboids.hasRightToThisLocation(player, playerGuildTag, brokenBlock.getLocation())) {// Damage jest rowny 0; niszczenie blokow jest wylaczone
            if (!canbreakEnemyBlock()) {
                e.setCancelled(true);
            }
            PlayerInventory inventory = player.getInventory();
            if (!hasSpecialBreakingitem(inventory)) {
                player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonsomeonearea"));
                e.setCancelled(true);
                return true;
            }
            ItemStack itemInMainHand = inventory.getItemInMainHand();
            ItemStack itemInHand = inventory.getItemInHand();
            try {
                itemInMainHand.setDurability((short) (itemInMainHand.getDurability() + BREAKING_DAMAGE));
            } catch (Exception ex) {
                itemInHand.setDurability((short) (itemInHand.getDurability() + BREAKING_DAMAGE));
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Block clickedBlock = e.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }
        Player player = e.getPlayer();
        String playerGuildTag = "";
        UUID playerUniqueId = player.getUniqueId();
        if (guilds.hasGuild(playerUniqueId)) {
            Guild playerGuild = guilds.getPlayerGuild(playerUniqueId);
            playerGuildTag = playerGuild.getName();
        }
        Location clickedLocation = clickedBlock.getLocation();
        if (SpawnChecker.isSpawn(clickedLocation) && !player.hasPermission("openguild.spawn.bypass")) {
            if (allowedInteractItems.contains(e.getMaterial())) {
                return;
            }
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return;
        }
        if (!EXTRA_PROTECTION) {
            return;
        }

        if (cuboids.hasRightToThisLocation(player, playerGuildTag, clickedLocation)) {
            return;
        }
        if (player.hasPermission("openguild.cuboid.bypassinteract")) {
            return;
        }

        if (!canbreakEnemyBlock()) {
            e.setCancelled(true); // Damage jest rowny 0; niszczenie blokow jest wylaczone
            player.sendMessage(MsgManager.get("cantdoitonsomeonearea"));
            return;
        }
        if (!hasSpecialBreakingitem(player.getInventory())) {
            player.sendMessage(MsgManager.get("cantdoitonsomeonearea"));
            return;
        }
        Guild playerGuild = guilds.getPlayerGuild(playerUniqueId);
        if (playerGuild == null) {
            return;
        }
        Location playerLocation = player.getLocation();
        for (Guild ally : guilds.getAllyGuilds(playerGuild)) {
            String allyGuildTag = ally.getName();
            boolean isinAllyCuboid = cuboids.isInCuboid(playerLocation, allyGuildTag);
            if (isinAllyCuboid) {
                e.setCancelled(true);
                return;
            }
        }


        String interactionConsoleMsg = createConsoleInteractionMsg(player, playerLocation);
        ogLogger
                .info(interactionConsoleMsg);

        String interactionMsg = createInteractionMsg(playerLocation);
        player.sendMessage(interactionMsg);
    }


    private boolean hasSpecialBreakingitem(PlayerInventory inventory) {
        ItemStack inHand;
        try {
            inHand = inventory.getItemInMainHand();
        } catch (Exception ex) {
            inHand = inventory.getItemInHand();
        }
        return breakingItems.contains(inHand.getType());
    }

    private boolean canbreakEnemyBlock() {
        return BREAKING_DAMAGE > 0;
    }

    @EventHandler
    public void onBlockPiStonExtend(BlockPistonExtendEvent e) {
        for (Block block : e.getBlocks()) {
            if (block.getType() == Material.DRAGON_EGG) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        String playerGuildTag = "";
        UUID playerUniqueId = player.getUniqueId();
        if (guilds.hasGuild(playerUniqueId)) {
            Guild playerGuild = guilds.getPlayerGuild(playerUniqueId);
            playerGuildTag = playerGuild.getName();

        }
        Block block = e.getBlock();
        if (PREVENT_GHOST_BLOCK_PLACE) {
            if (e.isCancelled()) {
                if (block.getType().equals(Material.AIR)) {
                    e.getBlockPlaced().setType(Material.AIR);
                }
            }
        }
        Location blockLocation = block.getLocation();
        if (SpawnChecker.isSpawn(blockLocation) && !player.hasPermission("openguild.spawn.bypass")) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return;
        }
        if (player.hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if (!cuboids.hasRightToThisLocation(player, playerGuildTag, blockLocation)) {
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonsomeonearea"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketTake(PlayerBucketFillEvent e) {
        Player player = e.getPlayer();
        String playerGuildTag = "";
        UUID playerUniqueId = player.getUniqueId();
        if (guilds.hasGuild(playerUniqueId)) {
            Guild playerGuild = guilds.getPlayerGuild(playerUniqueId);
            playerGuildTag = playerGuild.getName();

        }
        Location location = e.getBlockClicked().getLocation();
        if (SpawnChecker.isSpawn(location) && !player.hasPermission("openguild.spawn.bypass")) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return;
        }
        if (player.hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if (!cuboids.hasRightToThisLocation(player, playerGuildTag, location)) {
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonsomeonearea"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFlow(PlayerBucketEmptyEvent e) {
        Player player = e.getPlayer();

        String playerGuildTag = "";
        UUID playerUniqueId = player.getUniqueId();
        if (guilds.hasGuild(playerUniqueId)) {
            Guild playerGuild = guilds.getPlayerGuild(playerUniqueId);
            playerGuildTag = playerGuild.getName();

        }
        Block blockClicked = e.getBlockClicked();
        Location blockClickedLocation = blockClicked.getLocation();
        if (SpawnChecker.isSpawn(blockClickedLocation) && !player.hasPermission("openguild.spawn.bypass")) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return;
        }
        if (player.hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if (!cuboids.hasRightToThisLocation(player, playerGuildTag, blockClickedLocation)) {
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonsomeonearea"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntityType() == EntityType.PRIMED_TNT) {
            List<Block> blockList = e.blockList();
            for (Block block : blockList) {
                if (block.getType() == Material.DRAGON_EGG) {
                    blockList.remove(block);
                }
            }
        }
    }


    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        UUID playerUniqueId = player.getUniqueId();
        Guild playerGuild = guilds.getPlayerGuild(playerUniqueId);
        String playerGuildTag = "";
        if (playerGuild != null) {
            playerGuildTag = playerGuild.getName();
        }
        Location playerLocation = player.getLocation();
        if (EXTRA_PROTECTION && !cuboids.hasRightToThisLocation(player, playerGuildTag, playerLocation)) {
            Inventory inventory = e.getInventory();
            InventoryType type = inventory.getType();
            if (type.equals(InventoryType.PLAYER)) {
                return;
            }
            if (type.equals(InventoryType.CRAFTING)) {
                return;
            }
            String title = e.getView().getTitle();
            if (title.equals(MsgManager.getIgnorePref("gui-items"))) {
                return;
            }
            if (player.hasPermission("openguild.cuboid.bypassinteract")) {
                // I am not sure, but I think there should be an 'if' checking if inventory type is chest/enderchest etc.
                Location block = playerLocation;
                String interactionConsoleMsg = createConsoleInteractionMsg(player, block);
                ogLogger.info(interactionConsoleMsg);
                String interactionMsg = createInteractionMsg(block);
                player.sendMessage(interactionMsg);
            } else {
                player.sendMessage("Nie mozesz tego otworzyc: " + type);
                e.setCancelled(true);
            }
        }
    }

    private String createConsoleInteractionMsg(Player player, Location block) {
        return "Player " + player.getName() + " (" + player.getUniqueId() +
                ") interacted in guilds cuboid at X:" + block.getBlockX() + ", Y:" + block.getBlockY() +
                ", Z:" + block.getBlockZ() + " in world " + block.getWorld().getName() + " (" +
                block.getBlock().getType().name() + ")";
    }

    private String createInteractionMsg(Location block) {
        return MsgManager.get("interinguild")
                .replace("{WORLD}", block.getWorld().getName())
                .replace("{X}", String.valueOf(block.getBlockX()))
                .replace("{Y}", String.valueOf(block.getBlockY()))
                .replace("{Z}", String.valueOf(block.getBlockZ()));
    }


    public void loadSpecialItemsToDestroyEnemyCuboidBlocks() {
        breakingItems = new ArrayList<Material>();
        for (String item : BREAKING_ITEMS) {
            try {
                breakingItems.add(Material.valueOf(item.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                ogLogger.severe("Wystapil blad podczas ladowana itemow do niszczenia blokow na teranie gildii: Nie mozna wczytac " + item.toUpperCase());
            }
        }
    }

}
