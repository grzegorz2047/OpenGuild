/*
 * The MIT License
 *
 * Copyright 2014 Aleksander.
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

package pl.grzegorz2047.openguild2047.commands.arguments;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class ItemsArg {
    
    public static boolean execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.cmdonlyforplayer);
            return true;
        }
        Player player = (Player) sender;
        StringBuilder builder = new StringBuilder();
        for(String item : GenConf.reqitems) {
            String[] info = item.split(":");
            Material mat = Material.valueOf(info[0]);
            int amount = Integer.parseInt(info[1]);
            String nameLower = mat.name().replace("_", " ").toLowerCase();
            String name = " " + nameLower.substring(0, 1).toUpperCase() + nameLower.substring(1);
            
            if(player.getInventory().contains(mat, amount)) {
                builder.append(ChatColor.GREEN);
            } else {
                builder.append(ChatColor.RED);
            }
            builder.append(getAmount(player, mat));
            builder.append("/");
            builder.append(amount);
            builder.append(name);
            builder.append(ChatColor.DARK_GRAY);
            builder.append(", ");
        }
        sender.sendMessage(ChatColor.DARK_GRAY + " ----------------- " + ChatColor.GOLD + "Items" + ChatColor.DARK_GRAY + " ----------------- ");
        sender.sendMessage(builder.toString());
        return true;
    }
    
    private static int getAmount(Player player, Material material) {
        int amount = 0;
        ItemStack[] stack = player.getInventory().getContents();
        for (ItemStack stack1 : stack) {
            if(stack1 == null){
                continue;
            }
            if (stack1.getType() == material) {
                amount = amount + stack1.getAmount();
            }
        }
        return amount;
    }
    
}
