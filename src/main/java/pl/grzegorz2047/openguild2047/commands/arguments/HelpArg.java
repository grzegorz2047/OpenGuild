package pl.grzegorz2047.openguild2047.commands.arguments;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpArg {
	
	public static boolean execute(CommandSender sender, int page) {
		if(page <= 0) {
			execute(sender, 1);
			return true;
		}
		sender.sendMessage(getTitle(page));
		if(page == 1) {
			sender.sendMessage(help("help [strona]", "Pokaz pomoc [strona]"));
			sender.sendMessage(help("version", "Informacje o plugine OpenGuild2047"));
			return true;
		}
		return true;
	}
	
	private static String getTitle(int page) {
		return ChatColor.DARK_GRAY + " --------------- " + ChatColor.GOLD + "Help (" + page + "/1)" + ChatColor.DARK_GRAY + " --------------- ";
	}
	
	private static String help(String usage, String desc) {
		return ChatColor.GOLD + "" + ChatColor.ITALIC + "/gildia " + usage + ChatColor.RESET + ChatColor.DARK_GRAY + " - " + desc;
	}
	
}
