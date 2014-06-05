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

import com.github.grzegorz2047.openguild.OpenGuild;
import com.github.grzegorz2047.openguild.OpenGuildPlugin;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.command.CommandInfo;
import com.github.grzegorz2047.openguild.command.PermException;
import com.github.grzegorz2047.openguild.command.UsageException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

public class NewGuildCommand implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("guild")) {
            String result = null;
            if(args.length == 0) {
                result = "help";
            }
            
            OpenGuildPlugin og = OpenGuild.getPlugin();
            Iterator<String> it = og.getCommands().iterator();
            if(result == null) {
                while(it.hasNext()) {
                    String next = it.next();
                    if(next.toLowerCase().startsWith(args[0].toLowerCase())) {
                        result = next;
                    }
                }
            }
            
            if(result == null) {
                sender.sendMessage(MsgManager.get("cmdnotfound").replace("{COMMAND}", args[0]));
                sender.sendMessage(ChatColor.RED + "/g help [command|page]");
                return true;
            }
            
            try {
                CommandInfo info = og.getCommand(result);
                if(!info.hasPermission() || sender.hasPermission(info.getPermission())) {
                    og.getCommand(result).getExecutor().execute(sender, args);
                } else {
                    throw new PermException();
                }
            } catch(CommandException ex) {
                sender.sendMessage(ChatColor.RED + ex.getMessage());
                return true;
            } catch(PermException ex) {
                sender.sendMessage(ChatColor.RED + MsgManager.get("permission"));
                return true;
            } catch(UsageException ex) {
                sender.sendMessage(ChatColor.RED + ex.getMessage());
                sender.sendMessage(og.getCommand(result).getUsage());
                return true;
            } catch(Exception ex) {
                if(ex instanceof NumberFormatException) {
                    sender.sendMessage(ChatColor.RED + MsgManager.get("numneededsyntax").replace("{STRING}", ex.getMessage()));
                } else {
                    sender.sendMessage(ChatColor.RED + MsgManager.get("cmderror"));
                    ex.printStackTrace();
                }
                return true;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("guild")) {
            Set<String> cmds = OpenGuild.getPlugin().getCommands();
            
            List<String> complete = new ArrayList<String>();
            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("")) {
                    complete.addAll(cmds);
                } else {
                    for(String argument : cmds) {
                        if(OpenGuild.getCommand(argument).getName().equals(argument) // Remove aliases
                                && argument.startsWith(args[0].toLowerCase())) {
                            complete.add(argument);
                        }
                    }
                }
            }
            Collections.sort(complete);
            return complete;
        }
        return null;
    }
    
}
