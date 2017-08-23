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

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.grzegorz2047.openguild2047.database.SQLHandler;
import pl.grzegorz2047.openguild2047.guilds.Guilds;
import pl.grzegorz2047.openguild2047.guilds.Guild;
import pl.grzegorz2047.openguild2047.commands.command.Command;
import pl.grzegorz2047.openguild2047.commands.command.CommandException;
import pl.grzegorz2047.openguild2047.events.guild.GuildDescriptionChangeEvent;
import pl.grzegorz2047.openguild2047.managers.MsgManager;
import pl.grzegorz2047.openguild2047.utils.GenUtil;

/**
 * Command used to change guilds' description.
 * <p>
 * Usage: /guild desc set [new description]
 */
public class GuildDescriptionCommand extends Command {
    private final Guilds guilds;
    private final SQLHandler sqlHandler;

    public GuildDescriptionCommand(Guilds guilds, SQLHandler sqlHandler) {
        setPermission("openguild.command.description");
        this.guilds = guilds;
        this.sqlHandler = sqlHandler;
    }

    @Override
    public void execute(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MsgManager.get("cmdonlyforplayer"));
            return;
        }


        Player player = (Player) sender;
        if (!guilds.hasGuild(player)) {
            player.sendMessage(MsgManager.get("notinguild"));
            return;
        }

        Guild guild = guilds.getPlayerGuild(player.getUniqueId());

        if (args.length > 2) {
            String subCommand = args[1];
            if (subCommand.equalsIgnoreCase("set") || subCommand.equalsIgnoreCase("ustaw")) {
                if (!guild.getLeader().equals(player.getUniqueId())) {
                    player.sendMessage(MsgManager.get("playernotleader"));
                    return;
                }

                String newDescription = GenUtil.argsToString(args, 2, args.length);
                if (newDescription.length() > 32) {
                    player.sendMessage(MsgManager.get("desctoolong"));
                    return;
                }

                GuildDescriptionChangeEvent event = new GuildDescriptionChangeEvent(guild, newDescription);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }

                guild.setDescription(event.getNewDescription());
                sqlHandler.updateGuildDescription(guild);

                String msg = MsgManager.get("guild-description-changed").replace("{NEW}", event.getNewDescription());

                guild.notifyGuild(msg);
                return;
            }
        }

        player.sendMessage(ChatColor.GRAY + guild.getDescription());
    }

    @Override
    public int minArgs() {
        return 1;
    }

}
