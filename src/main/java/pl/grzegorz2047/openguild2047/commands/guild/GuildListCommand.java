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

import org.bukkit.command.CommandSender;
import pl.grzegorz2047.openguild2047.Guilds;
import com.github.grzegorz2047.openguild.Guild;
import com.github.grzegorz2047.openguild.command.Command;
import com.github.grzegorz2047.openguild.command.CommandException;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * This command shows list of guilds.
 * 
 * Usage: /guild list
 */
public class GuildListCommand extends Command {
    public GuildListCommand() {
        setPermission("openguild.command.list");
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        Guilds guilds = this.getPlugin().getGuilds();
        
        StringBuilder resultBuilder = new StringBuilder();
        for(Guild guild : guilds.getGuilds().values()) {
            String tag = guild.getTag();
            resultBuilder.append(tag).append(", ");
        }
        
        String result = resultBuilder.toString();
        
        sender.sendMessage(this.getTitle(MsgManager.getIgnorePref("titleguildlist")));
        resultBuilder.append(MsgManager.get("numguilds")).append(this.getPlugin().getGuilds().getGuilds().size());
        resultBuilder.append("\n");
        
        sender.sendMessage(result);
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
