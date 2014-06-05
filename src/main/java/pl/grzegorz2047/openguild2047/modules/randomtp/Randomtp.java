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

package pl.grzegorz2047.openguild2047.modules.randomtp;

import com.github.grzegorz2047.openguild.OpenGuild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import com.github.grzegorz2047.openguild.command.PermException;
import com.github.grzegorz2047.openguild.module.RandomTPModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Randomtp implements Command {
    
    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(args.length == 2) {
            if(sender.hasPermission("openguild.randomtp.other")) {
                String target = args[1];
                if(Bukkit.getPlayer(target) == null) {
                    throw new CommandException("Player \"" + target + "\" is not online");
                } else {
                    ((RandomTPModule) OpenGuild.getModules().getModule("random-tp")).teleport(Bukkit.getPlayer(target));
                }
            } else {
                throw new PermException();
            }
        }
        
        if(sender instanceof Player) {
            ((RandomTPModule) OpenGuild.getModules().getModule("random-tp")).teleport((Player) sender);
        } else {
            throw new CommandException("You must be a player in-game!");
        }
    }
    
}
