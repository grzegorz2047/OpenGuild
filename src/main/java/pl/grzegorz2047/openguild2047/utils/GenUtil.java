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

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.api.Guilds;

/**
 *
 * @author Grzegorz
 */
public class GenUtil {
    
    public static void removeFromInv(Inventory inv, Material mat, int dmgValue, int amount){
        if(inv.contains(mat)){
            int remaining = amount;
            ItemStack[] contents = inv.getContents();
            for (ItemStack is : contents){
                if(is != null){
                    if(is.getType() == mat){
                     
                        if(is.getDurability() == dmgValue || dmgValue <= 0){
                            if(is.getAmount() > remaining){
                                is.setAmount(is.getAmount() - remaining);
                                remaining = 0;
                            }
                            else if(is.getAmount() <= remaining){
                                if (remaining > 0){
                                    remaining -= is.getAmount();
                                    is.setType(Material.AIR);
                                }
                            }
                        }
                    }
                }
            }
            inv.setContents(contents);
        }
    }
    
    public static boolean hasEnoughItemsForGuild(Inventory inv){
        for(String linia:GenConf.reqitems){
            String[] splits = linia.split(":");
            try{
                Material material = Material.getMaterial(splits[0]);
                if(material == null){
                    material = Material.getMaterial(splits[0]);
                    if(material == null){
                        Guilds.getLogger().severe("Material "+splits[0]+"w ilosci "+splits[1]+" jest niepoprawny");
                    }
                } 
                int amount = Integer.parseInt(splits[1]);
                if(amount == 0){
                    Guilds.getLogger().severe("Material "+splits[0]+"w ilosci "+splits[1]+" ma niepoprawna ilosc");
                }
                //System.out.println(" "+BlockName);
                if(splits.length==2){
                    if(!inv.contains(material, amount)){
                        return false;
                    }
                }
            }
            catch(Exception ex){
                System.out.println("Config losowych blokow jest niepoprawny. Cos tam "+splits[0]+" "+splits[1]+"! Ignoruje!");
            }
        }
        return true;
    }
    
    public static void removeRequiredItemsForGuild(Inventory inv){
        for(String linia:GenConf.reqitems){
            String[] splits = linia.split(":");
            try{
                Material material = Material.getMaterial(splits[0]);
                if(material == null){
                    material = Material.getMaterial(splits[0]);
                    if(material == null){
                        Guilds.getLogger().severe("Material "+splits[0]+"w ilosci "+splits[1]+" jest niepoprawny");
                        continue;
                    }
                } 
                int amount = Integer.parseInt(splits[1]);
                if(amount == 0){
                    Guilds.getLogger().severe("Material "+splits[0]+"w ilosci "+splits[1]+" ma niepoprawna ilosc");
                    continue;
                }
                GenUtil.removeFromInv(inv, material, 0, amount);
                //System.out.println(" "+BlockName);
            }
            catch(Exception ex){
                System.out.println("Config losowych blokow jest niepoprawny. W tym miejscu "+splits[0]+":"+splits[1]+"! Ignoruje!");
                
            }
        }
    }
    
}
