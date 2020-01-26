package pl.grzegorz2047.openguild.commands.guild;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;

import java.util.UUID;

/**
 * Created by grzeg on 26.08.2017.
 */
public class GuildChangeHomeCommand extends Command {
    private final Guilds guilds;
    private final SQLHandler sqlHandler;

    public GuildChangeHomeCommand(String[] strings, Guilds guilds, SQLHandler sqlHandler) {
        super(strings);
        this.guilds = guilds;
        this.sqlHandler = sqlHandler;
    }


    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        Player player = (Player) sender;
        UUID playerUniqueId = player.getUniqueId();
        Location playerLocation = player.getLocation();
        if (!guilds.changeHome(player, playerUniqueId, playerLocation)) {
            return;
        }

    }




    @Override
    public int minArgs() {
        return 0;
    }
}
