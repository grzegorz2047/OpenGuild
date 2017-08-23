/*
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.grzegorz2047.openguild2047.commands.guild;

import pl.grzegorz2047.openguild2047.BagOfEverything;
import pl.grzegorz2047.openguild2047.commands.command.Command;
import pl.grzegorz2047.openguild2047.commands.command.CommandException;
import pl.grzegorz2047.openguild2047.commands.command.PermException;
import pl.grzegorz2047.openguild2047.modules.module.RandomTPModule;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GuildRandomTPCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(args.length == 2) {
            if(sender.hasPermission("openguild.randomtp.other")) {
                String target = args[1];
                if(Bukkit.getPlayer(target) == null) {
                    throw new CommandException("Player \"" + target + "\" is not online");
                } else {
                    ((RandomTPModule) BagOfEverything.getModules().getModule("random-tp")).teleport(Bukkit.getPlayer(target));
                }
            } else {
                throw new PermException();
            }
        }
        
        if(sender instanceof Player) {
            ((RandomTPModule) BagOfEverything.getModules().getModule("random-tp")).teleport((Player) sender);
        } else {
            throw new CommandException("You must be a player in-game!");
        }
    }
    
    @Override
    public int minArgs() {
        return 1;
    }
}
