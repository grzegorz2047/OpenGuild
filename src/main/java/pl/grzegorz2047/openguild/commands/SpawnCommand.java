package pl.grzegorz2047.openguild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.configuration.GenConf;
import pl.grzegorz2047.openguild.teleporters.Teleporter;
import pl.grzegorz2047.openguild.managers.MsgManager;

public class SpawnCommand implements CommandExecutor {

    private final Teleporter teleporter;

    public SpawnCommand(Teleporter teleporter) {
        this.teleporter = teleporter;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return true;
        }
        Player p = (Player) commandSender;
        if (command.getName().equalsIgnoreCase("spawn")) {
            teleporter.addRequest(p.getUniqueId(), p.getLocation(), p.getWorld().getSpawnLocation(), GenConf.TELEPORT_COOLDOWN);
            String hometpdontmoveMsg = MsgManager.get("hometpdontmove");
            String homedontMoveMsgUpdated = hometpdontmoveMsg.replace("%TIME%", String.valueOf(GenConf.TELEPORT_COOLDOWN));
            p.sendMessage(homedontMoveMsgUpdated);
        }
        return true;
    }
}
