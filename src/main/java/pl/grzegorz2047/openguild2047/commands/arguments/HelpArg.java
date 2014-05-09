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

import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class HelpArg {

    public static boolean execute(CommandSender sender, String[] args) {
        int page = 1;
        if(args.length != 2) {
            page = 1;
        } else {
            if(args[1].equalsIgnoreCase("admin") || args[1].equalsIgnoreCase("a")) {
                admin(sender);
                return true;
            }
            try {
                page = Integer.parseInt(args[1]);
            } catch(NumberFormatException ex) {
                sender.sendMessage(MsgManager.get("pagenotnumber"));
            }
        }
        sender.sendMessage(getTitle(page));
        if(page == 1) {
            if(GenConf.lang.name().equals("PL")) {
                sender.sendMessage(help("zaloz <tag> [opis...]", "Zaloz gildie"));
                sender.sendMessage(help("akceptuj <gracz>", "Akceptuj zaproszenie do gildii"));
                sender.sendMessage(help("opis <opis...>", "Stwórz opis gildii"));
                sender.sendMessage(help("lider <gracz>", "Oddaj lidera gildii innemu graczowi"));
                sender.sendMessage(help("dolacz", "Dolacz do gildii"));
                sender.sendMessage(help("opusc", "Opusc gildie w której teraz jestes"));
                sender.sendMessage(help("zamknij", "Zamknij gildie"));
                sender.sendMessage(help("dom", "Teleportuj sie do gildii"));
                sender.sendMessage(help("lista", "Lista wszystkich czlonkow gildii"));
                sender.sendMessage(help("help [admin|strona]", "Pokaz pomoc [admin/strona]"));
            } else {
                sender.sendMessage(help("create <tag> [opis...]", "Create guild"));
                sender.sendMessage(help("accept <gracz>", "Accept invite to join guild"));
                sender.sendMessage(help("description <description...>", "change description of guild"));
                sender.sendMessage(help("leader <gracz>", "Give leader to someone else"));
                sender.sendMessage(help("join", "Join to guild (send invite)"));
                sender.sendMessage(help("leave", "Leave from current guild"));
                sender.sendMessage(help("disband", "Disband your guild"));
                sender.sendMessage(help("home", "Teleport to your guild home location"));
                sender.sendMessage(help("list", "List of your guild members"));
                sender.sendMessage(help("help [admin|page]", "Show help [admin/page]"));
            }

        } else {
            if(GenConf.lang.name().equals("PL")) {
                sender.sendMessage(MsgManager.get("pagenotfound", "&cStrona o numerze {NUMBER} nie zostala odnaleziona").replace("{NUMBER}", String.valueOf(page)));
            } else {
                sender.sendMessage(MsgManager.get("pagenotfound", "&cPage number {NUMBER} not found").replace("{NUMBER}", String.valueOf(page)));
            }
            
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
        if(GenConf.lang.name().equals("PL")){
            return ChatColor.GOLD + "" + ChatColor.ITALIC + "/gildia " + usage + ChatColor.RESET + ChatColor.DARK_GRAY + " - " + desc;
        } else {
            return ChatColor.GOLD + "" + ChatColor.ITALIC + "/guild " + usage + ChatColor.RESET + ChatColor.DARK_GRAY + " - " + desc;
        }
        
    }

    private static void admin(CommandSender sender) {
        int hide = 0;
        sender.sendMessage(getTitleAdmin());
        sender.sendMessage(help("reload", "Przeladuj konfiguracje pluginu"));
        if(GenConf.hcBans) {
            sender.sendMessage(help("unban <player>", "Odbanuj gracza ze smierci"));
        } else {
            hide++;
        }
        sender.sendMessage(help("version", "Informacje o plugine OpenGuild2047"));
        
        if(hide > 0) {
            sender.sendMessage(MsgManager.get("skipped"));
        }
    }

}
