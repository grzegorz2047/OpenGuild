/*
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package pl.grzegorz2047.openguild2047.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.grzegorz2047.openguild2047.GenConf;

/**
 *
 * @author Grzegorz
 */
public class GenUtil {

    public static void removeFromInv(Inventory inv, Material mat, int dmgValue, int amount, byte data) {
        if(inv.contains(mat)) {
            int remaining = amount;
            ItemStack[] contents = inv.getContents();
            for(ItemStack is : contents) {
                if(is != null) {
                    if(is.getType() == mat) {
                        if(data != -1) {
                            if(is.getData() != null) {
                                if(is.getData().getData() == data) {
                                    if(is.getDurability() == dmgValue || dmgValue <= 0) {
                                        if(is.getAmount() > remaining) {
                                            is.setAmount(is.getAmount() - remaining);
                                            remaining = 0;
                                        }
                                        else if(is.getAmount() <= remaining) {
                                            if(remaining > 0) {
                                                remaining -= is.getAmount();
                                                is.setType(Material.AIR);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            if(is.getDurability() == dmgValue || dmgValue <= 0) {
                                if(is.getAmount() > remaining) {
                                    is.setAmount(is.getAmount() - remaining);
                                    remaining = 0;
                                }
                                else if(is.getAmount() <= remaining) {
                                    if(remaining > 0) {
                                        remaining -= is.getAmount();
                                        is.setType(Material.AIR);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            inv.setContents(contents);
        }
    }

    public static boolean hasEnoughItemsForGuild(Inventory inv) {
        for(ItemStack item : GenConf.reqitems) {
            if(!inv.containsAtLeast(item, item.getAmount())) {
                return false;
            }
        }
        
        return true;
    }

    public static void removeRequiredItemsForGuild(Inventory inv) {
        for(ItemStack item : GenConf.reqitems) {
            removeFromInv(inv, item.getType(), item.getDurability(), item.getAmount(), item.getData().getData());
        }
    }

    public static String argsToString(String args[], int minindex, int maxindex) {
        StringBuilder sb = new StringBuilder();
        for(int i = minindex; i < maxindex; i++) {
            sb.append(args[i]);
            sb.append(" ");
        }
        return sb.toString();
    }

    public static boolean isPlayerNearby(Player p, int radius) {//Znaleziony kod
        Location l = p.getLocation();
        for(Player player : p.getLocation().getWorld().getPlayers()) {
            if(p.equals(player)) {
                continue;
            }
            double distance = l.distance(player.getLocation());
            if(distance <= radius) {
                System.out.println(distance);
                return true;
            }
        }
        return false;
    }

}
