package pl.grzegorz2047.openguild2047.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.Teleporter;
import pl.grzegorz2047.openguild2047.TpaRequester;
import pl.grzegorz2047.openguild2047.managers.MsgManager;


public class TpaCommand implements CommandExecutor {

    private final Teleporter teleporter;
    private final TpaRequester tpaRequester;

    public TpaCommand(Teleporter teleporter, TpaRequester tpaRequester) {
        this.teleporter = teleporter;
        this.tpaRequester = tpaRequester;

    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return true;
        }
        Player p = (Player) commandSender;
        if (args.length == 0) {
            p.sendMessage(MsgManager.get("usagetpa"));
            return true;
        }
        if (args.length >= 2) {
            String subcmd = args[0];
            String sourceName = args[1];
            if (subcmd.equalsIgnoreCase("accept")) {
                if (isNotRequesting(p, sourceName)) return true;
                Player sourcePlayer = Bukkit.getPlayer(sourceName);
                if (isNotOnline(p, sourcePlayer)) return true;
                teleporter.addRequest(sourcePlayer.getUniqueId(), sourcePlayer.getLocation(), p.getLocation(), GenConf.TELEPORT_COOLDOWN);
                tpaRequester.removeRequest(sourceName);
                sourcePlayer.sendMessage(MsgManager.get("tpaccepteddontmove").replace("%SECONDS%", String.valueOf(GenConf.TELEPORT_COOLDOWN)));
            }
            if (subcmd.equalsIgnoreCase("deny")) {
                if (isNotRequesting(p, sourceName)) return true;
                Player sourcePlayer = Bukkit.getPlayer(sourceName);
                if (isNotOnline(p, sourcePlayer)) return true;
                tpaRequester.removeRequest(sourcePlayer.getName());
                sourcePlayer.sendMessage(MsgManager.get("tpadenied").replace("%PLAYER%", p.getName()));

            }
        }
        if (args.length == 1) {
            String destinationName = args[0];
            if (destinationName.equalsIgnoreCase("accept")) {
                p.sendMessage(MsgManager.get("usagetpaaccept"));
                return true;
            }
            if (destinationName.equalsIgnoreCase("deny")) {
                p.sendMessage(MsgManager.get("usagetpadeny"));
                return true;
            }

            Player destinationPlayer = Bukkit.getPlayer(destinationName);
            if (destinationPlayer == null) {
                p.sendMessage(MsgManager.get("playernotonline"));
                return true;
            }

            boolean requestSentSuccess = tpaRequester.addRequest(p.getName(), destinationName);
            if (requestSentSuccess) {
                destinationPlayer.sendMessage(MsgManager.get("tparequested").replace("%PLAYER%", p.getName()));
                p.sendMessage(MsgManager.get("tparequestsentsuccess").replace("%PLAYER%", destinationName));
            } else {
                p.sendMessage(MsgManager.get("tparequestsentfailed"));
            }
        }
        return true;
    }

    private boolean isNotOnline(Player p, Player sourcePlayer) {
        if (sourcePlayer == null) {
            p.sendMessage(MsgManager.get("playernotonline"));
            return true;
        }
        return false;
    }

    private boolean isNotRequesting(Player p, String sourceName) {
        if (!tpaRequester.isRequesting(sourceName)) {
            p.sendMessage(MsgManager.get("tparequestnotfound"));
            return true;
        }
        return false;
    }
}
