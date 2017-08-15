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
package pl.grzegorz2047.openguild2047.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
import org.bukkit.inventory.PlayerInventory;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.modules.spawn.SpawnChecker;

import java.util.ArrayList;
import java.util.List;

public class CuboidAndSpawnManipulationListeners implements Listener {


    private final Cuboids cuboids;

    public CuboidAndSpawnManipulationListeners(Cuboids cuboids) {
        this.cuboids = cuboids;
    }

    private static List<Material> breakingItems;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        if (SpawnChecker.isSpawn(e.getBlock().getLocation()) && !player.hasPermission("openguild.spawn.bypass")) {
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return;
        }
        if (player.hasPermission("openguild.cuboid.bypassbreak")) {
            return;
        }
        if (cuboids.allowedToDoItHere(player, e.getBlock().getLocation())) {
            return;
        }
        if (!canbreakEnemyBlock()) {
            e.setCancelled(true); // Damage jest rowny 0; niszczenie blokow jest wylaczone
            return;
        }
        PlayerInventory inventory = player.getInventory();
         if (!hasSpecialBreakingitem(inventory)) {
            player.sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
            e.setCancelled(true);
            return;
        }
        inventory.getItemInMainHand().setDurability((short) (inventory.getItemInMainHand().getDurability() + GenConf.BREAKING_DAMAGE));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            Player player = e.getPlayer();
            if (SpawnChecker.isSpawn(e.getClickedBlock().getLocation()) && !player.hasPermission("openguild.spawn.bypass")) {
                e.setCancelled(true);
                player.sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
                return;
            }
            if (GenConf.EXTRA_PROTECTION) {
                if (cuboids.allowedToDoItHere(player, e.getClickedBlock().getLocation())) {
                    return;
                }
                if (player.hasPermission("openguild.cuboid.bypassinteract")) {
                    Location block = player.getLocation();
                    String interactionConsoleMsg = createConsoleInteractionMsg(player, block);
                    Guilds.getLogger().info(interactionConsoleMsg);
                    String interactionMsg = createInteractionMsg(block);
                    player.sendMessage(interactionMsg);
                } else {
                    if (!canbreakEnemyBlock()) {
                        e.setCancelled(true); // Damage jest rowny 0; niszczenie blokow jest wylaczone
                        player.sendMessage(MsgManager.get("cantdoitonsomeonearea"));
                        return;
                    }
                    if(!hasSpecialBreakingitem(player.getInventory())) {
                        player.sendMessage(MsgManager.get("cantdoitonsomeonearea"));
                    }
                }
            }
        }

    }

    private boolean hasSpecialBreakingitem(PlayerInventory inventory) {
        return breakingItems.contains(inventory.getItemInMainHand().getType());
    }

    private boolean canbreakEnemyBlock() {
        return GenConf.BREAKING_DAMAGE > 0;
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
        if (GenConf.PREVENT_GHOST_BLOCK_PLACE) {
            if (e.isCancelled()) {
                if (e.getBlock().getType().equals(Material.AIR)) {
                    e.getBlockPlaced().setType(Material.AIR);
                }
            }
        }
        if (SpawnChecker.isSpawn(e.getBlock().getLocation()) && !e.getPlayer().hasPermission("openguild.spawn.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return;
        }
        if (e.getPlayer().hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if (!cuboids.allowedToDoItHere(e.getPlayer(), e.getBlock().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketTake(PlayerBucketFillEvent e) {

        if (SpawnChecker.isSpawn(e.getBlockClicked().getLocation()) && !e.getPlayer().hasPermission("openguild.spawn.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return;
        }
        if (e.getPlayer().hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if (!cuboids.allowedToDoItHere(e.getPlayer(), e.getBlockClicked().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBucketFlow(PlayerBucketEmptyEvent e) {
        if (SpawnChecker.isSpawn(e.getBlockClicked().getLocation()) && !e.getPlayer().hasPermission("openguild.spawn.bypass")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.get("cantdoitonspawn"));
            return;
        }
        if (e.getPlayer().hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if (!cuboids.allowedToDoItHere(e.getPlayer(), e.getBlockClicked().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntityType() == EntityType.PRIMED_TNT) {
            for (Block block : e.blockList()) {
                if (block.getType() == Material.DRAGON_EGG) {
                    e.blockList().remove(block);
                }
            }
        }
    }


    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        Player player = (Player) e.getPlayer();
        if (GenConf.EXTRA_PROTECTION && !cuboids.allowedToDoItHere(player, player.getLocation())) {
            if (e.getInventory().getType().equals(InventoryType.PLAYER)) {
                return;
            }
            if (e.getInventory().getType().equals(InventoryType.CRAFTING)) {
                return;
            }
            if (e.getInventory().getTitle().equals(MsgManager.getIgnorePref("gui-items"))) {
                return;
            }
            if (player.hasPermission("openguild.cuboid.bypassinteract")) {
                // I am not sure, but I think there should be an 'if' checking if inventory type is chest/enderchest etc.

                Location block = player.getLocation();
                String interactionConsoleMsg = createConsoleInteractionMsg(player, block);
                Guilds.getLogger().info(interactionConsoleMsg);
                String interactionMsg = createInteractionMsg(block);
                player.sendMessage(interactionMsg);
            } else {
                player.sendMessage("Nie mozesz tego otworzyc: " + e.getInventory().getType());
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


    public static void loadItems() {
        breakingItems = new ArrayList<Material>();
        for (String item : GenConf.BREAKING_ITEMS) {
            try {
                breakingItems.add(Material.valueOf(item.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                Guilds.getLogger().severe("Wystapil blad podczas ladowana itemow do niszczenia blokow na teranie gildii: Nie mozna wczytac " + item.toUpperCase());
            }
        }
    }

}
