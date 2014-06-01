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

package pl.grzegorz2047.openguild2047.commands2;

import com.github.grzegorz2047.openguild.OpenGuild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandInfo;
import com.github.grzegorz2047.openguild.command.UsageException;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild2047.GenConf;

public class Help implements Command {
    
    private static final HashMap<String, String> pages = new HashMap<String, String>();
    
    @Override
    public void execute(CommandSender sender, String[] args) throws UsageException {
        int page = 1;
        if(args.length > 1) {
            try {
                Integer.valueOf(args[1]);
            } catch(NumberFormatException ex) {
                throw new NumberFormatException(args[1]);
            }
        }
        
        if(page <= 0) {
            throw new UsageException("Page number must be positive");
        } else {
            help(sender, page);
        }
    }
    
    private void help(CommandSender sender, int page) {
        if(!pages.containsKey(sender.getName())) {
            generateHelp(sender);
        }
        
        sender.sendMessage(ChatColor.DARK_GRAY + " --------------- " + ChatColor.GOLD + "Help" + ChatColor.DARK_GRAY + " --------------- ");
        sender.sendMessage(pages.get(sender.getName()));
    }
    
    private void generateHelp(CommandSender sender) {
        StringBuilder builder = new StringBuilder();
        for(String cmd : OpenGuild.getCommands()) {
            CommandInfo info = OpenGuild.getCommand(cmd);
            if(!cmd.equals(info.getName()))
                continue; // Remove aliases
            
            if(!info.hasPermission() || sender.hasPermission(info.getPermission())) {
                builder.append(ChatColor.GOLD);
                builder.append(ChatColor.ITALIC);
                builder.append(info.getUsage());
                builder.append(ChatColor.RESET);
                builder.append(ChatColor.GRAY);
                builder.append(" - ");
                builder.append(info.getDescription().get(GenConf.lang.name().toUpperCase()));
                builder.append("\n");
            }
        }
        pages.remove(sender.getName());
        pages.put(sender.getName(), builder.toString());
    }
    
}
