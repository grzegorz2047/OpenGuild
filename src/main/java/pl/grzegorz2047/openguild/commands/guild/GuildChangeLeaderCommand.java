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

/**
 * File created by grzegorz2047 on 26.08.2017.
 */
public class GuildChangeLeaderCommand extends Command {
    private final Guilds guilds;
    private final SQLHandler sqlHandler;

    public GuildChangeLeaderCommand(Guilds guilds, SQLHandler sqlHandler) {
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
        if (playerGuild.getLeader().equals(player.getUniqueId())) {
            sender.sendMessage(MsgManager.get("cantchangeleadertoyourself"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(MsgManager.get("usagechangehomecommand"));
            return;
        }
        String newLeaderName = args[1];

        if(!playerGuild.getMembersNames().contains(newLeaderName)) {
            player.sendMessage(MsgManager.get("playernotinyourguild"));
            return;
        }
        Player newLeader = Bukkit.getPlayer(newLeaderName);
        if (newLeader == null) {
            player.sendMessage(MsgManager.get("playeroffline"));
            return;
        }
        playerGuild.setLeader(newLeader.getUniqueId());
        sqlHandler.changeLeader(playerGuild.getName(), playerGuild.getName());
        player.sendMessage(MsgManager.get("successfullychangedleader"));
    }

    @Override
    public int minArgs() {
        return 0;
    }
}
