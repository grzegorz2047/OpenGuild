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

package pl.grzegorz2047.openguild.commands.guild;

import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.grzegorz2047.openguild.configuration.GenConf;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.utils.ItemGUI;

/**
 * Command used to see items, which are needed to create a guild.
 * 
 * Usage: /guild items
 */
public class GuildItemsCommand extends Command {
    private final Plugin plugin;

    public GuildItemsCommand(Plugin plugin) {
        setPermission("openguild.command.items");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
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
            
            ItemGUI itemsGUI = new ItemGUI(MsgManager.getIgnorePref("gui-items"), inventorySize, plugin);
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
    public int minArgs() {
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
