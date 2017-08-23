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

import pl.grzegorz2047.openguild2047.commands.command.Command;
import pl.grzegorz2047.openguild2047.commands.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 *
 * @author Grzegorz
 */
public class GuildAdminCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        Player player = (Player) sender;
        if(args.length == 1){
            player.sendMessage(MsgManager.get("cmdhelp-us") + "/g admin <arg> <value>");
        }
    }

    @Override
    public int minArgs() {
       return 1;
    }
    
}
