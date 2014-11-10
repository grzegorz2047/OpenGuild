/*
 * The MIT License
 *
 * Copyright 2014 Adam.
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

package pl.grzegorz2047.openguild2047.commands.guild;

import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild2047.GenConf;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * This command shows list of 'guild' command sub-commands.
 * 
 * Usage: /guild help
 */
public class GuildHelpCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        int page = 1;
        if(args.length != 2) {
            page = 1;
        } else {
            if(args[1].equalsIgnoreCase("admin") || args[1].equalsIgnoreCase("a")) {
                admin(sender);
                return;
            }
            try {
                page = Integer.parseInt(args[1]);
            } catch(NumberFormatException ex) {
                sender.sendMessage(MsgManager.get("pagenotnumber"));
            }
        }
        
        sender.sendMessage(getTitle(page));
        
        if(page == 1) {
            if(GenConf.lang.equals("PL")) {
                sender.sendMessage(help("zaloz <tag> [opis...]", "Zaloz gildie"));
                sender.sendMessage(help("zapros <gracz>", "Zapros gracza do gildii"));
                sender.sendMessage(help("akceptuj <tag>", "Akceptuj zaproszenie od gildii"));
                sender.sendMessage(help("opis [ustaw <opis...>]", "Stwórz lub zobacz opis gildii"));
                sender.sendMessage(help("lider <gracz>", "Oddaj lidera gildii innemu graczowi"));
                sender.sendMessage(help("akceptuj <gildia>", "Akceptuje dolaczenie do gildii"));
                sender.sendMessage(help("opusc", "Opusc gildie w której teraz jestes"));
                sender.sendMessage(help("info <gildia>", "Informacje o gildii"));
                sender.sendMessage(help("relacja <gildia> ally/enemy", "Ustawienie sojuszu z inna gildia"));
                sender.sendMessage(help("zamknij", "Zamknij gildie"));
                sender.sendMessage(help("wyrzuc <gracz>", "Wyrzuca czlonka gildii"));
                sender.sendMessage(help("itemy", "Lista itemów na gildie"));
                sender.sendMessage(help("dom", "Teleportuj sie do gildii"));
                sender.sendMessage(help("lista", "Lista wszystkich  gildii"));
                sender.sendMessage(help("help [admin|strona]", "Pokaz pomoc [admin/strona]"));
            } else {
                sender.sendMessage(help("create <tag> [desc...]", "Create guild"));
                sender.sendMessage(help("invite <player>", "Accepts invite to joint guild"));
                sender.sendMessage(help("accept <guild>", "Accept invite to joint guild"));
                sender.sendMessage(help("description [set <description...>]", "Change or see description of guild"));
                sender.sendMessage(help("leader <gracz>", "Give leader to someone else"));
                sender.sendMessage(help("invite", "Invite to guild (sends invite)"));
                sender.sendMessage(help("leave", "Leave from current guild"));
                sender.sendMessage(help("info <guild>", "Information about the guild"));
                sender.sendMessage(help("relation <guild> ally/enemy/accept", "Set ally,enemy to other guild or accept alliance"));
                sender.sendMessage(help("disband", "Disband your guild"));
                sender.sendMessage(help("items", "List of required items"));
                sender.sendMessage(help("home", "Teleport to your guild home location"));
                sender.sendMessage(help("list", "List all guilds"));
                sender.sendMessage(help("kick <player>", "Kicks member of guild"));
                sender.sendMessage(help("help [admin|page]", "Show help [admin/page]"));
            }
        } else {
            sender.sendMessage(MsgManager.get("pagenotfound", "&cStrona o numerze {NUMBER} nie zostala odnaleziona").replace("{NUMBER}", String.valueOf(page)));
        }
    }
    
    private static String getTitleAdmin() {
        return ChatColor.DARK_GRAY + " --------------- " + ChatColor.GOLD + "Help Admin" + ChatColor.DARK_GRAY + " --------------- ";
    }

    private static String getTitle(int page) {
        return ChatColor.DARK_GRAY + " --------------- " + ChatColor.GOLD + "Help (" + page + "/1)" + ChatColor.DARK_GRAY + " --------------- ";
    }

    private static String help(String usage, String desc) {
        if(GenConf.lang.equals("PL")){
            return ChatColor.GOLD + "" + ChatColor.ITALIC +  "/gildia " + ChatColor.AQUA + usage + ChatColor.RESET + ChatColor.GRAY + " - " + desc;
        } else {
            return ChatColor.GOLD + "" + ChatColor.ITALIC + "/guild " + ChatColor.AQUA + usage + ChatColor.RESET + ChatColor.GRAY + " - " + desc;
        }
        
    }

    private static void admin(CommandSender sender) {
        int hide = 0;
        sender.sendMessage(getTitleAdmin());
        sender.sendMessage(help("reload", "Przeladuj konfiguracje pluginu"));
        if(GenConf.hcBans) {
            sender.sendMessage(help("unban <player>", "Odbanuj gracza"));
        } else {
            hide++;
        }
        sender.sendMessage(help("version", "Informacje o plugine OpenGuild2047"));
        
        if(hide > 0) {
            sender.sendMessage(MsgManager.get("skipped").replace("{HELP}", String.valueOf(hide)));
        }
    }

    @Override
    public int minArgs() {
        return 1;
    }

}