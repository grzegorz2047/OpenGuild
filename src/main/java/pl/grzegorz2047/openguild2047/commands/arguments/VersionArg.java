package pl.grzegorz2047.openguild2047.commands.arguments;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import pl.grzegorz2047.openguild2047.OpenGuild;

public class VersionArg {
	
	public static boolean execute(CommandSender sender) {
		sender.sendMessage(ChatColor.DARK_GRAY + " -------------------- " + ChatColor.GOLD + "OpenGuild2047" + ChatColor.DARK_GRAY + " -------------------- ");
		sender.sendMessage(ChatColor.DARK_GRAY + "Wersja: " + ChatColor.GOLD + OpenGuild.get().getDescription().getVersion());
		sender.sendMessage(ChatColor.DARK_GRAY + "Autor: " + ChatColor.GOLD + "grzegorz2047");
		return true;
	}
	
}
