package pl.grzegorz2047.openguild.commands.guild;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboid;
import pl.grzegorz2047.openguild.cuboidmanagement.Cuboids;
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
    private final int MAX_CUBOID_RADIUS;
    private final Cuboids cuboids;

    public GuildExpandCommand(String[] aliases, Guilds guilds, SQLHandler sqlHandler, Cuboids cuboids, FileConfiguration config) {
        super(aliases);
        this.guilds = guilds;
        this.sqlHandler = sqlHandler;
        this.cuboids = cuboids;
          MAX_CUBOID_RADIUS = config.getInt("cuboid.max-cube-size", 50);

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
        Cuboid cuboid = cuboids.getCuboids().get(playerGuild.getName());
        if(cuboid.getCuboidSize() >= MAX_CUBOID_RADIUS) {
            player.sendMessage(MsgManager.get("maxcuboidsizereached"));
            return;
        }
        //listRequiredItems + obecna wielkosc cuboida ?
        //wez wymagane itemy
        //sqlHandler.expandCuboid(playerGuild.getName());
    }

    @Override
    public int minArgs() {
        return 0;
    }
}
