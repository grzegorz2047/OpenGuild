/*
 * The MIT License
 *
 * Copyright 2014 Adam.
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

package pl.grzegorz2047.openguild2047.commands.guild;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.OpenGuild;
import pl.grzegorz2047.openguild2047.commands.CommandHandler;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.utils.ItemGUI;

/**
 * Command used to see items, which are needed to create a guild.
 * 
 * Usage: /guild items
 */
public class GuildItemsCommand extends CommandHandler {

    public GuildItemsCommand(OpenGuild plugin) {
        super(plugin);
    }

    @Override
    public void executeCommand(CommandSender sender, String[] args) {
        if(GenConf.reqitems == null || GenConf.reqitems.isEmpty()) {
            sender.sendMessage(MsgManager.get("reqitemsoff"));
            return;
        }
        
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        
        Player player = (Player) sender;
        
        if(GenConf.reqitems.size() > 0) {
            int inventorySize = 9;
            
            if(GenConf.reqitems.size() > 9) {
                inventorySize = 18;
            }
            else if(GenConf.reqitems.size() > 18) {
                inventorySize = 27;
            }
            else if(GenConf.reqitems.size() > 27) {
                inventorySize = 36;
            }
            else if(GenConf.reqitems.size() > 36) {
                inventorySize = 45;
            }
            else if(GenConf.reqitems.size() > 45) {
                inventorySize = 54;
            }
            
            ItemGUI itemsGUI = new ItemGUI(MsgManager.getIgnorePref("gui-items"), inventorySize);
            for(ItemStack item : GenConf.reqitems) {
                ItemStack cloned = item.clone();
                ItemMeta meta = cloned.getItemMeta();
                
                int amount = getAmount(player, cloned);
                
                if(amount < cloned.getAmount()) {
                    meta.setLore(Arrays.asList(
                        ChatColor.RED + "" + amount + "/" + cloned.getAmount()
                    ));
                } else {
                    meta.setLore(Arrays.asList(
                        ChatColor.GREEN + "" + amount + "/" + cloned.getAmount()
                    ));
                }
                cloned.setItemMeta(meta);
                
                itemsGUI.addItem(cloned, new ItemGUI.ItemGUIClickEventHandler() {
                    @Override
                    public void handle(ItemGUI.ItemGUIClickEvent event) {
                        event.getPlayer().closeInventory();
                    }
                });
            }
            player.openInventory(itemsGUI.getInventory());
        }
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }
    
    private int getAmount(Player player, ItemStack item) {
        int amount = 0;
        
        for(ItemStack i : player.getInventory().getContents()) {
            if(i != null && i.isSimilar(item)) {
                amount += i.getAmount();
            }
        }
        
        return amount;
    }

}
