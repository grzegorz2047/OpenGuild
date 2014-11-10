/**
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
import org.bukkit.event.block.Action;
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
import pl.grzegorz2047.openguild2047.cuboidmanagement.CuboidStuff;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class CuboidListeners implements Listener {

    private final OpenGuild plugin;

    public CuboidListeners(OpenGuild plugin) {
        this.plugin = plugin;
    }

    private static List<Material> breakingItems;

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if(e.getPlayer().hasPermission("openguild.cuboid.bypassbreak")) {
            return;
        }
        if(!isAllowed(e.getPlayer(), e.getBlock().getLocation())) {
            if(GenConf.BREAKING_DAMAGE <= 0) {
                e.setCancelled(true); // Damage jest rowny 0; niszczenie blokow jest wylaczone
            } else {
                if(breakingItems.contains(e.getPlayer().getItemInHand().getType())) {
                    e.getPlayer().getItemInHand().setDurability((short) (e.getPlayer().getItemInHand().getDurability() - GenConf.BREAKING_DAMAGE));
                } else {
                    e.getPlayer().sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
                    e.setCancelled(true);
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
        if(e.getPlayer().hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if(!isAllowed(e.getPlayer(), e.getBlock().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBucketTake(PlayerBucketFillEvent e) {
        if(e.getPlayer().hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if(!isAllowed(e.getPlayer(), e.getBlockClicked().getLocation())) {
            e.getPlayer().sendMessage(ChatColor.RED + MsgManager.cantdoitonsomeonearea);
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBucketFlow(PlayerBucketEmptyEvent e) {
        if(e.getPlayer().hasPermission("openguild.cuboid.bypassplace")) {
            return;
        }
        if(!isAllowed(e.getPlayer(), e.getBlockClicked().getLocation())) {
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
            if(GenConf.EXTRA_PROTECTION) {
                if(!isAllowed(e.getPlayer(), e.getClickedBlock().getLocation())){
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
        if(GenConf.EXTRA_PROTECTION && !isAllowed((Player) e.getPlayer(), e.getPlayer().getLocation())) {
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

    private boolean isAllowed(Player player, Location location) {
        if(CuboidStuff.checkIfInAnyCuboid(plugin.getGuildHelper().getCuboids().entrySet().iterator(), location)) {
            if(plugin.getGuildHelper().hasGuild(player.getUniqueId())) {
                String tag = plugin.getGuildHelper().getPlayerGuild(player.getUniqueId()).getTag();
                if(plugin.getGuildHelper().getCuboids().get(tag).isinCuboid(location)) {
                    return true;//Gdzies tu budowanie sojusznikow, ale na razie czarna magia
                }
                else if(!player.hasPermission("openguild.cuboid.bypassplace")) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
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
