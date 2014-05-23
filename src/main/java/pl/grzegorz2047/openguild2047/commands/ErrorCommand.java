/*
 * The MIT License
 *
 * Copyright 2014 Aleksander.
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

package pl.grzegorz2047.openguild2047.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ErrorCommand implements CommandExecutor {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("guild") || command.getName().equalsIgnoreCase("team")) {
            sender.sendMessage(ChatColor.RED + "Nie udalo sie poprawnie uruchomic OpenGuild2047. Nie bój sie, to pewnie jakis drobny blad.");
            sender.sendMessage(ChatColor.YELLOW + "Prosze przejrzyj konsole serwera i wyszaj tam blad.");
            sender.sendMessage("Jezeli nie umiesz sobie z nim poradzic, lub go nie rozumiesz napisz do naz zapytanie na "
                    + ChatColor.UNDERLINE + "https://github.com/grzegorz2047/OpenGuild2047/issues" + ChatColor.RESET + ChatColor.YELLOW + ".");
            sender.sendMessage(ChatColor.YELLOW + "Prosimy o przesylanie bledów do Pastebin - " + ChatColor.UNDERLINE
                    + "http://pastebin.com" + ChatColor.RESET + ChatColor.YELLOW + "!");
            return true;
        }
        return false;
    }
    
}
