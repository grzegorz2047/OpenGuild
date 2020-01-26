package pl.grzegorz2047.openguild.commands.guild;

import org.bukkit.Bukkit;
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
 * File created by grzegorz2047 on 26.08.2017.
 */
public class GuildChangeLeaderCommand extends Command {
    private final Guilds guilds;
    private final SQLHandler sqlHandler;

    public GuildChangeLeaderCommand(String[] aliases, Guilds guilds, SQLHandler sqlHandler) {
        super(aliases);
        this.guilds = guilds;
        this.sqlHandler = sqlHandler;
    }


    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }

        Player possibleCurrentLeader = (Player) sender;
        if (args.length < 2) {
            possibleCurrentLeader.sendMessage(MsgManager.get("usagechangehomecommand"));
            return;
        }
        String newLeaderName = args[1];
        guilds.changeLeader(possibleCurrentLeader, newLeaderName);
    }



    @Override
    public int minArgs() {
        return 0;
    }
}
