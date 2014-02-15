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

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import pl.grzegorz2047.openguild2047.GenConf;

/**
 * @author TheMolkaPL
 */
public class ReloadArg {
	
	private static File file = new File("plugins/OpenGuild2047/config.yml");
	private static FileConfiguration config = YamlConfiguration.loadConfiguration(file);
	
	public static boolean execute(CommandSender sender) {
		if(!sender.hasPermission("openguild.admin.reload") || !sender.isOp()) {
			sender.sendMessage(GenConf.prefix + ChatColor.RED + "Brak odpowiednich uprawnien.");
			return true;
		}
		if(file.exists()) {
			try {
				config.load(file);
				sender.sendMessage(ChatColor.GREEN + "Pomyslnie przeladowano plik config.yml!");
			} catch(IOException ex) {
				sender.sendMessage(ChatColor.RED + "Wystapil problem z plikiem config.yml! " + ex.getMessage());
				ex.printStackTrace();
			} catch(InvalidConfigurationException ex) {
				sender.sendMessage(ChatColor.RED + "Wystapil problem z konfiguracja pliku config.yml! Sprawdz wszystkie spacje!");
				ex.printStackTrace();
			}
			return true;
		} else {
			config.options().copyDefaults(true);
			sender.sendMessage(ChatColor.RED + "Wystapil problem z plikiem config.yml! Stworzono nowy.");
		}
		return true;
	}
	
}
