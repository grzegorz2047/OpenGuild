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

import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Command used to reload guilds.
 * 
 * Usage: /guild reload
 */
public class GuildReloadCommand extends Command {

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if(!sender.hasPermission("openguild.admin.reload") || !sender.isOp()) {
            sender.sendMessage(MsgManager.get("permission"));
            return;
        }
        
        try {
            this.getPlugin().getConfig().load(this.getPlugin().getConfig().getCurrentPath());
            sender.sendMessage(MsgManager.get("configreloaded"));
        } catch (FileNotFoundException ex) {
            this.getPlugin().getOGLogger().exceptionThrown(ex);
            sender.sendMessage(MsgManager.get("cmderror"));
        } catch (IOException ex) {
            this.getPlugin().getOGLogger().exceptionThrown(ex);
            sender.sendMessage(MsgManager.get("cmderror"));
        } catch (InvalidConfigurationException ex) {
            this.getPlugin().getOGLogger().exceptionThrown(ex);
            sender.sendMessage(MsgManager.get("cmderror"));
        }
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
