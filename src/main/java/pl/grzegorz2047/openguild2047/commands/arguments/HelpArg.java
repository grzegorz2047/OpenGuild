/**
 * The MIT License
 *
 * Copyright 2014 Grzegorz.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
