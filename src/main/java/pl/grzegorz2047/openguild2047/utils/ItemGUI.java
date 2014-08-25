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
            int clickedSlot = event.getSlot();
            if(!items.containsKey(clickedSlot) || clickedSlot > inventory.getSize()) {
                return;
            }
            
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            
            ItemGUIClickEvent clickEvent = new ItemGUIClickEvent(player, clickedItem);
            items.get(clickedSlot).handle(clickEvent);
            
            event.setCancelled(true);
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
