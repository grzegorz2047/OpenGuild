package pl.grzegorz2047.openguild;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemsLoader {

    private final OGLogger ogLogger = OpenGuild.getOGLogger();

    public ArrayList<ItemStack> loadItems(List<Map<?, ?>> requiredItemsString) {
        ArrayList<ItemStack> requiredItemStacks = new ArrayList<>();
        if (requiredItemsString == null) {
            ogLogger.warning("No specified guild create items (no section found: required-items)!");
            return requiredItemStacks;
        }
        if (requiredItemsString.isEmpty()) {
            ogLogger.warning("No specified guild create items (required-items)!");
            return requiredItemStacks;
        }
        if (requiredItemsString.size() > 54) {
            ogLogger.warning("Too many specified items (required-items)! Maximum size is 54!");
            return requiredItemStacks;
        }
        for (Map<?, ?> s : requiredItemsString) {
            Optional<ItemStack> parsedItem = getParsedItem((Map<String, Object>) s);
            if (!parsedItem.isPresent()) {
                ogLogger.warning("Couldnt parse required guild creation item");
                continue;
            }
            ItemStack item = parsedItem.get();
            for (ItemStack alreadyLoadedItem : requiredItemStacks) {
                if (alreadyLoadedItem.isSimilar(item)) {
                    ogLogger.warning("Duplicate item found! Skipping ...");
                }
            }
            requiredItemStacks.add(item);
        }
        return requiredItemStacks;
    }

    private Optional<ItemStack> getParsedItem(Map<String, Object> itemInfo) {
        Material material;
        short durability = 0;
        int amount;

        String type = String.valueOf(itemInfo.get("type"));
        try {
            material = Material.valueOf(type);
        } catch (IllegalArgumentException ex) {
            ogLogger.warning("Invalid material: " + type + "! Check your configuration file!");
            return Optional.empty();
        }

        try {
            durability = Short.parseShort(String.valueOf(itemInfo.get("damage")));
        } catch (NumberFormatException e) {
            ogLogger.warning("Durability for " + type + " invalid or not specifed in 'required-items' section in your config.yml");
        }
        try {
            amount = Integer.parseInt(String.valueOf(itemInfo.get("amount")));

            if (amount > 64) {
                amount = 64;
            } else if (amount < 0) {
                return Optional.empty();
            }
        } catch (NumberFormatException e) {
            ogLogger.warning("Amount must be a number! Please fix 'required-items' section in your config.yml");
            return Optional.empty();
        }
        ItemStack item = new ItemStack(material, amount);
        ItemMeta itemMeta = item.getItemMeta();
        ((Damageable) itemMeta).setDamage(durability);
        return Optional.of(item);
    }


}
