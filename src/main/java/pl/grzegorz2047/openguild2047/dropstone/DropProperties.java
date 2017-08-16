package pl.grzegorz2047.openguild2047.dropstone;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class DropProperties {

    private final Material dropType;
    private final int minQuantity;
    private final int maxQuantity;
    private final float chance;
    private final int minHeight;
    private final int maxHeight;
    private final String message;
    private final List<Material> items;
    private final Random r = new Random();
    private final ItemStack cobblestone = new ItemStack(Material.COBBLESTONE, 1);

    public DropProperties(Material dropType, int minQuantity,
                          int maxQuantity,
                          float chance,
                          int minHeight,
                          int maxHeight,
                          String message,
                          List<Material> items) {
        this.dropType = dropType;
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.chance = chance;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.message = message;
        this.items = items;
    }

    private boolean isInGoodHeight(int y) {
        return y >= minHeight && y <= maxHeight;
    }

    private boolean usedProperItemToDestroyBlock(Material itemType) {
        return items.contains(itemType);
    }

    public String getDropMessage() {
        return message;
    }

    private boolean isLucky() {
        return r.nextFloat() <= chance;
    }

    public ItemStack getDrop(int y, Material itemType, Map<Enchantment, Integer> itemEnchantmens) {
        if (itemEnchantmens.containsKey(Enchantment.SILK_TOUCH)) {
            return new ItemStack(Material.STONE, 1);
        }
        if (!isInGoodHeight(y) || !usedProperItemToDestroyBlock(itemType)) {
            return cobblestone.clone();
        }
        if (!isLucky()) {
            return cobblestone.clone();
        }
        int quantity = r.nextInt(maxQuantity) + minQuantity;
        if (quantity > maxQuantity) {
            quantity = maxQuantity;
        }
        return new ItemStack(dropType, quantity);
    }

    public void sendDropMessage(Player player) {
       player.sendMessage(message);
    }
}
