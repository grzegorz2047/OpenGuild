package pl.grzegorz2047.openguild.commands.guild;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;

/**
 * Created by grzeg on 26.08.2017.
 */
public class GuildExpandCommand extends Command {
    private final Guilds guilds;
    private final SQLHandler sqlHandler;

    public GuildExpandCommand(Guilds guilds, SQLHandler sqlHandler) {
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
        Guild playerGuild = guilds.getPlayerGuild(player.getUniqueId());
        if (playerGuild == null) {
            player.sendMessage(MsgManager.get("notinguild"));
            return;
        }
        if (!playerGuild.getLeader().equals(player.getUniqueId())) {
            player.sendMessage(MsgManager.get("playernotleader"));
            return;
        }
        playerGuild.setHome(player.getLocation());
        sqlHandler.changeHome(playerGuild.getName(), player.getLocation());
        player.sendMessage(MsgManager.get("successfullychangedhomeposition"));
    }

    @Override
    public int minArgs() {
        return 0;
    }
}
