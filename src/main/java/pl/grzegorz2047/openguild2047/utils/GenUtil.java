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
package pl.grzegorz2047.openguild2047.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.grzegorz2047.openguild2047.configuration.GenConf;

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
                //System.out.println(distance);
                return true;
            }
        }
        return false;
    }

}
