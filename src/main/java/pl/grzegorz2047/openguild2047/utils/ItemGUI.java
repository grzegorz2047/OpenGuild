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

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pl.grzegorz2047.openguild2047.OpenGuild;

public class ItemGUI implements Listener {

    private OpenGuild plugin;
    private Inventory inventory;
    
    private Map<Integer, ItemGUIClickEventHandler> items = new HashMap<Integer, ItemGUIClickEventHandler>();
    
    private int nextPosition = -1;
    
    public ItemGUI(String title, int size) {
        this.plugin = OpenGuild.getInstance();
        this.inventory = this.plugin.getServer().createInventory(null, size, title);
        
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }
    
    public void addItem(ItemStack item, ItemGUIClickEventHandler clickHandler) {
        nextPosition++;
        items.put(nextPosition, clickHandler);
        this.inventory.setItem(nextPosition, item);
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if(event.getInventory().equals(this.inventory)) {
            Player player = (Player) event.getWhoClicked();
           /* int clickedSlot = event.getSlot();
            if(!items.containsKey(clickedSlot) || clickedSlot > inventory.getSize()) {
                return;
            }
            
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            
            ItemGUIClickEvent clickEvent = new ItemGUIClickEvent(player, clickedItem);
            items.get(clickedSlot).handle(clickEvent);
            */
            event.setCancelled(true);
            player.closeInventory();
        }
    }
    
    @EventHandler
    public void handle(InventoryCloseEvent event) {
        if(event.getInventory().equals(this.inventory)) {
            HandlerList.unregisterAll(this);
        }
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
    public interface ItemGUIClickEventHandler {
        public void handle(ItemGUIClickEvent event);
    }
    
    public class ItemGUIClickEvent {
        private Player player;
        private ItemStack item;
        
        public ItemGUIClickEvent(Player player, ItemStack item) {
            this.player = player;
            this.item = item;
        }

        public Player getPlayer() {
            return player;
        }

        public ItemStack getItem() {
            return item;
        }
    }
}
