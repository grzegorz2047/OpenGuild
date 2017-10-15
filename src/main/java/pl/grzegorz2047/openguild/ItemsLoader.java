package pl.grzegorz2047.openguild;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemsLoader {

    public ArrayList<ItemStack> loadItems(List<String> requiredItemsString) {
        ArrayList<ItemStack> requiredItemStacks = new ArrayList<>();
        if (requiredItemsString == null) {
            return requiredItemStacks;
        }
        if (requiredItemsString.isEmpty()) {
            return requiredItemStacks;
        }
        if (requiredItemsString.size() > 54) {
            OpenGuild.getOGLogger().warning("Too many specified items (required-items)! Maximum size is 54!");
        } else {
            for (String s : requiredItemsString) {
                String[] info = s.split(":");
                if (info.length != 4) {
                    OpenGuild.getOGLogger().warning("Oops! It looks like you're using an old configuration file!/You have made mistake with required-items section! We changed pattern of required-items section. Now it looks like this: Material:Durability:Data:Amount (old was: Material:Amount) - please update your config.yml Exact line is " + s);
                    break;
                }
                Material material;
                try {
                    material = Material.valueOf(info[0]);
                } catch (IllegalArgumentException ex) {
                    OpenGuild.getOGLogger().warning("Invalid material: " + info[0] + "! Check your configuration file!");
                    continue;
                }

                short durability = 0;
                try {
                    durability = Short.valueOf(info[1]);
                } catch (NumberFormatException e) {
                    OpenGuild.getOGLogger().warning("Durability must be a number! Please fix 'required-items' section in your config.yml");
                }

                byte data = getItemData(info[2]);

                for (ItemStack i : requiredItemStacks) {
                    if (i.getType().equals(material) && data == i.getData().getData()) {
                        OpenGuild.getOGLogger().warning("Duplicate item found! Skipping ...");
                    }
                }

                int amount = 1;
                try {
                    amount = Integer.valueOf(info[3]);

                    if (amount > 64) {
                        amount = 64;
                    } else if (amount < 0) {
                        continue;
                    }
                } catch (NumberFormatException e) {
                    OpenGuild.getOGLogger().warning("Amount must be a number! Please fix 'required-items' section in your config.yml");
                }

                ItemStack item = new ItemStack(material, amount, durability, data);
                requiredItemStacks.add(item);
            }
        }
        return requiredItemStacks;
    }

    private byte getItemData(String dataPart) {
        byte data = 0;
        try {
            data = Byte.valueOf(dataPart);
        } catch (NumberFormatException e) {
            OpenGuild.getOGLogger().warning("Data must be a number! Please fix 'required-items' section in your config.yml");
        }
        return data;
    }


}
