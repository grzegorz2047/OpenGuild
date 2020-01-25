package pl.grzegorz2047.openguild;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.utils.ItemGUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryManipulator {


    public boolean hasEnoughItems(Inventory inv, List<ItemStack> requiredItemStacks) {
        for (ItemStack item : requiredItemStacks) {
            if (!inv.containsAtLeast(item, item.getAmount())) {
                return false;
            }
        }
        return true;
    }

    public void removeRequiredItemsForGuild(Inventory inv, List<ItemStack> requiredItemStacks) {
        for (ItemStack item : requiredItemStacks) {
            removeFromInv(inv, item.getType(), item.getDurability(), item.getAmount());
        }
    }

    private static void removeFromInv(Inventory inv, Material mat, int dmgValue, int amount) {
        if (!inv.contains(mat)) {
            return;
        }
        int remaining = amount;
        ItemStack[] contents = inv.getContents();
        for (ItemStack is : contents) {
            if (isEmptySlot(is)) {
                continue;
            }
            if (isDifferentMaterial(mat, is)) {
                continue;
            }

            if (isBroken(dmgValue, is)) {
                continue;
            }
            if (hasMoreThanEnough(remaining, is)) {
                is.setAmount(is.getAmount() - remaining);
                remaining = 0;
            } else if (hasLessOrEquals(remaining, is)) {
                if (isStillNotEnough(remaining)) {
                    remaining -= is.getAmount();
                    is.setType(Material.AIR);
                }
            }
        }
        inv.setContents(contents);
    }

    private static boolean isStillNotEnough(int remaining) {
        return remaining > 0;
    }

    private static boolean isEmptySlot(ItemStack is) {
        return is == null;
    }

    private static boolean isDifferentMaterial(Material mat, ItemStack is) {
        return is.getType() != mat;
    }


    private static boolean isBroken(int dmgValue, ItemStack is) {
        return ((Damageable) is.getItemMeta()).getDamage() != dmgValue && dmgValue > 0;
    }

    private static boolean hasLessOrEquals(int remaining, ItemStack is) {
        return is.getAmount() <= remaining;
    }

    private static boolean hasMoreThanEnough(int remaining, ItemStack is) {
        return is.getAmount() > remaining;
    }


    private ItemStack prepareItemStackWithCustomItemMeta(Inventory inventory, ItemStack item) {
        ItemStack cloned = item.clone();
        ItemMeta meta = cloned.getItemMeta();

        int amount = getAmount(cloned, inventory);

        String desc = "" + amount + "/" + cloned.getAmount();

        if (amount < cloned.getAmount()) {
            meta.setLore(Collections.singletonList(
                    ChatColor.RED + desc
            ));
        } else {
            meta.setLore(Collections.singletonList(
                    ChatColor.GREEN + "" + desc
            ));
        }
        cloned.setItemMeta(meta);
        return cloned;
    }

    private int getRequiredItemsWindowInventorySize(List<ItemStack> requiredItemStacks) {
        int inventorySize = 9;

        if (getRequiredItemsSize(requiredItemStacks) > 9) {
            inventorySize = 18;
        } else if (getRequiredItemsSize(requiredItemStacks) > 18) {
            inventorySize = 27;
        } else if (getRequiredItemsSize(requiredItemStacks) > 27) {
            inventorySize = 36;
        } else if (getRequiredItemsSize(requiredItemStacks) > 36) {
            inventorySize = 45;
        } else if (getRequiredItemsSize(requiredItemStacks) > 45) {
            inventorySize = 54;
        }
        return inventorySize;
    }

    private int getAmount(ItemStack item, Inventory inventory) {
        int amount = 0;
        for (ItemStack i : inventory.getContents()) {
            if (i != null && i.isSimilar(item)) {
                amount += i.getAmount();
            }
        }
        return amount;
    }


    public int getRequiredItemsSize(List<ItemStack> requiredItemStacks) {
        return requiredItemStacks.size();
    }


    public Inventory prepareItemGuildWindowInventory(Inventory inventory, List<ItemStack> requiredItemStacks, Plugin plugin) {
        int inventorySize = getRequiredItemsWindowInventorySize(requiredItemStacks);
        ItemGUI itemsGUI = new ItemGUI(MsgManager.getIgnorePref("gui-items"), inventorySize, plugin);
        for (ItemStack item : requiredItemStacks) {
            ItemStack cloned = prepareItemStackWithCustomItemMeta(inventory, item);

            addItemToInventoryWindowWithClosingWindowAfterClick(itemsGUI, cloned);
        }
        return itemsGUI.getInventory();
    }

    private void addItemToInventoryWindowWithClosingWindowAfterClick(ItemGUI itemsGUI, ItemStack cloned) {
        itemsGUI.addItem(cloned, new ItemGUI.ItemGUIClickEventHandler() {
            @Override
            public void handle(ItemGUI.ItemGUIClickEvent event) {
                event.getPlayer().closeInventory();
            }
        });
    }


}
