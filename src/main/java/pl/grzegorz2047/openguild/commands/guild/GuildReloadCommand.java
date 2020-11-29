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

package pl.grzegorz2047.openguild.commands.guild;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import pl.grzegorz2047.openguild.OpenGuild;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.managers.MsgManager;

import java.io.IOException;

/**
 * Command used to reload guilds.
 * <p>
 * Usage: /guild reload
 */
public class GuildReloadCommand extends Command {
    private FileConfiguration fileConfiguration;

    public GuildReloadCommand(String[] aliases, FileConfiguration fileConfiguration) {
        super(aliases);
        setPermission("openguild.command.reload");
        this.fileConfiguration = fileConfiguration;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        try {
            System.out.println(fileConfiguration.getCurrentPath());
            fileConfiguration.load(fileConfiguration.getCurrentPath());

            sender.sendMessage(MsgManager.get("configreloaded"));
        } catch (IOException | InvalidConfigurationException ex) {
            OpenGuild.getOGLogger().exceptionThrown(ex);
            sender.sendMessage(MsgManager.get("cmderror"));
        }
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
