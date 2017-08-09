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

import java.util.ArrayList;
import java.util.List;

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
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.api.Guilds;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.modules.spawn.SpawnChecker;

public class CuboidAndSpawnManipulationListeners implements Listener {

    private final OpenGuild plugin;

    public CuboidAndSpawnManipulationListeners(OpenGuild plugin) {
        this.plugin = plugin;
    }

    private static List<Material> breakingItems;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(SpawnChecker.isSpawn(e.getBlock().getLocation()) && !e.getPlayer().hasPermission("openguild.spawn.bypass")){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.get("CantDoItOnSpawn"));
            return;
        }
        if(e.getPlayer().hasPermission("openguild.cuboid.bypassbreak")) {
            return;
        }
        if(!plugin.getCuboids().allowedToDoItHere(e.getPlayer(), e.getBlock().getLocation())) {
            if(GenConf.BREAKING_DAMAGE <= 0) {
                e.setCancelled(true); // Damage jest rowny 0; niszczenie blokow jest wylaczone
            } else {
                if(breakingItems.contains(e.getPlayer().getItemInHand().getType())) {
                    e.getPlayer().getItemInHand().setDurability((short) (e.getPlayer().getItemInHand().getDurability() - GenConf.BREAKING_DAMAGE));
                } else {
                    e.getPlayer().sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
                    e.setCancelled(true);
                    return;
                }
            }
        }

    }

    @EventHandler
    public void onBlockPiStonExtend(BlockPistonExtendEvent e) {
        for(Block block : e.getBlocks()) {
            if(block.getType() == Material.DRAGON_EGG) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(SpawnChecker.isSpawn(e.getBlock().getLocation()) && !e.getPlayer().hasPermission("openguild.spawn.bypass")){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.get("CantDoItOnSpawn"));
            return;
        }
        if(e.getPlayer().hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if(!plugin.getCuboids().allowedToDoItHere(e.getPlayer(), e.getBlock().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBucketTake(PlayerBucketFillEvent e) {

        if(SpawnChecker.isSpawn(e.getBlockClicked().getLocation()) && !e.getPlayer().hasPermission("openguild.spawn.bypass")){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.get("CantDoItOnSpawn"));
            return;
        }
        if(e.getPlayer().hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if(!plugin.getCuboids().allowedToDoItHere(e.getPlayer(), e.getBlockClicked().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBucketFlow(PlayerBucketEmptyEvent e) {
        if(SpawnChecker.isSpawn(e.getBlockClicked().getLocation()) && !e.getPlayer().hasPermission("openguild.spawn.bypass")){
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.get("CantDoItOnSpawn"));
            return;
        }
        if(e.getPlayer().hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if(!plugin.getCuboids().allowedToDoItHere(e.getPlayer(), e.getBlockClicked().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if(e.getEntityType() == EntityType.PRIMED_TNT) {
            for(Block block : e.blockList()) {
                if(block.getType() == Material.DRAGON_EGG) {
                    e.blockList().remove(block);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getClickedBlock() != null) {
            if(SpawnChecker.isSpawn(e.getClickedBlock().getLocation()) && !e.getPlayer().hasPermission("openguild.spawn.bypass")){
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.RED + MsgManager.get("CantDoItOnSpawn"));
                return;
            }
            if(GenConf.EXTRA_PROTECTION) {
                if(!plugin.getCuboids().allowedToDoItHere(e.getPlayer(), e.getClickedBlock().getLocation())){
                    if(e.getPlayer().hasPermission("openguild.cuboid.bypassinteract")) {
                        Location block = e.getPlayer().getLocation();
                        Guilds.getLogger().info("Player " + e.getPlayer().getName() + " (" + e.getPlayer().getUniqueId() +
                                ") interacted in guilds cuboid at X:" + block.getBlockX() + ", Y:" + block.getBlockY() +
                                ", Z:" + block.getBlockZ() + " in world " + block.getWorld().getName() + " (" +
                                block.getBlock().getType().name() + ")");
                        e.getPlayer().sendMessage(MsgManager.get("interinguild")
                                .replace("{WORLD}", block.getWorld().getName())
                                .replace("{X}", String.valueOf(block.getBlockX()))
                                .replace("{Y}", String.valueOf(block.getBlockY()))
                                .replace("{Z}", String.valueOf(block.getBlockZ())));
                        return;
                    }else{
                        e.getPlayer().sendMessage(MsgManager.get("cantdoitonsomeonearea"));
                        e.setCancelled(true);
                        return;
                    }  
                } 
            }
        }
    }
    
    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if(GenConf.EXTRA_PROTECTION && !plugin.getCuboids().allowedToDoItHere((Player) e.getPlayer(), e.getPlayer().getLocation())) {
            if(e.getInventory().getType().equals(InventoryType.PLAYER)){
               return; 
            }
            if(e.getInventory().getType().equals(InventoryType.CRAFTING)){
               return; 
            }
            if(e.getInventory().getTitle().equals(MsgManager.getIgnorePref("gui-items"))){
               return; 
            }
            if(e.getPlayer().hasPermission("openguild.cuboid.bypassinteract")) {
                // I am not sure, but I think there should be an 'if' checking if inventory type is chest/enderchest etc.
                
                Location block = e.getPlayer().getLocation();
                Guilds.getLogger().info("Player " + e.getPlayer().getName() + " (" + e.getPlayer().getUniqueId() +
                        ") interacted in guilds cuboid at X:" + block.getBlockX() + ", Y:" + block.getBlockY() +
                        ", Z:" + block.getBlockZ() + " in world " + block.getWorld().getName() + " (" +
                        block.getBlock().getType().name() + ")");
                ((Player) e.getPlayer()).sendMessage(MsgManager.get("interinguild")
                        .replace("{WORLD}", block.getWorld().getName())
                        .replace("{X}", String.valueOf(block.getBlockX()))
                        .replace("{Y}", String.valueOf(block.getBlockY()))
                        .replace("{Z}", String.valueOf(block.getBlockZ())));
            } else {
                if(e.getPlayer() instanceof Player){
                    Player p = (Player) e.getPlayer();
                    p.sendMessage("Nie mozesz tego otworzyc: "+e.getInventory().getType());
                }
                e.setCancelled(true);
            }
        }
    }


    public static void loadItems() {
        breakingItems = new ArrayList<Material>();
        for(String item : GenConf.BREAKING_ITEMS) {
            try {
                breakingItems.add(Material.valueOf(item.toUpperCase()));
            } catch(IllegalArgumentException ex) {
                Guilds.getLogger().severe("Wystapil blad podczas ladowana itemow do niszczenia blokow na teranie gildii: Nie mozna wczytac " + item.toUpperCase());
            }
        }
    }

}
