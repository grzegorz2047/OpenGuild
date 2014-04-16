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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package pl.grzegorz2047.openguild2047.commands.arguments;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class HelpArg {

    public static boolean execute(CommandSender sender, String[] args) {
        int page = 1;
        if(args.length == 1) {
            page = 1;
        } else {
            try {
                page = Integer.parseInt(args[1]);
            } catch(NumberFormatException ex) {
                if(args[1].equalsIgnoreCase("admin") || args[1].equalsIgnoreCase("a")) {
                    admin(sender);
                } else {
                    sender.sendMessage(MsgManager.get("pagenotnumber", "&cNumer strony musi byc liczba!"));
                }
                return true;
            }
        }
        sender.sendMessage(getTitle(page));
        if(page == 1) {
            sender.sendMessage(help("disband", "Usun gildie jako operator"));
            sender.sendMessage(help("dom", "Teleportuj sie do gildii"));
            sender.sendMessage(help("help [admin|strona]", "Pokaz pomoc [admin/strona]"));
            sender.sendMessage(help("lider <gracz>", "Oddaj lidera gildii graczowi"));
            sender.sendMessage(help("lista", "Lista wszystkich czlonkow gildii"));
            sender.sendMessage(help("opis <opis...>", "Zmien opis gildii"));
            sender.sendMessage(help("opusc", "Opusc gildie w której teraz jestes"));
            sender.sendMessage(help("stworz <tag> [opis...]", "Zaloz gildie"));
            sender.sendMessage(help("zamknij", "Zamknij gildie"));
        } else {
            sender.sendMessage("");
        }
        return true;
    }

    private static String getTitleAdmin() {
        return ChatColor.DARK_GRAY + " --------------- " + ChatColor.GOLD + "Help Admin" + ChatColor.DARK_GRAY + " --------------- ";
    }

    private static String getTitle(int page) {
        return ChatColor.DARK_GRAY + " --------------- " + ChatColor.GOLD + "Help (" + page + "/1)" + ChatColor.DARK_GRAY + " --------------- ";
    }

    private static String help(String usage, String desc) {
        return ChatColor.GOLD + "" + ChatColor.ITALIC + "/gildia " + usage + ChatColor.RESET + ChatColor.DARK_GRAY + " - " + desc;
    }

    private static void admin(CommandSender sender) {
        sender.sendMessage(getTitleAdmin());
        sender.sendMessage(help("reload", "Przeladuj konfiguracje pluginu"));
        sender.sendMessage(help("version", "Informacje o plugine OpenGuild2047"));
    }

}
