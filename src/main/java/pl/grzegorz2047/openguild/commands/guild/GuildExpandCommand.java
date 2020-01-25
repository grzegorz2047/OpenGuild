package pl.grzegorz2047.openguild.commands.guild;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.grzegorz2047.openguild.InventoryManipulator;
import pl.grzegorz2047.openguild.ItemsLoader;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboid;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by grzeg on 26.08.2017.
 */
public class GuildExpandCommand extends Command {
    private final Guilds guilds;
    private final SQLHandler sqlHandler;
    private final int MAX_CUBOID_RADIUS;
    private final Cuboids cuboids;
    private ArrayList<ItemStack> requiredItems;

    public GuildExpandCommand(String[] aliases, Guilds guilds, SQLHandler sqlHandler, Cuboids cuboids, FileConfiguration config) {
        super(aliases);
        this.guilds = guilds;
        this.sqlHandler = sqlHandler;
        this.cuboids = cuboids;
        MAX_CUBOID_RADIUS = config.getInt("cuboid.max-cube-size", 50);
        List<Map<?, ?>> ITEMS_TO_EXPAND = config.getMapList("cuboid.expand.items-to-expand");
        loadItemsToExpand(ITEMS_TO_EXPAND);
    }
    private void loadItemsToExpand(List<Map<?, ?>> itemsToExpand) {
        ItemsLoader itemsLoader = new ItemsLoader();
        this.requiredItems = itemsLoader.loadItems(itemsToExpand);
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }

        Player player = (Player) sender;
        UUID playerUUID = player.getUniqueId();
        Guild playerGuild = guilds.getPlayerGuild(playerUUID);
        if (playerGuild == null) {
            player.sendMessage(MsgManager.get("notinguild"));
            return;
        }
        if (!playerGuild.isLeader(playerUUID)) {
            player.sendMessage(MsgManager.get("playernotleader"));
            return;
        }
        String playerGuildName = playerGuild.getName();
        Cuboid cuboid = cuboids.getCuboidByGuildName(playerGuildName);
        if (!isSmallEnough(cuboid)) {
            player.sendMessage(MsgManager.get("maxcuboidsizereached"));
            return;
        }
        Cuboid futureCuboid = new Cuboid(cuboid.getCenter(), cuboid.getOwner(), cuboid.getCuboidSize() + 1, cuboid.getWorldId());
        if (cuboids.isCuboidInterferingWithOtherCuboid(futureCuboid)) {
            player.sendMessage(MsgManager.get("cuboidcannotbeexpandeddueothercuboidclose"));
            return;
        }
        InventoryManipulator inventoryManipulator = new InventoryManipulator();
        if (!inventoryManipulator.hasEnoughItems(player.getInventory(), requiredItems)) {
            player.sendMessage(MsgManager.get("notenoughitemstoexpand"));
            return;
        }
        inventoryManipulator.removeRequiredItemsForGuild(player.getInventory(), requiredItems);
        //listRequiredItems + obecna wielkosc cuboida ?
        //wez wymagane itemy
        sqlHandler.expandCuboid(playerGuild.getName());
        cuboid.expand(1);
        player.sendMessage(MsgManager.get("cuboidexpandedsuccessfully"));
    }


    private boolean isSmallEnough(Cuboid cuboid) {
        return cuboid.getCuboidSize() < MAX_CUBOID_RADIUS;
    }


    @Override
    public int minArgs() {
        return 0;
    }
}
