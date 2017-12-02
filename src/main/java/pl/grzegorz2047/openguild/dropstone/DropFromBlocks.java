package pl.grzegorz2047.openguild.dropstone;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import pl.grzegorz2047.openguild.managers.MsgManager;

import java.util.*;

public class DropFromBlocks {
    private final static Random r = new Random();
    private boolean NOTIFY_NO_DROP_FROM_THIS_TYPE_OF_BLOCK;
    private boolean DROP_ENABLED;
    private boolean DROP_TO_EQ;
    private List<Material> eligibleBlocks = new ArrayList<>();
    private List<Material> ores =
            Arrays.asList(
                    Material.IRON_ORE,
                    Material.GOLD_ORE,
                    Material.DIAMOND_ORE,
                    Material.EMERALD_ORE,
                    Material.LAPIS_ORE,
                    Material.REDSTONE_ORE,
                    Material.GLOWING_REDSTONE_ORE,
                    Material.STONE
            );
    private List<DropProperties> loadedDrops = new ArrayList<>();


    public void loadMainDropData(List<String> blockList, List<DropProperties> loadedDrops, FileConfiguration config) {
        this.loadedDrops = loadedDrops;
        NOTIFY_NO_DROP_FROM_THIS_TYPE_OF_BLOCK = config.getBoolean("drop.notify-cant-drop-from-not-eligible-block", false);
        DROP_ENABLED = config.getBoolean("drop.enabled", false);
        DROP_TO_EQ = config.getBoolean("drop.drop-to-eq", false);

        if (loadedDrops.size() == 0) {
            System.out.println("Drop config is not configured! Turning drop off!");
            DROP_ENABLED = false;
        }
        this.eligibleBlocks = new ArrayList<>();
        for (String dropMat : blockList) {
            try {
                this.eligibleBlocks.add(Material.valueOf(dropMat));
            } catch (IllegalArgumentException ex) {
                System.out.println("Incorrect drop block " + dropMat + ". Check Material bukkit google it!");
            }

        }
    }

    private boolean isEligible(Material type) {
        return eligibleBlocks.contains(type);
    }

    private DropProperties getDropRandomProperties() {
        int dropIndex = r.nextInt(loadedDrops.size());
        //System.out.println("Wylosowany drop to " + dropIndex + " z size " + loadedDrops.size());
        return loadedDrops.get(dropIndex);
    }

    public void processDropFromBlocks(Player player, Block brokenBlock) {
        if (!player.getGameMode().equals(GameMode.SURVIVAL)) {
            return;
        }
        Material brokenBlockType = brokenBlock.getType();
        if (isNotUsedInDropFromBlocks(brokenBlockType)) {
            return;
        }
        Location blockLocation = brokenBlock.getLocation();
        int blockHeight = blockLocation.getBlockY();
        PlayerInventory playerInventory = player.getInventory();
        ItemStack itemInHand = playerInventory.getItemInMainHand();
        Material itemInHandType = itemInHand.getType();
        Map<Enchantment, Integer> itemEnchantmens = itemInHand.getEnchantments();
        if (!isEligible(brokenBlockType)) {
            if (NOTIFY_NO_DROP_FROM_THIS_TYPE_OF_BLOCK) {
                player.sendMessage(MsgManager.get("notifynodropfromthistypeofblock"));
            }
        }
        World playerWorld = player.getWorld();
        DropProperties dropProperties = getDropRandomProperties();
        ItemStack receivedDrop = dropProperties.getDrop();
        if (itemEnchantmens.containsKey(Enchantment.SILK_TOUCH)) {
            receivedDrop = new ItemStack(receivedDrop.getType(), 1);
        }
        // System.out.println(dropProperties.toString());
        if (!dropProperties.isInGoodHeight(blockHeight) || !dropProperties.usedProperItemToDestroyBlock(itemInHandType)) {
            receivedDrop = new ItemStack(Material.COBBLESTONE, 1);
            //System.out.println("zla wysokosc lub item");
        }
        if (!dropProperties.isLucky(itemEnchantmens)) {
            receivedDrop = new ItemStack(Material.COBBLESTONE, 1);
            //System.out.println("So unlucky");
        }

        Material receivedDropType = receivedDrop.getType();
        if (DROP_TO_EQ) {
            int firstFreeSlot = playerInventory.firstEmpty();
            if (hasDroppedBlockTypeInInventory(playerInventory, receivedDropType)) {
                boolean canAddToItemStack = false;
                for (ItemStack itInEq : playerInventory.getContents()) {
                    if (itInEq == null) {
                        continue;
                    }
                    Material itInEqType = itInEq.getType();
                    if (!isTheSameType(brokenBlockType, itInEqType)) {
                        continue;
                    }
                    int amount = itInEq.getAmount();
                    int maxStackSize = playerInventory.getMaxStackSize();
                    if (stackDoesntHaveEnoughFreeSpace(receivedDrop, amount, maxStackSize)) {
                        continue;
                    }
                    canAddToItemStack = true;
                    break;
                }
                if (canAddToItemStack) {
                    processDropToEq(player, playerInventory, dropProperties, receivedDrop, brokenBlock);
                    return;
                }
                if (hasFreeSlot(firstFreeSlot)) {
                    processDropToEq(player, playerInventory, dropProperties, receivedDrop, brokenBlock);
                }
            } else if (hasFreeSlot(firstFreeSlot)) {
                processDropToEq(player, playerInventory, dropProperties, receivedDrop, brokenBlock);
            } else {

                processDropNaturally(player, blockLocation, playerWorld, dropProperties, receivedDrop, brokenBlock);
            }
        } else {
            processDropNaturally(player, blockLocation, playerWorld, dropProperties, receivedDrop, brokenBlock);
        }
    }

    private boolean hasDroppedBlockTypeInInventory(PlayerInventory playerInventory, Material receivedDropType) {
        return playerInventory.contains(receivedDropType);
    }

    private boolean isTheSameType(Material brokenBlockType, Material itInEqType) {
        return itInEqType.equals(brokenBlockType);
    }

    private boolean stackDoesntHaveEnoughFreeSpace(ItemStack receivedDrop, int amount, int maxStackSize) {
        return maxStackSize - amount < receivedDrop.getAmount();
    }

    private boolean hasFreeSlot(int firstFreeSlot) {
        return firstFreeSlot != -1;
    }

    private void processDropToEq(Player player, PlayerInventory playerInventory, DropProperties dropProperties, ItemStack receivedDrop, Block b) {
        addItemToEq(playerInventory, receivedDrop);
        if (!receivedDrop.getType().equals(Material.COBBLESTONE)) {
            player.sendMessage(dropProperties.getDropMessage(receivedDrop.getAmount()));
        }
    }

    private void processDropNaturally(Player player, Location blockLocation, World playerWorld, DropProperties dropProperties, ItemStack receivedDrop, Block b) {
        dropNaturally(blockLocation, playerWorld, receivedDrop);
        if (!b.getType().equals(Material.COBBLESTONE)) {
            player.sendMessage(dropProperties.getDropMessage(receivedDrop.getAmount()));
        }
    }

    public boolean isNotUsedInDropFromBlocks(Material type) {
        //OpenGuild.getOGLogger().IS_IN_DEBUG_MODE("Zwraca " + (!ores.contains(type) && !eligibleBlocks.contains(type)) + "dla " + type);
        return !ores.contains(type) && !eligibleBlocks.contains(type);
    }

    private void addItemToEq(PlayerInventory playerInventory, ItemStack receivedDrop) {
        playerInventory.addItem(receivedDrop);
    }

    private void dropNaturally(Location blockLocation, World playerWorld, ItemStack receivedDrop) {
        //OpenGuild.getOGLogger().IS_IN_DEBUG_MODE("DROP: " + receivedDrop.toString());
        playerWorld.dropItemNaturally(blockLocation, receivedDrop);

    }
}
