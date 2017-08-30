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
package pl.grzegorz2047.openguild.guilds;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.configuration.GenConf;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.metada.PlayerMetadataController;
import pl.grzegorz2047.openguild.metada.PlayerMetadataController.PlayerMetaDataColumn;
import pl.grzegorz2047.openguild.relations.Relation;
import pl.grzegorz2047.openguild.utils.ItemGUI;

import java.util.*;

public class Guilds {

    private final GuildInvitations guildInvitations;
    private final Plugin plugin;
    private final Cuboids cuboids;
    private final String playerTemplateNameLabel = "{PLAYER}";
    private final PlayerMetadataController playerMetadataController;
    private Map<String, Guild> guilds = new HashMap<>();
    private List<String> onlineGuilds = new ArrayList<>();
    private ArrayList<ItemStack> requiredItemStacks;


    public Guilds(final SQLHandler sqlHandler, Plugin plugin, Cuboids cuboids) {
        this.cuboids = cuboids;
        this.plugin = plugin;
        this.guildInvitations = new GuildInvitations(sqlHandler, this);
        playerMetadataController = new PlayerMetadataController(plugin);
    }


    public void notifyMembersAboutSomeoneEnteringTheirCuboid(Player player, String guildscuboidtag, Guild enemy) {
        Guild guild = getGuild(guildscuboidtag);
        for (UUID mem : guild.getMembers()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(mem);
            if (op.isOnline()) {
                if (enemy != null) {
                    notifySomeoneEnteredCuboid(op, player, enemy);
                } else {
                    notifySomeoneEnteredCuboid(op, player);
                }
                playSoundOnSomeoneEnteredCuboid(op);
            }
        }
    }

    private void playSoundOnSomeoneEnteredCuboid(OfflinePlayer op) {
        if (GenConf.CUBOID_ENTER_SOUND_ENABLED) {
            op.getPlayer().playSound(op.getPlayer().getLocation(), GenConf.CUBOID_ENTER_SOUND_TYPE, 10f, 5f);
        }
    }

    private void notifySomeoneEnteredCuboid(OfflinePlayer op, Player player) {
        op.getPlayer().sendMessage(
                MsgManager.get("entercubmemsnoguild")
                        .replace(playerTemplateNameLabel, player.getName()));
    }

    private void notifySomeoneEnteredCuboid(OfflinePlayer op, Player player, Guild enemy) {
        op.getPlayer().sendMessage(
                MsgManager.get("entercubmems")
                        .replace(playerTemplateNameLabel, player.getName())
                        .replace("{GUILD}", enemy.getName().toUpperCase()));

    }

    public List<Guild> getAllyGuilds(Guild g) {
        List<Guild> allies = new ArrayList<>();
        for (Relation r : g.getAlliances()) {
            String alliedGuildTag = r.getAlliedGuildTag();
            if (r.getAlliedGuildTag().equals(g.getName())) {
                alliedGuildTag = r.getBaseGuildTag();
            }
            Guild allyGuild = this.getGuild(alliedGuildTag);
            allies.add(allyGuild);
        }
        return allies;
    }

    public void guildMemberLeftServer(Player player, UUID uuid) {
        Guild guild = getPlayerGuild(uuid);
        if (guild == null) {
            return;
        }
        notifyGuildThatMemberLeft(player, guild);
        verifyOnlineGuild(player, guild);
    }

    public void notifyMembersJoinedGame(Player player, Guild guild) {
        String msg = MsgManager.get("guildmemberjoined");
        guild.notifyGuild(msg.replace(playerTemplateNameLabel, player.getDisplayName()));
    }

    private void notifyGuildThatMemberLeft(Player player, Guild guild) {
        String msg = MsgManager.get("guildmemberleft");
        guild.notifyGuild(msg.replace(playerTemplateNameLabel, player.getDisplayName()));
    }

    public boolean isPlayerInGuild(Player player) {
        return hasGuild(player);
    }


    private void verifyOnlineGuild(Player player, Guild guild) {
        List<String> onlineMembers = guild.getOnlineMembers();
        if (onlineMembers.size() == 0) {
            removeOnlineGuild(guild.getName());
        } else if (onlineMembers.size() == 1) {
            if (onlineMembers.contains(player.getName())) {
                removeOnlineGuild(guild.getName());
            }
        }
    }

    public Guild getPlayerGuild(UUID uuid) {
        String guildTag = playerMetadataController.getPlayerGuildTagFromMetaData(uuid);
        return guilds.get(guildTag);
    }


    public Guild getGuild(String guildTag) {
        return guilds.get(guildTag);
    }

    public Map<String, Guild> getGuilds() {
        return guilds;
    }

    /**
     * @param uuid UUID of player, who should be checked.
     * @return boolean
     */
    public boolean hasGuild(UUID uuid) {
        String guildTag = playerMetadataController.getPlayerGuildTagFromMetaData(uuid);
        return !Objects.equals(guildTag, "");
    }

    /**
     * @param player Player class instance, of player, who should be checked.
     * @return boolean
     */
    public boolean hasGuild(Player player) {
        return hasGuild(player.getUniqueId());
    }

    /**
     * @param tag tag of guild, for which existance should map be searched.
     * @return boolean
     */
    public boolean doesGuildExists(String tag) {
        return !tag.isEmpty() && guilds.containsKey(tag);
    }

    public void setGuilds(Map<String, Guild> guilds) {
        this.guilds = guilds;
    }


    /**
     * @return map which contains all players, who are members of guilds.
     */

    public Guild addGuild(Location home, UUID owner, String tag, String description) {
        Guild guild = new Guild(tag, description, home, owner);
        guild.addMember(owner);
        guilds.put(tag, guild);
        return guild;
    }

    public void invitePlayer(final Player player, Player who, Guild guild) {
        String guildName = guild.getName();
        guildInvitations.addGuildInvitation(player, who, guild, guildName);
    }


    public void addOnlineGuild(String guild) {
        if (onlineGuilds.contains(guild)) {
            return;
        }
        this.onlineGuilds.add(guild);
    }

    public void removeOnlineGuild(String guild) {
        if (!onlineGuilds.contains(guild)) {
            return;
        }
        this.onlineGuilds.remove(guild);
    }

    public boolean isGuildOnline(String guild) {
        return onlineGuilds.contains(guild);
    }

    public int getNumberOfGuilds() {
        return guilds.size();
    }

    public void addPlayer(UUID uuid, Guild playersGuild) {
        playerMetadataController.updatePlayerMetadata(uuid, PlayerMetaDataColumn.GUILD.name(), playersGuild.getName());
    }

    public List<String> getOnlineGuilds() {
        return onlineGuilds;
    }

    public void checkPlayerInvitations() {
        guildInvitations.checkPlayerInvitations();
    }

    public GuildInvitation getGuildInvitation(String playerName, String guildName) {
        return guildInvitations.getPlayerInvitation(playerName, guildName);
    }

    public void acceptInvitation(Player player, Guild guild) {
        this.guildInvitations.acceptGuildInvitation(player, guild);
    }

    public Guild getGuild(Location location) {
        return guilds.get(cuboids.getGuildTagInLocation(location));
    }

    public boolean hasEnoughItemsForGuild(Inventory inv) {
        for (ItemStack item : requiredItemStacks) {
            if (!inv.containsAtLeast(item, item.getAmount())) {
                return false;
            }
        }

        return true;
    }

    public void removeRequiredItemsForGuild(Inventory inv) {
        for (ItemStack item : requiredItemStacks) {
            removeFromInv(inv, item.getType(), item.getDurability(), item.getAmount(), item.getData().getData());
        }
    }

    private static void removeFromInv(Inventory inv, Material mat, int dmgValue, int amount, byte data) {
        if (inv.contains(mat)) {
            int remaining = amount;
            ItemStack[] contents = inv.getContents();
            for (ItemStack is : contents) {
                if (isEmptySlot(is)) {
                    continue;
                }
                if (isDifferentMaterial(mat, is)) {
                    continue;
                }
                if (isSpecificType(data)) {
                    if (is.getData() == null) {
                        continue;
                    }
                    if (isDifferentType(data, is)) {
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
                } else {
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
            }
            inv.setContents(contents);
        }
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

    private static boolean isSpecificType(byte data) {
        return data != -1;
    }

    private static boolean isDifferentType(byte data, ItemStack is) {
        return is.getData().getData() != data;
    }

    private static boolean isBroken(int dmgValue, ItemStack is) {
        return is.getDurability() != dmgValue && dmgValue > 0;
    }

    private static boolean hasLessOrEquals(int remaining, ItemStack is) {
        return is.getAmount() <= remaining;
    }

    private static boolean hasMoreThanEnough(int remaining, ItemStack is) {
        return is.getAmount() > remaining;
    }


    public void loadRequiredItemsForGuild(List<String> requiredItemsString) {
        requiredItemStacks = new ArrayList<>();

        if (requiredItemsString == null) {
            return;
        }
        if (requiredItemsString.isEmpty()) {
            return;
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

    public Inventory prepareItemGuildWindowInventory(Inventory inventory) {
        int inventorySize = getRequiredItemsWindowInventorySize();
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

    private int getRequiredItemsWindowInventorySize() {
        int inventorySize = 9;

        if (getRequiredItemsSize() > 9) {
            inventorySize = 18;
        } else if (getRequiredItemsSize() > 18) {
            inventorySize = 27;
        } else if (getRequiredItemsSize() > 27) {
            inventorySize = 36;
        } else if (getRequiredItemsSize() > 36) {
            inventorySize = 45;
        } else if (getRequiredItemsSize() > 45) {
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

    public boolean isPlayerInForbiddenWorld(String playerWorldName) {
        return GenConf.FORBIDDEN_WORLDS.contains(playerWorldName);
    }

    public int getRequiredItemsSize() {
        return requiredItemStacks.size();
    }

    public void updatePlayerMetadata(UUID uuid, String guildColumn, Object guildName) {
        playerMetadataController.updatePlayerMetadata(uuid, guildColumn, guildName);
    }

    public void updatePlayerMeta(UUID uuid, String guildName, int eloPoints, int playerKills, int playerDeaths) {
        playerMetadataController.updatePlayerMetaAll(uuid, guildName, eloPoints, playerKills, playerDeaths);
    }
}
