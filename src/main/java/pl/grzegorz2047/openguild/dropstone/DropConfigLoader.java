package pl.grzegorz2047.openguild.dropstone;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.grzegorz2047.openguild.managers.MsgManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DropConfigLoader {

    private final FileConfiguration config;

    public DropConfigLoader() {
        String openGuildFolderName = "OpenGuild";
        File file = new File("plugins/" + openGuildFolderName + "/drop.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public List<DropProperties> getLoadedListDropPropertiesFromConfig() {
        List<DropProperties> drops = new ArrayList<>();
        ConfigurationSection materialsDropData = config.getConfigurationSection("drop.materials");
        if (materialsDropData == null) {
            return drops;
        }
        Set<String> materialsConfigData = materialsDropData.getKeys(false);
        for (String materialString : materialsConfigData) {

            try {
                Material type = Material.valueOf(materialString);
                int minQuantity = 1;
                int maxQuantity = 1;

                String messageString = config.getString("drop.materials." + materialString + ".message");
                String message = MsgManager.get("defaultdropmsg").replace("%d", type.name());

                if (messageString != null) {
                    message = messageString;
                }
                message = ChatColor.translateAlternateColorCodes('&', message);
                String quantityString = config.getString("drop.materials." + materialString + ".quantity");
                double chance = config.getDouble("drop.materials." + materialString + ".chance");
                int minHeight = config.getInt("drop.materials." + materialString + ".minHeight");
                int maxHeight = config.getInt("drop.materials." + materialString + ".maxHeight");

                List<String> toolsStrings = config.getStringList("drop.materials." + materialString + ".tool");
                List<Material> tools = new ArrayList<>();
                if (toolsStrings != null) {
                    for (String toolString : toolsStrings) {
                        tools.add(Material.valueOf(toolString));
                    }
                }
                String[] quantitties = quantityString.split("-");
                if (quantitties.length == 2) {
                    minQuantity = Integer.valueOf(quantitties[0]);
                    maxQuantity = Integer.valueOf(quantitties[1]);
                } else if (quantitties.length == 0) {
                    minQuantity = maxQuantity = Integer.valueOf(quantityString);
                }

                DropProperties dropProperties = new DropProperties(type, minQuantity, maxQuantity, chance, minHeight, maxHeight, message, tools);
                drops.add(dropProperties);

            } catch (IllegalArgumentException ex) {
                System.out.println("Drop with name " + materialString + " has incorrect valuse!");
            }
        }
        return drops;
    }


}
