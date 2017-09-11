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

package pl.grzegorz2047.openguild.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild.guilds.Guild;
import pl.grzegorz2047.openguild.guilds.Guilds;
import pl.grzegorz2047.openguild.managers.MsgManager;
import pl.grzegorz2047.openguild.utils.GenUtil;

import java.util.UUID;

/**
 * This command is used to send message to members of guild.
 * <p>
 * Usage: /team [message]
 */
public class TeamCommand implements CommandExecutor {

    private final Guilds guilds;
    private final String GUILD_ONLY_CHAT_FORMAT;

    public TeamCommand(Guilds guilds, FileConfiguration config) {
        this.guilds = guilds;
        GUILD_ONLY_CHAT_FORMAT = config.getString("chat.guild-format", "&8[&aGuild&8] &b{PLAYER}&7: &f{MESSAGE}").replace("&", "§");


    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MsgManager.get("usage").replace("{USAGE}", "/t <msg>"));
            return true;
        }


        Player player = (Player) sender;
        if (!guilds.hasGuild(player)) {
            player.sendMessage(MsgManager.get("notinguild"));
            return true;
        }

        String message = GenUtil.argsToString(args, 0, args.length);

        Guild guild = guilds.getPlayerGuild(player.getUniqueId());
        String format = GUILD_ONLY_CHAT_FORMAT
                .replace("{GUILD}", guild.getName())
                .replace("{PLAYER}", player.getName())
                .replace("{MESSAGE}", message);
        for (UUID uuid : guild.getMembers()) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
            if (op.isOnline()) {
                op.getPlayer().sendMessage(format);
            }
        }

        return true;
    }

}
