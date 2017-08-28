package pl.grzegorz2047.openguild.dropstone;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class DropProperties {

    private final Material dropType;
    private final int minQuantity;
    private final int maxQuantity;
    private final double chance;
    private final int minHeight;
    private final int maxHeight;
    private String message;
    private final List<Material> items;
    private final Random r = new Random();

    public DropProperties(Material dropType, int minQuantity,
                          int maxQuantity,
                          double chance,
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

    public boolean isInGoodHeight(int y) {
        return y >= minHeight && y <= maxHeight;
    }

    public boolean usedProperItemToDestroyBlock(Material itemType) {
        return items.contains(itemType) || items.size() == 0;
    }

 
    public boolean isLucky(Map<Enchantment, Integer> itemEnchantmens) {
        if (itemEnchantmens.containsKey(Enchantment.LUCK)) {
            Integer level = itemEnchantmens.get(Enchantment.LUCK);
            return r.nextDouble() <= chance * level;
        }
        return r.nextDouble() <= chance;
    }

    public ItemStack getDrop() {
        int quantity = r.nextInt(maxQuantity) + minQuantity;
        if (quantity > maxQuantity) {
            quantity = maxQuantity;
        }
        return new ItemStack(dropType, quantity);
    }

    public String getDropMessage(int quantity) {
        return message.replace("%q", String.valueOf(quantity));
    }


    @Override
    public String toString() {
        return "[" +
                "dropType=" + dropType.name() +
                ", minQuantity=" + minQuantity +
                ", maxQuantity=" + maxQuantity +
                ", minHeight=" + minHeight +
                ", maxHeight=" + maxHeight +
                ", chance=" + chance +
                ", message=" + message +
                ", items=" + items.toString() +
                "]";
    }
}
