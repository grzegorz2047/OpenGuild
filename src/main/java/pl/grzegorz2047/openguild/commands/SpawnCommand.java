package pl.grzegorz2047.openguild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.teleporters.Teleporter;

public class SpawnCommand implements CommandExecutor {

    private final Teleporter teleporter;
    private final int TELEPORT_COOLDOWN;

    public SpawnCommand(Teleporter teleporter, FileConfiguration config) {
        this.teleporter = teleporter;
        TELEPORT_COOLDOWN = config.getInt("teleport-cooldown", 10);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return true;
        }
        Player p = (Player) commandSender;
        if (command.getName().equalsIgnoreCase("spawn")) {
            teleporter.addRequest(p.getUniqueId(), p.getLocation(), p.getWorld().getSpawnLocation(), TELEPORT_COOLDOWN);
            String hometpdontmoveMsg = MsgManager.get("hometpdontmove");
            String homedontMoveMsgUpdated = hometpdontmoveMsg.replace("%TIME%", String.valueOf(TELEPORT_COOLDOWN));
            p.sendMessage(homedontMoveMsgUpdated);
        }
        return true;
    }
}
