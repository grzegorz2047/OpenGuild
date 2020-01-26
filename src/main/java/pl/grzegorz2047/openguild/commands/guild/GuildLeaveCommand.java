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
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.database.SQLHandler;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.commands.command.Command;
import pl.grzegorz2047.openguild.commands.command.CommandException;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.managers.TagManager;

/**
 * Command used by players to leave current guild.
 * <p>
 * Usage: /guild leave
 */
public class GuildLeaveCommand extends Command {
    private final Guilds guilds;

    public GuildLeaveCommand(String[] aliases, Guilds guilds) {
        super(aliases);
        setPermission("openguild.command.leave");
        this.guilds = guilds;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }
        Player player = (Player) sender;
        String replace = MsgManager.get("broadcast-leave")
                .replace("{PLAYER}", player.getDisplayName())
                .replace("{TAG}", guilds.getPlayerGuild(player.getUniqueId()).getName().toUpperCase());
        guilds.playerLeaveGuild(player, replace);
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
